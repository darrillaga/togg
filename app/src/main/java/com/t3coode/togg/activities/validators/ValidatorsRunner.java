package com.t3coode.togg.activities.validators;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.t3coode.togg.R;
import com.t3coode.togg.activities.utils.Resources;

public class ValidatorsRunner {

    private List<Validator> validators;
    private Toast toast;

    private Context context;

    public ValidatorsRunner(Context context) {
        super();
        this.context = context;
        this.validators = new ArrayList<Validator>();
    }

    public ValidatorsRunner(Context context, List<Validator> validators) {
        super();
        this.validators = validators;
        this.context = context;
    }

    public List<Validator> getValidators() {
        return validators;
    }

    public void setValidators(List<Validator> validators) {
        this.validators = validators;
    }

    public ValidatorsRunner addValidation(Validator validation) {
        getValidators().add(validation);
        return this;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean validateAll() {
        boolean localValid = true;
        boolean valid = true;
        StringBuilder messages = new StringBuilder();
        for (Validator validator : validators) {
            localValid = validator.valid();
            if (!localValid) {
                messages.append(makeValidatorMessage(validator));
                messages.append("\n");
                valid = localValid;
            }
        }
        if (!valid) {
            messages.delete(messages.length() - 2, messages.length());
            makeToastWithFailedValidations(messages.toString());
        }
        return valid;
    }

    private void makeToastWithFailedValidations(String messages) {
        if (toast == null
                || toast.getView().getWindowVisibility() != View.VISIBLE) {
            toast = Toast.makeText(this.context, messages, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private String makeValidatorMessage(Validator validator) {
        String error = "";
        try {
            StringBuilder string = new StringBuilder();
            string.append(validator.getName() + "\n");
            for (String errorS : validator.getErrors()) {
                error = errorS;
                string.append(
                        context.getString(Resources.getIdFromResource(
                                R.string.class, error))).append("\n");
            }
            return string.toString();
        } catch (Exception e) {
            throw new IllegalAccessError("the string " + error
                    + " does not defined on resources");
        }

    }

    public void stopMessages() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
