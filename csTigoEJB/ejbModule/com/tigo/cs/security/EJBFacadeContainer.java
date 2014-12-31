package com.tigo.cs.security;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import com.tigo.cs.api.facade.ws.GetClientBirthdayAPI;
import com.tigo.cs.api.facade.jms.TrackQueueAPI;
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
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.facade.AndroidVersionFacade;
import com.tigo.cs.facade.ApplicationFacade;
import com.tigo.cs.facade.AuditoryActionFacade;
import com.tigo.cs.facade.AuditoryFacade;
import com.tigo.cs.facade.CellFacade;
import com.tigo.cs.facade.ClassificationFacade;
import com.tigo.cs.facade.ClientFacade;
import com.tigo.cs.facade.ClientFeatureFacade;
import com.tigo.cs.facade.ClientFileFacade;
import com.tigo.cs.facade.ClientGoalFacade;
import com.tigo.cs.facade.ClientParameterValueFacade;
import com.tigo.cs.facade.ClientServiceFunctionalityFacade;
import com.tigo.cs.facade.FeatureElementEntryFacade;
import com.tigo.cs.facade.FeatureElementFacade;
import com.tigo.cs.facade.FeatureEntryTypeFacade;
import com.tigo.cs.facade.FeatureFacade;
import com.tigo.cs.facade.FeatureValueDataFacade;
import com.tigo.cs.facade.FeatureValueFacade;
import com.tigo.cs.facade.FileMetaDataFacade;
import com.tigo.cs.facade.FunctionalityFacade;
import com.tigo.cs.facade.GlobalParameterFacade;
import com.tigo.cs.facade.I18nBean;
import com.tigo.cs.facade.IconTypeFacade;
import com.tigo.cs.facade.MapMarkFacade;
import com.tigo.cs.facade.MessageFacade;
import com.tigo.cs.facade.MetaClientFacade;
import com.tigo.cs.facade.MetaDataFacade;
import com.tigo.cs.facade.MetaFacade;
import com.tigo.cs.facade.MetaMemberFacade;
import com.tigo.cs.facade.PhoneFacade;
import com.tigo.cs.facade.PhoneListFacade;
import com.tigo.cs.facade.ReportConfigFacade;
import com.tigo.cs.facade.ReportConfigMailFacade;
import com.tigo.cs.facade.RoleClientFacade;
import com.tigo.cs.facade.RoleClientScreenFacade;
import com.tigo.cs.facade.RoleadminFacade;
import com.tigo.cs.facade.RouteDetailFacade;
import com.tigo.cs.facade.RouteDetailUserphoneFacade;
import com.tigo.cs.facade.RouteFacade;
import com.tigo.cs.facade.RouteTypeFacade;
import com.tigo.cs.facade.RouteUserphoneFacade;
import com.tigo.cs.facade.ScreenClientFacade;
import com.tigo.cs.facade.ScreenFacade;
import com.tigo.cs.facade.ServiceFacade;
import com.tigo.cs.facade.ServiceFunctionalityFacade;
import com.tigo.cs.facade.ServiceOperationFacade;
import com.tigo.cs.facade.ServiceValueDetailFacade;
import com.tigo.cs.facade.ServiceValueFacade;
import com.tigo.cs.facade.ShiftConfigurationFacade;
import com.tigo.cs.facade.ShiftPeriodFacade;
import com.tigo.cs.facade.SubscriberFacade;
import com.tigo.cs.facade.TrackingConfigurationFacade;
import com.tigo.cs.facade.TrackingOnDemandFacade;
import com.tigo.cs.facade.TrackingPeriodFacade;
import com.tigo.cs.facade.UseradminFacade;
import com.tigo.cs.facade.UserphoneFacade;
import com.tigo.cs.facade.UserphoneMetaFacade;
import com.tigo.cs.facade.UserwebFacade;
import com.tigo.cs.facade.UssdDriverFacade;
import com.tigo.cs.facade.WnOperadoraFacade;
import com.tigo.cs.facade.jms.FileMetaDataQueueFacade;
import com.tigo.cs.facade.jms.MetaDataQueueFacade;
import com.tigo.cs.facade.jms.SMSQueueFacade;
import com.tigo.cs.facade.jms.TrackQueueFacade;
import com.tigo.cs.facade.menumovil.MenuMovilFacade;
import com.tigo.cs.facade.menumovil.MenuMovilPeriodFacade;
import com.tigo.cs.facade.menumovil.MenuMovilUserFacade;
import com.tigo.cs.facade.ncenter.NCenterWSFacade;
import com.tigo.cs.facade.ussd.UssdApplicationFacade;
import com.tigo.cs.facade.ussd.UssdMenuEntryFacade;
import com.tigo.cs.facade.ussd.UssdMenuEntryUssdUserFacade;
import com.tigo.cs.facade.ussd.UssdServiceCorrespFacade;
import com.tigo.cs.facade.ussd.UssdUserFacade;
import com.tigo.cs.facade.view.DataActivityFacade;
import com.tigo.cs.facade.view.DataAttendantFacade;
import com.tigo.cs.facade.view.DataHoraryFacade;
import com.tigo.cs.facade.view.DataIconFacade;
import com.tigo.cs.facade.view.DataStatusFacade;
import com.tigo.cs.facade.view.DataZoneFacade;
import com.tigo.cs.facade.view.OtFacade;
import com.tigo.cs.facade.ws.GetClientBirthdayFacade;
import com.tigo.cs.facade.ws.InterfisaInformconfServiceFacade;
import com.tigo.cs.facade.ws.IpsServiceFacade;
import com.tigo.cs.facade.ws.LbsGwServiceFacade;
import com.tigo.cs.facade.ws.MTSServiceFacade;
import com.tigo.cs.facade.ws.RetrievePoliceBaseInfoFacade;
import com.tigo.cs.facade.ws.SMSTransmitterServiceFacade;
import com.tigo.cs.facade.ws.TicketCSIFacade;
import com.tigo.cs.facade.ws.TigoMoneyServiceFacade;
import com.tigo.cs.facade.ws.ValidacionClienteBCCSNacServiceFacade;
import com.tigo.cs.facade.ws.ValidationClientCIFacade;

@Singleton
public class EJBFacadeContainer implements FacadeContainer {

    @EJB
    protected ServiceValueDetailFacade svdFacade;
    @EJB
    protected ServiceValueFacade svFacade;
    @EJB
    protected MetaClientFacade mcFacade;
    @EJB
    protected ClientParameterValueFacade cpvFacade;
    @EJB
    protected MetaDataFacade mdFacade;
    @EJB
    private Notifier notifier;
    @EJB
    protected ServiceFacade serviceFacade;
    @EJB
    protected RouteDetailUserphoneFacade routeDetailUserphoneFacade;
    @EJB
    protected SMSTransmitterServiceFacade smsTransmitterServiceFacade;
    @EJB
    private I18nBean i18nBean;
    @EJB
    private GlobalParameterFacade globalParameterFacade;
    @EJB
    private AuditoryActionFacade auditoryActionFacade;
    @EJB
    private ScreenFacade screenFacade;
    @EJB
    private AuditoryFacade auditoryFacade;
    @EJB
    private UserwebFacade userwebFacade;
    @EJB
    private ClassificationFacade classificationFacade;
    @EJB
    private UssdUserFacade ussdUserFacade;
    @EJB
    private CellFacade cellFacade;
    @EJB
    private ApplicationFacade applicationFacade;
    @EJB
    private ClientFileFacade clientFileFacade;
    @EJB
    private ClientParameterValueFacade clientParameterValueFacade;
    @EJB
    private RoleClientFacade roleClientFacade;
    @EJB
    private RoleClientScreenFacade roleClientScreenFacade;
    @EJB
    private UssdMenuEntryUssdUserFacade ussdMenuEntryUssdUserFacade;
    @EJB
    private UseradminFacade useradminFacade;
    @EJB
    private UserphoneFacade userphoneFacade;
    @EJB
    private RoleadminFacade roleadminFacade;
    @EJB
    private ClientServiceFunctionalityFacade clientServiceFunctionalityFacade;
    @EJB
    private ServiceFunctionalityFacade serviceFunctionalityFacade;
    @EJB
    private TrackingConfigurationFacade trackingConfigurationFacade;
    @EJB
    private TrackingPeriodFacade trackingPeriodFacade;
    @EJB
    private UssdMenuEntryFacade ussdMenuEntryFacade;
    @EJB
    private UssdApplicationFacade ussdApplicationFacade;
    @EJB
    private PhoneFacade phoneFacade;
    @EJB
    private PhoneListFacade phoneListFacade;
    @EJB
    private FeatureElementFacade featureElementFacade;
    @EJB
    private FeatureElementEntryFacade featureElementEntryFacade;
    @EJB
    private FeatureFacade featureFacade;
    @EJB
    private FunctionalityFacade functionalityFacade;
    @EJB
    private MetaFacade metaFacade;
    @EJB
    private MetaMemberFacade metaMemberFacade;
    @EJB
    private MetaClientFacade metaClientFacade;
    @EJB
    private ReportConfigFacade reportConfigFacade;
    @EJB
    private ReportConfigMailFacade reportConfigMailFacade;
    @EJB
    private MapMarkFacade mapMarkFacade;
    @EJB
    private ClientFacade clientFacade;
    @EJB
    private ClientFeatureFacade clientFeatureFacade;
    @EJB
    private FeatureEntryTypeFacade featureEntryTypeFacade;
    @EJB
    private FeatureValueFacade featureValueFacade;
    @EJB
    private FeatureValueDataFacade featureValueDataFacade;
    @EJB
    private RouteFacade routeFacade;
    @EJB
    private RouteTypeFacade routeTypeFacade;
    @EJB
    private RouteDetailFacade routeDetailFacade;
    @EJB
    private ShiftPeriodFacade shiftPeriodFacade;
    @EJB
    private RouteUserphoneFacade routeUserphoneFacade;
    @EJB
    private MetaDataFacade metaDataFacade;
    @EJB
    private ShiftConfigurationFacade shiftConfigurationFacade;
    @EJB
    private MessageFacade messageFacade;
    @EJB
    private UserphoneMetaFacade userphoneMetaFacade;
    @EJB
    private ClientGoalFacade clientGoalFacade;
    @EJB
    private ServiceOperationFacade serviceOperationFacade;
    @EJB
    protected LbsGwServiceFacade lbsGwServiceFacade;
    @EJB
    protected DataActivityFacade dataActivityFacade;
    @EJB
    protected DataStatusFacade dataStatusFacade;
    @EJB
    protected DataZoneFacade dataZoneFacade;
    @EJB
    protected ScreenClientFacade screenClientFacade;
    @EJB
    protected OtFacade otFacade;
    @EJB
    protected TicketCSIFacade ticketCsiFacade;
    @EJB
    protected WnOperadoraFacade wnOperadoraFacade;
    @EJB
    protected IpsServiceFacade ipsServiceFacade;
    @EJB
    protected MenuMovilFacade menuMovilFacade;
    @EJB
    protected MenuMovilPeriodFacade menuMovilPeriodFacade;
    @EJB
    protected MenuMovilUserFacade menuMovilUserFacade;
    @EJB
    protected NCenterWSFacade nCenterWSFacade;
    @EJB
    protected DataAttendantFacade dataAttendantFacade;
    @EJB
    protected InterfisaInformconfServiceFacade informconfServiceFacade;
    @EJB
    protected IconTypeFacade iconTypeFacade;
    @EJB
    protected DataIconFacade dataIconFacade;
    @EJB
    protected TrackingOnDemandFacade trackingOnDemandFacade;
    @EJB
    protected UssdDriverFacade ussdDriverFacade;
    @EJB
    protected AndroidVersionFacade androidVersionFacade;
    @EJB
    protected SubscriberFacade subscriberFacade;
    @EJB
    protected MTSServiceFacade mtsServiceFacade;
    @EJB
    protected ValidacionClienteBCCSNacServiceFacade validacionClienteBCCSNacServiceFacade;
    @EJB
    protected TigoMoneyServiceFacade tigoMoneyServiceFacade;
    @EJB
    protected SMSQueueFacade smsQueueFacade;

    @EJB
    protected ValidationClientCIFacade validationClientCIFacade;
    @EJB
    protected RetrievePoliceBaseInfoFacade retrievePoliceBaseInfoFacade;
    @EJB
    protected GetClientBirthdayFacade getClientBirthdayFacade;
    @EJB
    protected TrackQueueFacade trackQueueFacade;
    // @EJB
    protected FileMetaDataQueueFacade fileMetaDataQueueFacade;
    // @EJB
    protected MetaDataQueueFacade metaDataQueueFacade;
    @EJB
    protected FileMetaDataFacade fileMetaDataFacade;
    @EJB
    protected DataHoraryFacade dataHoraryFacade;
    @EJB
    protected UssdServiceCorrespFacade ussdServiceCorrespFacade;

    @PersistenceContext(unitName = "csTigoPU")
    private EntityManager em;

    public EJBFacadeContainer() {
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public boolean isCutMessage() {
        return true;
    }

    @Override
    public boolean isSendSMSViaWS() {
        return true;
    }

    @Override
    public boolean isEntityManagerTransactional() {
        return false;
    }

    @Override
    public ServiceValueAPI getServiceValueAPI() {
        return svFacade;
    }

    @Override
    public ServiceValueDetailAPI getServiceValueDetailAPI() {
        return svdFacade;
    }

    @Override
    public NotifierAPI getNotifier() {
        return notifier;
    }

    @Override
    public I18nAPI getI18nAPI() {
        return i18nBean;
    }

    @Override
    public GlobalParameterAPI getGlobalParameterAPI() {
        return globalParameterFacade;
    }

    @Override
    public AuditoryActionAPI getAuditoryActionAPI() {
        return auditoryActionFacade;
    }

    @Override
    public ScreenAPI getScreenAPI() {
        return screenFacade;
    }

    @Override
    public AuditoryAPI getAuditoryAPI() {
        return auditoryFacade;
    }

    @Override
    public UserwebAPI getUserwebAPI() {
        return userwebFacade;
    }

    @Override
    public ClassificationAPI getClassificationAPI() {
        return classificationFacade;
    }

    @Override
    public UssdUserAPI getUssdUserAPI() {
        return ussdUserFacade;
    }

    @Override
    public CellAPI getCellAPI() {
        return cellFacade;
    }

    @Override
    public ApplicationAPI getApplicationAPI() {
        return applicationFacade;
    }

    @Override
    public ClientParameterValueAPI getClientParameterValueAPI() {
        return clientParameterValueFacade;
    }

    @Override
    public ClientFileAPI getClientFileAPI() {
        return clientFileFacade;
    }

    @Override
    public RoleClientAPI getRoleClientAPI() {
        return roleClientFacade;
    }

    @Override
    public RoleClientScreenAPI getRoleClientScreenAPI() {
        return roleClientScreenFacade;
    }

    @Override
    public UssdMenuEntryUssdUserAPI getUssdMenuEntryUssdUserAPI() {
        return ussdMenuEntryUssdUserFacade;
    }

    @Override
    public UseradminAPI getUseradminAPI() {
        return useradminFacade;
    }

    @Override
    public UserphoneAPI getUserphoneAPI() {
        return userphoneFacade;
    }

    @Override
    public RoleadminAPI getRoleadminAPI() {
        return roleadminFacade;
    }

    @Override
    public ClientServiceFunctionalityAPI getClientServiceFunctionalityAPI() {
        return clientServiceFunctionalityFacade;
    }

    @Override
    public ServiceFunctionalityAPI getServiceFunctionalityAPI() {
        return serviceFunctionalityFacade;
    }

    @Override
    public TrackingConfigurationAPI getTrackingConfigurationAPI() {
        return trackingConfigurationFacade;
    }

    @Override
    public TrackingPeriodAPI getTrackingPeriodAPI() {
        return trackingPeriodFacade;
    }

    @Override
    public UssdMenuEntryAPI getUssdMenuEntryAPI() {
        return ussdMenuEntryFacade;
    }

    @Override
    public UssdApplicationAPI getUssdApplicationAPI() {
        return ussdApplicationFacade;
    }

    @Override
    public PhoneAPI getPhoneAPI() {
        return phoneFacade;
    }

    @Override
    public ClientAPI getClientAPI() {
        return clientFacade;
    }

    @Override
    public PhoneListAPI getPhoneListAPI() {
        return phoneListFacade;
    }

    @Override
    public FeatureElementAPI getFeatureElementAPI() {
        return featureElementFacade;
    }

    @Override
    public FeatureElementEntryAPI getFeatureElementEntryAPI() {
        return featureElementEntryFacade;
    }

    @Override
    public FeatureAPI getFeatureAPI() {
        return featureFacade;
    }

    @Override
    public FunctionalityAPI getFunctionalityAPI() {
        return functionalityFacade;
    }

    @Override
    public MetaAPI getMetaAPI() {
        return metaFacade;
    }

    @Override
    public MetaClientAPI getMetaClientAPI() {
        return metaClientFacade;
    }

    @Override
    public ReportConfigAPI getReportConfigAPI() {
        return reportConfigFacade;
    }

    @Override
    public ReportConfigMailAPI getReportConfigMailAPI() {
        return reportConfigMailFacade;
    }

    @Override
    public MapMarkAPI getMapMarkAPI() {
        return mapMarkFacade;
    }

    @Override
    public RouteAPI getRouteAPI() {
        return routeFacade;
    }

    @Override
    public RouteDetailAPI getRouteDetailAPI() {
        return routeDetailFacade;
    }

    @Override
    public RouteTypeAPI getRouteTypeAPI() {
        routeTypeFacade.setFacadeContainer(this);
        return routeTypeFacade;
    }

    @Override
    public RouteUserphoneAPI getRouteUserphoneAPI() {
        return routeUserphoneFacade;
    }

    @Override
    public ShiftPeriodAPI getShiftPeriodAPI() {
        return shiftPeriodFacade;
    }

    @Override
    public MetaDataAPI getMetaDataAPI() {
        return metaDataFacade;
    }

    @Override
    public ShiftConfigurationAPI getShiftConfigurationAPI() {
        return shiftConfigurationFacade;
    }

    @Override
    public MessageAPI getMessageAPI() {
        return messageFacade;
    }

    @Override
    public ServiceAPI getServiceAPI() {
        return serviceFacade;
    }

    @Override
    public RouteDetailUserphoneAPI getRouteDetailUserphoneAPI() {
        return routeDetailUserphoneFacade;
    }

    @Override
    public SMSTransmitterServiceAPI getSmsTransmitterServiceAPI() {
        return smsTransmitterServiceFacade;
    }

    @Override
    public MetaMemberAPI getMetaMemberAPI() {
        return metaMemberFacade;
    }

    @Override
    public InvalidmessageAPI getInvalidmessageAPI() {
        return null;
    }

    @Override
    public MessageNotSentAPI getMessageNotSentAPI() {
        return null;
    }

    @Override
    public ClientFeatureAPI getClientFeatureAPI() {
        return clientFeatureFacade;
    }

    @Override
    public FeatureEntryTypeAPI getFeatureEntryTypeAPI() {
        return featureEntryTypeFacade;
    }

    @Override
    public UserphoneMetaAPI getUserphoneMetaAPI() {
        return userphoneMetaFacade;
    }

    @Override
    public FeatureValueAPI getFeatureValueAPI() {
        return featureValueFacade;
    }

    @Override
    public FeatureValueDataAPI getFeatureValueDataAPI() {
        return featureValueDataFacade;
    }

    @Override
    public ClientGoalAPI getClientGoalAPI() {
        return clientGoalFacade;
    }

    @Override
    public ServiceOperationAPI getServiceOperationAPI() {
        return serviceOperationFacade;
    }

    @Override
    public LbsGwServiceAPI getLbsGwServiceAPI() {
        return lbsGwServiceFacade;
    }

    @Override
    public DataActivityAPI getDataActivityAPI() {
        return dataActivityFacade;
    }

    @Override
    public DataStatusAPI getDataStatusAPI() {
        return dataStatusFacade;
    }

    @Override
    public DataZoneAPI getDataZoneAPI() {
        return dataZoneFacade;
    }

    @Override
    public ScreenClientAPI getScreenClientAPI() {
        return screenClientFacade;
    }

    @Override
    public OtAPI getOtAPI() {
        return otFacade;
    }

    @Override
    public TicketCSIAPI getTicketCSIAPI() {
        return ticketCsiFacade;
    };

    @Override
    public WnOperadoraAPI getWnOperadoraAPI() {
        return wnOperadoraFacade;
    }

    @Override
    public NCenterWSAPI getNCenterWSAPI() {
        return null;
    };

    @Override
    public IpsServiceAPI getIpsServiceAPI() {
        return ipsServiceFacade;
    }

    @Override
    public MenuMovilUserAPI getMenuMovilUserAPI() {
        // TODO Auto-generated method stub
        return menuMovilUserFacade;
    }

    @Override
    public MenuMovilPeriodAPI getMenuMovilPeriodAPI() {
        // TODO Auto-generated method stub
        return menuMovilPeriodFacade;
    }

    @Override
    public MenuMovilAPI getMenuMovilAPI() {
        // TODO Auto-generated method stub
        return menuMovilFacade;
    }

    @Override
    public DataAttendantAPI getDataAttendantAPI() {
        // TODO Auto-generated method stub
        return dataAttendantFacade;
    }

    @Override
    public CacheAPI getCacheAPI() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InterfisaInformconfServiceAPI getInterfisaInformconfServiceAPI() {
        return informconfServiceFacade;
    };

    @Override
    public UssdDriverAPI getUssdDriverAPI() {
        return ussdDriverFacade;
    }

    @Override
    public AndroidVersionAPI getAndroidVersionAPI() {
        return androidVersionFacade;
    };

    @Override
    public IconTypeAPI getIconTypeAPI() {
        return iconTypeFacade;
    }

    @Override
    public DataIconAPI getDataIconAPI() {
        return dataIconFacade;
    }

    @Override
    public TrackingOnDemandAPI getTrackingOnDemandAPI() {
        return trackingOnDemandFacade;
    }

    @Override
    public EntityManager getEntityManagerMTS() {

        return null;
    }

    @Override
    public SubscriberAPI getSubscriberAPI() {
        return subscriberFacade;
    }

    @Override
    public MTSServiceAPI getMtsServiceAPI() {
        return mtsServiceFacade;
    }

    @Override
    public ValidacionClienteBCCSNacServiceAPI getValidacionClienteBCCSNacServiceAPI() {
        return validacionClienteBCCSNacServiceFacade;
    }

    @Override
    public TigoMoneyWSServiceAPI getTigoMoneyWSServiceAPI() {
        return tigoMoneyServiceFacade;
    }

    @Override
    public WsClientAPI getWsClientAPI() {
        return null;
    }

    @Override
    public SMSQueueAPI getSmsQueueAPI() {
        return smsQueueFacade;
    }

    @Override
    public RetrievePoliceBaseInfoAPI getRetrievePoliceBaseInfoAPI() {
        return retrievePoliceBaseInfoFacade;
    }

    @Override
    public GetClientBirthdayAPI getGetClientBirthdayAPI() {
        return getClientBirthdayFacade;
    }

    @Override
    public ValidationClientCIAPI getValidationClientCIAPI() {
        return validationClientCIFacade;
    }

    @Override
    public TrackQueueAPI getTrackQueueAPI() {
        return trackQueueFacade;
    }

    @Override
    public FileMetaDataQueueAPI getFileMetaDataQueueAPI() {
        return fileMetaDataQueueFacade;
    }

    @Override
    public MetaDataQueueAPI getMetaDataQueueAPI() {
        return metaDataQueueFacade;
    };

    @Override
    public FileMetaDataAPI getFileMetaDataAPI() {
        return fileMetaDataFacade;
    }

    @Override
    public DataHoraryAPI getDataHoraryAPI() {

        return dataHoraryFacade;
    }

    @Override
    public UssdServiceCorrespAPI getUssdServiceCorrespAPI() {
        // TODO Auto-generated method stub
        return ussdServiceCorrespFacade;
    }

}
