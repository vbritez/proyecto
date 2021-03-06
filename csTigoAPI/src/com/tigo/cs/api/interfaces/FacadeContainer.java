package com.tigo.cs.api.interfaces;

import javax.persistence.EntityManager;

import com.tigo.cs.api.facade.AndroidVersionAPI;
import com.tigo.cs.api.facade.ApplicationAPI;
import com.tigo.cs.api.facade.AuditoryAPI;
import com.tigo.cs.api.facade.AuditoryActionAPI;
import com.tigo.cs.api.facade.CacheAPI;
import com.tigo.cs.api.facade.CellAPI;
import com.tigo.cs.api.facade.ClassificationAPI;
import com.tigo.cs.api.facade.ClientAPI;
import com.tigo.cs.api.facade.ClientFeatureAPI;
import com.tigo.cs.api.facade.ClientFileAPI;
import com.tigo.cs.api.facade.ClientGoalAPI;
import com.tigo.cs.api.facade.ClientParameterValueAPI;
import com.tigo.cs.api.facade.ClientServiceFunctionalityAPI;
import com.tigo.cs.api.facade.DataActivityAPI;
import com.tigo.cs.api.facade.DataAttendantAPI;
import com.tigo.cs.api.facade.DataHoraryAPI;
import com.tigo.cs.api.facade.DataIconAPI;
import com.tigo.cs.api.facade.DataStatusAPI;
import com.tigo.cs.api.facade.DataZoneAPI;
import com.tigo.cs.api.facade.FeatureAPI;
import com.tigo.cs.api.facade.FeatureElementAPI;
import com.tigo.cs.api.facade.FeatureElementEntryAPI;
import com.tigo.cs.api.facade.FeatureEntryTypeAPI;
import com.tigo.cs.api.facade.FeatureValueAPI;
import com.tigo.cs.api.facade.FeatureValueDataAPI;
import com.tigo.cs.api.facade.FileMetaDataAPI;
import com.tigo.cs.api.facade.FunctionalityAPI;
import com.tigo.cs.api.facade.GlobalParameterAPI;
import com.tigo.cs.api.facade.I18nAPI;
import com.tigo.cs.api.facade.IconTypeAPI;
import com.tigo.cs.api.facade.InvalidmessageAPI;
import com.tigo.cs.api.facade.MapMarkAPI;
import com.tigo.cs.api.facade.MenuMovilAPI;
import com.tigo.cs.api.facade.MenuMovilPeriodAPI;
import com.tigo.cs.api.facade.MenuMovilUserAPI;
import com.tigo.cs.api.facade.MessageAPI;
import com.tigo.cs.api.facade.MessageNotSentAPI;
import com.tigo.cs.api.facade.MetaAPI;
import com.tigo.cs.api.facade.MetaClientAPI;
import com.tigo.cs.api.facade.MetaDataAPI;
import com.tigo.cs.api.facade.MetaMemberAPI;
import com.tigo.cs.api.facade.NCenterWSAPI;
import com.tigo.cs.api.facade.NotifierAPI;
import com.tigo.cs.api.facade.OtAPI;
import com.tigo.cs.api.facade.PhoneAPI;
import com.tigo.cs.api.facade.PhoneListAPI;
import com.tigo.cs.api.facade.ReportConfigAPI;
import com.tigo.cs.api.facade.ReportConfigMailAPI;
import com.tigo.cs.api.facade.RoleClientAPI;
import com.tigo.cs.api.facade.RoleClientScreenAPI;
import com.tigo.cs.api.facade.RoleadminAPI;
import com.tigo.cs.api.facade.RouteAPI;
import com.tigo.cs.api.facade.RouteDetailAPI;
import com.tigo.cs.api.facade.RouteDetailUserphoneAPI;
import com.tigo.cs.api.facade.RouteTypeAPI;
import com.tigo.cs.api.facade.RouteUserphoneAPI;
import com.tigo.cs.api.facade.ScreenAPI;
import com.tigo.cs.api.facade.ScreenClientAPI;
import com.tigo.cs.api.facade.ServiceAPI;
import com.tigo.cs.api.facade.ServiceFunctionalityAPI;
import com.tigo.cs.api.facade.ServiceOperationAPI;
import com.tigo.cs.api.facade.ServiceValueAPI;
import com.tigo.cs.api.facade.ServiceValueDetailAPI;
import com.tigo.cs.api.facade.ShiftConfigurationAPI;
import com.tigo.cs.api.facade.ShiftPeriodAPI;
import com.tigo.cs.api.facade.SubscriberAPI;
import com.tigo.cs.api.facade.TrackingConfigurationAPI;
import com.tigo.cs.api.facade.TrackingPeriodAPI;
import com.tigo.cs.api.facade.UseradminAPI;
import com.tigo.cs.api.facade.UserphoneAPI;
import com.tigo.cs.api.facade.UserphoneMetaAPI;
import com.tigo.cs.api.facade.UserwebAPI;
import com.tigo.cs.api.facade.UssdApplicationAPI;
import com.tigo.cs.api.facade.UssdDriverAPI;
import com.tigo.cs.api.facade.UssdMenuEntryAPI;
import com.tigo.cs.api.facade.UssdMenuEntryUssdUserAPI;
import com.tigo.cs.api.facade.UssdServiceCorrespAPI;
import com.tigo.cs.api.facade.UssdUserAPI;
import com.tigo.cs.api.facade.WnOperadoraAPI;
import com.tigo.cs.api.facade.WsClientAPI;
import com.tigo.cs.api.facade.jms.FileMetaDataQueueAPI;
import com.tigo.cs.api.facade.jms.MetaDataQueueAPI;
import com.tigo.cs.api.facade.jms.SMSQueueAPI;
import com.tigo.cs.api.facade.jms.TrackQueueAPI;
import com.tigo.cs.api.facade.ws.GetClientBirthdayAPI;
import com.tigo.cs.api.facade.ws.InterfisaInformconfServiceAPI;
import com.tigo.cs.api.facade.ws.IpsServiceAPI;
import com.tigo.cs.api.facade.ws.LbsGwServiceAPI;
import com.tigo.cs.api.facade.ws.MTSServiceAPI;
import com.tigo.cs.api.facade.ws.RetrievePoliceBaseInfoAPI;
import com.tigo.cs.api.facade.ws.SMSTransmitterServiceAPI;
import com.tigo.cs.api.facade.ws.TicketCSIAPI;
import com.tigo.cs.api.facade.ws.TigoMoneyWSServiceAPI;
import com.tigo.cs.api.facade.ws.TrackingOnDemandAPI;
import com.tigo.cs.api.facade.ws.ValidacionClienteBCCSNacServiceAPI;
import com.tigo.cs.api.facade.ws.ValidationClientCIAPI;

public interface FacadeContainer {

    public EntityManager getEntityManager();

    public EntityManager getEntityManagerMTS();

    public boolean isEntityManagerTransactional();

    public boolean isCutMessage();

    public boolean isSendSMSViaWS();

    public ServiceValueAPI getServiceValueAPI();

    public ServiceValueDetailAPI getServiceValueDetailAPI();

    public NotifierAPI getNotifier();

    public I18nAPI getI18nAPI();

    public GlobalParameterAPI getGlobalParameterAPI();

    public AuditoryActionAPI getAuditoryActionAPI();

    public ScreenAPI getScreenAPI();

    public AuditoryAPI getAuditoryAPI();

    public UserwebAPI getUserwebAPI();

    public ClassificationAPI getClassificationAPI();

    public UssdUserAPI getUssdUserAPI();

    public CellAPI getCellAPI();

    public ApplicationAPI getApplicationAPI();

    public ClientParameterValueAPI getClientParameterValueAPI();

    public ClientFileAPI getClientFileAPI();

    public RoleClientAPI getRoleClientAPI();

    public RoleClientScreenAPI getRoleClientScreenAPI();

    public UssdMenuEntryUssdUserAPI getUssdMenuEntryUssdUserAPI();

    public UseradminAPI getUseradminAPI();

    public UserphoneAPI getUserphoneAPI();

    public RoleadminAPI getRoleadminAPI();

    public ClientServiceFunctionalityAPI getClientServiceFunctionalityAPI();

    public ServiceFunctionalityAPI getServiceFunctionalityAPI();

    public TrackingConfigurationAPI getTrackingConfigurationAPI();

    public TrackingPeriodAPI getTrackingPeriodAPI();

    public UssdMenuEntryAPI getUssdMenuEntryAPI();

    public UssdApplicationAPI getUssdApplicationAPI();

    public PhoneAPI getPhoneAPI();

    public ClientAPI getClientAPI();

    public PhoneListAPI getPhoneListAPI();

    public FeatureElementAPI getFeatureElementAPI();

    public FeatureElementEntryAPI getFeatureElementEntryAPI();

    public FeatureAPI getFeatureAPI();

    public FunctionalityAPI getFunctionalityAPI();

    public MetaAPI getMetaAPI();

    public MetaMemberAPI getMetaMemberAPI();

    public MetaClientAPI getMetaClientAPI();

    public ReportConfigAPI getReportConfigAPI();

    public ReportConfigMailAPI getReportConfigMailAPI();

    public MapMarkAPI getMapMarkAPI();

    public RouteAPI getRouteAPI();

    public RouteDetailAPI getRouteDetailAPI();

    public RouteTypeAPI getRouteTypeAPI();

    public RouteUserphoneAPI getRouteUserphoneAPI();

    public ShiftPeriodAPI getShiftPeriodAPI();

    public MetaDataAPI getMetaDataAPI();

    public ShiftConfigurationAPI getShiftConfigurationAPI();

    public MessageAPI getMessageAPI();

    public ServiceAPI getServiceAPI();

    public RouteDetailUserphoneAPI getRouteDetailUserphoneAPI();

    public SMSTransmitterServiceAPI getSmsTransmitterServiceAPI();

    public InvalidmessageAPI getInvalidmessageAPI();

    public MessageNotSentAPI getMessageNotSentAPI();

    public ClientFeatureAPI getClientFeatureAPI();

    public FeatureEntryTypeAPI getFeatureEntryTypeAPI();

    public UserphoneMetaAPI getUserphoneMetaAPI();

    public FeatureValueAPI getFeatureValueAPI();

    public FeatureValueDataAPI getFeatureValueDataAPI();

    public ClientGoalAPI getClientGoalAPI();

    public ServiceOperationAPI getServiceOperationAPI();

    public LbsGwServiceAPI getLbsGwServiceAPI();

    public DataActivityAPI getDataActivityAPI();

    public DataStatusAPI getDataStatusAPI();

    public DataZoneAPI getDataZoneAPI();

    public ScreenClientAPI getScreenClientAPI();

    public OtAPI getOtAPI();

    public TicketCSIAPI getTicketCSIAPI();

    public WnOperadoraAPI getWnOperadoraAPI();

    public IpsServiceAPI getIpsServiceAPI();

    public MenuMovilUserAPI getMenuMovilUserAPI();

    public MenuMovilPeriodAPI getMenuMovilPeriodAPI();

    public MenuMovilAPI getMenuMovilAPI();

    public NCenterWSAPI getNCenterWSAPI();

    public DataAttendantAPI getDataAttendantAPI();

    public CacheAPI getCacheAPI();

    public InterfisaInformconfServiceAPI getInterfisaInformconfServiceAPI();

    public UssdDriverAPI getUssdDriverAPI();

    public AndroidVersionAPI getAndroidVersionAPI();

    public IconTypeAPI getIconTypeAPI();

    public DataIconAPI getDataIconAPI();

    public TrackingOnDemandAPI getTrackingOnDemandAPI();

    public SubscriberAPI getSubscriberAPI();

    public MTSServiceAPI getMtsServiceAPI();

    public ValidacionClienteBCCSNacServiceAPI getValidacionClienteBCCSNacServiceAPI();

    public TigoMoneyWSServiceAPI getTigoMoneyWSServiceAPI();

    public WsClientAPI getWsClientAPI();

    public SMSQueueAPI getSmsQueueAPI();

    public RetrievePoliceBaseInfoAPI getRetrievePoliceBaseInfoAPI();

    public GetClientBirthdayAPI getGetClientBirthdayAPI();

    public ValidationClientCIAPI getValidationClientCIAPI();

    public FileMetaDataAPI getFileMetaDataAPI();

    public DataHoraryAPI getDataHoraryAPI();

    public UssdServiceCorrespAPI getUssdServiceCorrespAPI();
public TrackQueueAPI getTrackQueueAPI();

    public FileMetaDataQueueAPI getFileMetaDataQueueAPI();

    public MetaDataQueueAPI getMetaDataQueueAPI();

}
