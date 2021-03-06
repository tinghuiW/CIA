/*
 * Copyright (c) 2017 CMPUT301F17T15. This project is distributed under the MIT license.
 */

package com.cmput301.cia.activities.habits;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cmput301.cia.R;
import com.cmput301.cia.controller.TimedClickListener;
import com.cmput301.cia.fragments.DatePickerFragment;
import com.cmput301.cia.models.Habit;
import com.cmput301.cia.utilities.DateUtilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;

/**
 * @author Shipin Guan
 * @version 2
 * Created on 2017/11/12.
 *
 * Edit existing habit
 */

public class EditHabitActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Date chooseStartDate;
    private EditText habitName;
    private EditText reason;
    private TextView startDate;
    private MaterialDayPicker dayPicker;
    //spinner for habit type
    private Spinner spinner;
    //the habit is editing
    private Habit target;

    /**
     * Initializing/editing all the existing information of specific habit
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_habit_detail);
        Intent intent = getIntent();

        target = (Habit) intent.getSerializableExtra("Habit");
        ArrayList<String> habitCategories = intent.getStringArrayListExtra("Categories");

        chooseStartDate = new Date();
        habitName = (EditText) findViewById(R.id.habitName);
        habitName.setText(target.getTitle());
        reason = (EditText) findViewById(R.id.reason);
        reason.setText(target.getReason());
        startDate = (TextView) findViewById(R.id.startDate);
        dayPicker = (MaterialDayPicker) findViewById(R.id.day_picker);
        startDate.setText(DateUtilities.formatDate(chooseStartDate));

        //spinner activity, could be placed in another activity file for better practice
        //get the current habit types

        spinner = (Spinner) findViewById(R.id.habitTypeSpinner);
        final List<String> type = new ArrayList<String>();
        if (habitCategories.size() == 0){
            type.add("Create new type");
        }
        else {
            for (String t : habitCategories){
                type.add(t);
            }
            type.remove(type.indexOf(target.getType()));
            type.add(0,target.getType());
            type.add("Create new type");
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(adapterView.getContext(), "Selected " + adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
                if (i == type.size() - 1){
                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditHabitActivity.this);
                    View mview = getLayoutInflater().inflate(R.layout.dialog_input,null);
                    final EditText minput = (EditText) mview.findViewById(R.id.edit_Type_Input);
                    Button okButton = (Button) mview.findViewById(R.id.Ok_Button);
                    mBuilder.setView(mview);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();
                    okButton.setOnClickListener(new TimedClickListener(){
                        @Override
                        public void handleClick(){
                            if (!minput.getText().toString().isEmpty()){
                                type.add(0, minput.getText().toString());
                                dialog.dismiss();
                            } else {
                                Toast.makeText(EditHabitActivity.this, "The type name can not be empty", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    mBuilder.setView(mview);
                    dialog.show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //spinner finished

    }
    //saving
    public void saveChange(View view){
        List<MaterialDayPicker.Weekday> daysSelected = dayPicker.getSelectedDays();
        if (daysSelected.size() == 0) {
            Toast.makeText(EditHabitActivity.this, "Please select at least one day of notification frequency.", Toast.LENGTH_SHORT).show();
        } else if (habitName.getText().toString().length() == 0){
            Toast.makeText(EditHabitActivity.this, "The habit title can not be left blank.", Toast.LENGTH_SHORT).show();
        }
        else{
            //set changes on current habit.
            target.setTitle(habitName.getText().toString());
            target.setReason(reason.getText().toString());
            target.setStartDate(chooseStartDate);
            target.setDaysOfWeek(getPickedDates(daysSelected));
            target.setType(spinner.getSelectedItem().toString());

            Intent returnIntent = new Intent();
            returnIntent.putExtra("Habit", target);
            setResult(RESULT_OK, returnIntent);
            finish();

        }
    }

    public List<Integer> getPickedDates(List<MaterialDayPicker.Weekday> pickedDates) {
        List<Integer> outputDatesList = new ArrayList<>();
        for (MaterialDayPicker.Weekday weekday : pickedDates) {
            outputDatesList.add(weekday.ordinal() + 1);
        }

        return outputDatesList;
    }

    public void datePickerDialog(View v) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        final Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        chooseStartDate = calendar.getTime();
        startDate.setText(DateUtilities.formatDate(chooseStartDate));
    }
}
