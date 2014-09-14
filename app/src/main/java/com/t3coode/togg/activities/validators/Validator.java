package com.t3coode.togg.activities.validators;

import java.util.List;

public interface Validator {

    boolean valid();

    List<String> getErrors();

    String getName();
}
