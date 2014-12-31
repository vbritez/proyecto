package com.tigo.cs.api.facade;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Asynchronous;

import org.hibernate.exception.ConstraintViolationException;

import com.tigo.cs.api.facade.jms.FileMetaDataMessage;
import com.tigo.cs.api.facade.jms.MetaDataMessage;
import com.tigo.cs.commons.ejb.ConstraintException;
import com.tigo.cs.commons.ejb.MassivePersistenceException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Application;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.MetaDataPK;

public abstract class FileMetaDataAPI extends AbstractAPI<String> {

    public FileMetaDataAPI() {
        super(String.class);
    }

    @Asynchronous
    public void procesarCargaMasiva(FileMetaDataMessage jmsMessage) {
        try {
            BufferedReader br = null;
            InputStream is = new ByteArrayInputStream(jmsMessage.getBytes());

            // Long maxCodMember =
            // getFacadeContainer().getMetaMemberAPI().findMaxCodMemberByCodMeta(
            // jmsMessage.getMetaCod());
            /*
             * Si el archivo tiene relacion con userphone se crea un hashmap en
             * donde se va a almacenar dicha relacion
             */

            BufferedInputStream bis = new BufferedInputStream(is);
            br = new BufferedReader(new InputStreamReader(bis));

            String line = null; // linea leida del archivo

            int j = 0;
            int k = 0;

            class MetaDataCreator extends Thread {

                private List<MetaDataMessage> metaDataMessageList;
                private boolean lastThread;
                private String cellphoneNum;
                private Application application;
                private int cantidadFilas;
                private boolean running = false;

                public MetaDataCreator(int id) {
                    super("MetaDataCreator-" + id);

                }

                public List<MetaDataMessage> getMetaDataMessageList() {
                    if (metaDataMessageList == null) {
                        metaDataMessageList = new ArrayList<MetaDataMessage>();
                    }
                    return metaDataMessageList;
                }

                public boolean getLastThread() {
                    return lastThread;
                }

                public void setLastThread(boolean lastThread) {
                    this.lastThread = lastThread;
                }

                public void setCellphoneNum(String cellphoneNum) {
                    this.cellphoneNum = cellphoneNum;
                }

                public void setApplication(Application application) {
                    this.application = application;
                }

                public void setCantidadFilas(int cantidadFilas) {
                    this.cantidadFilas = cantidadFilas;
                }

                @Override
                public void run() {

                    if (running) {
                        return;
                    }
                    running = true;
                    getFacadeContainer().getNotifier().info(getClass(), null,
                            "Coriendo proceso: " + this.getName());
                    if (metaDataMessageList != null) {
                        for (final MetaDataMessage jmsBean : metaDataMessageList) {

                            try {
                                getFacadeContainer().getMetaDataAPI().processUserphoneMeta(
                                        jmsBean.getMetaData(),
                                        jmsBean.getUserphoneCode());
                            } catch (ConstraintViolationException e) {
                                getFacadeContainer().getNotifier().error(
                                        getClass(), null, e.getMessage(), e);
                            } catch (ConstraintException e) {
                                getFacadeContainer().getNotifier().error(
                                        getClass(), null, e.getMessage(), e);
                            } catch (GenericFacadeException e) {
                                getFacadeContainer().getNotifier().error(
                                        getClass(), null, e.getMessage(), e);
                            } catch (MassivePersistenceException e) {
                                getFacadeContainer().getNotifier().error(
                                        getClass(), null, e.getMessage(), e);
                            }

                        }

                    }
                    if (getLastThread()) {

                        getFacadeContainer().getSmsQueueAPI().sendToJMS(
                                cellphoneNum,
                                application,
                                MessageFormat.format(
                                        "El archivo se ha procesado exitosamente. Se procesaron {0} registros.",
                                        cantidadFilas));
                    }

                }

            }
            MetaDataCreator currentMetaDataCreator = new MetaDataCreator(j);

            currentMetaDataCreator.setLastThread(true);
            currentMetaDataCreator.setCellphoneNum(jmsMessage.getUserwebCellphoneNum());
            currentMetaDataCreator.setApplication(jmsMessage.getApplication());

            MetaDataCreator prevoiusMetaDataCreator = null;
            while ((line = br.readLine()) != null) {
                j++;
                currentMetaDataCreator.setCantidadFilas(j);
                if (j % 500 == 0) {
                    currentMetaDataCreator.start();
                    if (prevoiusMetaDataCreator != null) {
                        prevoiusMetaDataCreator.setLastThread(false);
                    }
                    prevoiusMetaDataCreator = currentMetaDataCreator;
                    prevoiusMetaDataCreator.setLastThread(true);
                    prevoiusMetaDataCreator.setCellphoneNum(jmsMessage.getUserwebCellphoneNum());
                    prevoiusMetaDataCreator.setApplication(jmsMessage.getApplication());
                    prevoiusMetaDataCreator.setCantidadFilas(j);

                    currentMetaDataCreator = new MetaDataCreator(++k);
                }
                String[] result = line.split(";");

                if (result.length > 0 && result[0].equals("")
                    && result[1].equals("")) {

                    // TODO: enviar notificacion que el campo no fue procesado y
                    // pasar el sigueinte
                    // throw new MassivePersistenceException("El campo "
                    // + metaLabel + " requiere de un valor.");
                    continue;
                }

                try {
                    MetaDataPK pk = new MetaDataPK();
                    pk.setCodClient(jmsMessage.getClientCod());
                    pk.setCodMeta(jmsMessage.getMetaCod());
                    pk.setCodeChr(result[0]);
                    pk.setCodMember(1L);

                    /* Se recorre hasta la penultima columna */
                    for (int i = 1; i < result.length - 1; i++) {
                        pk = (MetaDataPK) pk.clone();
                        pk.setCodMember(new Long(i));

                        MetaData metaData = new MetaData(pk);
                        metaData.setValueChr(result[i]);

                        final MetaDataMessage jmsBean = new MetaDataMessage();
                        jmsBean.setMetaData(metaData);

                        currentMetaDataCreator.getMetaDataMessageList().add(
                                jmsBean);

                    }

                    /*
                     * Si el archivo tiene relacion con userphone, la ultima
                     * columna representa la relacion
                     */
                    if (!jmsMessage.getProcessUserphone()) {
                        pk = (MetaDataPK) pk.clone();
                        pk.setCodMember(new Long(result.length - 1));

                        final MetaDataMessage jmsBean = new MetaDataMessage();
                        MetaData metaData = new MetaData(pk);
                        metaData.setValueChr(result[result.length - 1]);
                        jmsBean.setMetaData(metaData);

                        currentMetaDataCreator.getMetaDataMessageList().add(
                                jmsBean);

                    } else {
                        pk = (MetaDataPK) pk.clone();
                        Long userphoneCod = null;
                        MetaData metaData = new MetaData(pk);

                        final MetaDataMessage jmsBean = new MetaDataMessage();
                        jmsBean.setMetaData(metaData);

                        try {
                            pk.setCodMember(new Long(1));
                            userphoneCod = new Long(result[result.length - 1]);
                            jmsBean.setUserphoneCode(userphoneCod);
                        } catch (NumberFormatException e) {
                            pk.setCodMember(new Long(result.length - 1));
                            metaData.setValueChr(result[result.length - 1]);
                        }

                        currentMetaDataCreator.getMetaDataMessageList().add(
                                jmsBean);
                        currentMetaDataCreator.getMetaDataMessageList().add(
                                jmsBean);

                    }
                } catch (Exception e) {
                    continue;
                }
            }

            currentMetaDataCreator.start();

            getFacadeContainer().getNotifier().info(getClass(), null,
                    "Fin del archivo.");

        } catch (IOException e) {

        }
    }

}
