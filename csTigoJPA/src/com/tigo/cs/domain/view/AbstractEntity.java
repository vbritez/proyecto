/**
 * 
 */
package com.tigo.cs.domain.view;

/**
 * Clase abstract para proporcionar funcionalidades compartidas en los JPA
 * Entities utilizadas en las vistas (backing beans) para el Primefaces.
 * 
 * @author Miguel Zorrilla
 * @version CS Fase 7
 */
public abstract class AbstractEntity {

    /**
     * Default constructor
     */
    public AbstractEntity() {
        // TODO Auto-generated constructor stub
    }
    
    public abstract <T> T getPrimaryKey();

}
