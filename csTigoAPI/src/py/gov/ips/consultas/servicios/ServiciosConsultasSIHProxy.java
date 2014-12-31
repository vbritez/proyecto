package py.gov.ips.consultas.servicios;

public class ServiciosConsultasSIHProxy implements py.gov.ips.consultas.servicios.ServiciosConsultasSIH {
  private String _endpoint = null;
  private py.gov.ips.consultas.servicios.ServiciosConsultasSIH serviciosConsultasSIH = null;
  
  public ServiciosConsultasSIHProxy() {
    _initServiciosConsultasSIHProxy();
  }
  
  public ServiciosConsultasSIHProxy(String endpoint) {
    _endpoint = endpoint;
    _initServiciosConsultasSIHProxy();
  }
  
  private void _initServiciosConsultasSIHProxy() {
    try {
      serviciosConsultasSIH = (new py.gov.ips.consultas.servicios.ServiciosConsultasSIHServiceLocator()).getServiciosConsultasSIHPort();
      if (serviciosConsultasSIH != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)serviciosConsultasSIH)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)serviciosConsultasSIH)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (serviciosConsultasSIH != null)
      ((javax.xml.rpc.Stub)serviciosConsultasSIH)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public py.gov.ips.consultas.servicios.ServiciosConsultasSIH getServiciosConsultasSIH() {
    if (serviciosConsultasSIH == null)
      _initServiciosConsultasSIHProxy();
    return serviciosConsultasSIH;
  }
  
  public py.gov.ips.consultas.servicios.RetornoCancelacionesMedicas[] buscarCitasPorCi(java.lang.String nroCi, java.lang.String nroCelular, java.lang.String entidad, java.lang.Long token, java.lang.String usuario, java.lang.String password) throws java.rmi.RemoteException{
    if (serviciosConsultasSIH == null)
      _initServiciosConsultasSIHProxy();
    return serviciosConsultasSIH.buscarCitasPorCi(nroCi, nroCelular, entidad, token, usuario, password);
  }
  
  public java.lang.String cancelarCitas(java.lang.String nroCi, java.lang.String nroCelular, java.lang.Long token, java.lang.Integer codEmpresa, java.lang.Long nroAgend, java.lang.Integer nroRegAmb, java.lang.String entidad, java.lang.String usuario, java.lang.String password) throws java.rmi.RemoteException{
    if (serviciosConsultasSIH == null)
      _initServiciosConsultasSIHProxy();
    return serviciosConsultasSIH.cancelarCitas(nroCi, nroCelular, token, codEmpresa, nroAgend, nroRegAmb, entidad, usuario, password);
  }
  
  public java.lang.String confirmarTurno(java.lang.String nroCi, java.lang.String nroCelular, java.lang.String entidad, java.lang.Long token, java.lang.Integer empresa, java.lang.Integer codEspecMedic, java.lang.String codMedicoTurnoFecha, java.lang.String usuario, java.lang.String password) throws java.rmi.RemoteException{
    if (serviciosConsultasSIH == null)
      _initServiciosConsultasSIHProxy();
    return serviciosConsultasSIH.confirmarTurno(nroCi, nroCelular, entidad, token, empresa, codEspecMedic, codMedicoTurnoFecha, usuario, password);
  }
  
  public py.gov.ips.consultas.servicios.RetornoAgendamientoMedico consultaCentroMedico(java.lang.String nroCi, java.lang.String nroCelular, java.lang.String entidad, java.lang.Long token, java.lang.Short codEspecMd, java.lang.String usuario, java.lang.String password) throws java.rmi.RemoteException{
    if (serviciosConsultasSIH == null)
      _initServiciosConsultasSIHProxy();
    return serviciosConsultasSIH.consultaCentroMedico(nroCi, nroCelular, entidad, token, codEspecMd, usuario, password);
  }
  
  public py.gov.ips.consultas.servicios.RetornoAgendamientoMedico consultaEspecialidades(java.lang.String nroCi, java.lang.String nroCelular, java.lang.String entidad, java.lang.Long token, java.lang.String usuario, java.lang.String password) throws java.rmi.RemoteException{
    if (serviciosConsultasSIH == null)
      _initServiciosConsultasSIHProxy();
    return serviciosConsultasSIH.consultaEspecialidades(nroCi, nroCelular, entidad, token, usuario, password);
  }
  
  public py.gov.ips.consultas.servicios.RetornoAgendamientoMedico consultaMedicos(java.lang.String nroCi, java.lang.String nroCelular, java.lang.String entidad, java.lang.Long token, java.lang.Integer empresa, java.lang.Integer codEspecMedic, java.lang.String usuario, java.lang.String password, java.lang.String turno) throws java.rmi.RemoteException{
    if (serviciosConsultasSIH == null)
      _initServiciosConsultasSIHProxy();
    return serviciosConsultasSIH.consultaMedicos(nroCi, nroCelular, entidad, token, empresa, codEspecMedic, usuario, password, turno);
  }
  
  public py.gov.ips.consultas.servicios.RetornoAgendamientoMedico consultaMedicosX30Dias(java.lang.String nroCi, java.lang.String nroCelular, java.lang.String entidad, java.lang.Long token, java.lang.Integer empresa, java.lang.Integer codEspecMedic, java.lang.String usuario, java.lang.String password) throws java.rmi.RemoteException{
    if (serviciosConsultasSIH == null)
      _initServiciosConsultasSIHProxy();
    return serviciosConsultasSIH.consultaMedicosX30Dias(nroCi, nroCelular, entidad, token, empresa, codEspecMedic, usuario, password);
  }
  
  public py.gov.ips.consultas.servicios.RetornoAgendamientoMedico validarCi(java.lang.String nroCi, java.lang.String nroCelular, java.lang.String entidad, java.lang.String usuario, java.lang.String password) throws java.rmi.RemoteException{
    if (serviciosConsultasSIH == null)
      _initServiciosConsultasSIHProxy();
    return serviciosConsultasSIH.validarCi(nroCi, nroCelular, entidad, usuario, password);
  }
  
  
}