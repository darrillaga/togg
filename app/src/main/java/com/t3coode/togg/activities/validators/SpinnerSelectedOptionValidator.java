package com.t3coode.togg.activities.validators;

import android.widget.Spinner;

public class SpinnerSelectedOptionValidator extends BasicValidator {

    public enum ValidationTypes implements ValidationType {
        SELECTION_IS_NOT_EMPTY("selectionIsNotEmpty");

        private final String value;

        private ValidationTypes(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

    private Spinner spinner;

    public SpinnerSelectedOptionValidator(String keyName, String name,
            Spinner spinner, ValidationTypes[] validations) {
        super(keyName, name, validations);
        this.spinner = spinner;
    }

    @SuppressWarnings("unused")
    private boolean selectionIsNotEmpty() {
        return spinner.getSelectedItem() != null;
    }
}
