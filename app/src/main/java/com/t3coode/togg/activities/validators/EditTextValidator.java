package com.t3coode.togg.activities.validators;

import java.text.ParseException;

import android.widget.EditText;

import com.t3coode.togg.activities.utils.DecimalFormatter;

public class EditTextValidator extends BasicValidator {

    public enum ValidationTypes implements ValidationType {
        TEXT_IS_NOT_EMPTY("textIsNotEmpty"),
        TEXT_IS_VALID_DOUBLE("textIsValidDouble"),
        TEXT_IS_EQUAL("textIsEqual");

        private final String value;

        private ValidationTypes(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

    private EditText[] editTexts;

    public EditTextValidator(String keyName, String name, EditText editText,
            ValidationTypes[] validations) {
        super(keyName, name, validations);
        this.editTexts = new EditText[] { editText };
    }

    public EditTextValidator(String keyName, String name, EditText[] editTexts,
            ValidationTypes[] validations) {
        super(keyName, name, validations);
        this.editTexts = editTexts;
    }

    private EditText getEditText() {
        return editTexts[0];
    }

    @SuppressWarnings("unused")
    private boolean textIsNotEmpty() {
        return getEditText().getText() != null
                && !getEditText().getText().toString().equals("");
    }

    @SuppressWarnings("unused")
    private boolean textIsValidDouble() {
        String text = getEditText().getText().toString();
        boolean valid = true;
        Double value = null;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            valid = false;
        }
        if (!valid) {
            try {
                value = DecimalFormatter.singleParse(text);
                valid = true;
            } catch (ParseException e1) {
                valid = false;
            }
        }
        getEditText().setTag(value);
        return valid;
    }

    @SuppressWarnings("unused")
    private boolean textIsEqual() {
        String text = getEditText().getText().toString();
        boolean equal = true;
        int index = 1;

        while (index < editTexts.length && equal) {
            equal = (editTexts[index].getText().toString().equals(text));
            index++;
        }

        return equal;
    }
}
