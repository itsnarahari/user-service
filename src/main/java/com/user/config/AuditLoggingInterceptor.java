package com.user.config;


import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;

@Component
public class AuditLoggingInterceptor implements Interceptor, Serializable {


    private static final String CREATED_ON = "createdOn";
    private static final String CREATED_BY = "createdBy";
    private static final String UPDATED_ON = "updatedOn";
    private static final String UPDATED_BY = "updatedBy";


    /**
     * This method is invoked when an entity is saved
     */
    @Override
    public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, org.hibernate.type.Type[] types) throws CallbackException {
        boolean found = false;
        for (int i = 0; i < propertyNames.length; i++) {
            if (CREATED_BY.equals(propertyNames[i]) && state[i] == null) {
                state[i] = getUserName();
                found = true;
            } else if (CREATED_ON.equals(propertyNames[i]) && state[i] == null) {
                state[i] = getCurrentDate();
                found = true;
            } else if (UPDATED_BY.equals(propertyNames[i]) && state[i] == null) {
                state[i] = getUserName();
                found = true;
            } else if (UPDATED_ON.equals(propertyNames[i]) && state[i] == null) {
                state[i] = getCurrentDate();
                found = true;
            }
        }
        return found;
    }

    /**
     * This method is invoked when an entity is updated
     */
    @Override
    public boolean onFlushDirty(Object entity, Object id, Object[] currentState, Object[] previousState, String[] propertyNames, org.hibernate.type.Type[] types) throws CallbackException {
        boolean found = false;
        for (int i = 0; i < propertyNames.length; i++) {
            if (UPDATED_BY.equals(propertyNames[i])) {
                currentState[i] = getUserName();
                found = true;
            }
            if (UPDATED_ON.equals(propertyNames[i])) {
                currentState[i] = getCurrentDate();
                found = true;
            }
        }
        return found;
    }

    /**
     * This method is invoked when an entity is saved
     */

    private LocalDateTime getCurrentDate() {
        return LocalDateTime.now();
    }


    private String getUserName() {
        return "CO111"; // TODO Map username here
    }
}


