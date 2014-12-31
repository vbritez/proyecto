package com.tigo.cs.api.facade;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import py.com.lothar.menumovil.data.MessageQuotaResponse;
import py.com.lothar.menumovil.data.SendMessageResponse;
import py.com.lothar.menumovil.data.ValidMsisdnResponse;

import com.tigo.core.common.header.request.v2.RequestHeader;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.CellPhoneNumberUtil;
import com.tigo.cs.commons.util.Cryptographer;
import com.tigo.cs.domain.Application;
import com.tigo.cs.domain.Auditory;
import com.tigo.cs.domain.MenuMovilPeriod;
import com.tigo.cs.domain.MenuMovilUser;
import com.tigo.cs.domain.Message;
import com.tigo.getaccountportinginfo.v1.GetAccountPortingInfo;
import com.tigo.getaccountportinginfo.v1.GetAccountPortingInfoEp;
import com.tigo.getaccountportinginforequest.v1.GetAccountPortingInfoRequest;
import com.tigo.getaccountportinginforequest.v1.GetAccountPortingInfoRequest.RequestBody;
import com.tigo.getaccountportinginforesponse.v1.GetAccountPortingInfoResponse;

public abstract class MenuMovilAPI extends AbstractAPI<String> implements Serializable {

    private static final long serialVersionUID = 676254282735321034L;

    public static String idPlataforma;
    public static String idProceso;
    public static String shortNumber;
    private static String GET_ACCOUNT_INFO_PORITNG_WSDL;
    protected static GetAccountPortingInfo serviceAccountPorting;
    private String portableMSISDNPattern;
    private List<String> whiteList;

    protected MenuMovilAPI() {
        super(String.class);
    }

    @PostConstruct
    public void postConstruct() {
        try {
            idPlataforma = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "menumovil.ncenter.idPlataforma");
            idProceso = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "menumovil.ncenter.idProceso");
            shortNumber = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "menumovil.shortnumber");
            GET_ACCOUNT_INFO_PORITNG_WSDL = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "menumovil.getaccount.info.porting.wsdl");
            URL url = new URL(GET_ACCOUNT_INFO_PORITNG_WSDL);

            if (serviceAccountPorting == null) {
                GetAccountPortingInfoEp service = new GetAccountPortingInfoEp(url);
                serviceAccountPorting = service.getGetAccountPortingInfoPt();
            }
        } catch (GenericFacadeException e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
        } catch (MalformedURLException e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
        }
    }

    public GetAccountPortingInfo getServiceAccountPorting() {
        if (serviceAccountPorting == null) {
            postConstruct();
        }
        return serviceAccountPorting;
    }

    public SendMessageResponse sendMessage(String user, String pass, String appKey, String msisdn, String message) {
        SendMessageResponse response = new SendMessageResponse();
        if (getServiceAccountPorting() != null) {
            Long availableQuota = 0l;

            if (user != null && !user.trim().equals("") && pass != null
                && !pass.trim().equals("") && appKey != null
                && !appKey.trim().equals("") && message != null
                && !message.trim().equals("") && msisdn != null
                && !msisdn.trim().equals("")) {
                try {
                    Application application = getFacadeContainer().getApplicationAPI().findByKey(
                            appKey);

                    /* Si es que existe la aplicacion */
                    if (application != null) {
                        /*
                         * Si es distinto de nulo significa que es un usuario
                         * valido para MenuMovil y puede enviar mensajes
                         */
                        MenuMovilUser menuMovilUser = getFacadeContainer().getMenuMovilUserAPI().getMenuMovileUser(
                                user, Cryptographer.sha256Doble(pass), appKey);
                        if (menuMovilUser != null) {
                            /* Si la quota disponible es mayor a 0 puede enviar */
                            MenuMovilPeriod menuMovilPeriod = getFacadeContainer().getMenuMovilPeriodAPI().getMenuMovilPeriodByPeriod(
                                    new Date(), menuMovilUser);

                            if (menuMovilPeriod == null) {
                                response.setAvailableQuota(0);
                                response.setCode(0);
                                response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                                        "menu.movil.response.availablequota.period.error"));
                            } else if (menuMovilPeriod.getMenuMovilPeriodCod() == null) {
                                response.setAvailableQuota(0);
                                response.setCode(0);
                                response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                                        "menu.movil.response.expirationdate.error"));

                            } else {
                                availableQuota = menuMovilPeriod != null
                                    && menuMovilPeriod.getMenuMovilPeriodCod() != null ? menuMovilPeriod.getQuotaTotal()
                                    - menuMovilPeriod.getQuotaUsed() : 0l;

                                if (availableQuota > 0l) {
                                    /* Se valida que el msisdn sea valido */
                                    if (validateMsisdn(msisdn)) {
                                        msisdn = CellPhoneNumberUtil.correctMsisdnToLocalFormat(msisdn);
                                        if (getWhiteList() != null) {
                                            if (getWhiteList().contains(msisdn)) {
                                                Boolean result = getFacadeContainer().getNCenterWSAPI().sendSMSNotification(
                                                        idPlataforma,
                                                        idProceso, msisdn,
                                                        message);
                                                if (result) {
                                                    response.setCode(1);
                                                    response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                                                            "menu.movil.response.success"));
                                                    updateMessageSended(menuMovilPeriod);
                                                    response.setAvailableQuota(availableQuota.intValue() - 1);

                                                } else {
                                                    response.setAvailableQuota(availableQuota.intValue());
                                                    response.setCode(0);
                                                    response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                                                            "menu.movil.response.error"));
                                                }
                                            } else {
                                                response.setAvailableQuota(availableQuota.intValue());
                                                response.setCode(0);
                                                response.setDescription(MessageFormat.format(
                                                        "El {0} no esta en el whiteList",
                                                        msisdn));
                                            }
                                        } else {
                                            Boolean result = getFacadeContainer().getNCenterWSAPI().sendSMSNotification(
                                                    idPlataforma, idProceso,
                                                    msisdn, message);
                                            if (result) {
                                                response.setCode(1);
                                                response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                                                        "menu.movil.response.success"));
                                                updateMessageSended(menuMovilPeriod);
                                                response.setAvailableQuota(availableQuota.intValue() - 1);

                                            } else {
                                                response.setAvailableQuota(availableQuota.intValue());
                                                response.setCode(0);
                                                response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                                                        "menu.movil.response.error"));
                                            }
                                        }

                                    } else {
                                        response.setAvailableQuota(availableQuota.intValue());
                                        response.setCode(0);
                                        response.setDescription(MessageFormat.format(
                                                getFacadeContainer().getI18nAPI().iValue(
                                                        "menu.movil.msisdn.not.valid"),
                                                msisdn));
                                    }

                                } else {
                                    response.setCode(0);
                                    response.setAvailableQuota(0);
                                    response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                                            "menu.movil.response.availablequota.error"));
                                }

                            }

                        } else {
                            response.setCode(0);
                            response.setAvailableQuota(availableQuota.intValue());
                            response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                                    "menu.movil.response.user.error"));

                        }
                    } else {
                        response.setCode(0);
                        response.setAvailableQuota(availableQuota.intValue());
                        response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                                "menu.movil.response.application.error"));
                    }

                    if (response.getCode() == 1) {
                        Message mess = new Message();
                        mess.setApplication(application);
                        mess.setDateinDat(new Date());
                        mess.setDateoutDat(new Date());
                        mess.setDestinationChr(msisdn);
                        mess.setOriginChr(shortNumber);
                        mess.setLengthinNum(message.length());
                        mess.setMessageinChr(message);
                        mess = getFacadeContainer().getMessageAPI().create(mess);
                        response.setRefCode(mess.getMessageCod());
                        getFacadeContainer().getNotifier().signalMenuMovil(
                                getClass(),
                                Action.INFO,
                                MessageFormat.format(
                                        getFacadeContainer().getI18nAPI().iValue(
                                                "menu.movil.consult.sendMessage"),
                                        user, msisdn, response.getCode(),
                                        response.getDescription(), "message",
                                        response.getRefCode()));
                        getFacadeContainer().getNotifier().info(
                                getClass(),
                                null,
                                MessageFormat.format(
                                        getFacadeContainer().getI18nAPI().iValue(
                                                "menu.movil.consult.sendMessage"),
                                        user, msisdn, response.getCode(),
                                        response.getDescription(), "message",
                                        response.getRefCode()));
                    } else {
                        Auditory au = getFacadeContainer().getNotifier().signalMenuMovil(
                                getClass(),
                                Action.WARNING,
                                MessageFormat.format(
                                        getFacadeContainer().getI18nAPI().iValue(
                                                "menu.movil.consult.sendMessage.without.code"),
                                        user, msisdn, response.getCode(),
                                        response.getDescription(), "auditory"));
                        response.setRefCode(au.getAuditoryCod());
                        getFacadeContainer().getNotifier().warn(
                                MenuMovilAPI.class,
                                null,
                                MessageFormat.format(
                                        getFacadeContainer().getI18nAPI().iValue(
                                                "menu.movil.consult.sendMessage"),
                                        user, msisdn, response.getCode(),
                                        response.getDescription(), "auditory",
                                        response.getRefCode()));
                    }

                } catch (Exception e) {
                    response.setAvailableQuota(0);
                    response.setCode(0);
                    response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                            "menu.movil.general.error"));
                    Auditory au = getFacadeContainer().getNotifier().signalMenuMovil(
                            getClass(),
                            Action.ERROR,
                            MessageFormat.format(
                                    getFacadeContainer().getI18nAPI().iValue(
                                            "menu.movil.consult.sendMessage.without.code"),
                                    user, msisdn, response.getCode(),
                                    response.getDescription(), "auditory"));
                    response.setRefCode(au.getAuditoryCod());
                    getFacadeContainer().getNotifier().error(
                            MenuMovilAPI.class,
                            null,
                            MessageFormat.format(
                                    getFacadeContainer().getI18nAPI().iValue(
                                            "menu.movil.consult.sendMessage"),
                                    user, msisdn, response.getCode(),
                                    response.getDescription(), "auditory",
                                    response.getRefCode()), e);
                    getFacadeContainer().getNotifier().error(getClass(), null,
                            e.getMessage(), e);
                }

            } else {
                response.setAvailableQuota(availableQuota.intValue());
                response.setCode(0);
                response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                        "menu.movil.sendMessage.fields.not.null"));
                Auditory au = getFacadeContainer().getNotifier().signalMenuMovil(
                        getClass(),
                        Action.WARNING,
                        MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "menu.movil.consult.sendMessage.without.code"),
                                user, msisdn, response.getCode(),
                                response.getDescription(), "auditory"));
                response.setRefCode(au.getAuditoryCod());
                getFacadeContainer().getNotifier().warn(
                        MenuMovilAPI.class,
                        null,
                        MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "menu.movil.consult.sendMessage"),
                                user, msisdn, response.getCode(),
                                response.getDescription(), "auditory",
                                response.getRefCode()));
            }

        } else {
            response.setAvailableQuota(0);
            response.setCode(0);
            response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "menu.movil.general.error"));
            Auditory au = getFacadeContainer().getNotifier().signalMenuMovil(
                    getClass(),
                    Action.ERROR,
                    MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "menu.movil.consult.sendMessage.without.code"),
                            user, msisdn, response.getCode(),
                            response.getDescription(), "auditory"));
            response.setRefCode(au.getAuditoryCod());
            getFacadeContainer().getNotifier().error(
                    MenuMovilAPI.class,
                    null,
                    MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "menu.movil.consult.sendMessage"), user,
                            msisdn, response.getCode(),
                            response.getDescription(), "auditory",
                            response.getRefCode()), null);
            getFacadeContainer().getNotifier().error(
                    getClass(),
                    null,
                    String.format(
                            "No se ha inicializado el serviceAccountPorting. URL: %s",
                            GET_ACCOUNT_INFO_PORITNG_WSDL), null);
        }

        return response;

    }

    private synchronized void updateMessageSended(MenuMovilPeriod periodo) {
        try {
            periodo.setQuotaUsed(periodo.getQuotaUsed() + 1l);
            getFacadeContainer().getMenuMovilPeriodAPI().edit(periodo);
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(MenuMovilAPI.class, null,
                    e.getMessage(), e);
        }

    }

    public MessageQuotaResponse messageQuota(String user, String pass, String appKey) {
        MessageQuotaResponse response = new MessageQuotaResponse();
        if (getServiceAccountPorting() != null) {
            Long availableQuota = 0l;
            if (user != null && !user.trim().equals("") && pass != null
                && !pass.trim().equals("") && appKey != null
                && !appKey.trim().equals("")) {
                try {
                    Application application = getFacadeContainer().getApplicationAPI().findByKey(
                            appKey);
                    /* Si es que existe la aplicacion */
                    if (application != null) {
                        /*
                         * Si es distinto de nulo significa que es un usuario
                         * valido para MenuMovil y puede enviar mensajes
                         */
                        MenuMovilUser menuMovilUser = getFacadeContainer().getMenuMovilUserAPI().getMenuMovileUser(
                                user, Cryptographer.sha256Doble(pass), appKey);
                        if (menuMovilUser != null) {
                            MenuMovilPeriod menuMovilPeriod = getFacadeContainer().getMenuMovilPeriodAPI().getMenuMovilPeriodByPeriod(
                                    new Date(), menuMovilUser);

                            if (menuMovilPeriod != null) {
                                availableQuota = menuMovilPeriod.getQuotaTotal()
                                    - menuMovilPeriod.getQuotaUsed();
                                response.setAvailableQuota(availableQuota.intValue());
                                response.setUsedQuota(menuMovilPeriod.getQuotaUsed().intValue());
                                response.setCode(1);
                                response.setDescription("OK");
                            } else {
                                response.setAvailableQuota(availableQuota.intValue());
                                response.setUsedQuota(0);
                                response.setCode(0);
                                response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                                        "menu.movil.quota.error"));
                            }

                        } else {
                            response.setAvailableQuota(availableQuota.intValue());
                            response.setUsedQuota(0);
                            response.setCode(0);
                            response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                                    "menu.movil.response.user.error"));
                        }
                    } else {
                        response.setAvailableQuota(availableQuota.intValue());
                        response.setUsedQuota(0);
                        response.setCode(0);
                        response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                                "menu.movil.response.application.error"));
                    }

                    if (response.getCode() == 1) {
                        Auditory au = getFacadeContainer().getNotifier().signalMenuMovil(
                                getClass(),
                                Action.INFO,
                                MessageFormat.format(
                                        getFacadeContainer().getI18nAPI().iValue(
                                                "menu.movil.consult.messageQuota.without.code"),
                                        user, response.getCode(),
                                        response.getDescription(),
                                        response.getAvailableQuota()));
                        getFacadeContainer().getNotifier().info(
                                MenuMovilAPI.class,
                                null,
                                MessageFormat.format(
                                        getFacadeContainer().getI18nAPI().iValue(
                                                "menu.movil.consult.messageQuota"),
                                        user, response.getCode(),
                                        response.getDescription(),
                                        response.getAvailableQuota(),
                                        au.getAuditoryCod()));
                    } else {
                        Auditory au = getFacadeContainer().getNotifier().signalMenuMovil(
                                getClass(),
                                Action.WARNING,
                                MessageFormat.format(
                                        getFacadeContainer().getI18nAPI().iValue(
                                                "menu.movil.consult.messageQuota.without.code"),
                                        user, response.getCode(),
                                        response.getDescription(),
                                        response.getAvailableQuota()));
                        getFacadeContainer().getNotifier().warn(
                                MenuMovilAPI.class,
                                null,
                                MessageFormat.format(
                                        getFacadeContainer().getI18nAPI().iValue(
                                                "menu.movil.consult.messageQuota"),
                                        user, response.getCode(),
                                        response.getDescription(),
                                        response.getAvailableQuota(),
                                        au.getAuditoryCod()));
                    }

                } catch (Exception e) {
                    response.setAvailableQuota(availableQuota.intValue());
                    response.setUsedQuota(0);
                    response.setCode(0);
                    response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                            "menu.movil.general.error"));
                    Auditory au = getFacadeContainer().getNotifier().signalMenuMovil(
                            getClass(),
                            Action.ERROR,
                            MessageFormat.format(
                                    getFacadeContainer().getI18nAPI().iValue(
                                            "menu.movil.consult.messageQuota.without.code"),
                                    user, response.getCode(),
                                    response.getDescription(),
                                    response.getAvailableQuota()));
                    getFacadeContainer().getNotifier().error(
                            MenuMovilAPI.class,
                            null,
                            MessageFormat.format(
                                    getFacadeContainer().getI18nAPI().iValue(
                                            "menu.movil.consult.messageQuota"),
                                    user, response.getCode(),
                                    response.getDescription(),
                                    response.getAvailableQuota(),
                                    au.getAuditoryCod()), e);
                    getFacadeContainer().getNotifier().error(getClass(), null,
                            e.getMessage(), e);

                }

            } else {
                response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                        "menu.movil.messageQuota.fields.not.null"));
                response.setAvailableQuota(availableQuota.intValue());
                response.setCode(0);
                Auditory au = getFacadeContainer().getNotifier().signalMenuMovil(
                        getClass(),
                        Action.WARNING,
                        MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "menu.movil.consult.messageQuota.without.code"),
                                user, response.getCode(),
                                response.getDescription(),
                                response.getAvailableQuota()));
                getFacadeContainer().getNotifier().warn(
                        MenuMovilAPI.class,
                        null,
                        MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "menu.movil.consult.messageQuota"),
                                user, response.getCode(),
                                response.getDescription(),
                                response.getAvailableQuota(),
                                au.getAuditoryCod()));
            }
        } else {
            response.setAvailableQuota(0);
            response.setUsedQuota(0);
            response.setCode(0);
            response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "menu.movil.general.error"));
            Auditory au = getFacadeContainer().getNotifier().signalMenuMovil(
                    getClass(),
                    Action.ERROR,
                    MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "menu.movil.consult.messageQuota.without.code"),
                            user, response.getCode(),
                            response.getDescription(),
                            response.getAvailableQuota()));
            getFacadeContainer().getNotifier().error(
                    MenuMovilAPI.class,
                    null,
                    MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "menu.movil.consult.messageQuota"), user,
                            response.getCode(), response.getDescription(),
                            response.getAvailableQuota(), au.getAuditoryCod()),
                    null);
            getFacadeContainer().getNotifier().error(
                    getClass(),
                    null,
                    String.format(
                            "No se ha inicializado el serviceAccountPorting. URL: %s",
                            GET_ACCOUNT_INFO_PORITNG_WSDL), null);
        }
        return response;
    }

    /* Valida si el MSISDN pertenece a tigo */
    public ValidMsisdnResponse validMsisdn(String user, String pass, String appKey, String msisdn) {
        ValidMsisdnResponse response = new ValidMsisdnResponse();
        if (getServiceAccountPorting() != null) {
            if (user != null && !user.trim().equals("") && pass != null
                && !pass.trim().equals("") && appKey != null
                && !appKey.trim().equals("") && msisdn != null
                && !msisdn.trim().equals("")) {
                try {
                    Application application = getFacadeContainer().getApplicationAPI().findByKey(
                            appKey);
                    /* Si es que existe la aplicacion */
                    if (application != null) {
                        /*
                         * Si es distinto de nulo significa que es un usuario
                         * valido para MenuMovil y puede enviar mensajes
                         */
                        MenuMovilUser menuMovilUser = getFacadeContainer().getMenuMovilUserAPI().getMenuMovileUser(
                                user, Cryptographer.sha256Doble(pass), appKey);
                        if (menuMovilUser != null) {

                            if (validateMsisdn(msisdn)) {
                                msisdn = CellPhoneNumberUtil.correctMsisdnToLocalFormat(msisdn);
                                GetAccountPortingInfoRequest request = new GetAccountPortingInfoRequest();
                                request.setRequestHeader(new RequestHeader());
                                request.getRequestHeader().setConsumer(
                                        new RequestHeader.Consumer());
                                request.getRequestHeader().getConsumer().setCode(
                                        "0");
                                request.getRequestHeader().getConsumer().setName(
                                        "0");
                                request.getRequestHeader().setService(
                                        new RequestHeader.Service());
                                request.getRequestHeader().getService().setCode(
                                        "0");
                                request.getRequestHeader().getService().setName(
                                        "0");
                                request.getRequestHeader().setMessage(
                                        new RequestHeader.Message());
                                request.getRequestHeader().getMessage().setMessageId(
                                        0l);
                                request.getRequestHeader().getMessage().setMessageIdCorrelation(
                                        0l);
                                request.setRequestBody(new RequestBody());
                                request.getRequestBody().setMsisdn(msisdn);

                                GetAccountPortingInfoResponse respon = serviceAccountPorting.process(request);

                                if (respon.getResponseHeader().getMessage().getState().toString().equalsIgnoreCase(
                                        "OK")) {
                                    if (respon.getResponseBody().getPortingInfo().getRountingNumberCode().trim().equals(
                                            "404")
                                        && respon.getResponseBody().getPortingInfo().getRountingNumberName().equalsIgnoreCase(
                                                "TIGO")) {
                                        response.setValid(true);
                                        response.setCode(1);
                                        response.setDescription(MessageFormat.format(
                                                getFacadeContainer().getI18nAPI().iValue(
                                                        "menu.movil.response.valid.tigo"),
                                                msisdn));
                                    } else {
                                        response.setValid(false);
                                        response.setCode(1);
                                        response.setDescription(MessageFormat.format(
                                                getFacadeContainer().getI18nAPI().iValue(
                                                        "menu.movil.response.not.valid.tigo"),
                                                msisdn));
                                    }
                                } else {
                                    if (msisdn.startsWith("098")) {
                                        response.setValid(true);
                                        response.setCode(1);
                                        response.setDescription(MessageFormat.format(
                                                getFacadeContainer().getI18nAPI().iValue(
                                                        "menu.movil.response.valid.tigo"),
                                                msisdn));
                                    } else {
                                        response.setValid(false);
                                        response.setCode(1);
                                        response.setDescription(MessageFormat.format(
                                                getFacadeContainer().getI18nAPI().iValue(
                                                        "menu.movil.response.not.valid.tigo"),
                                                msisdn));
                                    }
                                }
                            } else {
                                response.setValid(false);
                                response.setCode(0);
                                response.setDescription(MessageFormat.format(
                                        getFacadeContainer().getI18nAPI().iValue(
                                                "menu.movil.msisdn.not.valid"),
                                        msisdn));
                            }

                        } else {
                            response.setValid(false);
                            response.setCode(0);
                            response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                                    "menu.movil.response.user.error"));
                        }
                    } else {
                        response.setValid(false);
                        response.setCode(0);
                        response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                                "menu.movil.response.application.error"));
                    }

                    if (response.getCode() == 1) {
                        Auditory au = getFacadeContainer().getNotifier().signalMenuMovil(
                                getClass(),
                                Action.INFO,
                                MessageFormat.format(
                                        getFacadeContainer().getI18nAPI().iValue(
                                                "menu.movil.consult.validMsisdn.without.code"),
                                        user, msisdn, response.getCode(),
                                        response.getDescription(),
                                        response.isValid()));
                        getFacadeContainer().getNotifier().info(
                                MenuMovilAPI.class,
                                null,
                                MessageFormat.format(
                                        getFacadeContainer().getI18nAPI().iValue(
                                                "menu.movil.consult.validMsisdn"),
                                        user, msisdn, response.getCode(),
                                        response.getDescription(),
                                        response.isValid(), au.getAuditoryCod()));
                    } else {
                        Auditory au = getFacadeContainer().getNotifier().signalMenuMovil(
                                getClass(),
                                Action.WARNING,
                                MessageFormat.format(
                                        getFacadeContainer().getI18nAPI().iValue(
                                                "menu.movil.consult.validMsisdn.without.code"),
                                        user, msisdn, response.getCode(),
                                        response.getDescription(),
                                        response.isValid()));
                        getFacadeContainer().getNotifier().warn(
                                MenuMovilAPI.class,
                                null,
                                MessageFormat.format(
                                        getFacadeContainer().getI18nAPI().iValue(
                                                "menu.movil.consult.validMsisdn"),
                                        user, msisdn, response.getCode(),
                                        response.getDescription(),
                                        response.isValid(), au.getAuditoryCod()));
                    }

                } catch (Exception e) {
                    response.setValid(false);
                    response.setCode(0);
                    response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                            "menu.movil.general.error"));
                    Auditory au = getFacadeContainer().getNotifier().signalMenuMovil(
                            getClass(),
                            Action.ERROR,
                            MessageFormat.format(
                                    getFacadeContainer().getI18nAPI().iValue(
                                            "menu.movil.consult.validMsisdn.without.code"),
                                    user, msisdn, response.getCode(),
                                    response.getDescription(),
                                    response.isValid()));
                    getFacadeContainer().getNotifier().error(
                            MenuMovilAPI.class,
                            null,
                            MessageFormat.format(
                                    getFacadeContainer().getI18nAPI().iValue(
                                            "menu.movil.consult.validMsisdn"),
                                    user, msisdn, response.getCode(),
                                    response.getDescription(),
                                    response.isValid(), au.getAuditoryCod()), e);
                    getFacadeContainer().getNotifier().error(
                            MenuMovilAPI.class, null, e.getMessage(), e);
                }

            } else {
                response.setValid(false);
                response.setCode(0);
                response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                        "menu.movil.validMsisdn.fields.not.null"));
                Auditory au = getFacadeContainer().getNotifier().signalMenuMovil(
                        getClass(),
                        Action.WARNING,
                        MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "menu.movil.consult.validMsisdn.without.code"),
                                user, msisdn, response.getCode(),
                                response.getDescription(), response.isValid()));
                getFacadeContainer().getNotifier().info(
                        MenuMovilAPI.class,
                        null,
                        MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "menu.movil.consult.validMsisdn"),
                                user, msisdn, response.getCode(),
                                response.getDescription(), response.isValid(),
                                au.getAuditoryCod()));
            }
        } else {

            response.setValid(false);
            response.setCode(0);
            response.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "menu.movil.general.error"));
            Auditory au = getFacadeContainer().getNotifier().signalMenuMovil(
                    getClass(),
                    Action.ERROR,
                    MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "menu.movil.consult.validMsisdn.without.code"),
                            user, msisdn, response.getCode(),
                            response.getDescription(), response.isValid()));
            getFacadeContainer().getNotifier().error(
                    MenuMovilAPI.class,
                    null,
                    MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "menu.movil.consult.validMsisdn"), user,
                            msisdn, response.getCode(),
                            response.getDescription(), response.isValid(),
                            au.getAuditoryCod()), null);
            getFacadeContainer().getNotifier().error(
                    MenuMovilAPI.class,
                    null,
                    String.format(
                            "No se ha inicializado el serviceAccountPorting. URL: %s",
                            GET_ACCOUNT_INFO_PORITNG_WSDL), null);

        }

        return response;

    }

    private boolean validateMsisdn(String value) {
        return value.matches(getPortableMSISDNPattern());
    }

    private String getPortableMSISDNPattern() {
        if (this.portableMSISDNPattern == null) {
            try {
                portableMSISDNPattern = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "portability.msisdn.format.AllFormat");
            } catch (GenericFacadeException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null,
                        ex);
            }
        }
        return portableMSISDNPattern;
    }

    public List<String> getWhiteList() {
        if (getFacadeContainer().getI18nAPI().iValue("menu.movil.whiteList").equalsIgnoreCase(
                "ON")) {
            this.whiteList = new ArrayList<String>();
            try {
                // Abrimos el archivo
                FileInputStream fstream = new FileInputStream(getFacadeContainer().getI18nAPI().iValue(
                        "menu.movil.whiteList.path"));
                // Creamos el objeto de entrada
                DataInputStream entrada = new DataInputStream(fstream);
                // Creamos el Buffer de Lectura
                BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));
                String strLinea;
                // Leer el archivo linea por linea
                while ((strLinea = buffer.readLine()) != null) {
                    whiteList.add(strLinea);
                }
                // Cerramos el archivo
                entrada.close();
            } catch (Exception e) { // Catch de excepciones
                System.err.println("Ocurrio un error: " + e.getMessage());
            }
        }
        return whiteList;
    }

}
