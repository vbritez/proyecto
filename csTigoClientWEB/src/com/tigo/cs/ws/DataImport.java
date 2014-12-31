package com.tigo.cs.ws;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.ws.data.ArpCabData;
import com.tigo.cs.ws.data.ArpResponse;
import com.tigo.cs.ws.data.AsistenciaCabData;
import com.tigo.cs.ws.data.AsistenciaResponse;
import com.tigo.cs.ws.data.CobranzaCabData;
import com.tigo.cs.ws.data.CobranzaResponse;
import com.tigo.cs.ws.data.CourierCab;
import com.tigo.cs.ws.data.CourierResponse;
import com.tigo.cs.ws.data.DeliveryCabData;
import com.tigo.cs.ws.data.DeliveryResponse;
import com.tigo.cs.ws.data.FlotaCabData;
import com.tigo.cs.ws.data.FlotaResponse;
import com.tigo.cs.ws.data.FormularioDinamicoCab;
import com.tigo.cs.ws.data.FormularioDinamicoResponse;
import com.tigo.cs.ws.data.GuardiaCabData;
import com.tigo.cs.ws.data.GuardiaResponse;
import com.tigo.cs.ws.data.InformconfData;
import com.tigo.cs.ws.data.InformconfResponse;
import com.tigo.cs.ws.data.InventarioDAData;
import com.tigo.cs.ws.data.InventarioDAResponse;
import com.tigo.cs.ws.data.InventarioSTDResponse;
import com.tigo.cs.ws.data.InventarioStdData;
import com.tigo.cs.ws.data.OTCab;
import com.tigo.cs.ws.data.OTResponse;
import com.tigo.cs.ws.data.PedidoCab;
import com.tigo.cs.ws.data.PedidoResponse;
import com.tigo.cs.ws.data.RondaGuardiaCabData;
import com.tigo.cs.ws.data.RondaGuardiaResponse;
import com.tigo.cs.ws.data.TrackingData;
import com.tigo.cs.ws.data.TrackingResponse;
import com.tigo.cs.ws.data.VisitaData;
import com.tigo.cs.ws.data.VisitaPedidoResponse;
import com.tigo.cs.ws.data.VisitaResponse;
import com.tigo.cs.ws.data.VisitaVPData;
import com.tigo.cs.ws.data.VisitadorMedicoCabData;
import com.tigo.cs.ws.data.VisitadorMedicoResponse;
import com.tigo.cs.ws.facade.DataMigration;
import com.tigo.cs.ws.facade.DataMigration.WSOperation;

/**
 * 
 * @author Miguel Zorrilla
 */
@WebService()
public class DataImport {
    @EJB
    private DataMigration ejbRef;
    @EJB
    private FacadeContainer facadeContainer;
    
    @WebMethod(operationName = "importVisitas")
    public String importVisitas(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importVisitas(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importVisitasComplex")
    public VisitaResponse importVisitasComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importVisitasComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            VisitaResponse resp = new VisitaResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((VisitaData[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importPedidos")
    public String importPedidos(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importPedidos(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importPedidosComplex")
    public PedidoResponse importPedidosComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importPedidosComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            PedidoResponse resp = new PedidoResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((PedidoCab[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importPedidosIlimitados")
    public String importPedidosIlimitados(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importPedidosIlimitados(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importPedidosIlimitadosComplex")
    public PedidoResponse importPedidosIlimitadosComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importPedidosIlimitadosComplex(id, user, password,
                    limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            PedidoResponse resp = new PedidoResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((PedidoCab[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importVisitaPedidos")
    public String importVisitaPedidos(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importVisitaPedidos(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importVisitaPedidosComplex")
    public VisitaPedidoResponse importVisitaPedidosComplex(@WebParam(
            name = "id") long id, @WebParam(name = "user") String user, @WebParam(
            name = "password") String password, @WebParam(name = "limit") int limit) {
        try {
            return ejbRef.importVisitaPedidosComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            VisitaPedidoResponse resp = new VisitaPedidoResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((VisitaVPData[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importGuardias")
    public String importGuardias(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importGuardias(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importGuardiasComplex")
    public GuardiaResponse importGuardiasComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importGuardiasComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            GuardiaResponse resp = new GuardiaResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((GuardiaCabData[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importCobranzas")
    public String importCobranzas(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importCobranzas(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importCobranzasComplex")
    public CobranzaResponse importCobranzasComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importCobranzasComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            CobranzaResponse resp = new CobranzaResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((CobranzaCabData[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importDeliveries")
    public String importDeliveries(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importDeliveries(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importDeliveriesComplex")
    public DeliveryResponse importDeliveriesComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importDeliveriesComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            DeliveryResponse resp = new DeliveryResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((DeliveryCabData[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importInventarioDA")
    public String importInventarioDA(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importInventarioDA(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importInventarioDAComplex")
    public InventarioDAResponse importInventarioDAComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importInventarioDAComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            InventarioDAResponse resp = new InventarioDAResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((InventarioDAData[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importArp")
    public String importArp(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importArp(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importArpComplex")
    public ArpResponse importArpComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importArpComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            ArpResponse resp = new ArpResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((ArpCabData[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importInventarioEstandar")
    public String importInventarioEstandar(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importInventarioEstandar(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importInventarioEstandarComplex")
    public InventarioSTDResponse importInventarioEstandarComplex(@WebParam(
            name = "id") long id, @WebParam(name = "user") String user, @WebParam(
            name = "password") String password, @WebParam(name = "limit") int limit) {
        try {
            return ejbRef.importInventarioEstandarComplex(id, user, password,
                    limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            InventarioSTDResponse resp = new InventarioSTDResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((InventarioStdData[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importAsistencias")
    public String importAsistencias(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importAsistencias(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importAsistenciasComplex")
    public AsistenciaResponse importAsistenciasComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importAsistenciasComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            AsistenciaResponse resp = new AsistenciaResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((AsistenciaCabData[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importFlotas")
    public String importFlotas(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importFlotas(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importFlotasComplex")
    public FlotaResponse importFlotasComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importFlotasComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            FlotaResponse resp = new FlotaResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((FlotaCabData[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importInformconf")
    public String importInformconf(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importInformconf(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importInformconfComplex")
    public InformconfResponse importInformconfComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importInformconfComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            InformconfResponse resp = new InformconfResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((InformconfData[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importTracking")
    public String importTracking(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importTracking(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importTrackingComplex")
    public TrackingResponse importTrackingComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importTrackingComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            TrackingResponse resp = new TrackingResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((TrackingData[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "sendMessage")
    public String sendMessage(@WebParam(name = "user") String user, @WebParam(
            name = "password") String password, @WebParam(name = "mobileNum") String mobileNum, @WebParam(
            name = "message") String message) {
        try {
            return ejbRef.sendMessage(user, password, mobileNum, message);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importRondasGuardias")
    public String importRondasGuardias(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importRondasGuardias(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importRondasGuardiasComplex")
    public RondaGuardiaResponse importRondasGuardiasComplex(@WebParam(
            name = "id") long id, @WebParam(name = "user") String user, @WebParam(
            name = "password") String password, @WebParam(name = "limit") int limit) {
        try {
            return ejbRef.importRondasGuardiasComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            RondaGuardiaResponse resp = new RondaGuardiaResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((RondaGuardiaCabData[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importVisitadoresMedicos")
    public String importVisitadoresMedicos(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importVisitadoresMedicos(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importVisitadoresMedicosComplex")
    public VisitadorMedicoResponse importVisitadoresMedicosComplex(@WebParam(
            name = "id") long id, @WebParam(name = "user") String user, @WebParam(
            name = "password") String password, @WebParam(name = "limit") int limit) {
        try {
            return ejbRef.importVisitadoresMedicosComplex(id, user, password,
                    limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            VisitadorMedicoResponse resp = new VisitadorMedicoResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((VisitadorMedicoCabData[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importCourier")
    public String importCourier(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importCourier(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importCourierComplex")
    public CourierResponse importCourierComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importCourierComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            CourierResponse resp = new CourierResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((CourierCab[]) null);
            return resp;
        }
    }

    @WebMethod(operationName = "importOT")
    public String importOT(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importOT(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importOTComplex")
    public OTResponse importOTComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importOTComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            OTResponse resp = new OTResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((OTCab[]) null);
            return resp;
        }
    }
    
    @WebMethod(operationName = "importPrestacion")
    public String importPrestacion(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importFeature(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importPrestacionComplex")
    public FormularioDinamicoResponse importPrestacionComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importFeatureComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            FormularioDinamicoResponse resp = new FormularioDinamicoResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((FormularioDinamicoCab[]) null);
            return resp;
        }
    }
    
    @WebMethod(operationName = "importFormularioDinamico")
    public String importFormularioDinamico(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importDynamicForm(id, user, password, limit);
        } catch (Exception e) {
            return ejbRef.verifyException(user, e);
        }
    }

    @WebMethod(operationName = "importFormularioDinamicoComplex")
    public FormularioDinamicoResponse importFormularioDinamicoComplex(@WebParam(name = "id") long id, @WebParam(
            name = "user") String user, @WebParam(name = "password") String password, @WebParam(
            name = "limit") int limit) {
        try {
            return ejbRef.importDynamicFormComplex(id, user, password, limit);
        } catch (Exception e) {
            String response = ejbRef.verifyException(user, e);

            FormularioDinamicoResponse resp = new FormularioDinamicoResponse();

            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(response);
            resp.setResults((FormularioDinamicoCab[]) null);
            return resp;
        }
    }
    

}
