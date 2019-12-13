package com.example.motolifeflota.Vertical_form_steps;

import android.app.DatePickerDialog;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.motolifeflota.MainActivity;

import java.util.Calendar;

import ernestoyaquello.com.verticalstepperform.Step;

public class SelectDateStep extends Step<String> {


    private DatePickerDialog picker;
    private DatePicker picker2;
    private TextView dateTextView;
    private Button pickDateButton;

    public SelectDateStep(String stepTitle) {
        super(stepTitle);
    }



    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        LinearLayout content = new LinearLayout(getContext());

        dateTextView = new TextView(getContext());
        pickDateButton = new Button(getContext());
        pickDateButton.setText("Wybierz datę");
        pickDateButton.setTextSize(20);

        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    picker = new DatePickerDialog(getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    dateTextView.setVisibility(View.VISIBLE);
                                    dateTextView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    markAsCompletedOrUncompleted(true);
                                }
                            }, year, month, day);
                    picker.show();
                    dateTextView.setError(null);


            }
        });



        content.addView(pickDateButton);
        content.addView(dateTextView);


        return content;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        // The step's data (i.e., the user name) will be considered valid only if it is longer than
        // three characters. In case it is not, we will display an error message for feedback.
        // In an optional step, you should implement this method to always return a valid value.
        
        boolean isDateValid = !dateTextView.getText().toString().matches("");
        Log.d("isDateValid",String.valueOf(isDateValid));

        return new IsDataValid(isDateValid);
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        String date = dateTextView.getText().toString();
        return date != null ? dateTextView.getText().toString() : "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        String userName = getStepData();
        return !userName.isEmpty() ? userName : "(Empty)";
    }

    @Override
    protected void onStepOpened(boolean animated) {
        // This will be called automatically whenever the step gets opened.
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // This will be called automatically whenever the step gets closed.
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as completed.
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as uncompleted.
    }

    @Override
    public void restoreStepData(String stepData) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        dateTextView.setText(stepData);
    }
}