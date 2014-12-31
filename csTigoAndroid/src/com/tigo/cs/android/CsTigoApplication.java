package com.tigo.cs.android;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import com.tigo.cs.android.activity.tigomoney.TigoMoneyTmpEntity;
import com.tigo.cs.android.asynctask.response.AbstractAsyncTask;
import com.tigo.cs.android.helper.DynamicFormElementEntryHelper;
import com.tigo.cs.android.helper.DynamicFormElementHelper;
import com.tigo.cs.android.helper.DynamicFormEntryTypeHelper;
import com.tigo.cs.android.helper.GlobalParameterHelper;
import com.tigo.cs.android.helper.IconHelper;
import com.tigo.cs.android.helper.LocationHelper;
import com.tigo.cs.android.helper.MessageHelper;
import com.tigo.cs.android.helper.MetaDataHelper;
import com.tigo.cs.android.helper.MetaHelper;
import com.tigo.cs.android.helper.MetaMemberHelper;
import com.tigo.cs.android.helper.OperationHelper;
import com.tigo.cs.android.helper.ServiceEventHelper;
import com.tigo.cs.android.helper.ServiceHelper;
import com.tigo.cs.android.helper.ServiceOperationDataHelper;
import com.tigo.cs.android.helper.ServiceOperationHelper;
import com.tigo.cs.android.helper.SessionHelper;
import com.tigo.cs.android.helper.UserphoneHelper;
import com.tigo.cs.android.helper.UserphoneTrackHelper;
import com.tigo.cs.android.helper.domain.ServiceEntity;
import com.tigo.cs.android.util.CSTigoNotificationID;
import com.tigo.cs.android.util.Cypher;
import com.tigo.cs.api.entities.BaseServiceBean;

public class CsTigoApplication extends Application {

    private static ServiceHelper serviceHelper;
    private static ServiceOperationHelper serviceOperationHelper;
    private static ServiceOperationDataHelper serviceOperationDataHelper;
    private static ServiceEventHelper serviceEventHelper;
    private static GlobalParameterHelper globalParameterHelper;
    private static OperationHelper operationHelper;
    private static MessageHelper messageHelper;
    private static MetaHelper metaHelper;
    private static MetaDataHelper metaDataHelper;
    private static LocationHelper locationHelper;
    private static MetaMemberHelper metaMemberHelper;
    private static UserphoneHelper userphoneHelper;
    private static IconHelper iconHelper;

    private static DynamicFormElementHelper dynamicFormElementHelper;
    private static DynamicFormElementEntryHelper dynamicFormElementEntryHelper;
    private static DynamicFormEntryTypeHelper dynamicFormEntryTypeHelper;
    private static UserphoneTrackHelper userphoneTrackHelper;

    private static SessionHelper sessionHelper;

    private static Map<Integer, Class> servicesEnabled;
    public static boolean notificatApn = true;
    public static boolean alarmado = false;
    public static Integer messageId = 0;

    protected static Context context;
    private static Notification notification;
    private static Long uniqueNumberIntent = 0l;
    private static String signalStrength;

    private static TigoMoneyTmpEntity currentTigoMoneyEntity;

    public static Context getContext() {
        return context;
    }

    public static TigoMoneyTmpEntity getCurrentTigoMoneyEntity() {
        return currentTigoMoneyEntity;
    }

    public static void setCurrentTigoMoneyEntity(TigoMoneyTmpEntity currentTigoMoneyEntity) {
        CsTigoApplication.currentTigoMoneyEntity = currentTigoMoneyEntity;
    }

    public static GlobalParameterHelper getGlobalParameterHelper() {
        if (globalParameterHelper == null) {
            globalParameterHelper = new GlobalParameterHelper(context);
        }
        return globalParameterHelper;
    }

    public static OperationHelper getOperationHelper() {
        if (operationHelper == null) {
            operationHelper = new OperationHelper(context);
        }
        return operationHelper;
    }

    public static MessageHelper getMessageHelper() {
        if (messageHelper == null) {
            messageHelper = new MessageHelper(context);
        }
        return messageHelper;
    }

    public static MetaHelper getMetaHelper() {
        if (metaHelper == null) {
            metaHelper = new MetaHelper(context);
        }
        return metaHelper;
    }

    public static MetaDataHelper getMetaDataHelper() {
        if (metaDataHelper == null) {
            metaDataHelper = new MetaDataHelper(context);
        }
        return metaDataHelper;
    }

    public static ServiceEventHelper getServiceEventHelper() {
        if (serviceEventHelper == null) {
            serviceEventHelper = new ServiceEventHelper(context);
        }
        return serviceEventHelper;
    }

    public static ServiceHelper getServiceHelper() {
        if (serviceHelper == null) {
            serviceHelper = new ServiceHelper(context);
        }
        return serviceHelper;
    }

    public static ServiceOperationDataHelper getServiceOperationDataHelper() {
        if (serviceOperationDataHelper == null) {
            serviceOperationDataHelper = new ServiceOperationDataHelper(context);
        }
        return serviceOperationDataHelper;
    }

    public static ServiceOperationHelper getServiceOperationHelper() {
        if (serviceOperationHelper == null) {
            serviceOperationHelper = new ServiceOperationHelper(context);
        }
        return serviceOperationHelper;
    }

    public static LocationHelper getLocationHelper() {
        if (locationHelper == null) {
            locationHelper = new LocationHelper(context);
        }
        return locationHelper;
    }

    public static MetaMemberHelper getMetaMemberHelper() {
        if (metaMemberHelper == null) {
            metaMemberHelper = new MetaMemberHelper(context);
        }
        return metaMemberHelper;
    }

    public static UserphoneHelper getUserphoneHelper() {
        if (userphoneHelper == null) {
            userphoneHelper = new UserphoneHelper(context);
        }
        return userphoneHelper;
    }

    public static IconHelper getIconHelper() {
        if (iconHelper == null) {
            iconHelper = new IconHelper(context);
        }
        return iconHelper;
    }

    public static DynamicFormElementHelper getDynamicFormElementHelper() {
        if (dynamicFormElementHelper == null) {
            dynamicFormElementHelper = new DynamicFormElementHelper(context);
        }
        return dynamicFormElementHelper;
    }

    public static DynamicFormEntryTypeHelper getDynamicFormEntryTypeHelper() {
        if (dynamicFormEntryTypeHelper == null) {
            dynamicFormEntryTypeHelper = new DynamicFormEntryTypeHelper(context);
        }
        return dynamicFormEntryTypeHelper;
    }

    public static DynamicFormElementEntryHelper getDynamicFormElementEntryHelper() {
        if (dynamicFormElementEntryHelper == null) {
            dynamicFormElementEntryHelper = new DynamicFormElementEntryHelper(context);
        }
        return dynamicFormElementEntryHelper;
    }

    public static UserphoneTrackHelper getUserphoneTrackHelper() {
        if (userphoneTrackHelper == null) {
            userphoneTrackHelper = new UserphoneTrackHelper(context);
        }
        return userphoneTrackHelper;
    }

    public static SessionHelper getSessionHelper() {
        if (sessionHelper == null) {
            sessionHelper = new SessionHelper(context);
        }
        return sessionHelper;
    }

    public static Map<Integer, Class> getServicios() {

        List<ServiceEntity> serviceEntityList = getServiceHelper().findAll();
        if (serviceEntityList != null && serviceEntityList.size() > 0) {
            if (servicesEnabled == null) {
                servicesEnabled = new HashMap<Integer, Class>();

                /*
                 * verificamos cuales son los servicios habilitados para el
                 * dispositivo
                 */
                for (ServiceEntity service : serviceEntityList) {
                    servicesEnabled.put(service.getServicecod(),
                            service.getClassName());
                }

            }
        }

        return servicesEnabled;

    }

    /*
     * utilizado para la internacionalizacion de la base de datos
     */
    public static String i18n(String key) {
        try {
            for (Field field : R.string.class.getDeclaredFields()) {
                if (field.getName().compareTo(key) == 0) {
                    return CsTigoApplication.getContext().getString(
                            field.getInt(field));
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static boolean isAlarmado() {
        return alarmado;
    }

    public static void setAlarmado(boolean alarmado) {
        CsTigoApplication.alarmado = alarmado;
    }

    public static void setContext(Context context) {
        CsTigoApplication.context = context;
    }

    public static void setServiceOperationDataHelper(ServiceOperationDataHelper serviceOperationDataHelper) {
        CsTigoApplication.serviceOperationDataHelper = serviceOperationDataHelper;
    }

    public static void setServiceOperationHelper(ServiceOperationHelper serviceOperationHelper) {
        CsTigoApplication.serviceOperationHelper = serviceOperationHelper;
    }

    public static void showNotification(CSTigoNotificationID id, String title, String text, Class activityClass, HashMap<String, Serializable> extras) {
        NotificationManager notificationManager = (NotificationManager) CsTigoApplication.getContext().getSystemService(
                Context.NOTIFICATION_SERVICE);

        if (notification == null) {
            notification = new Notification(R.drawable.ic_header, CsTigoApplication.getContext().getString(
                    R.string.platform_new_message), System.currentTimeMillis());
        }
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.number += 1;
        uniqueNumberIntent += 1l;

        Intent messageHistoryIntent = new Intent(context, activityClass);
        if (extras != null) {
            Iterator it = extras.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                messageHistoryIntent.putExtra((String) e.getKey(),
                        (Serializable) e.getValue());
            }
        }

        PendingIntent activity = PendingIntent.getActivity(context,
                uniqueNumberIntent.intValue(), messageHistoryIntent, 0);
        notification.setLatestEventInfo(context, title, text, activity);
        notificationManager.notify(id.ordinal(), notification);

    }

    public static void showNotification(CSTigoNotificationID id, String title, String text, Class activityClass, Map<String, String> extras) {
        NotificationManager notificationManager = (NotificationManager) CsTigoApplication.getContext().getSystemService(
                Context.NOTIFICATION_SERVICE);

        if (notification == null) {
            notification = new Notification(R.drawable.ic_header, CsTigoApplication.getContext().getString(
                    R.string.platform_new_message), System.currentTimeMillis());
        }
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.number += 1;
        uniqueNumberIntent += 1l;

        Intent messageHistoryIntent = new Intent(context, activityClass);
        if (extras != null) {
            Iterator it = extras.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                messageHistoryIntent.putExtra((String) e.getKey(),
                        (String) e.getValue());
            }
        }

        PendingIntent activity = PendingIntent.getActivity(context,
                uniqueNumberIntent.intValue(), messageHistoryIntent, 0);
        notification.setLatestEventInfo(context, title, text, activity);
        notificationManager.notify(id.ordinal(), notification);

    }

    public static void showNotification(CSTigoNotificationID id, String title, String text, Class activityClass) {
        NotificationManager notificationManager = (NotificationManager) CsTigoApplication.getContext().getSystemService(
                Context.NOTIFICATION_SERVICE);

        if (notification == null) {
            notification = new Notification(R.drawable.ic_header, CsTigoApplication.getContext().getString(
                    R.string.platform_new_message), System.currentTimeMillis());
        }
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.number += 1;
        uniqueNumberIntent += 1l;

        Intent messageHistoryIntent = new Intent(context, activityClass);

        PendingIntent activity = PendingIntent.getActivity(context,
                uniqueNumberIntent.intValue(), messageHistoryIntent, 0);
        notification.setLatestEventInfo(context, title, text, activity);
        notificationManager.notify(id.ordinal(), notification);

    }

    public static void showNotification(CSTigoNotificationID id, String title, String text, String intent) {
        NotificationManager notificationManager = (NotificationManager) CsTigoApplication.getContext().getSystemService(
                Context.NOTIFICATION_SERVICE);

        if (notification == null) {
            notification = new Notification(R.drawable.ic_header, CsTigoApplication.getContext().getString(
                    R.string.platform_new_message), System.currentTimeMillis());
        }
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.number += 1;
        uniqueNumberIntent += 1l;

        Intent messageHistoryIntent = new Intent(intent);

        PendingIntent activity = PendingIntent.getActivity(context,
                uniqueNumberIntent.intValue(), messageHistoryIntent, 0);
        notification.setLatestEventInfo(context, title, text, activity);
        notificationManager.notify(id.ordinal(), notification);

    }

    public static void vibrate(Boolean longVibrate) {
        if (getGlobalParameterHelper().getPlatformVibrate()) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (longVibrate) {
                vibrator.vibrate(1000);
            } else {
                vibrator.vibrate(300);
            }

        }
    }

    public CsTigoApplication() throws Exception {
        CsTigoApplication.context = this;
    }

    public static synchronized boolean manage(BaseServiceBean entity) {

        ServiceEntity service = CsTigoApplication.getServiceHelper().findByServiceCod(
                entity.getServiceCod());

        AbstractAsyncTask<BaseServiceBean> task = createDispatcher(service.getServiceClass());
        task.setResponseEntity(entity);
        task.execute(new String[] {});

        return true;
    }

    public static synchronized boolean manage(String messageIn) {

        ServiceEntity serviceEntity = null;

        String[] messageParts = null;

        try {
            messageIn = Cypher.decrypt(
                    CsTigoApplication.getGlobalParameterHelper().getDeviceCellphoneNum(),
                    messageIn);
        } catch (Exception e) {
            /*
             * el mensaje no esta toalmente encriptado, es posiblemente un
             * rastreo
             */
        }

        messageParts = messageIn.split("\\%+\\*");
        /*
         * obtenemos el codigo de servicio del mensaje
         */
        Integer serviceCod = null;
        try {
            serviceCod = Integer.parseInt(messageParts[0]);
            serviceEntity = getServiceHelper().findByServiceCod(serviceCod);
        } catch (Exception e) {
            return false;
        }

        try {
            messageIn = messageIn.substring(0,
                    serviceCod.toString().length() + 2)
                + Cypher.decrypt(
                        CsTigoApplication.getGlobalParameterHelper().getDeviceCellphoneNum(),
                        messageIn.substring(serviceCod.toString().length() + 2));
        } catch (Exception e) {
        }

        /*
         * verificamos si tiene el servicio de rastreo habilitado
         */

        ServiceEntity service = CsTigoApplication.getServiceHelper().findByServiceCod(
                serviceCod);

        AbstractAsyncTask<BaseServiceBean> task = createDispatcher(service.getServiceClass());
        task.getResponseEntity().setGrossMessage(messageIn);
        task.execute(new String[] {});

        if (serviceCod == 17) {
            return false;
        }
        return true;
    }

    protected static <T extends AbstractAsyncTask<BaseServiceBean>> T createDispatcher(Class<T> daClass) {
        Class[] paramConstructor = {};

        Constructor<T> constructor;
        try {
            constructor = daClass.getConstructor(paramConstructor);

            Object[] initArgs = {};
            T t = constructor.newInstance(initArgs);

            return t;
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    public static Class getServiceClass(String className) {
        if (className != null) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {

            }
        }
        return null;
    }

    public static String getSignalStrength() {
        return signalStrength;
    }

    public static void setSignalStrength(String signalStrength) {
        CsTigoApplication.signalStrength = signalStrength;
    }

}
