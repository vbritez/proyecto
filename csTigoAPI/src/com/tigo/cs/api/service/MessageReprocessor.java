package com.tigo.cs.api.service;


//@Stateless
public class MessageReprocessor 
//implements DriverInterface, Serializable 
{
//
//	/**
//     * 
//     */
//	private static final long serialVersionUID = 1018584452132411120L;
//	@EJB
//	private MessageFacade messageFacade;
//	@EJB
//	private ServiceValueDetailFacade serviceValueDetailFacade;
//	@EJB
//	private ServiceValueFacade serviceValueFacade;
//
//	private DriverInterface driver;
//	private HashMap<String, HashMap<String, String>> nodes;
//
//	@Override
//	public String execute(String msisdn, HashMap<String, HashMap<String, String>> hm) {
//		try {
//			List<String> returnMessages = new ArrayList<String>();
//			String jpql = "SELECT m FROM Message m WHERE 1 = 1 AND m.userphone.enabledChr = '1' AND m.messageinChr not like '%CONSULTA-META-OPCIONES%' ";
//
//			if (hm != null) {
//				this.nodes = hm;
//
//				/*
//				 * recuperamos el numero de la linea que queremos recuperar sus
//				 * mensajes
//				 */
//				String cellphoneChr = getNodeValue("cellphoneNum");
//				if (cellphoneChr != null) {
//					jpql = jpql.concat(" AND m.userphone.cellphoneNum = ").concat(cellphoneChr);
//				}
//
//				/*
//				 * recueperamos el codigo del cliente para reprocesar todos los
//				 * mensajes del cliente en el caso que no se haya proporcionado
//				 * un numero de linea especificamente
//				 */
//				String client = getNodeValue("clientCod");
//				if (client != null) {
//					jpql = jpql.concat(" AND m.userphone.client = ").concat(client);
//				}
//				/*
//             *  
//             */
//				String messageInLike = getNodeValue("messageInLike");
//				if (messageInLike != null) {
//					jpql = jpql.concat(" AND m.messageinChr like '%").concat(messageInLike).concat("%'");
//				}
//
//				/*
//				 * filtramos los mensajes por un rango de fecha
//				 */
//				String dateFrom = getNodeValue("dateFrom");
//				if (dateFrom != null) {
//					jpql = jpql.concat(" AND m.dateinDat >= '").concat(dateFrom).concat("'");
//				}
//				String dateTo = getNodeValue("dateTo");
//				if (dateTo != null) {
//					jpql = jpql.concat(" AND m.dateinDat <= '").concat(dateTo).concat("'");
//				}
//
//				/*
//				 * para verificar el codigo del servicio de los mensajes a ser
//				 * reprocesados,
//				 */
//				Integer serviceCod = Integer.parseInt(getNodeValue("serviceCod"));
//				Integer serviceVersion = Integer.parseInt(getNodeValue("serviceVersion"));
//
//				/*
//				 * este valor define si los mensajes que son reprocesados y
//				 * almacenaron valores en SERVICE_VALUE y SERVICE_VALUE_DETAIL
//				 * seran eliminados de la plataforma
//				 */
//				Boolean deleteMessageData = Boolean.valueOf(getNodeValue("deleteMessageData") != null ? getNodeValue("deleteMessageData") : "false");
//
//				/*
//				 * obtenemos la lista de los mensajes que vamos a reprocesar
//				 */
//				List<Message> messages = messageFacade.getMessages(jpql);
//
//				/*
//				 * obtenemos el driver que reprocesara el mensaje
//				 */
//				driver = (DriverInterface) AbstractFacade.getRemoteInstance(DriverInterface.class, getServiceClass(serviceCod, serviceVersion));
//
//				for (Message m : messages) {
//					String returnMessage = driver.execute(m.getUserphone().getCellphoneNum().toString(), parseInput(m.getMessageinChr()));
//					System.out.println(returnMessage);
//					returnMessages.add(returnMessage);
//
//					/*
//					 * recuperamos el ultimo mensaje del userphone
//					 * correspondiente al servicio que se ha registrado
//					 */
//					jpql = "SELECT m FROM Message m WHERE 1 = 1 ";
//
//					if (cellphoneChr != null) {
//						jpql = jpql.concat(" AND m.userphone.cellphoneNum = ").concat(cellphoneChr);
//					}
//
//					/*
//					 * recueperamos el codigo del cliente para reprocesar todos
//					 * los mensajes del cliente en el caso que no se haya
//					 * proporcionado un numero de linea especificamente
//					 */
//
//					if (client != null) {
//						jpql = jpql.concat(" AND m.userphone.client = ").concat(client);
//					}
//					/*
//	             *  
//	             */
//
//					if (messageInLike != null) {
//						jpql = jpql.concat(" AND m.messageinChr like '%").concat(messageInLike).concat("%'");
//					}
//
//					jpql = jpql.concat(" ORDER BY m.messageCod DESC");
//					Message createdMessage = messageFacade.getMessage(jpql);
//
//					/*
//					 * modificamos la fecha del mensaje creado con la fecha del
//					 * mensaje no procesado correctamente
//					 */
//					createdMessage.setDateinDat(m.getDateinDat());
//					createdMessage.setDateoutDat(m.getDateoutDat());
//					messageFacade.edit(createdMessage);
//
//					if (deleteMessageData) {
//						/*
//						 * recuperamos todos los detalles que se refieren al
//						 * mensaje no procesado correctamente
//						 */
//
//						List<ServiceValueDetail> serviceValueDetailList = serviceValueDetailFacade.findByMessage(m);
//						for (ServiceValueDetail svd : serviceValueDetailList) {
//							/*
//							 * eliminamos el detalle que genero el mensaje
//							 */
//							serviceValueDetailFacade.remove(svd);
//						}
//
//						List<ServiceValue> serviceValueList = serviceValueFacade.findByMessage(m);
//						for (ServiceValue sv : serviceValueList) {
//							/*
//							 * eliminamos la cabecera que genero el mensaje
//							 */
//							serviceValueFacade.remove(sv);
//						}
//
//
//					}
//					
//					List<ServiceValueDetail> serviceValueDetailList = serviceValueDetailFacade.findByMessage(createdMessage);
//					for (ServiceValueDetail svd : serviceValueDetailList) {
//						/*
//						 * modificamos la fecha del service value detail con la hora
//						 */
//						svd.setRecorddateDat(m.getDateinDat());
//						serviceValueDetailFacade.edit(svd);
//					}
//
//					List<ServiceValue> serviceValueList = serviceValueFacade.findByMessage(createdMessage);
//					for (ServiceValue sv : serviceValueList) {
//						/*
//						 * eliminamos la cabecera que genero el mensaje
//						 */
//						sv.setRecorddateDat(m.getDateinDat());
//						serviceValueFacade.edit(sv);
//					}
//
//					
//
//				}
//
//			}
//
//			return returnMessages.toString();
//		} catch (NamingException e) {
//			e.printStackTrace();
//			return "ERROR";
//		}
//	}
//
//	private String getNodeValue(String key) {
//		if (nodes != null) {
//			if (nodes.get(key) != null) {
//				return nodes.get(key).get("Value");
//			}
//		}
//		return null;
//	}
//
//	private Class getServiceClass(int serviceCod, int serviceEvent) {
//		switch (serviceCod) {
//		case 1:
//			return VisitDriver.class;
//		case 2:
//			return OrderDriver.class;
//		case 3:
//			return VisitOrderDriver.class;
//		case 5:
//			switch (serviceEvent) {
//			case 1:
//				return CollectionDriver.class;
//			case 2:
//				return MiddleCollectionDriver.class;
//			}
//		}
//		return null;
//	}
//
//	public static HashMap<String, HashMap<String, String>> parseInput(String message) {
//
//		try {
//			HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
//			String[] parts = message.replace("={", ", ").replace("{", "").replace("}", "").split(", ");
//			for (int i = 0; i < parts.length; i = i + 3) {
//
//				HashMap<String, String> innerMap = new HashMap<String, String>();
//				if (parts[i + 1].startsWith("Value")) {
//					innerMap.put("Value", parts[i + 1].substring(6));
//				} else if (parts[i + 1].startsWith("Description")) {
//					innerMap.put("Description", parts[i + 1].substring(12));
//				}
//				if (parts[i + 2].startsWith("Value")) {
//					innerMap.put("Value", parts[i + 2].substring(6));
//				} else if (parts[i + 2].startsWith("Description")) {
//					innerMap.put("Description", parts[i + 2].substring(12));
//				}
//				map.put(parts[i], innerMap);
//
//			}
//			return map;
//		} catch (Exception e) {
//			System.out.println(message);
//			return null;
//		}
//	}
//
//	public static void main(String... arsgs) {
//		System.out.print(parseInput("{LAC={Description=Location area asociado al móvil durante la sesión., Value=1003}, COBRANZA-ACTIVO-OPCIONES={Description=Opciones para la seleccion del tipo de activo a registrar, Value=COBRANZA-ACTIVO-CHEQUE}, SC MENU={Description=Menu principal de Soluciones Corporativas, Value=SC-COBRANZA-MEDIA}, CELL_ID={Description=Cell Id asociado al móvil durante la sesión., Value=10283}, COBRANZA-COD-CLIENTE={Description=Codigo del cliente, Value=6184}, COBRANZA-CHEQUE-BANCO={Description=Banco emisor del cheque, Value=5}, COBRANZA-FACTURA-MONTO={Description=Monto de la factura a ser registrada, Value=395000}, COBRANZA-CONFIRMACION-REGISTRO={Description=Confirmacion de registro de los datos del cobro, Value=1}, COBRANZA-NRO-RECIBO={Description=Numero de recibo del cobro, Value=135238}, COBRANZA-OPCIONES-GENERAL={Description=Opciones del servicio de Cobranzas  , Value=COBRANZA-REGISTRAR}, COBRANZA-OBSERVACION={Description=Observacion del activo, Value=1}, COBRANZA-CHEQUE-NRO-CHEQUE={Description=Numero del cheque a cobrar, Value=11058267}}").toString());
//	}

}
