package com.tigo.cs.view.metadata;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.Cryptographer;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.view.DataAttendant;
import com.tigo.cs.facade.MetaMemberFacade;
import com.tigo.cs.facade.view.DataAttendantFacade;

@ManagedBean(name = "crudMetaDataAttendantBean")
@ViewScoped
public class CrudMetaDataAttendantBean extends AbstractCrudMetaDataBean<DataAttendant> implements Serializable {

    private static final Long COD_META = 18L;
    private static final Long COD_MEMBER = 1L;
    private String passAux;
    private String usernameAux;
    private boolean duplicateUsername;
    @EJB
    private DataAttendantFacade dataAttendantFacade;
    @EJB
    private MetaMemberFacade metaMemberFacade;
    private String messageResume;

    public CrudMetaDataAttendantBean() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_dataattendant");
        setXlsReportName("rep_dataattendant_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.attendantCrudMetaData.Attendant"));
        }
        return metaLabel;
    }

    @Override
    public String editEntityPlusMember() {
        super.editEntityPlusMember();
        passAux = "";
        passAux = data.getPass();
        usernameAux = data.getNombreUsuario();

        if (data.getHabilitado() != null
            && data.getHabilitado().equalsIgnoreCase(
                    i18n.iValue("web.client.backingBean.metadata.attendant.yes"))) {
            data.setEnabledAux(true);
        } else {
            data.setEnabledAux(false);
        }

        return null;
    }

    @Override
    public String savePlusMember() {
        if (data.getCode() == null || data.getCode().equals("")) {
            data.setCode(null);
            setErrorMessage(i18n.iValue("web.client.backingBean.metadata.CodeNull"));
            return null;
        }

        if (data.getNombre() == null || data.getNombre().equals("")) {
            setErrorMessage(i18n.iValue("web.client.backingBean.metadata.NamesNull"));
            return null;
        }

        Client client = getUserweb().getClient();
        if (data.getNombreUsuario() != null
            && !data.getNombreUsuario().equals("")) {
            if (!data.getNombreUsuario().equals(usernameAux)) {
                DataAttendant attendant = dataAttendantFacade.findByClientMetaCodigo(
                        client.getClientCod(), cod_meta,
                        data.getNombreUsuario());
                if (attendant != null) {
                    setWarnMessage(i18n.iValue("web.client.backingBean.metadata.UsernameDuplicate"));
                    return null;
                }
            }
        } else {
            setErrorMessage(i18n.iValue("web.client.backingBean.metadata.UsernameNull"));
            return null;
        }

        if (data.getPass() == null || data.getPass().equals("")) {
            setErrorMessage(i18n.iValue("web.client.backingBean.metadata.PassNull"));
            return null;
        }

        if (data.getPass() != null && !data.getPass().equals("")
            && !data.getPass().equals(passAux)) {
            try {
                if (data.getPass().length() < 6) {
                    setErrorMessage(i18n.iValue("web.client.backingBean.metadata.PassLength"));
                    return null;
                }
                data.setPass(Cryptographer.sha256Doble(data.getPass()));
                data.setReintentos("0");
            } catch (Exception e) {
                setErrorMessage(i18n.iValue("web.client.backingBean.message.Error")
                    + e.getMessage() + ".");
                this.signal(
                        Action.ERROR,
                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotSaveError"),
                        e);
            }
        }

        if (data.getEnabledAux()) {
            data.setHabilitado(i18n.iValue("web.client.backingBean.metadata.attendant.yes"));
        } else {
            data.setHabilitado(i18n.iValue("web.client.backingBean.metadata.attendant.no"));
        }

        if (data.getReintentos() == null) {
            data.setReintentos("0");
        }

        Date fecha = new Date();
        data.setFechaModificacion(String.valueOf(fecha.getTime()));
        super.savePlusMember();
        filterSelectedField = "-1";
        filterCriteria = "";
        paginationHelper = null; // For pagination recreation
        dataModelSpecific = null; // For data model recreation
        dataModel = null;
        selectedItems = null; // For clearing selection
        return null;
    }

    @Override
    public String procesarArchivo() {
        super.procesarArchivo();
        data = null;
        return null;
    }

//    @Override
//    public String procesarCargaMasiva(UploadedFile file, Long codMeta, Long codMember) throws Exception, IOException, MassivePersistenceException, FileFormatException, GenericFacadeException, ConstraintException, ConstraintViolationException {
//        BufferedInputStream bis = null;
//        BufferedReader br = null;
//        String message = i18n.iValue("web.client.backingBean.abstractCrudBean.message.ClientMassProcess");
//        messageResume = "El/los registros con código {0} no se guardaron debido a que el nombre de usuario ya existe";
//        Client client = getUserweb().getClient();
//        HashMap<MetaDataPK, String> hashMetaData = new HashMap<MetaDataPK, String>();
//        HashMap<MetaDataPK, List<Long>> hashMetaDataUserphone = null;
//        Long maxCodMember = metaMemberFacade.findMaxCodMemberByCodMeta(codMeta);
//        /*
//         * Si el archivo tiene relacion con userphone se crea un hashmap en
//         * donde se va a almacenar dicha relacion
//         */
//        if (getSaveUserphone().equals("true")) {
//            hashMetaDataUserphone = new HashMap<MetaDataPK, List<Long>>();
//        }
//
//        duplicateUsername = false;
//
//        try {
//            bis = new BufferedInputStream(file.getInputstream());
//            br = new BufferedReader(new InputStreamReader(bis));
//            String aux = "";
//            String line = null; // linea leida del archivo
//            while ((line = br.readLine()) != null) {
//                String[] result = line.split(";");
//
//                /* Los campos codigo, username y pass deben tener valor */
//                try {
//                    if (result[0].equals("")) {
//                        throw new ConstraintException(i18n.iValue("web.client.backingBean.metadata.CodeNull"));
//                    }
//                } catch (IndexOutOfBoundsException e) {
//                    throw new ConstraintException(i18n.iValue("web.client.backingBean.metadata.CodeNull"));
//                }
//
//                try {
//                    if (result[1].equals("")) {
//                        throw new ConstraintException(i18n.iValue("web.client.backingBean.metadata.NamesNull"));
//                    }
//                } catch (IndexOutOfBoundsException e) {
//                    throw new ConstraintException(i18n.iValue("web.client.backingBean.metadata.NamesNull"));
//                }
//
//                try {
//                    if (result[2].equals("")) {
//                        throw new ConstraintException(i18n.iValue("web.client.backingBean.metadata.UsernameNull"));
//                    }
//                } catch (IndexOutOfBoundsException e) {
//                    throw new ConstraintException(i18n.iValue("web.client.backingBean.metadata.UsernameNull"));
//                }
//
//                try {
//                    if (result[3].equals("")) {
//                        throw new ConstraintException(i18n.iValue("web.client.backingBean.metadata.PassNull"));
//                    }
//                } catch (IndexOutOfBoundsException e) {
//                    throw new ConstraintException(i18n.iValue("web.client.backingBean.metadata.PassNull"));
//                }
//
//                try {
//                    if (result[4].equals("")) {
//                        throw new ConstraintException(i18n.iValue("web.client.backingBean.metadata.EnabledNull"));
//                    } else if (!result[4].equalsIgnoreCase(i18n.iValue("web.client.backingBean.metadata.attendant.yes"))
//                        && !result[4].equalsIgnoreCase(i18n.iValue("web.client.backingBean.metadata.attendant.no"))) {
//                        throw new ConstraintException(i18n.iValue("web.client.backingBean.metadata.EnabledNotCorrect"));
//                    }
//                } catch (IndexOutOfBoundsException e) {
//                    throw new ConstraintException(i18n.iValue("web.client.backingBean.metadata.EnabledNull"));
//                }
//
//                MetaDataPK pk = new MetaDataPK();
//                pk.setCodClient(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod());
//                pk.setCodMeta(cod_meta);
//                pk.setCodeChr(result[0]);
//                pk.setCodMember(1L);
//
//                /* Se recorre hasta la penultima columna */
//                for (int i = 1; i < result.length - 1; i++) {
//                    pk = (MetaDataPK) pk.clone();
//                    pk.setCodMember(new Long(i));
//                    /*
//                     * Preguntamos si en el hashmap de metadata ya se encuentra
//                     * el metadata, de ser asi preguntamos si es que tiene valor
//                     * si es su valor es vacio o nulo guardamos el ultimo
//                     * metadata
//                     */
//
//                    /*
//                     * Si i=2 se trata de username y se verifica que ya no
//                     * exista ese username
//                     */
//                    if (i == 2) {
//                        if (hashMetaData.containsValue(result[i])) {
//                            duplicateUsername = true;
//                            aux = aux.concat(pk.getCodeChr() + ",");
//                        } else {
//                            DataAttendant attendant = dataAttendantFacade.findByClientMetaCodigo(
//                                    client.getClientCod(), cod_meta, result[i]);
//                            if (attendant != null) {
//                                duplicateUsername = true;
//                                aux = aux.concat(pk.getCodeChr() + ",");
//                            }
//                        }
//                    }
//
//                    /* Se encripta el pass */
//                    if (i == 3) {
//                        result[i] = Cryptographer.sha256Doble(result[i]);
//                    }
//
//                    if (i == 4) {
//                        if (result[i].equalsIgnoreCase(i18n.iValue("web.client.backingBean.metadata.attendant.yes"))) {
//                            result[i] = i18n.iValue("web.client.backingBean.metadata.attendant.yes");
//                        } else {
//                            result[i] = i18n.iValue("web.client.backingBean.metadata.attendant.no");
//                        }
//                    }
//
//                    if (!hashMetaData.containsKey(pk)) {
//                        hashMetaData.put(pk, result[i]);
//                    } else if (hashMetaData.get(pk) == null
//                        || hashMetaData.get(pk).equals("")) {
//                        hashMetaData.put(pk, result[i]);
//                    }
//
//                }
//
//                /*
//                 * Si el archivo tiene relacion con userphone, la ultima columna
//                 * representa la relacion
//                 */
//                if (getSaveUserphone().equals("false")) {
//                    pk = (MetaDataPK) pk.clone();
//                    pk.setCodMember(new Long(result.length - 1));
//
//                    if (result.length - 1 == 2) {
//                        if (hashMetaData.containsValue(result[result.length - 1])) {
//                            duplicateUsername = true;
//                            aux = aux.concat(pk.getCodeChr() + ",");
//                        } else {
//                            DataAttendant attendant = dataAttendantFacade.findByClientMetaCodigo(
//                                    client.getClientCod(), cod_meta,
//                                    result[result.length - 1]);
//                            if (attendant != null) {
//                                duplicateUsername = true;
//                                aux = aux.concat(pk.getCodeChr() + ",");
//                            }
//                        }
//                    }
//
//                    if (result.length - 1 == 3) {
//                        result[result.length - 1] = Cryptographer.sha256Doble(result[result.length - 1]);
//                    }
//
//                    if (result.length - 1 == 4) {
//                        if (result[result.length - 1].equalsIgnoreCase(i18n.iValue("web.client.backingBean.metadata.attendant.yes"))) {
//                            result[result.length - 1] = i18n.iValue("web.client.backingBean.metadata.attendant.yes");
//                        } else {
//                            result[result.length - 1] = i18n.iValue("web.client.backingBean.metadata.attendant.no");
//                        }
//                    }
//
//                    /*
//                     * Preguntamos si en el hashmap de metadata ya se encuentra
//                     * el metadata, de ser asi preguntamos si es que tiene valor
//                     * si es su valor es vacio o nulo guardamos el ultimo
//                     * metadata
//                     */
//                    if (!hashMetaData.containsKey(pk)) {
//                        hashMetaData.put(pk, result[result.length - 1]);
//                    } else if (hashMetaData.get(pk) == null
//                        || hashMetaData.get(pk).equals("")) {
//                        hashMetaData.put(pk, result[result.length - 1]);
//                    }
//
//                } else {
//                    pk = (MetaDataPK) pk.clone();
//                    pk.setCodMember(1L);
//                    try {
//                        /*
//                         * Preguntamos si en el hashmap de userphone ya se tiene
//                         * al metadata, de ser asi agregamos el userphone a la
//                         * lista de userphones del metadata
//                         */
//                        try {
//                            Long userphoneCod = new Long(result[result.length - 1]);
//                            if (hashMetaDataUserphone.containsKey(pk)) {
//                                hashMetaDataUserphone.get(pk).add(userphoneCod);
//
//                            } else {
//                                List<Long> list = new ArrayList<Long>();
//                                list.add(userphoneCod);
//                                hashMetaDataUserphone.put(pk, list);
//
//                            }
//                            /*
//                             * Capturamos el error que se da cuando se trata de
//                             * convertir el valor a Long, si es el numero de
//                             * columna es menor o igual al numero mayor
//                             * codMember del meta deducimos que esa linea no
//                             * tiene asociacion con userphone
//                             */
//                        } catch (Exception e) {
//                            if (result.length - 1 <= maxCodMember) {
//                                pk.setCodMember(new Long(result.length - 1));
//                                if (!hashMetaData.containsKey(pk)) {
//                                    hashMetaData.put(pk,
//                                            result[result.length - 1]);
//                                } else if (hashMetaData.get(pk) == null
//                                    || hashMetaData.get(pk).equals("")) {
//                                    hashMetaData.put(pk,
//                                            result[result.length - 1]);
//                                }
//                            } else {
//                                notifier.signal(getClass(), Action.ERROR,
//                                        e.getMessage(), e);
//                                throw new MassivePersistenceException(MessageFormat.format(
//                                        i18n.iValue("ejb.client.metaDatafacade.exception.MassProcessCreateClientMetaData"),
//                                        pk.getCodClient().toString()), pk.getCodeChr());
//                            }
//                        }
//                        /*
//                         * En caso que ocurra un error al tratar de convertir el
//                         * valor a Long, asumimos que este no se una valor
//                         * numerico, por lo que no tiene relacion con userphone,
//                         * entonces agregamos ese campo como un metadato mas
//                         */
//                    } catch (Exception e) {
//                        notifier.signal(getClass(), Action.ERROR,
//                                e.getMessage(), e);
//                        throw e;
//                    }
//
//                }
//                Date fecha = new Date();
//                pk = (MetaDataPK) pk.clone();
//                pk.setCodMember(new Long(5));
//                hashMetaData.put(pk, String.valueOf(fecha.getTime()));
//
//                pk = (MetaDataPK) pk.clone();
//                pk.setCodMember(new Long(6));
//                hashMetaData.put(pk, "0");
//            }
//            if (duplicateUsername) {
//                messageResume = MessageFormat.format(messageResume, aux);
//                throw new ConstraintException(messageResume);
//            }
//            metaDataFacade.processMetadatas(
//                    SecurityBean.getInstance().getLoggedInUserClient(),
//                    hashMetaData, hashMetaDataUserphone);
//            notifier.signal(getClass(), Action.INFO, String.format(message,
//                    client.getClientCod().toString(), codMeta.toString(),
//                    codMember.toString()));
//
//            filterSelectedField = "-1";
//            filterCriteria = "";
//            paginationHelper = null; // For pagination recreation
//            dataModelSpecific = null; // For data model recreation
//            dataModel = null;
//            selectedItems = null; // For clearing selection
//        } catch (MassivePersistenceException ex) {
//            notifier.signal(getClass(), Action.ERROR, ex.getMessage(), ex);
//            throw ex;
//        } catch (IOException e) {
//            notifier.signal(getClass(), Action.ERROR, e.getMessage(), e);
//            throw e;
//        } catch (GenericFacadeException gfe) {
//            // notifier.signal(getClass(), Action.ERROR, gfe.getMessage(), gfe);
//            throw gfe;
//        } catch (ConstraintViolationException ex) {
//            throw ex;
//        } catch (ConstraintException e) {
//            throw e;
//        }
//        return null;
//    }

    public boolean isDuplicateUsername() {
        return duplicateUsername;
    }

    public void setDuplicateUsername(boolean duplicateUsername) {
        this.duplicateUsername = duplicateUsername;
    }

    public String getMessageResume() {
        return messageResume;
    }

    public void setMessageResume(String messageResume) {
        this.messageResume = messageResume;
    }

    public void selectEnabled() {
        if (data.getEnabledAux()) {
            data.setHabilitado(i18n.iValue("web.client.backingBean.metadata.attendant.yes"));
        } else {
            data.setHabilitado(i18n.iValue("web.client.backingBean.metadata.attendant.no"));
        }
    }
}
