package com.t3coode.togg.services.dtos;

import java.util.List;
import java.util.Map;

public class ErrorWrapperDTO extends BaseDTO {

    private List<String> errorMessages;

    private Map<String, String> errors;

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
