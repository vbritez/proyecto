package com.tigo.cs.domain.view;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;

import com.tigo.cs.commons.jpa.Order;
import com.tigo.cs.commons.jpa.Searchable;

/**
 *
 * @author Miguel Zorrilla
 */
@MappedSuperclass
public abstract class Data {

    @EmbeddedId
    @Searchable(label = "web.client.metadata.screen.field.Code", internalField = "codigo")
    protected DataPK dataPK;
    
    

    public Data() {
        this.dataPK = new DataPK();
    }

    public String getCode() {
        return dataPK.getCodigo();
    }
    
    /**
     * Asigna el c�digo del meta-dato.
     * @param code c�digo �nico para el registro del meta-dato.
     * */
    public void setCode(String code){
        dataPK.setCodigo(code);
    }
    
    public DataPK getDataPK() {
        return dataPK;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Data other = (Data) obj;
        if (this.dataPK != other.dataPK && (this.dataPK == null || !this.dataPK.equals(other.dataPK))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.dataPK != null ? this.dataPK.hashCode() : 0);
        return hash;
    }

    public Map<Integer, String> getMembers(){
        Map<Integer, String> orderedMembers = new HashMap<Integer, String>();        
        Field[] fields = this.getClass().getDeclaredFields();
        
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()))continue;                              
            Order order = field.getAnnotation(Order.class);            
            if (order != null){
                if (orderedMembers.containsKey(order.value())){
                    System.out.println("ATENCI�N: El campo '" + field.getName() + "' tiene el mismo n�mero de orden que el campo '" + orderedMembers.get(order.value()) + "'");
                }else{                    
                    orderedMembers.put(order.value(), field.getName());
                }
                                
            }else{
                System.out.println("ATENCI�N: El campo '" + field.getName()+ "' no est� anotado con @Order para determinar el orden de los meta-members.");
            }            
        }
        return orderedMembers;
    }

    @Override
    public String toString() {
        return this.getClass().getCanonicalName() + "[DataPK=" + dataPK + "]";
    }
}
