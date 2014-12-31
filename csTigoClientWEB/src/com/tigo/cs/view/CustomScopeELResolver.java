package com.tigo.cs.view;

import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.event.ScopeContext;

/**
 * This ELResolver handles the resolution of entities within a custom EL scope
 * called <code>customScope</code>.
 */
public class CustomScopeELResolver extends ELResolver {

    private static final String SCOPE_NAME = "customScope";


    // ------------------------------------------------- Methods From ELResolver


    @Override
	public Object getValue(ELContext elContext, Object base, Object property) {
        if (property == null) {
            throw new PropertyNotFoundException();
        }
        if (base == null && SCOPE_NAME.equals(property.toString())) {
            // explicit scope lookup request
            CustomScope customScope = getScope(elContext);
            elContext.setPropertyResolved(true);
            return customScope;
        } else if (base != null && base instanceof CustomScope) {
            // We're dealing with the custom scope that has been explicity referenced
            // by an expression.  'property' will be the name of some entity
            // within the scope.
            return lookup(elContext, (CustomScope) base, property.toString());
        } else if (base == null) {
            // bean may have already been created and is in scope.
            // check to see if the bean is present
            return lookup(elContext, getScope(elContext), property.toString());
        }
        return null;
    }

    @Override
	public Class<?> getType(ELContext elContext, Object base, Object property) {
        return Object.class;
    }

    @Override
	public void setValue(ELContext elContext, Object base, Object property, Object value) {
        // this scope isn't writable in the strict sense, so do nothing.
    }

    @Override
	public boolean isReadOnly(ELContext elContext, Object base, Object property) {
        return true;
    }

    @Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object base) {
        return Collections.<FeatureDescriptor>emptyList().iterator();
    }

    @Override
	public Class<?> getCommonPropertyType(ELContext elContext, Object base) {
        if (base != null) {
            return null;
        }
        return String.class;
    }


    // ---------------------------------------------------------- Public Methods


    public static void destroyScope(FacesContext ctx) {

        Map<String,Object> sessionMap = ctx.getExternalContext().getSessionMap();
        CustomScope customScope = (CustomScope) sessionMap.remove(SCOPE_NAME);
        customScope.notifyDestroy();

    }


    // --------------------------------------------------------- Private Methods


    private CustomScope getScope(ELContext elContext) {

        FacesContext ctx = (FacesContext) elContext.getContext(FacesContext.class);
        Map<String,Object> sessionMap = ctx.getExternalContext().getSessionMap();
        CustomScope customScope = (CustomScope) sessionMap.get(SCOPE_NAME);
        if (customScope == null) {
            customScope = new CustomScope(ctx.getApplication());
            sessionMap.put(SCOPE_NAME, customScope);
            customScope.notifyCreate();
        }
        return customScope;

    }

    
    private Object lookup(ELContext elContext,
                          CustomScope scope,
                          String key) {

        Object value = scope.get(key);
        elContext.setPropertyResolved(value != null);
        return value;

    }


    // ---------------------------------------------------------- Nested Classes

    private static final class CustomScope extends ConcurrentHashMap<String,Object> {

        private final Application application;

        // -------------------------------------------------------- Constructors


        private CustomScope(Application application) {
            this.application = application;
        }


        // ------------------------------------------------------ Public Methods


        /**
         * Publishes <code>PostConstructCustomScopeEvent</code> to notify
         * interested parties that this scope is now available.
         */
        public void notifyCreate() {

            ScopeContext context = new ScopeContext(SCOPE_NAME, this);
//            application.publishEvent(PostConstructCustomScopeEvent.class, context);
        }


        /**
         * Publishes <code>PreDestroyCustomScopeEvent</code> to notify
         * interested parties that this scope is being destroyed.
         */
        public void notifyDestroy() {

            // notify interested parties that this scope is being
            // destroyed
            ScopeContext scopeContext = new ScopeContext(SCOPE_NAME,
                                                         this);
//            application.publishEvent(PreDestroyCustomScopeEvent.class,
//                                     scopeContext);

        }

    }
}
