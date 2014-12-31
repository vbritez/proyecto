package com.tigo.cs.api.facade.jms;


public abstract class FileMetaDataQueueAPI extends AbstractQueueAPI<FileMetaDataMessage> {

    private static final long serialVersionUID = -2392886169036426987L;

    @Override
    protected String getConnectionFactory() {
        return "jms/MetaDataQueueConnectionFactory";
    }

    @Override
    protected String getDestinyResource() {
        return "jms/FileMetaDataQueue";
    }

    // public String procesarCargaMasiva(FileMetaDataMessage jmsMessage) throws
    // Exception, IOException, MassivePersistenceException,
    // GenericFacadeException, ConstraintException {
    // BufferedReader br = null;
    // InputStream is = new ByteArrayInputStream(jmsMessage.getBytes());
    //
    // // Long maxCodMember =
    // // getFacadeContainer().getMetaMemberAPI().findMaxCodMemberByCodMeta(
    // // jmsMessage.getMetaCod());
    // /*
    // * Si el archivo tiene relacion con userphone se crea un hashmap en
    // * donde se va a almacenar dicha relacion
    // */
    //
    // BufferedInputStream bis = new BufferedInputStream(is);
    // br = new BufferedReader(new InputStreamReader(bis));
    //
    // String line = null; // linea leida del archivo
    //
    // int j = 0;
    //
    // while ((line = br.readLine()) != null) {
    // j++;
    // String[] result = line.split(";");
    //
    // if (result.length >= 0 && result[0].equals("")
    // && result[1].equals("")) {
    //
    // // TODO: enviar notificacion que el campo no fue procesado y
    // // pasar el sigueinte
    // // throw new MassivePersistenceException("El campo "
    // // + metaLabel + " requiere de un valor.");
    // continue;
    // }
    //
    // try {
    // MetaDataPK pk = new MetaDataPK();
    // pk.setCodClient(jmsMessage.getClientCod());
    // pk.setCodMeta(jmsMessage.getMetaCod());
    // pk.setCodeChr(result[0]);
    // pk.setCodMember(1L);
    //
    // /* Se recorre hasta la penultima columna */
    // for (int i = 1; i < result.length - 1; i++) {
    // pk = (MetaDataPK) pk.clone();
    // pk.setCodMember(new Long(i));
    //
    // MetaData metaData = new MetaData(pk);
    // metaData.setValueChr(result[i]);
    //
    // MetaDataMessage jmsBean = new MetaDataMessage();
    // jmsBean.setMetaData(metaData);
    //
    // // getFacadeContainer().getMetaDataQueueAPI().sendToJMS(
    // // metaDataMessage);
    // getFacadeContainer().getNotifier().info(getClass(), null,
    // "MetaData:" + pk);
    // getFacadeContainer().getMetaDataAPI().processUserphoneMeta(
    // jmsBean.getMetaData(), jmsBean.getUserphoneCode());
    //
    // }
    //
    // /*
    // * Si el archivo tiene relacion con userphone, la ultima columna
    // * representa la relacion
    // */
    // if (!jmsMessage.getProcessUserphone()) {
    // pk = (MetaDataPK) pk.clone();
    // pk.setCodMember(new Long(result.length - 1));
    //
    // MetaDataMessage jmsBean = new MetaDataMessage();
    // MetaData metaData = new MetaData(pk);
    // metaData.setValueChr(result[result.length - 1]);
    // jmsBean.setMetaData(metaData);
    //
    // // getFacadeContainer().getMetaDataQueueAPI().sendToJMS(
    // // jmsBean);
    // getFacadeContainer().getNotifier().info(getClass(), null,
    // "MetaData:" + pk);
    // getFacadeContainer().getMetaDataAPI().processUserphoneMeta(
    // jmsBean.getMetaData(), jmsBean.getUserphoneCode());
    //
    // } else {
    // pk = (MetaDataPK) pk.clone();
    // Long userphoneCod = null;
    // MetaData metaData = new MetaData(pk);
    //
    // MetaDataMessage jmsBean = new MetaDataMessage();
    // jmsBean.setMetaData(metaData);
    //
    // try {
    // pk.setCodMember(new Long(1));
    // userphoneCod = new Long(result[result.length - 1]);
    // jmsBean.setUserphoneCode(userphoneCod);
    // } catch (NumberFormatException e) {
    // pk.setCodMember(new Long(result.length - 1));
    // metaData.setValueChr(result[result.length - 1]);
    // }
    //
    // // getFacadeContainer().getMetaDataQueueAPI().sendToJMS(
    // // metaDataMessage);
    // getFacadeContainer().getNotifier().info(getClass(), null,
    // "MetaData+Userphone:" + pk + "+" + userphoneCod);
    // getFacadeContainer().getMetaDataAPI().processUserphoneMeta(
    // jmsBean.getMetaData(), jmsBean.getUserphoneCode());
    //
    // }
    // } catch (Exception e) {
    // continue;
    // }
    // }
    //
    // getFacadeContainer().getNotifier().info(getClass(), null,
    // "Fin del archivo.");
    //
    // getFacadeContainer().getSmsQueueAPI().sendToJMS(
    // jmsMessage.getUserwebCellphoneNum(),
    // jmsMessage.getApplication(),
    // MessageFormat.format(
    // "El archivo se ha procesado exitosamente. Se procesaron {0} registros.",
    // j));
    //
    // return null;
    //
    // }

}
