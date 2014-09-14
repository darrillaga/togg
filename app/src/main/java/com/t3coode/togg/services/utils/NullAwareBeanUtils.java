package com.t3coode.togg.services.utils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtilsBean;

public class NullAwareBeanUtils extends BeanUtilsBean {

    private static NullAwareBeanUtils instance = new NullAwareBeanUtils();

    @Override
    public void copyProperty(Object dest, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {

        if (value == null)
            return;

        super.copyProperty(dest, name, value);
    }

    public static NullAwareBeanUtils getInstance() {
        return instance;
    }

}
