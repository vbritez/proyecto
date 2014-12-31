package com.tigo.cs.view.administrative;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.validation.ConstraintViolationException;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

import com.tigo.cs.commons.ejb.MassivePersistenceException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.FeatureElement;
import com.tigo.cs.domain.Phone;
import com.tigo.cs.domain.PhoneList;
import com.tigo.cs.domain.ussd.UssdApplication;
import com.tigo.cs.domain.ussd.UssdMenuEntryUssdUser;
import com.tigo.cs.domain.ussd.UssdMenuEntryUssdUserPK;
import com.tigo.cs.domain.ussd.UssdUser;
import com.tigo.cs.exception.FileFormatException;
import com.tigo.cs.facade.FeatureElementFacade;
import com.tigo.cs.facade.FeatureValueFacade;
import com.tigo.cs.facade.PhoneFacade;
import com.tigo.cs.facade.PhoneListFacade;
import com.tigo.cs.facade.ussd.UssdMenuEntryUssdUserFacade;
import com.tigo.cs.facade.ussd.UssdUserFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.metadata.AbstractCrudBeanClient;

@ManagedBean(name = "crudPhoneListBean")
@ViewScoped
public class CrudPhoneListBean extends AbstractCrudBeanClient<PhoneList, PhoneListFacade> implements Serializable {

    private DualListModel<Phone> listPhones = null;
    private List<Phone> phonesAsignados = null;
    private List<Phone> phonesNoAsignados = null;
    private List<Phone> listAux = null;
    private boolean newPhone = false;
    private boolean massPhone = false;
    private Phone phone;
    private String fileName = null;
    protected UploadedFile file;
    private boolean fileReady = false;
    private final DateFormat sqlSdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    
    private DataModel<Phone> dataModelDetail;
    private Map<Long, Boolean> selectedItemsDetail;
    private PaginationHelper paginationHelperDetail;
    private int lastActualPage = -1;
    boolean editing = false;
    private List<Phone> listRemoved;
    
    @EJB
    private PhoneListFacade phoneListFacade;
    @EJB
    private UssdUserFacade ussdUserFacade;
    @EJB
    private FeatureValueFacade featureValueFacade;
    @EJB
    private FeatureElementFacade featureElementFacade;
    @EJB
    private PhoneFacade phoneFacade;
    @EJB
    private UssdMenuEntryUssdUserFacade ussdMenuEntryUssdUserFacade;

    public CrudPhoneListBean() {
        super(PhoneList.class, PhoneListFacade.class);
    }

    @Override
    public String getReportWhereCriteria() {
        return null;
    }

    public List<Phone> getPhonesAsignados() {
        if (phonesAsignados == null) {
            this.phonesAsignados = new ArrayList<Phone>();
        }

        /* Es edicion */
        if (getEntity().getClient() != null) {
            if (phonesAsignados.size() > 0) {
                List<Phone> listAux = new ArrayList<Phone>();
                listAux = phoneListFacade.getClientPhone(getEntity().getPhoneListCod(), SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod());

                for (Phone p : listAux) {
                    if (!phonesAsignados.contains(p)) {
                        phonesAsignados.add(p);
                    }
                }
            } else {
                phonesAsignados = phoneListFacade.getClientPhone(getEntity().getPhoneListCod(), SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod());
            }

        }

        return this.phonesAsignados;
    }

    public void setPhonesAsignados(List<Phone> phonesAsignados) {
        this.phonesAsignados = phonesAsignados;
    }

    public List<Phone> getPhonesNoAsignados() {
        if (phonesNoAsignados == null) {
            this.phonesNoAsignados = new ArrayList<Phone>();
        }

        /* Es edicion */
        if (getEntity().getClient() != null) {
            List<Phone> list = phoneFacade.getClientPhone(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod());

            for (Phone p : list) {
                if (!phonesAsignados.contains(p)
                    && !phonesNoAsignados.contains(p)) {
                    phonesNoAsignados.add(p);
                }
            }

        }
        /* Es nuevo, traemos todos los phones asociados al cliente */
        else {
            phonesNoAsignados = phoneFacade.getClientPhone(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod());
        }

        return this.phonesNoAsignados;
    }

    public void setPhonesNoAsignados(List<Phone> phonesNoAsignados) {
        this.phonesNoAsignados = phonesNoAsignados;
    }

    public DualListModel<Phone> getListPhones() {
        if (listPhones == null) {
            this.setPhonesAsignados(getPhonesAsignados());
            this.setPhonesNoAsignados(getPhonesNoAsignados());
            this.listPhones = new DualListModel<Phone>(this.phonesNoAsignados, this.phonesAsignados);
        }

        return listPhones;
    }

    public void setListPhones(DualListModel<Phone> listPhones) {
        this.listPhones = listPhones;
    }

    @Override
    public String saveEditing() {
        if (getEntity().getDescriptionChr() == null
            || getEntity().getDescriptionChr().length() == 0) {
            setWarnMessage(i18n.iValue("web.client.phone.message.DescriptionCanNotBeEmpty"));
            return null;
        }

        if (getEntity().getTypeChr() == null
            || getEntity().getTypeChr().length() == 0) {
            setWarnMessage(i18n.iValue("web.client.phone.message.TypeCanNotBeEmpty"));
            return null;
        }
        
        if (listPhones.getTarget() == null || listPhones.getTarget().size() == 0) {
           setWarnMessage(i18n.iValue("web.client.phone.message.PhonesCanNotBeEmpty"));
           return null;
        }

        /* Lista de phones no asignados */
        List<Phone> list = getListPhones().getSource();       

        /* Se crea el phoneList */
        if (getEntity().getClient() == null) {
            getEntity().setClient(SecurityBean.getInstance().getLoggedInUserClient().getClient());
            phoneListFacade.create(getEntity());
        }  
        
        /* Recorremos la lista de phones no asignados */
        for (Phone p : list) {
            /* Traemos la lista de PhoneList del Phone */
            List<PhoneList> plList = phoneFacade.getPhoneList(p.getPhoneCod());
            Iterator<PhoneList> iteratorPhoneList = plList.iterator();

            /* Traemos la lista de Phone del PhoneList */
            List<Phone> listPhone = phoneListFacade.getPhones(getEntity().getPhoneListCod());
            Iterator<Phone> iteratorPhone = listPhone.iterator();

            /*
             * Recorremos la lista de phones del phoneList para desasociar
             * el phone que se desea eliminar del mismo
             */
            while (iteratorPhone.hasNext()) {
                Phone p2 = iteratorPhone.next();

                if (p2.getPhoneCod().equals(p.getPhoneCod())) {
                    iteratorPhone.remove();
                    getEntity().setPhones(listPhone);
                }
            }

            /*
             * Recorremos la lista de phonesList del phone que se desea
             * desasociar del phoneList
             */
            while (iteratorPhoneList.hasNext()) {
                PhoneList phoneList = iteratorPhoneList.next();

                if (phoneList.getPhoneListCod().equals(getEntity().getPhoneListCod())) {
                    iteratorPhoneList.remove();
                    p.setPhoneLists(plList);
                }
            }

            phoneFacade.edit(p);
            phoneListFacade.edit(getEntity());
            
            /*
             * Solo si el tipo de lista es White, se debe borrar los
             * ussd menus entries ussd user de los desasociados
             * */
            if (getEntity().getTypeChr().compareToIgnoreCase("W") == 0){
                UssdUser ussdUser = ussdUserFacade.find(p.getPhoneCod());
                List<UssdMenuEntryUssdUser> ussdMenuEntryUssdUsers = null;
                if (ussdUser != null){
                    try {
                        ussdMenuEntryUssdUsers = ussdMenuEntryUssdUserFacade.findByUssduserId(ussdUser.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
    
                    for (UssdMenuEntryUssdUser entryUssdUser : ussdMenuEntryUssdUsers) {
                        ussdMenuEntryUssdUserFacade.remove(entryUssdUser);
                    }
                    ussdUser.setUssdMenuEntryUssdUserList(new ArrayList<UssdMenuEntryUssdUser>());
                    ussdUserFacade.edit(ussdUser);
                }
            }

        }
        
       // phoneListFacade.edit(getEntity());
        List<Phone> phones = new ArrayList<Phone>();
        /*
         * Como al cargar un phone de forma masiva o mediante la forma de
         * agregar phone, este no es seteado en el targe del dualList, le
         * seteamos al mismo los phonesAsignados que ya no pertenecen al target
         * o al source
         */
        for (Phone p : phonesAsignados) {
            if (!listPhones.getTarget().contains(p)
                && !listPhones.getSource().contains(p)) {
                listPhones.getTarget().add(p);
            }
        }

        /* Asociamos los phones al phoneList */
        for (Phone p : listPhones.getTarget()) {
            if (!phoneListFacade.getPhones(getEntity().getPhoneListCod()).contains(p)) {
                List<PhoneList> pl = phoneFacade.getPhoneList(p.getPhoneCod());
                pl.add(getEntity());
                /*
                 * if (phoneFacade.find(p.getPhoneCod()) == null) {
                 * p.setPhoneCod(null); p = phoneFacade.create(p);
                 */// creamos el phone

                UssdUser ussdUser = ussdUserFacade.find(p.getPhoneCod());
                
                if(ussdUser == null) {
                	/*creamos el USSD-USER*/
                    ussdUser = new UssdUser();
                    ussdUser.setName(p.getNameChr());
                    ussdUser.setDescription(MessageFormat.format(i18n.iValue("ussd.user.date.create"), sqlSdf.format(new Date())));
                    ussdUser.setUssdApplication(new UssdApplication(1L));
                    ussdUser.setId(p.getPhoneCod());
                    ussdUser.setCode(p.getPhoneCod().toString());
                    ussdUserFacade.create(ussdUser);
                }

                /*
                 * una vez que esta creado el USSD_USER agregar a la table
                 * USSD_MENU_ENTRY_USSD_USER si esta asignado a un feature esta
                 * lista
                 */

                if (getEntity().getTypeChr().compareToIgnoreCase("W") == 0) {
                    /* Traemos la lista de featureElements del phoneList */
                    List<FeatureElement> featureElements = phoneListFacade.find(getEntity().getPhoneListCod(), "featureElements").getFeatureElements();
                    if (featureElements != null && featureElements.size() > 0) {
                        /*
                         * Recorremos la lista de featureElemnts para realizar
                         * la asociacion del Ussd-User con Ussd-Menu-Entries
                         */
                        for (FeatureElement element : featureElements) {
                        	if(element.getEnabledChr()) {
	                            UssdMenuEntryUssdUserPK pk = new UssdMenuEntryUssdUserPK();
	                            pk.setUssdmenuentriesId(element.getFeatureElementCod());
	                            pk.setUssduserId(ussdUser.getId());
	
	                            UssdMenuEntryUssdUser menuEntryUssdUser = new UssdMenuEntryUssdUser();
	                            menuEntryUssdUser.setUssdMenuEntryUssdUserPK(pk);
	                            menuEntryUssdUser.setUssdUser(ussdUser);
	
	                            ussdMenuEntryUssdUserFacade.create(menuEntryUssdUser);
                        	}
                        }
                    }
                }
                p.setPhoneLists(pl);
                phones.add(p);
                phoneFacade.edit(p);
            }
        }

        getEntity().setPhones(phones);
        super.saveEditing();
        listPhones = null;
        phonesAsignados = null;
        phonesNoAsignados = null;
        listAux = null;
        return null;

    }

    @Override
    public String deleteEntities() {
        boolean flagAtLeastOne = false;
        String entitiesNotDeletedInUse = "";
        String entitiesNotDeletedError = "";
        List<PhoneList> listPl = featureElementFacade.getPhoneList();
        Iterator<PhoneList> iteratorPl = listPl.iterator();

        Iterator<PhoneList> iteratorDataModel = getDataModel().iterator();
        for (; iteratorDataModel.hasNext();) {
            PhoneList currentEntity = iteratorDataModel.next();
            Object obj = getKeyValue(currentEntity);
            if (getSelectedItems().get(obj)) {
                flagAtLeastOne = true;
                try {
                    /*
                     * Recorremos la lista de phoneList que tienen relaciones
                     * con featureElement
                     */
                    while (iteratorPl.hasNext()) {
                        PhoneList pl = iteratorPl.next();
                        /*
                         * Si el phoneList que se desea eliminar esta
                         * relacionado con featureElement mostramos un mensaje
                         * de que el mismo no puede ser eliminado
                         */
                        if (pl.getPhoneListCod().equals(currentEntity.getPhoneListCod())) {
                            this.signal(Action.ERROR, MessageFormat.format(i18n.iValue("web.client.phonelist.message.ErrorCouldNotDelete"), currentEntity.getDescriptionChr()));
                            setWarnMessage(MessageFormat.format(i18n.iValue("web.client.phonelist.message.ErrorCouldNotDelete"), currentEntity.getDescriptionChr()));
                            return null;
                        }
                    }

                    /* Traemos la lista de Phones del PhoneList */
                    List<Phone> listPhone = phoneListFacade.getPhones(currentEntity.getPhoneListCod());
                    Iterator<Phone> iteratorPhone = listPhone.iterator();

                    /*
                     * Recorremos la lista de phones del phoneList para
                     * desasociar los phones del mismo
                     */
                    while (iteratorPhone.hasNext()) {
                        Phone phone = iteratorPhone.next();
                        /* Traemos la lista de PhoneList del Phone */
                        List<PhoneList> plList = phoneFacade.getPhoneList(phone.getPhoneCod());
                        Iterator<PhoneList> iteratorPhoneList = plList.iterator();

                        /*
                         * Recorremos la lista de phonesList del phone que se
                         * desea desasociar
                         */
                        while (iteratorPhoneList.hasNext()) {
                            PhoneList phoneList = iteratorPhoneList.next();

                            if (phoneList.getPhoneListCod().equals(currentEntity.getPhoneListCod())) {
                                iteratorPhoneList.remove();
                                phone.setPhoneLists(plList);
                                iteratorPhone.remove();
                                currentEntity.setPhones(listPhone);
                            }
                        }

                        phoneFacade.edit(phone);
                        phoneListFacade.edit(currentEntity);

//                        /*
//                         * Si el phone ya no esta relacionado a ningun phoneList
//                         * y si el mismo no se encuentra asociado a ninguna
//                         * prestacion, lo eliminamos
//                         */
//                        if (plList.size() == 0
//                            && featureValueFacade.findFeatureValuePhone(phone.getPhoneCod()).size() == 0) {
//                            UssdUser ussdUser = ussdUserFacade.find(phone.getPhoneCod());
//                            /*
//                             * Traemos la lista de ussMenuEntry del ussdUser que
//                             * se desea borrar
//                             */
//                            List<UssdMenuEntryUssdUser> ussdMenuEntryUssdUsers = ussdMenuEntryUssdUserFacade.findByUssduserId(ussdUser.getId());
//                            for (UssdMenuEntryUssdUser entryUssdUser : ussdMenuEntryUssdUsers) {
//                                ussdMenuEntryUssdUserFacade.remove(entryUssdUser);
//                            }
//                            ussdUser = ussdUserFacade.find(phone.getPhoneCod());
//                            if (ussdUser != null) {
//                                ussdUser.setUssdMenuEntryUssdUserList(new ArrayList<UssdMenuEntryUssdUser>());
//                                ussdUserFacade.edit(ussdUser);
//                                ussdUserFacade.remove(ussdUser);
//                            }
//                            phoneFacade.remove(phone);
//                        }

                    }

                    phoneListFacade.remove(currentEntity);
                    signalDelete(currentEntity);
                } catch (EJBException ejbe) {
                    if (ejbe.getCausedByException().getClass().equals(javax.transaction.RollbackException.class)) {
                        entitiesNotDeletedInUse += ", ".concat(currentEntity.toString());
                    } else {
                        entitiesNotDeletedError += ", ".concat(currentEntity.toString());
                    }
                } catch (Exception e) {
                    // @APP NOTIFICATION
                    this.signal(Action.ERROR, i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"), e);
                    entitiesNotDeletedError += ", ".concat(currentEntity.toString());
                }
            }
        }

        if (flagAtLeastOne) {
            setLastActualPage(getPaginationHelper().getActualPage());
            setPaginationHelper(null); // For pagination recreation
            setDataModel(null); // For data model recreation
            setSelectedItems(null); // For clearing selection

            if (entitiesNotDeletedInUse.length() == 0
                && entitiesNotDeletedError.length() == 0) {
                setSuccessMessage(getOkMessageOnDeleteByGroup());
            } else {
                if (entitiesNotDeletedInUse.length() > 0) {
                    entitiesNotDeletedInUse = entitiesNotDeletedInUse.substring(2);
                    setWarnMessage(getWarnMessageOnDeleteConstraintByGroup().concat(entitiesNotDeletedInUse).concat("."));
                }
                if (entitiesNotDeletedError.length() > 0) {
                    entitiesNotDeletedError = entitiesNotDeletedError.substring(2);
                    setWarnMessage(getErrorMessageOnDeleteByGroup().concat(entitiesNotDeletedError).concat("."));
                    // @APP NOTIFICATION
                    this.signal(Action.ERROR, i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"));
                }
            }
        } else {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneDelete"));
        }

        return null;
    }

    public String savePhone() {
        try {
            
            if(phone.getCellphoneNum() == null) {
                setSuccessMessage(i18n.iValue("error.cellphone.required"));
                phone = new Phone();
                editing = false;
                resetPaginationDetail();
                return null;
            }
            
            if(editing) {
                phoneFacade.edit(phone);
                setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.SaveSuccess"));
                phone = new Phone();
                editing = false;
                resetPaginationDetail();
                return null;
            }
            
            
            phone.setClient(SecurityBean.getInstance().getLoggedInUserClient().getClient());
            /*
             * Verificamos si el phone que se desea agregar ya no esta asignado
             * al cliente o si ya no se encuentra en la lista de phoneList y
             * target
             */
            if (phoneFacade.getPhone(phone.getCellphoneNum(), phone.getClient().getClientCod()) == null) {
                phoneFacade.create(phone);
                
                listPhones.getTarget().add(phone);
                if (listAux == null) {
                    listAux = new ArrayList<Phone>();
                }
                listAux.add(phone);
                /*
                 * Al crear un phone tambien debemos agregarlo a la tabla de
                 * USSD-USER
                 */
                UssdUser ussdUser = new UssdUser();
                ussdUser.setName(phone.getNameChr());
                ussdUser.setDescription(MessageFormat.format(i18n.iValue("ussd.user.date.create"), sqlSdf.format(new Date())));
                ussdUser.setUssdApplication(new UssdApplication(1L));
                ussdUser.setId(phone.getPhoneCod());
                ussdUser.setCode(phone.getPhoneCod().toString());
                ussdUserFacade.create(ussdUser);
                

            } else {
                setWarnMessage(i18n.iValue("error.cellphone.duplicate"));
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        phone = new Phone();
        editing = false;
        resetPaginationDetail();
       // newPhone = false;
        return null;
    }

    public boolean isNewPhone() {
        return newPhone;
    }

    public void setNewPhone(boolean newPhone) {
        this.newPhone = newPhone;
    }

    public boolean isMassPhone() {
        return massPhone;
    }

    public void setMassPhone(boolean massPhone) {
        this.massPhone = massPhone;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public void addPhone() {
        if(phone == null)  {
            phone = new Phone();
        }
        if(listRemoved == null) {
            listRemoved = new ArrayList<Phone>();
        }
        
        this.newPhone = true;
    }

    public void massLoad() {
        this.massPhone = true;
    }

    public String cancelPhone() {
        phone = new Phone();
        editing = false;
        selectedItemsDetail = null;
        //newPhone = false;
        return null;
    }

    @Override
    public String newEntity() {
        listPhones = null;
        phonesAsignados = null;
        phonesNoAsignados = null;
        return super.newEntity();
    }

    @Override
    public String editEntity() {
        listPhones = null;
        phonesAsignados = null;
        phonesNoAsignados = null;
        return super.editEntity();
    }

    @Override
    public String cancelEditing() {
        phonesAsignados = null;
        phonesNoAsignados = null;
        listPhones = null;
        listAux = null;
        return super.cancelEditing();
    }

    public void handleFileUpload(FileUploadEvent event) {
        if (event.getFile() != null) {
            file = event.getFile();
            fileReady = true;
        } else {
            fileReady = false;
        }
    }

    public String procesarArchivo() {
        if (file != null) {
            try {
                procesarCargaMasiva(file);
                setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.FileProcessSuccess"));
            } catch (IOException ex) {
                setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.FileProcessError"));
            } catch (MassivePersistenceException ex) {
                setErrorMessage(MessageFormat.format(i18n.iValue("web.client.backingBean.abstractCrudBean.message.FileProcessErrorConstraint"), ex.getCodigoConflicto()));
            } catch (FileFormatException ex) {
                setErrorMessage(ex.getMessage());
            } catch (GenericFacadeException gfe) {
                setErrorMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.FileProcessError"));
            } catch (ConstraintViolationException ex) {
                setErrorMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.ConstraintErrorMassLoad"));
            } catch (Exception e) {
                setErrorMessage(e.getMessage());
            } finally {
                massPhone = false;
                file = null;
            }

        }
        return null;
    }

    /* Carga masiva de phones */
    public String procesarCargaMasiva(UploadedFile file) throws IOException, MassivePersistenceException, FileFormatException, GenericFacadeException {
        BufferedInputStream bis = null;
        BufferedReader br = null;
        String message = i18n.iValue("web.client.backingBean.abstractCrudBean.message.ClientMassProcess");
        Client client = SecurityBean.getInstance().getLoggedInUserClient().getClient();
        boolean cellphoneDuplicate = true;
        String cellphoneNum = "";
        try {
            bis = new BufferedInputStream(file.getInputstream());
            br = new BufferedReader(new InputStreamReader(bis));

            String line = null; // linea leida del archivo
            while ((line = br.readLine()) != null) {
                String[] result = line.split(";");
                if (result[0].equals("")) {
                    throw new MassivePersistenceException(i18n.iValue("error.cellphone.required"));
                }

                try {
                    Phone phone = new Phone();
                    phone.setClient(client);
                    BigInteger cellphone = new BigInteger(result[0]);
                    phone.setCellphoneNum(cellphone);

                    if (!result[1].equals("") && result[1] != null) {
                        phone.setNameChr(result[1]);
                    }

                    /*
                     * Verificamos si el phone que se desea agregar ya no esta
                     * asignado al cliente o si ya no se encuentra en la lista
                     * de phoneList y target
                     */

                    if (phoneFacade.getPhone(cellphone, client.getClientCod()) == null
                        && !listPhones.getTarget().contains(phone)) {
                        phoneFacade.create(phone);
                        listPhones.getTarget().add(phone);
                        if (listAux == null) {
                            listAux = new ArrayList<Phone>();
                        }
                        listAux.add(phone);
                        /*
                         * Al crear un phone tambien debemos agregarlo a la
                         * tabla de USSD-USER
                         */
                        UssdUser ussdUser = new UssdUser();
                        ussdUser.setName(phone.getNameChr());
                        ussdUser.setDescription(MessageFormat.format(i18n.iValue("ussd.user.date.create"), sqlSdf.format(new Date())));
                        ussdUser.setUssdApplication(new UssdApplication(1L));
                        ussdUser.setId(phone.getPhoneCod());
                        ussdUser.setCode(phone.getPhoneCod().toString());
                        ussdUserFacade.create(ussdUser);
                        // phonesAsignados.add(phone);
                    }else{
                        cellphoneDuplicate = true;
                        cellphoneNum = phone.getCellphoneNum().toString()+".";
                    }
                } catch (Exception e) {
                    throw new FileFormatException(MessageFormat.format(i18n.iValue("error.field.required"), "primera"));
                }

            }

        } catch (MassivePersistenceException ex) {
            notifier.signal(getClass(), Action.ERROR, ex.getMessage(), ex);
            throw ex;
        } catch (IOException e) {
            notifier.signal(getClass(), Action.ERROR, e.getMessage(), e);
            throw e;
        } catch (ConstraintViolationException ex) {
            throw ex;
        }
        
        if(cellphoneDuplicate) {
            setWarnMessage(MessageFormat.format(i18n.iValue("error.cellphone.duplicate2"),cellphoneNum));
        }
        return null;
    }

    public String getFileName() {
        return file != null ? file.getFileName() : "";
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String cancelUpload() {
        massPhone = false;
        file = null;
        return null;
    }

    public boolean isFileReady() {
        return fileReady;
    }

    public void setFileReady(boolean fileReady) {
        this.fileReady = fileReady;
    }
    

    ///--------------------------------------------------------------------------------//
    // --------------------------------------------------------------------------------------
    // LIST AND TABLE METHODS
    // --------------------------------------------------------------------------------------
    
    public PaginationHelper getPaginationHelperDetail() {
        if (paginationHelperDetail == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer
                    .valueOf(getRowQuantSelected()).intValue() : 0;

            paginationHelperDetail = new PaginationHelper(pageSize) {

                String innerWhere = null;
                Integer count = null;

                @Override
                public int getItemsCount() {

                    String where = "";
                    if (innerWhere == null) {
                        innerWhere = where;
                    } else {
                        if (innerWhere.compareTo(where) == 0) {
                            return count;
                        }
                    }

                    where = where.concat(" WHERE o.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    where = where.concat(" AND o.cellphoneNum IS NOT NULL ");
                    count = phoneFacade.count(where);
                    return count;
                }

                @Override
                public DataModel createPageDataModel() {
                    String where = "";
  
                    where = where.concat(" WHERE o.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    where = where.concat(" AND o.cellphoneNum IS NOT NULL ");
                    String orderby = "o.phoneCod ";

                    return new ListDataModelViewCsTigo(phoneFacade.findRange(
                            new int[] { getPageFirstItem(),
                                    getPageFirstItem() + getPageSize() },
                            where, orderby));
                }
            };
            if (lastActualPage >= 0) {
                paginationHelperDetail.setActualPage(lastActualPage);
                lastActualPage = -1;
            }
        }

        return paginationHelperDetail;
    }

    @Override
    protected void setPaginationHelper(PaginationHelper paginationHelperDetail) {
        this.paginationHelperDetail = paginationHelperDetail;
    }

    public DataModel<Phone> getDataModelDetail() {
        if (dataModelDetail == null) {
            dataModelDetail = getPaginationHelperDetail().createPageDataModel();
        }

        return dataModelDetail;
    }
    
    
    public void setDataModelDetail(DataModel<Phone> dataModelDetail) {
        this.dataModelDetail = dataModelDetail;
    }

    public String nextPageDetail() {
        getPaginationHelperDetail().nextPage();
        dataModelDetail = null; // For data model recreation
        selectedItemsDetail = null; // For clearing selection
        return null;
    }
    
    public void resetPaginationDetail(){
        paginationHelperDetail = null; // For pagination recreation
        dataModelDetail = null; // For data model recreation
        selectedItemsDetail = null; // For clearing selection
    }

    public String previousPageDetail() {
        getPaginationHelperDetail().previousPage();
        dataModelDetail = null; // For data model recreation
        selectedItemsDetail = null; // For clearing selection
        return null;
    }

    public Map<Long, Boolean> getSelectedItemsDetail() {
        if (selectedItemsDetail == null) {
            selectedItemsDetail = new HashMap<Long, Boolean>();
        }

        return selectedItemsDetail;
    }

    public void setSelectedItemsDetail(Map<Long, Boolean> selectedItems) {
        this.selectedItemsDetail = selectedItems;
    }

    public String applySortDetail() {
        dataModelDetail = null; // For data model recreation
        selectedItemsDetail = null; // For clearing selection
        return null;
    }
    
    public String editEntityDetail() {
        Iterator<Phone> iteratorDataModel = getDataModelDetail().iterator();
        int cantSelected = 0;
        
       
        
        for (; iteratorDataModel.hasNext();) {
            Phone currentEntity = iteratorDataModel.next();
            if(selectedItemsDetail.get(currentEntity.getPhoneCod())) {
                phone = currentEntity;
                editing = true;
                cantSelected++;
            }
        }
        
        if(cantSelected > 1) {
            phone = new Phone();
            editing = false;
            selectedItemsDetail = null;
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectJustOneEdit"));
            return null; 
        }

        if (phone.getCellphoneNum() == null) {
            phone = new Phone();
            editing = false;
            selectedItemsDetail = null;
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
        }

        selectedItemsDetail = null;
        return null;
    }
    
    private List<Phone> getSelectedPhones(){
        Iterator<Phone> iteratorDataModel = getDataModelDetail().iterator();
        List<Phone> list = new ArrayList<Phone>();
        try{
            for (; iteratorDataModel.hasNext();) {
                Phone currentEntity = iteratorDataModel.next();
                if (getSelectedItemsDetail().get(currentEntity.getPhoneCod())) {
                    Phone p = phoneFacade.find(currentEntity.getPhoneCod());
                    list.add(p);
                }
            }
        }catch (Exception e) {            
        }
        return list;
    }
    
    public String deleteEditingDetail(){
           List<Phone> selectedPhones = getSelectedPhones();
           if (selectedPhones == null || selectedPhones.size() == 0){
               setWarnMessage(i18n.iValue("web.client.phone.message.MustSelectAtLeastOneRecord"));
               return null;
           }
//           Iterator<Phone> iteratorDataModel = getDataModelDetail().iterator();
           String name ="";
           boolean entityDeleted = false;
           try{
            for (Phone currentEntity : selectedPhones) {
//                Phone currentEntity = iteratorDataModel.next();
                if (getSelectedItemsDetail().get(currentEntity.getPhoneCod())) {
                    
                    /*
                     * Si el phone no se encuentra asociado a ninguna
                     * prestacion, lo eliminamos
                     */
                    if (featureValueFacade.findFeatureValuePhone(currentEntity.getPhoneCod()).size() > 0) {
                         name = currentEntity.getNameChr()+".";
                    }else{
                        currentEntity.setPhoneLists(new ArrayList<PhoneList>());
                        phoneFacade.edit(currentEntity);                       
                        UssdUser ussdUser = ussdUserFacade.find(currentEntity.getPhoneCod());
                        List<UssdMenuEntryUssdUser> ussdMenuEntryUssdUsers = null;
                        if (ussdUser != null){
                            try {
                                ussdMenuEntryUssdUsers = ussdMenuEntryUssdUserFacade.findByUssduserId(ussdUser.getId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
    
                            for (UssdMenuEntryUssdUser entryUssdUser : ussdMenuEntryUssdUsers) {
                                ussdMenuEntryUssdUserFacade.remove(entryUssdUser);
                            }
//                          ussdUser = ussdUserFacade.find(currentEntity.getPhoneCod());
                            ussdUser.setUssdMenuEntryUssdUserList(new ArrayList<UssdMenuEntryUssdUser>());
                            ussdUserFacade.edit(ussdUser);
                            ussdUserFacade.remove(ussdUser);
                        }
                        
                        if(listPhones.getSource().contains(currentEntity)){
                            listRemoved.add(currentEntity);
                        }
                        
                        if(listPhones.getTarget().contains(currentEntity)) {
                            listRemoved.add(currentEntity);
                        }
                        phoneFacade.remove(currentEntity);
                        entityDeleted = true;
                    }
                       
                }
            }
            if (entityDeleted){
                setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletingSucces"));
                resetPaginationDetail();
            }
            if(!name.equals("")) 
               setWarnMessage("Los siguientes usuarios telefónicos no pudieron ser eliminados por estar asociados a prestaciones: "+name);
               
            return null;
            } catch (Exception e) {
                setErrorMessage(i18n.iValue("web.client.backingBean.message.Error"));
               
            }
           resetPaginationDetail();
           return null;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public String back() {
        phonesAsignados = null;
        phonesNoAsignados = null;
        
        List<Phone> listSource = new ArrayList<Phone>();
        List<Phone> listTarget = new ArrayList<Phone>();

        for(Phone p : getPhonesAsignados()) {
            if(listPhones.getTarget().contains(p)) {
                listTarget.add(p);
            }
            
            if(listPhones.getSource().contains(p)) {
                listSource.add(p);
            }
        }
        
        for(Phone p : getPhonesNoAsignados()) {
            if(listPhones.getTarget().contains(p)) {
                listTarget.add(p);
            }
            
            if(listPhones.getSource().contains(p)) {
                listSource.add(p);
            }
        }
        
        for(Phone p : listPhones.getTarget()) {
            if(!listTarget.contains(p)) {
                if(!listRemoved.contains(p)) {
                    listTarget.add(p); 
                }
            }
        }
        
        
        setListPhones(null);
        listPhones = new DualListModel<Phone>(listSource, listTarget);
        listRemoved = null;
        newPhone = false;
        resetPaginationDetail();
        editing = false;
        return null;
    }
}
