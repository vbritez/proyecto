package com.tigo.cs.api.facade;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.DataCheck;
import com.tigo.cs.domain.FeatureElement;
import com.tigo.cs.domain.FeatureElementEntry;
import com.tigo.cs.domain.FeatureEntryType;
import com.tigo.cs.domain.FeatureValue;
import com.tigo.cs.domain.Phone;
import com.tigo.cs.domain.PhoneList;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.ussd.UssdApplication;
import com.tigo.cs.domain.ussd.UssdApplicationServer;
import com.tigo.cs.domain.ussd.UssdDriver;
import com.tigo.cs.domain.ussd.UssdMenuEntry;
import com.tigo.cs.domain.ussd.UssdMenuEntryState;
import com.tigo.cs.domain.ussd.UssdMenuEntryType;
import com.tigo.cs.domain.ussd.UssdMenuEntryUssdUser;
import com.tigo.cs.domain.ussd.UssdMenuEntryUssdUserPK;
import com.tigo.cs.domain.ussd.UssdUser;
import com.tigo.cs.domain.ussd.UssdValidation;

public abstract class FeatureElementAPI extends AbstractAPI<FeatureElement> {

    private UssdApplication ussdApplication;

    public FeatureElementAPI() {
        super(FeatureElement.class);
    }

    public String deleteEntity(List<FeatureElement> entities, String root) throws Exception {
        try {
            for (FeatureElement entity : entities) {
                // BORRAR TODAS LAS ENTRADAS USSD
                UssdMenuEntry ussdMenuEntryRoot = getFacadeContainer().getUssdMenuEntryAPI().findByApplicationAndOwner(
                        entity.getFeatureElementCod(), Long.parseLong(root));
                if (ussdMenuEntryRoot != null) {
                    List<UssdMenuEntryUssdUser> ussdMenuEntryUssdUser = getFacadeContainer().getUssdMenuEntryUssdUserAPI().findByMenuEntry(
                            ussdMenuEntryRoot.getId());
                    for (UssdMenuEntryUssdUser mu : ussdMenuEntryUssdUser) {
                        getFacadeContainer().getUssdMenuEntryUssdUserAPI().remove(
                                mu);
                    }
                    getFacadeContainer().getUssdMenuEntryAPI().remove(
                            ussdMenuEntryRoot);
                }

                FeatureElement entity_ = find(entity.getFeatureElementCod());
                entity_.setEnabledChr(false);
                super.edit(entity_);
            }

            return null;
        } catch (Exception e) {
            throw new Exception(MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "web.admin.backingBean.commons.message.AnErrorHasOcurred"),
                    e));
        }
    }

    public String saveEdit(FeatureElement entity, String rootId, String ussdApplicationServerId, String driverId, String validatorId, String dynamicDriverId) throws Exception {
        try {
            if (entity.getFeatureElementCod() == null) {

                // SE CREA EL FEATURE ELEMENT
                Long pk = getFeatureElementNextval();
                entity.setFeatureElementCod(pk);

                FeatureElementEntry aux = entity.getFeatureElementEntries().get(
                        0);
                FeatureElementEntry feeRoot = new FeatureElementEntry();
                feeRoot.setCodFeatureEntryType(aux.getCodFeatureEntryType());
                feeRoot.setDataCheck(aux.getDataCheck());
                feeRoot.setDescriptionChr(aux.getDescriptionChr());
                feeRoot.setFeatureElement(aux.getFeatureElement());
                feeRoot.setFeatureElementEntries(aux.getFeatureElementEntries());
                feeRoot.setCodOwnerEntry(aux.getCodOwnerEntry());
                feeRoot.setOrderNum(aux.getOrderNum());
                feeRoot.setTitleChr(aux.getTitleChr());

                // SE GUARDA EL USSD APPLICATION
                ussdApplication = new UssdApplication();
                ussdApplication.setId(pk);
                ussdApplication.setDescription(entity.getDescriptionChr());
                ussdApplication.setCode(entity.getDescriptionChr());
                ussdApplication.setUssdApplicationServer(new UssdApplicationServer(new Long(ussdApplicationServerId)));
                getFacadeContainer().getUssdApplicationAPI().create(ussdApplication);
                
                /*cuando se meta MENU GUIADO,  se debe de guardar tambien una entrada en USSD_APP_APP_SERVER */

                saveFeatureElementEntries(entity, ussdApplication, feeRoot,
                        rootId, validatorId, ussdApplicationServerId, driverId,
                        dynamicDriverId);
                return null;
            } else {

                // VERIFICAR SI EL FEATURE ELEMENT YA TIENE DATOS ASOCIADOS
                List<FeatureValue> datas = getFacadeContainer().getFeatureValueAPI().getFeatureValueList(
                        entity.getFeatureElementCod());
                if (datas != null && datas.size() > 0) { // EN CASO DE QUE YA
                                                         // EXISTAN DATOS PARA
                                                         // EL FEATURE ELEMENT

                    // MODIFICAR LA DESCRIPCION DE USSD APPLICATION
                    UssdApplication ussdApplication = getFacadeContainer().getUssdApplicationAPI().find(
                            entity.getFeatureElementCod());
                    ussdApplication.setDescription(entity.getDescriptionChr());
                    ussdApplication.setCode(entity.getDescriptionChr());
                    getFacadeContainer().getUssdApplicationAPI().edit(
                            ussdApplication);

                    // MODIFICAR EL USSD MENU ENTRY PARA LA ENTRADA DEL FEATURE
                    // ELEMENT
                    UssdMenuEntry ussdElementEntry = getFacadeContainer().getUssdMenuEntryAPI().find(
                            entity.getFeatureElementCod());
                    ussdElementEntry.setDescription(entity.getDescriptionChr());
                    ussdElementEntry.setTitle(entity.getDescriptionChr());

                    // MODIFICAR LA DESCRIPCION DE CADA FEE Y USSD_MENU_ENTRY
                    FeatureElementEntry feeRoot = getFirstFeatureElementEntry(entity.getFeatureElementEntries());
                    editDescription(feeRoot);

                    // ELIMINAR LAS RELACINES EN USSD MENU ENTRY USSD USER
                    List<UssdMenuEntryUssdUser> ussdMenuEntryUssdUser = getFacadeContainer().getUssdMenuEntryUssdUserAPI().findByMenuEntry(
                            entity.getFeatureElementCod());
                    for (UssdMenuEntryUssdUser mu : ussdMenuEntryUssdUser) {
                        getFacadeContainer().getUssdMenuEntryUssdUserAPI().remove(
                                mu);
                    }

                    // GUARDAR LAS RELACIONES CON USSD MENU ENTRY USSD USER
                    saveUserphonesOrPhones(entity, ussdApplication,
                            ussdElementEntry);

                    super.edit(entity);

                } else { // EN CASO DE QUE TODAVIA NO SE HAYAN CARGADO DATOS

                    // BORRAR TODOS LOS FEATURE ELEMENT ENTRIES
                    List<FeatureElementEntry> entries = getAllFeatureElementEntriesList(entity.getFeatureElementCod());
                    for (FeatureElementEntry entry : entries) {
                        FeatureElementEntry _entry = getFacadeContainer().getFeatureElementEntryAPI().find(
                                entry.getFeatureElementEntryCod());
                        getFacadeContainer().getFeatureElementEntryAPI().remove(
                                _entry);
                    }

                    // BORRAR TODAS LAS ENTRADAS USSD
                    UssdMenuEntry ussdMenuEntryRoot = getFacadeContainer().getUssdMenuEntryAPI().findByApplicationAndOwner(
                            entity.getFeatureElementCod(),
                            Long.parseLong(rootId));
                    if (ussdMenuEntryRoot != null) {
                        List<UssdMenuEntryUssdUser> ussdMenuEntryUssdUser = getFacadeContainer().getUssdMenuEntryUssdUserAPI().findByMenuEntry(
                                ussdMenuEntryRoot.getId());
                        for (UssdMenuEntryUssdUser mu : ussdMenuEntryUssdUser) {
                            getFacadeContainer().getUssdMenuEntryUssdUserAPI().remove(
                                    mu);
                        }
                        getFacadeContainer().getUssdMenuEntryAPI().remove(
                                ussdMenuEntryRoot);
                    }

                    // SE CREA EL FEE PARA EL ROOT
                    FeatureElementEntry aux = entity.getFeatureElementEntries().get(
                            0);
                    FeatureElementEntry feeRoot = new FeatureElementEntry();
                    feeRoot.setCodFeatureEntryType(aux.getCodFeatureEntryType());
                    feeRoot.setDataCheck(aux.getDataCheck());
                    feeRoot.setDescriptionChr(aux.getDescriptionChr());
                    feeRoot.setFeatureElement(aux.getFeatureElement());
                    feeRoot.setFeatureElementEntries(aux.getFeatureElementEntries());
                    feeRoot.setCodOwnerEntry(aux.getCodOwnerEntry());
                    feeRoot.setOrderNum(aux.getOrderNum());
                    feeRoot.setTitleChr(aux.getTitleChr());

                    ussdApplication = getFacadeContainer().getUssdApplicationAPI().find(
                            entity.getFeatureElementCod());
                    saveFeatureElementEntries(entity, ussdApplication, feeRoot,
                            rootId, validatorId, ussdApplicationServerId,
                            driverId, dynamicDriverId);
                }

                return null;
            }
        } catch (Exception e) {
            throw new Exception(MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "web.admin.backingBean.commons.message.AnErrorHasOcurred"),
                    e));
        }
    }

    private void editDescription(FeatureElementEntry root) {
        List<FeatureElementEntry> children = root.getFeatureElementEntries();
        if (children != null && children.size() > 0) {
            for (FeatureElementEntry fee : children) {
                editDescription(fee);
            }
            /* se actualiza el feature element entry */
            FeatureElementEntry oldFee = getFacadeContainer().getFeatureElementEntryAPI().find(
                    root.getFeatureElementEntryCod());
            oldFee.setDescriptionChr(root.getDescriptionChr());
            oldFee.setTitleChr(root.getTitleChr());
            getFacadeContainer().getFeatureElementEntryAPI().edit(oldFee);

            /* se actualiza el ussd menu entry */
            UssdMenuEntry ussdMenuEntry = getFacadeContainer().getUssdMenuEntryAPI().find(
                    root.getFeatureElementEntryCod());
            if (ussdMenuEntry != null) {
                if (ussdMenuEntry.getOwner().getId().longValue() == root.getFeatureElement().getFeatureElementCod().longValue()) {
                    ussdMenuEntry.setTitle(root.getDescriptionChr().concat(
                            "\n1 para continuar \n0 para cancelar"));
                } else if (root.getCodFeatureEntryType().getNameChr().equals(
                        "OUTPUT")) {
                    ussdMenuEntry.setTitle(root.getDescriptionChr().concat(
                            "\n1 para continuar"));
                } else
                    ussdMenuEntry.setTitle(root.getDescriptionChr());

                ussdMenuEntry.setDescription(root.getDescriptionChr());
                getFacadeContainer().getUssdMenuEntryAPI().edit(ussdMenuEntry);
            }
        } else {
            /* se actualiza el feature element entry */
            FeatureElementEntry oldFee = getFacadeContainer().getFeatureElementEntryAPI().find(
                    root.getFeatureElementEntryCod());
            oldFee.setDescriptionChr(root.getDescriptionChr());
            oldFee.setTitleChr(root.getTitleChr());
            getFacadeContainer().getFeatureElementEntryAPI().edit(oldFee);

            /* se actualiza el ussd menu entry */
            UssdMenuEntry ussdMenuEntry = getFacadeContainer().getUssdMenuEntryAPI().find(
                    root.getFeatureElementEntryCod());
            if (ussdMenuEntry != null) {
                if (ussdMenuEntry.getOwner().getId().longValue() == root.getFeatureElement().getFeatureElementCod().longValue()) {
                    ussdMenuEntry.setTitle(root.getDescriptionChr().concat(
                            "\n1 para continuar \n0 para cancelar"));
                } else if (root.getCodFeatureEntryType().getNameChr().equals(
                        "OUTPUT")) {
                    ussdMenuEntry.setTitle(root.getDescriptionChr().concat(
                            "\n1 para continuar"));
                } else
                    ussdMenuEntry.setTitle(root.getDescriptionChr());
                ussdMenuEntry.setDescription(root.getDescriptionChr());
                getFacadeContainer().getUssdMenuEntryAPI().edit(ussdMenuEntry);
            }
        }
    }

    private FeatureElementEntry getFirstFeatureElementEntry(List<FeatureElementEntry> list) {
        FeatureElementEntry aux = list.get(0);
        FeatureElementEntry root = new FeatureElementEntry();
        root.setCodFeatureEntryType(aux.getCodFeatureEntryType());
        root.setDataCheck(aux.getDataCheck());
        root.setDescriptionChr(aux.getDescriptionChr());
        root.setFeatureElement(aux.getFeatureElement());
        root.setFeatureElementEntries(aux.getFeatureElementEntries());
        root.setCodOwnerEntry(aux.getCodOwnerEntry());
        root.setOrderNum(aux.getOrderNum());
        root.setTitleChr(aux.getTitleChr());
        root.setFeatureElementEntryCod(aux.getFeatureElementEntryCod());
        return root;
    }

    private void createUssdProfileId(FeatureElement entity, UssdApplication ussdApplication, UssdMenuEntry ussdElementEntry) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Phone openPhone = new Phone();
        openPhone.setCellphoneNum(null);
        openPhone.setClient(entity.getClientFeature().getClient());
        openPhone.setNameChr(entity.getClientFeature().getClient().getNameChr());
        getFacadeContainer().getPhoneAPI().create(openPhone);

        UssdUser ussdUser = new UssdUser();
        ussdUser.setId(openPhone.getPhoneCod());
        ussdUser.setCode(openPhone.getPhoneCod().toString());
        ussdUser.setName(openPhone.getNameChr());
        ussdUser.setDescription(MessageFormat.format(
                getFacadeContainer().getI18nAPI().iValue(
                        "web.client.screen.featureelement.message.PhoneProvisionedOnDate"),
                sdf.format(new Date())));
        ussdUser.setUssdApplication(ussdApplication);
        getFacadeContainer().getUssdUserAPI().create(ussdUser);

        UssdMenuEntryUssdUser menuEntryUssdUser = new UssdMenuEntryUssdUser();
        UssdMenuEntryUssdUserPK menuEntryUssdUserPk = new UssdMenuEntryUssdUserPK();
        menuEntryUssdUserPk.setUssdmenuentriesId(ussdElementEntry.getId());
        menuEntryUssdUserPk.setUssduserId(ussdUser.getId());
        menuEntryUssdUser.setUssdMenuEntryUssdUserPK(menuEntryUssdUserPk);
        menuEntryUssdUser.setUssdUser(ussdUser);
        getFacadeContainer().getUssdMenuEntryUssdUserAPI().create(
                menuEntryUssdUser);

        Client c = null;
        try {
            c = getFacadeContainer().getClientAPI().find(
                    entity.getClientFeature().getClient().getClientCod());
        } catch (Exception e) {
        }
        if (c != null) {
            c.setUssdProfileId(openPhone.getPhoneCod());
            getFacadeContainer().getClientAPI().edit(c);
        }
    }

    private void saveUserphonesOrPhones(FeatureElement entity, UssdApplication ussdApplication, UssdMenuEntry ussdElementEntry) {
        // GUARDAR LAS RELACIONES CON USSD MENU ENTRY USSD USER
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        if (entity.getOpenChr()) {
            if (entity.getClientFeature().getClient().getUssdProfileId() == null) {
                createUssdProfileId(entity, ussdApplication, ussdElementEntry);
            } else {
                UssdUser ussdUser = getFacadeContainer().getUssdUserAPI().find(
                        entity.getClientFeature().getClient().getUssdProfileId());
                if (ussdUser != null) {
                    UssdMenuEntryUssdUser menuEntryUssdUser = new UssdMenuEntryUssdUser();
                    UssdMenuEntryUssdUserPK menuEntryUssdUserPk = new UssdMenuEntryUssdUserPK();
                    menuEntryUssdUserPk.setUssdmenuentriesId(ussdElementEntry.getId());
                    menuEntryUssdUserPk.setUssduserId(ussdUser.getId());
                    menuEntryUssdUser.setUssdMenuEntryUssdUserPK(menuEntryUssdUserPk);
                    menuEntryUssdUser.setUssdUser(ussdUser);
                    getFacadeContainer().getUssdMenuEntryUssdUserAPI().create(
                            menuEntryUssdUser);
                } else {
                    createUssdProfileId(entity, ussdApplication,
                            ussdElementEntry);
                }
            }

        } else if (entity.getUserphones() != null) {
            for (Userphone u : entity.getUserphones()) {
                UssdUser ussdUser = getFacadeContainer().getUssdUserAPI().find(
                        u.getUserphoneCod());
                if (ussdUser == null) {
                    ussdUser = new UssdUser();
                    ussdUser.setId(u.getUserphoneCod());
                    ussdUser.setCode(u.getUserphoneCod().toString());
                    ussdUser.setName(u.getNameChr());
                    ussdUser.setDescription(u.getDescriptionChr());
                    getFacadeContainer().getUssdUserAPI().create(ussdUser);
                }

                UssdMenuEntryUssdUser menuEntryUssdUser = new UssdMenuEntryUssdUser();
                UssdMenuEntryUssdUserPK menuEntryUssdUserPk = new UssdMenuEntryUssdUserPK();
                menuEntryUssdUserPk.setUssdmenuentriesId(ussdElementEntry.getId());
                menuEntryUssdUserPk.setUssduserId(ussdUser.getId());
                menuEntryUssdUser.setUssdMenuEntryUssdUserPK(menuEntryUssdUserPk);
                menuEntryUssdUser.setUssdUser(ussdUser);
                getFacadeContainer().getUssdMenuEntryUssdUserAPI().create(
                        menuEntryUssdUser);
            }
        } else if (entity.getPhoneLists() != null) {
            for (PhoneList phList : entity.getPhoneLists()) {
                List<Phone> phoneList = getFacadeContainer().getPhoneListAPI().getPhones(
                        phList.getPhoneListCod());
                for (Phone ph : phoneList) {
                    UssdUser ussdUser = getFacadeContainer().getUssdUserAPI().find(
                            ph.getPhoneCod());
                    if (ussdUser == null) {
                        ussdUser = new UssdUser();
                        ussdUser.setId(ph.getPhoneCod());
                        ussdUser.setCode(ph.getPhoneCod().toString());
                        ussdUser.setName(ph.getNameChr());
                        ussdUser.setDescription(MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "web.client.screen.featureelement.message.PhoneProvisionedOnDate"),
                                sdf.format(new Date())));
                        getFacadeContainer().getUssdUserAPI().create(ussdUser);
                    }

                    UssdMenuEntryUssdUser menuEntryUssdUser = new UssdMenuEntryUssdUser();
                    UssdMenuEntryUssdUserPK menuEntryUssdUserPk = new UssdMenuEntryUssdUserPK();
                    menuEntryUssdUserPk.setUssdmenuentriesId(ussdElementEntry.getId());
                    menuEntryUssdUserPk.setUssduserId(ussdUser.getId());
                    menuEntryUssdUser.setUssdMenuEntryUssdUserPK(menuEntryUssdUserPk);
                    menuEntryUssdUser.setUssdUser(ussdUser);
                    getFacadeContainer().getUssdMenuEntryUssdUserAPI().create(
                            menuEntryUssdUser);
                }
            }
        }
    }

    private String saveFeatureElementEntries(FeatureElement entity, UssdApplication ussdApplication, FeatureElementEntry titleEntry, String rootId, String validationId, String ussdApplicationServerId, String driverId, String dynamicDriverId) throws Exception {
        try {
            FeatureElement fe = getFacadeContainer().getFeatureElementAPI().find(
                    entity.getFeatureElementCod());
            if (fe == null) {
                super.create(entity);
            } else {
                super.edit(entity);
            }

            Long pk = entity.getFeatureElementCod();

            // SE GUARDA EL USSD MENU ENTRY PARA EL FEATURE ELEMENT
            UssdMenuEntry ussdElementEntry = new UssdMenuEntry();
            ussdElementEntry.setId(pk);
            ussdElementEntry.setCode(pk.toString());
            ussdElementEntry.setMenuorder(new BigInteger(pk.toString()));
            ussdElementEntry.setDescription(entity.getDescriptionChr());
            ussdElementEntry.setTitle(entity.getDescriptionChr());
            ussdElementEntry.setFinalmenu(new Short("0"));
            ussdElementEntry.setRootmenu(new Short("0"));
            ussdElementEntry.setUssdMenuEntryState(new UssdMenuEntryState(1L));
            ussdElementEntry.setUssdMenuEntryType(new UssdMenuEntryType(4L)); // 4
                                                                              // -
                                                                              // ItemList
            ussdElementEntry.setUssdValidation(new UssdValidation(new Long(validationId)));
            ussdElementEntry.setOwner(new UssdMenuEntry(Long.parseLong(rootId)));
            ussdElementEntry.setUssdApplication(ussdApplication);
            ussdElementEntry.setUssdApplicationServer(new UssdApplicationServer(new Long(ussdApplicationServerId)));
            getFacadeContainer().getUssdMenuEntryAPI().create(ussdElementEntry);

            // SE CREA UN USER PARA CADA USERPHONE/PHONE
            /*
             * Si el feture element es de tipo abierto Verificar si tiene un
             * UssdUser abierto para el clientFeature Si no tiene, crear un
             * phone, ussdUser y setear a ClientFeature.
             */
            saveUserphonesOrPhones(entity, ussdApplication, ussdElementEntry);

            /* SE GUARDA EL FEATURE ELEMENT PARA EL TITULO */
            Long featureElementEntryPk = pk + 1;
            // Long pkEntryTitle = pk + 1;
            titleEntry.setFeatureElementEntryCod(featureElementEntryPk);
            titleEntry.setFeatureElement(entity);
            titleEntry.setCodOwnerEntry(null);
            titleEntry.setOrderNum(1);
            titleEntry.setFinalChr(false);
            getFacadeContainer().getFeatureElementEntryAPI().create(titleEntry);

            // SE GUARDA EL USSD MENU ENTRY PARA EL FEATURE ELEMENT ENTRY TITULO
            UssdMenuEntry ussdEntryTitle = new UssdMenuEntry();
            ussdEntryTitle.setId(featureElementEntryPk);
            ussdEntryTitle.setCode(featureElementEntryPk.toString());
            ussdEntryTitle.setMenuorder(new BigInteger(titleEntry.getOrderNum().toString()));
            ussdEntryTitle.setDescription(titleEntry.getDescriptionChr());
            ussdEntryTitle.setTitle(titleEntry.getDescriptionChr());
            ussdEntryTitle.setFinalmenu(titleEntry.getFinalChr() ? new Short("1") : new Short("0"));
            ussdEntryTitle.setRootmenu(new Short("0"));

            UssdDriver driver = null;
            if (titleEntry.getFinalChr()) {
                driver = getFacadeContainer().getUssdDriverAPI().find(
                        Long.parseLong(driverId));
            }

            ussdEntryTitle.setUssdDriver1(driver);
            ussdEntryTitle.setUssdMenuEntryState(new UssdMenuEntryState(1L));
            ussdEntryTitle.setUssdMenuEntryType(new UssdMenuEntryType(2L));
            ussdEntryTitle.setUssdValidation(new UssdValidation(new Long(validationId)));
            ussdEntryTitle.setOwner(ussdElementEntry);
            ussdEntryTitle.setUssdApplication(ussdApplication);
            ussdEntryTitle.setUssdApplicationServer(new UssdApplicationServer(new Long(ussdApplicationServerId)));
            ussdEntryTitle.setTitle(ussdEntryTitle.getTitle().concat(
                    "\n1 para continuar \n0 para cancelar"));
            getFacadeContainer().getUssdMenuEntryAPI().create(ussdEntryTitle);

            Long owner = featureElementEntryPk;

            saveEntries(titleEntry, entity, featureElementEntryPk, owner,
                    validationId, ussdApplicationServerId, driverId,
                    dynamicDriverId);

        } catch (Exception e) {
            throw new Exception(MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "web.admin.backingBean.commons.message.AnErrorHasOcurred"),
                    e));
        }

        return null;
    }

    // RECURSIVO
    private Long saveEntries(FeatureElementEntry root, FeatureElement entity, Long pk, Long owner, String validationId, String ussdApplicationServerId, String driverId, String dynamicDriverId) {
        try {
            List<FeatureElementEntry> entryList = root.getFeatureElementEntries();

            for (int i = 0; i < entryList.size(); i++) {
                FeatureElementEntry fee = entryList.get(i);
                // Long pkEntry = pk + i + 1;
                fee.setFeatureElementEntryCod(++pk);
                fee.setFeatureElement(entity);
                fee.setFinalChr(false);
                fee.setCodOwnerEntry(root);
                List<FeatureElementEntry> optionList = fee.getFeatureElementEntries();

                /*
                 * para poner un FeatureElemenEntry como final, se tiene en
                 * cuenta lo siguiente: 1- Si es el ultimo elemento de la lista
                 * y si ese elemento no es del tipo Bifurcacion o ItemList (osea
                 * es de tipo Input, Output o Option) 2- O si el elemento actual
                 * no tiene hijos y el tipo del elemento actual es Bifurcacion o
                 * ItemList
                 */
                if ((i == entryList.size() - 1
                    && !fee.getCodFeatureEntryType().getFeatureEntryTypeCod().equals(
                            1L) && !fee.getCodFeatureEntryType().getFeatureEntryTypeCod().equals(
                        4L))
                    || ((optionList == null || optionList.size() == 0) && (fee.getCodFeatureEntryType().getFeatureEntryTypeCod().equals(
                            1L) || fee.getCodFeatureEntryType().getFeatureEntryTypeCod().equals(
                            4L))))
                    fee.setFinalChr(true);

                getFacadeContainer().getFeatureElementEntryAPI().create(fee);

                // SE GUARDA EL USSD MENU ENTRY PARA EL FEATURE ELEMENT ENTRY
                UssdMenuEntry ussdEntry = new UssdMenuEntry();
                ussdEntry.setId(pk);
                ussdEntry.setCode(pk.toString());
                // ussdEntry.setMenuorder(new
                // BigInteger(fee.getOrderNum().toString()));
                ussdEntry.setDescription(fee.getDescriptionChr());
                ussdEntry.setTitle(fee.getDescriptionChr());
                ussdEntry.setFinalmenu(fee.getFinalChr() ? new Short("1") : new Short("0"));
                ussdEntry.setRootmenu(new Short("0"));

                UssdDriver driver = null;
                if (fee.getFinalChr()) {
                    driver = getFacadeContainer().getUssdDriverAPI().find(
                            new Long(driverId));
                }

                ussdEntry.setUssdDriver1(driver);
                ussdEntry.setUssdMenuEntryState(new UssdMenuEntryState(1L));
                ussdEntry.setUssdMenuEntryType(new UssdMenuEntryType(fee.getCodFeatureEntryType().getFeatureEntryTypeCod()));
                ussdEntry.setUssdValidation(new UssdValidation(new Long(validationId)));

                if (fee.getCodFeatureEntryType().getFeatureEntryTypeCod().equals(
                        4L)) {
                    ussdEntry.setOwner(getFacadeContainer().getUssdMenuEntryAPI().find(
                            root.getFeatureElementEntryCod()));
                    ussdEntry.setMenuorder(new BigInteger(fee.getOrderNum().toString()));
                } else {
                    ussdEntry.setMenuorder(new BigInteger("1"));
                    ussdEntry.setOwner(getFacadeContainer().getUssdMenuEntryAPI().find(
                            owner));
                }

                ussdEntry.setUssdApplication(ussdApplication);
                ussdEntry.setUssdApplicationServer(new UssdApplicationServer(new Long(ussdApplicationServerId)));

                if (fee.getCodFeatureEntryType().getNameChr().equals("OPTION")) {

                    UssdDriver driverDynamic = null;

                    driverDynamic = getFacadeContainer().getUssdDriverAPI().find(
                            new Long(dynamicDriverId));

                    ussdEntry.setUssdDriver(driverDynamic);
                }
                if (fee.getCodFeatureEntryType().getNameChr().equals("OUTPUT")) {
                    ussdEntry.setTitle(ussdEntry.getTitle().concat(
                            "\n1 para continuar"));
                }

                getFacadeContainer().getUssdMenuEntryAPI().create(ussdEntry);

                // SE GUARDA EL PK ACTUAL PARA EL OWNER DEL SIGTE ENTRY
                owner = pk;

                // SI ES ENTRADA DE TIPO OPTION o BIFURCATION, SE GUARDA SUS
                // OPCIONES COMO ENTRADAS
                if (optionList != null && optionList.size() > 0) {
                    if (fee.getCodFeatureEntryType().getFeatureEntryTypeCod().equals(
                            5L)) {
                        for (int j = 0; j < optionList.size(); j++) {
                            FeatureElementEntry option = optionList.get(j);
                            option.setFeatureElement(entity);
                            option.setFinalChr(false);
                            // option.setFeatureElementEntryCod(new
                            // Long(featureElementEntryPk.toString().concat(
                            // String.valueOf(j + 1))));
                            option.setFeatureElementEntryCod(++pk);
                            option.setCodOwnerEntry(fee);
                            getFacadeContainer().getFeatureElementEntryAPI().create(
                                    option);
                        }
                    } else {
                        // Long newPk =
                        // Long.parseLong(pkEntry.toString().concat(
                        // "0"));
                        Long newPk = saveEntries(fee, entity, pk, owner,
                                validationId, ussdApplicationServerId,
                                driverId, dynamicDriverId);
                        pk = newPk;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pk;
    }

    public Long getFeatureElementNextval() {
        return super.getNextval("FEATURE_ELEMENT_SEQ");
    }

    public Long getFeatureElementEntryMaxVal(Long feeCode) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createNativeQuery("select max(feature_element_entry_cod) "
                + "from feature_element_entry fee "
                + "where fee.COD_FEATURE_ELEMENT = " + feeCode);
            BigDecimal nextVal = (BigDecimal) q.getSingleResult();
            return nextVal.longValue();
        } catch (Exception e) {
            return 0L;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<FeatureElementEntry> getFeatureElementEntriesList(Long featureElementCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT feList "
                + "FROM FeatureElement fe, "
                + "IN (fe.featureElementEntries) feList "
                + "WHERE fe.featureElementCod = :featureElementCod "
                + "AND feList.codOwnerEntry is null "
                + "ORDER BY feList.orderNum ASC ");
            query.setParameter("featureElementCod", featureElementCod);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<FeatureElementEntry> getAllFeatureElementEntriesList(Long featureElementCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT feList "
                + "FROM FeatureElement fe, "
                + "IN (fe.featureElementEntries) feList "
                + "WHERE fe.featureElementCod = :featureElementCod ");
            query.setParameter("featureElementCod", featureElementCod);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Userphone> getUserphoneList(Long featureElementCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT fe.userphones "
                + "FROM FeatureElement fe "
                + "WHERE fe.featureElementCod = :featureElementCod ");
            query.setParameter("featureElementCod", featureElementCod);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<PhoneList> getPhoneList(Long featureElementCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT fe.phoneLists "
                + "FROM FeatureElement fe "
                + "WHERE fe.featureElementCod = :featureElementCod ");
            query.setParameter("featureElementCod", featureElementCod);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Classification> getClassificationList(Long featureElementCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT fe.classifications "
                + "FROM FeatureElement fe "
                + "WHERE fe.featureElementCod = :featureElementCod ");
            query.setParameter("featureElementCod", featureElementCod);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<FeatureElementEntry> getFeatureElementEntriesChildren(Long featureElementEntryCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT distinct fee "
                + " FROM FeatureElementEntry fee "
                + " LEFT JOIN FETCH fee.featureElementEntries "
                + " WHERE fee.codOwnerEntry.featureElementEntryCod = :featureElementEntryCod "
                + " ORDER BY fee.orderNum ");
            query.setParameter("featureElementEntryCod", featureElementEntryCod);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public DataCheck getDataCheckAlphanumeric(BigDecimal dataCheckCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT d " + "FROM DataCheck d "
                + "WHERE d.dataCheckCod = :dataCheckCod ");
            query.setParameter("dataCheckCod", dataCheckCod);
            return (DataCheck) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public FeatureEntryType getFeatureEntryType(String nameChr) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT d "
                + "FROM FeatureEntryType d " + "WHERE d.nameChr = :nameChr ");
            query.setParameter("nameChr", nameChr);
            return (FeatureEntryType) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Integer getFeatureElementCount(Long clientCod, Long featureCod) {
        EntityManager em = null;
        try {

            GregorianCalendar gc = new GregorianCalendar();
            gc.set(Calendar.DAY_OF_MONTH, 1);
            gc.set(Calendar.HOUR_OF_DAY, 0);
            gc.set(Calendar.MINUTE, 0);
            gc.set(Calendar.SECOND, 0);
            gc.set(Calendar.MILLISECOND, 0);

            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT count(*) "
                + " FROM FeatureElement d "
                + " WHERE d.clientFeature.clientFeatureCod.codFeature = :featureCod "
                + " AND d.clientFeature.clientFeatureCod.codClient = :clientCod "
                + " AND trunc(d.startPeriodDat) >= trunc(:date) ");
            query.setParameter("featureCod", featureCod);
            query.setParameter("clientCod", clientCod);
            query.setParameter("date", gc.getTime());
            return ((Long) query.getSingleResult()).intValue();
        } catch (NoResultException e) {
            return 0;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public FeatureElement getIsAbierta(Long featureElementCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT fe FROM FeatureElement fe "
                + "WHERE fe.featureElementCod = :featureElementCod "
                + " AND fe.openChr = true ");
            query.setParameter("featureElementCod", featureElementCod);
            return ((FeatureElement) query.getSingleResult());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Userphone> getIsInterna(Long featureElementCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT fe.userphones FROM FeatureElement fe "
                + "WHERE fe.featureElementCod = :featureElementCod "
                + " AND fe.openChr = false ");
            query.setParameter("featureElementCod", featureElementCod);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<Userphone>();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<PhoneList> getIsExterna(Long featureElementCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT fe.phoneLists FROM FeatureElement fe "
                + "WHERE fe.featureElementCod = :featureElementCod "
                + " AND fe.openChr = false ");
            query.setParameter("featureElementCod", featureElementCod);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<PhoneList>();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<FeatureElement> getFeatureElements(Long clientCod, Long featureCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT fe FROM FeatureElement fe "
                + "WHERE fe.clientFeature.client.clientCod = :clientCod "
                + " AND fe.clientFeature.feature.featureCode = :featureCod "
                + " ORDER BY fe.descriptionChr ASC");
            query.setParameter("clientCod", clientCod);
            query.setParameter("featureCod", featureCod);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<FeatureElement>();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public FeatureEntryType getFeatureElement(String nameChr) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT d "
                + "FROM FeatureEntryType d " + "WHERE d.nameChr = :nameChr ");
            query.setParameter("nameChr", nameChr);
            return (FeatureEntryType) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<PhoneList> getPhoneList() {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT fe.phoneLists "
                + "FROM FeatureElement fe ");
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<PhoneList>();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<FeatureElement> getFeatureElementList(Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT d "
                + "FROM FeatureElement d "
                + "WHERE d.clientFeature.clientFeatureCod.codClient = :clientCod "
                + "AND d.enabledChr = true ");
            query.setParameter("clientCod", clientCod);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Userphone> getUserphones(Long clientCod, Long featureElementCod) {
        try {
            Query query = getFacadeContainer().getEntityManager().createQuery(
                    "SELECT fe.userphones FROM FeatureElement fe "
                        + "WHERE fe.clientFeature.client.clientCod = :clientCod "
                        + " AND fe.featureElementCod = :featureElementCod ");
            query.setParameter("clientCod", clientCod);
            query.setParameter("featureElementCod", featureElementCod);
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<Userphone>();
        }
    }

    public List<Classification> getClassificationList(Long clientCod, Long featureElementCod) {
        try {
            Query query = getFacadeContainer().getEntityManager().createQuery(
                    "SELECT fe.classifications FROM FeatureElement fe "
                        + "WHERE fe.clientFeature.client.clientCod = :clientCod "
                        + " AND fe.featureElementCod = :featureElementCod ");
            query.setParameter("clientCod", clientCod);
            query.setParameter("featureElementCod", featureElementCod);
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<Classification>();
        }
    }

    public List<PhoneList> getPhoneList(Long clientCod, Long featureElementCod) {
        try {
            Query query = getFacadeContainer().getEntityManager().createQuery(
                    "SELECT fe.phoneLists FROM FeatureElement fe "
                        + "WHERE fe.clientFeature.client.clientCod = :clientCod "
                        + " AND fe.featureElementCod = :featureElementCod ");
            query.setParameter("clientCod", clientCod);
            query.setParameter("featureElementCod", featureElementCod);
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<PhoneList>();
        }
    }

    public List<FeatureElement> getFeatureElementList(Long clientCod, Boolean showOnMenu) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT d "
                + "FROM FeatureElement d "
                + "WHERE d.clientFeature.clientFeatureCod.codClient = :clientCod "
                + "AND d.enabledChr = true "
                + "AND d.clientFeature.feature.showOnMenuChr = :showOnMenu ");
            query.setParameter("clientCod", clientCod);
            query.setParameter("showOnMenu", showOnMenu);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<FeatureElement> findByUserphone(Userphone userphone) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT fe FROM FeatureElement fe, IN (fe.userphones) u "
                + "WHERE fe.enabledChr = true "
                + "AND u.enabledChr = true "
                + "AND u.client.enabledChr = true "
                + "AND fe.openChr = false "
                + "AND fe.clientFeature.enabledChr = true "
                + "AND fe.clientFeature.feature.featureCode = 20 "
                + "AND u.userphoneCod = :userphoneCod "
                + "AND fe.finishPeriodDat >= :today "
                + "ORDER BY fe.featureElementCod");
            query.setParameter("userphoneCod", userphone.getUserphoneCod());
            query.setParameter("today", new Date(), TemporalType.DATE);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
