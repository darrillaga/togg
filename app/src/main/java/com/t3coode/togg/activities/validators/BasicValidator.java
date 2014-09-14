package com.t3coode.togg.activities.validators;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public abstract class BasicValidator implements Validator {

    protected static interface ValidationType {
        String value();
    }

    private ValidationType[] validations;
    private List<String> errors;
    private String name;
    private String keyName;

    public BasicValidator(String keyName, String name,
            ValidationType[] validations) {
        super();
        this.validations = validations;
        this.name = name;
        this.keyName = keyName;
        this.errors = new ArrayList<String>();
    }

    @Override
    public boolean valid() {
        errors.clear();
        boolean valid = true;

        for (ValidationType validationName : validations) {
            try {
                Method m = this.getClass().getDeclaredMethod(
                        validationName.value());
                m.setAccessible(true);
                boolean tempValid = (Boolean) m.invoke(this);
                if (!tempValid) {
                    errors.add(keyName
                            + StringUtils.capitalize(validationName.value())
                            + "ValidationError");
                    valid = tempValid;
                }
            } catch (Exception e) {
            }
        }
        return valid;
    }

    @Override
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
