package com.tigo.cs.view.metadata;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.IconType;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.MetaDataPK;
import com.tigo.cs.domain.view.Data;
import com.tigo.cs.domain.view.DataIcon;
import com.tigo.cs.facade.IconTypeFacade;
import com.tigo.cs.facade.MetaDataFacade;
import com.tigo.cs.security.SecurityBean;

@ManagedBean(name = "crudMetaDataIconBean")
@ViewScoped
public class CrudMetaDataIconBean extends AbstractCrudMetaDataBean<DataIcon>
		implements Serializable {

	private static final long serialVersionUID = -369508588682186969L;
	private static final Long COD_META = 21L;
	private static final Long COD_MEMBER = 1L;

	private List<IconType> iconsList;
	@EJB
	private IconTypeFacade iconTypeFacade;
	private Map<Long, Boolean> selectedIconMap;
	private IconType selectIcon;
	private DataIcon editEntity;
	@EJB
	private MetaDataFacade metadataFacade;

	public CrudMetaDataIconBean() {
		super(COD_META, COD_MEMBER);
		setPdfReportName("rep_dataicon");
		setXlsReportName("rep_dataicon_xls");
	}

	@Override
	public String getMetaLabel() {
		if (metaLabel == null) {
			super.setMetaLabel(i18n
					.iValue("web.client.backingBean.iconCrudMetaData.Icon"));
		}
		return metaLabel;
	}

	@Override
	public String editEntityPlusMember() {
		data = null;
		for (Data dataBean : dataModelSpecific) {
			if (getSelectedItems().get(dataBean.getDataPK())) {
				if (data == null) {
					data = dataFacade.findByClass(getDataClass(),
							dataBean.getDataPK());
				} else {
					data = null;
					setWarnMessage(i18n
							.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectJustOneEdit"));
					return null;
				}
			}
		}

		if (data == null) {
			setWarnMessage(i18n
					.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
			return null;
		}
		super.editEntityPlusMember();

		selectedIconMap = null;
		selectIcon = iconTypeFacade.findByUrl(getData().getUrl());
		editEntity = getData();
		getSelectedIconMap().put(selectIcon.getIconTypeCod(), true);
		return null;
	}

	@Override
	public String newEntity() {
		super.newEntity();
		selectIcon = null;
		selectedIconMap = null;
		return null;
	}

	@Override
	public String savePlusMember() {
		if (selectIcon == null) {
			setWarnMessage(i18n
					.iValue("web.client.backingBean.metadataIcon.IconNull"));
			return null;
		}

		MetaData md = metadataFacade.findByClientMetaMemberAndValue(
				SecurityBean.getInstance().getLoggedInUserClient().getClient()
						.getClientCod(), COD_META, COD_MEMBER,
				selectIcon.getUrl());

		/* Si se encontro un metadata del cliente con la misma URL del icono */
		if (md != null) {
			/*
			 * Se verifica que no sea el mismo que el que se esta editando. Cada
			 * icono puede tener solo un significado por cliente
			 */
			if (editEntity != null) {
				if (!md.getValueChr().equals(editEntity.getUrl())) {
					setWarnMessage(i18n
							.iValue("web.client.backingBean.metadataIcon.not.unique"));
					return null;
				}
			} else {
				setWarnMessage(i18n
						.iValue("web.client.backingBean.metadataIcon.not.unique"));
				return null;
			}
		}
		
		if (editEntity != null) {

			List<MetaData> list = metadataFacade.findByClientMemberValue(
					SecurityBean.getInstance().getLoggedInUserClient()
							.getClient().getClientCod(), -1L,
					editEntity.getUrl());

			if (list != null && list.size() > 0) {
				for (MetaData m : list) {
					m.setValueChr(selectIcon.getUrl());
					try {
						createOrSaveMetaData(m);
					} catch (Exception e) {
						this.signal(
								Action.ERROR,
								i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotSaveError"),
								e);
					}
				}
			}
		}

		getData().setUrl(selectIcon.getUrl());

		
		if (data.getCode() == null || data.getCode().equals("")) {
			data.setCode(null);
			setErrorMessage(i18n
					.iValue("web.client.backingBean.metadata.CodeNull"));
			return null;
		}

		try {
			Client client = getUserweb().getClient();
			MetaDataPK pk = new MetaDataPK();
			// PK del METADATA
			pk.setCodClient(client.getClientCod());
			pk.setCodMeta(cod_meta);
			pk.setCodeChr(data.getCode());

			Map<Integer, String> members = data.getMembers();
			Set<Entry<Integer, String>> memberSet = members.entrySet();
			for (Entry<Integer, String> entry : memberSet) {
				pk.setCodMember(new Long(entry.getKey()));
				MetaData metaData = new MetaData(pk);

				// Obtiene el valor para el meta-data "valueChr"
				String member = members.get(entry.getKey());
				String memberMethod = "get".concat(
						member.substring(0, 1).toUpperCase()).concat(
						member.substring(1));
				Method method = data.getClass().getMethod(memberMethod,
						(Class<?>[]) null);
				String metaMemberValue = (String) method.invoke(data,
						(Object[]) null);

				// Los metamembers 1 representan el mÃ­nimo dato para el
				// meta-data, no puede ser nulo
				if (entry.getKey() == 1
						&& (metaMemberValue == null || metaMemberValue
								.isEmpty())) {
					throw new Exception(" El código y el campo " + member
							+ " requieren de un valor para continuar.");
				} else if (metaMemberValue != null
						&& !metaMemberValue.isEmpty()) {
					metaData.setValueChr(metaMemberValue);
					createOrSaveMetaData(metaData);
				}
			}

			setSuccessMessage(i18n
					.iValue("web.client.backingBean.abstractCrudBean.message.SaveSuccess"));

		} catch (Exception e) {
			setErrorMessage(i18n.iValue("web.client.backingBean.message.Error")
					+ e.getMessage() + ".");
			this.signal(
					Action.ERROR,
					i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotSaveError"),
					e);
			// Las excepciones ya fueron tratadas en la clase padre
			// De igual manera se espera que al primer error en la escritura de
			// los datos en la BD
			// se interrumpa la operaciÃ³n
		}
		data = null;
		filterSelectedField = "-1";
		filterCriteria = "";
		paginationHelper = null; // For pagination recreation
		dataModelSpecific = null; // For data model recreation
		getSelectedItems().clear();// clearing selection
		selectedUserphone = null;
		selectedAssociatedUserphones = null;
		selectedUserphoneList = null;
		associatedUserphonesList = null;
		validatedAllUserphones = false;
		validatedAllUserphonesAssociated = false;
		userphoneList = null;
		dataModel = null;

		return null;
	}
	

	public void listenerIconSelect(IconType ip) {
		selectedIconMap = null;
		selectIcon = ip;
		getSelectedIconMap().put(ip.getIconTypeCod(), true);
	}

	public List<IconType> getIconsList() {
		if (iconsList == null) {
			iconsList = iconTypeFacade.findIconEnabled();
		}
		return iconsList;
	}

	public void setIconsList(List<IconType> iconsList) {
		this.iconsList = iconsList;
	}

	public Map<Long, Boolean> getSelectedIconMap() {
		if (selectedIconMap == null) {
			selectedIconMap = new HashMap<Long, Boolean>();
		}
		return selectedIconMap;
	}

	public void setSelectedIconMap(Map<Long, Boolean> selectedIconMap) {
		this.selectedIconMap = selectedIconMap;
	}

	public IconType getSelectIcon() {
		return selectIcon;
	}

	public void setSelectIcon(IconType selectIcon) {
		this.selectIcon = selectIcon;
	}

}