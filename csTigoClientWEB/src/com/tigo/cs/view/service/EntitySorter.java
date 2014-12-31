package com.tigo.cs.view.service;

import java.lang.reflect.Method;
import java.util.Comparator;

import org.primefaces.model.SortOrder;

import com.tigo.cs.commons.util.Generic;
import com.tigo.cs.domain.view.AbstractEntity;

public class EntitySorter implements Comparator<AbstractEntity> {

        private final String sortField;
        
        private final SortOrder sortOrder;
        
        public EntitySorter(String sortField, SortOrder sortOrder) {
            this.sortField = sortField;
            this.sortOrder = sortOrder;
        }
                

        @Override
        public int compare(AbstractEntity o1, AbstractEntity o2) {
            try {                
                Method method = o1.getClass().getMethod(Generic.getterCapitalMethodNameFor(this.sortField), (Class<?>[]) null);
                Object value1 = method.invoke(o1, (Object[])null);
                Object value2 = method.invoke(o2, (Object[])null);

                //Se agrego para manejar valore nulos
                if (value1 == null && value2 != null)
                    return SortOrder.ASCENDING.equals(sortOrder) ? -1 : 1 ; 
                if (value1 != null && value2 == null)
                    return SortOrder.ASCENDING.equals(sortOrder) ? 1 : -1 ; 
                if (value1 == null && value2 == null)
                    return 0;
                            
                @SuppressWarnings("unchecked")
                int value = ((Comparable<Object>)value1).compareTo(value2);                
                return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
            }
            catch(Exception e) {
                throw new RuntimeException();
            }
        }

        
        protected interface GenericComparison{            
            public abstract <T extends Comparable<T>> int getComparable(T o1, T o2, Comparable<T> clazz);            
        }
    }