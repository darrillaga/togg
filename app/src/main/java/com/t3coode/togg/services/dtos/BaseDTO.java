package com.t3coode.togg.services.dtos;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.t3coode.togg.services.utils.JsonableImpl;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseDTO extends JsonableImpl {

    private List<DTOObserver> observers = new ArrayList<DTOObserver>();

    public void registerDTOObserver(DTOObserver observer) {
        observers.add(observer);
    }

    public void unregisterDTOObserver(DTOObserver observer) {
        observers.remove(observer);
    }

    protected void notifyChange(String fieldName) {
        for (DTOObserver obserber : observers) {
            obserber.notifyDTOChanged(this, fieldName);
        }
    }

    public static interface DTOObserver {
        void notifyDTOChanged(BaseDTO object, String fieldName);
    }

    @Override
    public String toString() {
        try {
            Method m = getClassMethodByAnnotation(getClass(), ToString.class);
            if (m != null) {
                return m.invoke(this).toString();
            }

            return super.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getClassMethodByAnnotation(Class<?> clazz,
            Class<? extends Annotation> annotationClass) {
        Method[] methods = clazz.getMethods();
        int index = 0;
        Method m = null;
        while (index < methods.length && m == null) {
            if (methods[index].isAnnotationPresent(annotationClass)) {
                m = methods[index];
            }
            index++;
        }
        return m;
    }
}
