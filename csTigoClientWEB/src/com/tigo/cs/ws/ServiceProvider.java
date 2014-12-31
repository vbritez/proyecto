package com.tigo.cs.ws;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.tigo.cs.api.entities.AttendanceService;
import com.tigo.cs.api.entities.BaseServiceBean;
import com.tigo.cs.api.entities.CollectionService;
import com.tigo.cs.api.entities.CourrierService;
import com.tigo.cs.api.entities.DynamicFormService;
import com.tigo.cs.api.entities.InterfisaService;
import com.tigo.cs.api.entities.InventoryService;
import com.tigo.cs.api.entities.MetadataCrudService;
import com.tigo.cs.api.entities.OrderService;
import com.tigo.cs.api.entities.PermissionService;
import com.tigo.cs.api.entities.ServiceOperationService;
import com.tigo.cs.api.entities.ShiftGuardService;
import com.tigo.cs.api.entities.SupervisorService;
import com.tigo.cs.api.entities.TerportService;
import com.tigo.cs.api.entities.TigoMoneyService;
import com.tigo.cs.api.entities.TrackingService;
import com.tigo.cs.api.entities.VisitMedicService;
import com.tigo.cs.api.entities.VisitService;
import com.tigo.cs.facade.rest.AttendanceProcessor;
import com.tigo.cs.facade.rest.CollectionProcessor;
import com.tigo.cs.facade.rest.CourrierProcessor;
import com.tigo.cs.facade.rest.DynamicFormProcessor;
import com.tigo.cs.facade.rest.InterfisaProcessor;
import com.tigo.cs.facade.rest.InventoryProcessor;
import com.tigo.cs.facade.rest.MetaQueryProcessor;
import com.tigo.cs.facade.rest.OrderProcessor;
import com.tigo.cs.facade.rest.PermissionProcessor;
import com.tigo.cs.facade.rest.ServiceOperationProcessor;
import com.tigo.cs.facade.rest.ShiftGuardProcessor;
import com.tigo.cs.facade.rest.SupervisorProcessor;
import com.tigo.cs.facade.rest.TerportProcessor;
import com.tigo.cs.facade.rest.TigoMoneyProcessor;
import com.tigo.cs.facade.rest.TrackingProcessor;
import com.tigo.cs.facade.rest.VisitMedicProcessor;
import com.tigo.cs.facade.rest.VisitProcessor;
import com.tigo.cs.security.Notifier;

@Stateless
@Path("/serviceProvider")
public class ServiceProvider {

    @EJB
    private Notifier notifier;
    @EJB
    private VisitProcessor visitProcessor;
    @EJB
    private OrderProcessor orderProcessor;
    @EJB
    private TrackingProcessor trackingProcessor;
    @EJB
    private ShiftGuardProcessor shiftGuardProcessor;
    @EJB
    private VisitMedicProcessor visitMedicProcessor;
    @EJB
    private CourrierProcessor courrierProcessor;
    @EJB
    private PermissionProcessor permissionProcessor;
    @EJB
    private MetaQueryProcessor metaQueryProcessor;
    @EJB
    private TerportProcessor terportProcessor;
    @EJB
    private InventoryProcessor inventoryProcessor;
    @EJB
    private CollectionProcessor collectionProcessor;
    @EJB
    private InterfisaProcessor interfisaProcessor;
    @EJB
    private AttendanceProcessor attendanceProcessor;
    @EJB
    private DynamicFormProcessor dynamicFormProcessor;
    @EJB
    private SupervisorProcessor supervisorProcessor;
    @EJB
    private ServiceOperationProcessor serviceOperationProcessor;
    @EJB
    private TigoMoneyProcessor tigoMoneyProcessor;

    public ServiceProvider() {
    }

    private void verifyHash(BaseServiceBean entity) {
        if (entity.getHash() == null) {
            entity.setHash("");
        }
        entity.setApplicationKey("r357w5");
    }

    @POST
    @Path("/visit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public VisitService visit(VisitService entity) {

        verifyHash(entity);
        visitProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(),
                visitProcessor.getReturnEntity().getResponse());
        return visitProcessor.getReturnEntity();
    }

    @POST
    @Path("/order")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OrderService order(OrderService entity) {

        verifyHash(entity);
        orderProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(),
                orderProcessor.getReturnEntity().getResponse());
        return orderProcessor.getReturnEntity();
    }

    @POST
    @Path("/collection")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CollectionService collection(CollectionService entity) {

        verifyHash(entity);
        collectionProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(),
                collectionProcessor.getReturnEntity().getResponse());
        return collectionProcessor.getReturnEntity();
    }

    @POST
    @Path("/track")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TrackingService track(TrackingService entity) {

        verifyHash(entity);
        entity.setSendSMS(false);
        trackingProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(),
                trackingProcessor.getReturnEntity().getResponse());
        return trackingProcessor.getReturnEntity();
    }

    @POST
    @Path("/shiftguard")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ShiftGuardService shiftGuard(ShiftGuardService entity) {

        verifyHash(entity);
        shiftGuardProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(),
                shiftGuardProcessor.getReturnEntity().getResponse());
        return shiftGuardProcessor.getReturnEntity();
    }

    @POST
    @Path("/visitmedic")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public VisitMedicService visitMedic(VisitMedicService entity) {

        verifyHash(entity);
        visitMedicProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(),
                visitMedicProcessor.getReturnEntity().getResponse());
        return visitMedicProcessor.getReturnEntity();
    }

    @POST
    @Path("/courrier")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CourrierService courrier(CourrierService entity) {

        verifyHash(entity);
        courrierProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(),
                courrierProcessor.getReturnEntity().getResponse());
        return courrierProcessor.getReturnEntity();
    }

    @POST
    @Path("/permission")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PermissionService permission(PermissionService entity) {

        verifyHash(entity);
        permissionProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(),
                permissionProcessor.getReturnEntity().getResponse());
        return permissionProcessor.getReturnEntity();
    }

    @POST
    @Path("/metadata")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataCrudService metaQuery(MetadataCrudService entity) {

        verifyHash(entity);
        metaQueryProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(),
                metaQueryProcessor.getReturnEntity().getResponse());
        return metaQueryProcessor.getReturnEntity();
    }

    @POST
    @Path("/terport")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String terport(TerportService entity) {

        verifyHash(entity);
        String returnMesage = terportProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(), returnMesage);
        return returnMesage;
    }

    @POST
    @Path("/metadata/findAll")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String metaAll(MetadataCrudService entity) {

        verifyHash(entity);
        String returnMesage = metaQueryProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(), returnMesage);
        return returnMesage;
    }

    @POST
    @Path("/inventory")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public InventoryService inventory(InventoryService entity) {

        verifyHash(entity);
        inventoryProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(),
                inventoryProcessor.getReturnEntity().getResponse());
        return inventoryProcessor.getReturnEntity();
    }

    @POST
    @Path("/interfisa")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String interfisa(InterfisaService entity) {

        verifyHash(entity);
        String returnMesage = interfisaProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(), returnMesage);
        return returnMesage;
    }

    @POST
    @Path("/dynamicform")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DynamicFormService dynamicForm(DynamicFormService entity) {

        verifyHash(entity);
        dynamicFormProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(),
                dynamicFormProcessor.getReturnEntity().getResponse());
        return dynamicFormProcessor.getReturnEntity();
    }

    @POST
    @Path("/attendance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AttendanceService attendance(AttendanceService entity) {

        verifyHash(entity);
        attendanceProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(),
                attendanceProcessor.getReturnEntity().getResponse());
        return attendanceProcessor.getReturnEntity();
    }

    @POST
    @Path("/supervisor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SupervisorService supervisor(SupervisorService entity) {

        verifyHash(entity);
        entity.setSendSMS(false);
        supervisorProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(),
                supervisorProcessor.getReturnEntity().getResponse());
        return supervisorProcessor.getReturnEntity();
    }

    @POST
    @Path("/serviceoperation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ServiceOperationService serviceOperation(ServiceOperationService entity) {

        verifyHash(entity);
        entity.setSendSMS(false);
        serviceOperationProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(),
                serviceOperationProcessor.getReturnEntity().getResponse());
        return serviceOperationProcessor.getReturnEntity();
    }

    @POST
    @Path("/tigomoney")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TigoMoneyService tigomoney(TigoMoneyService entity) {

        verifyHash(entity);
        entity.setSendSMS(false);
        tigoMoneyProcessor.execute(entity);
        notifier.info(ServiceProvider.class, entity.getMsisdn(),
                tigoMoneyProcessor.getReturnEntity().getResponse());
        return tigoMoneyProcessor.getReturnEntity();
    }
}