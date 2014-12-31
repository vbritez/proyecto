/**
 * ServiciosConsultasSIH.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package py.gov.ips.consultas.servicios;

public interface ServiciosConsultasSIH extends java.rmi.Remote {
    public py.gov.ips.consultas.servicios.RetornoCancelacionesMedicas[] buscarCitasPorCi(java.lang.String nroCi, java.lang.String nroCelular, java.lang.String entidad, java.lang.Long token, java.lang.String usuario, java.lang.String password) throws java.rmi.RemoteException;
    public java.lang.String cancelarCitas(java.lang.String nroCi, java.lang.String nroCelular, java.lang.Long token, java.lang.Integer codEmpresa, java.lang.Long nroAgend, java.lang.Integer nroRegAmb, java.lang.String entidad, java.lang.String usuario, java.lang.String password) throws java.rmi.RemoteException;
    public java.lang.String confirmarTurno(java.lang.String nroCi, java.lang.String nroCelular, java.lang.String entidad, java.lang.Long token, java.lang.Integer empresa, java.lang.Integer codEspecMedic, java.lang.String codMedicoTurnoFecha, java.lang.String usuario, java.lang.String password) throws java.rmi.RemoteException;
    public py.gov.ips.consultas.servicios.RetornoAgendamientoMedico consultaCentroMedico(java.lang.String nroCi, java.lang.String nroCelular, java.lang.String entidad, java.lang.Long token, java.lang.Short codEspecMd, java.lang.String usuario, java.lang.String password) throws java.rmi.RemoteException;
    public py.gov.ips.consultas.servicios.RetornoAgendamientoMedico consultaEspecialidades(java.lang.String nroCi, java.lang.String nroCelular, java.lang.String entidad, java.lang.Long token, java.lang.String usuario, java.lang.String password) throws java.rmi.RemoteException;
    public py.gov.ips.consultas.servicios.RetornoAgendamientoMedico consultaMedicos(java.lang.String nroCi, java.lang.String nroCelular, java.lang.String entidad, java.lang.Long token, java.lang.Integer empresa, java.lang.Integer codEspecMedic, java.lang.String usuario, java.lang.String password, java.lang.String turno) throws java.rmi.RemoteException;
    public py.gov.ips.consultas.servicios.RetornoAgendamientoMedico consultaMedicosX30Dias(java.lang.String nroCi, java.lang.String nroCelular, java.lang.String entidad, java.lang.Long token, java.lang.Integer empresa, java.lang.Integer codEspecMedic, java.lang.String usuario, java.lang.String password) throws java.rmi.RemoteException;
    public py.gov.ips.consultas.servicios.RetornoAgendamientoMedico validarCi(java.lang.String nroCi, java.lang.String nroCelular, java.lang.String entidad, java.lang.String usuario, java.lang.String password) throws java.rmi.RemoteException;
}
