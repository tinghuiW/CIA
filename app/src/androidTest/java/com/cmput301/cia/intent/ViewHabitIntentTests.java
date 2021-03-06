/*
 * Copyright (c) 2017 CMPUT301F17T15. This project is distributed under the MIT license.
 */

package com.cmput301.cia.intent;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.cmput301.cia.R;
import com.cmput301.cia.TestProfile;
import com.cmput301.cia.activities.events.HistoryActivity;
import com.cmput301.cia.activities.habits.CreateHabitActivity;
import com.cmput301.cia.activities.habits.EditHabitActivity;
import com.cmput301.cia.activities.habits.HabitViewActivity;
import com.cmput301.cia.activities.HomePageActivity;
import com.cmput301.cia.activities.MainActivity;
import com.cmput301.cia.models.Habit;
import com.cmput301.cia.models.HabitEvent;
import com.cmput301.cia.models.Profile;
import com.robotium.solo.Solo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Version 1
 * Author: Adil Malik
 * Date: Nov 13 2017
 *
 * This class tests the UI for viewing and deleting habits
 * NOTE: These tests require an internet connection
 */

public class ViewHabitIntentTests extends ActivityInstrumentationTestCase2<HomePageActivity> {
    private Solo solo;

    public ViewHabitIntentTests(){super(com.cmput301.cia.activities.HomePageActivity.class);}

    public void setUp() throws Exception {

        Profile profile = new TestProfile("xyz");
        Habit habit = new Habit("T1", "", new Date(), Arrays.asList(1, 2, 3), "x");
        habit.setId("one");
        habit.addHabitEvent(new HabitEvent(""));
        habit.getEvents().get(0).setId("two");

        profile.addHabit(habit);
        Habit habit2 = new Habit("T2", "", new Date(), Arrays.asList(1, 2, 3), "y");
        habit2.setId("two");
        profile.addHabit(habit2);

        Intent intent = new Intent();
        intent.putExtra(HomePageActivity.ID_PROFILE, profile);
        setActivityIntent(intent);

        solo = new Solo(getInstrumentation(), getActivity());
        Log.d("SETUP", "setUp()");
    }

    /**
     * Select the first habit in the user's list
     * @return the name of the first habit
     */
    private String loadHabit(){
        // expand the first category
        solo.clickOnView(((ExpandableListView)solo.getView(R.id.HabitTypeExpandableListView)).getAdapter().getView(0, null, null));
        solo.sleep(600);

        String habitName = (String) ((ExpandableListView)solo.getView(R.id.HabitTypeExpandableListView)).getAdapter().getItem(1);

        // click in the first habit in the category
        solo.clickInList(2, 0);
        solo.sleep(1500);

        solo.assertCurrentActivity("wrong activity", HabitViewActivity.class);
        return habitName;
    }

    public void testView(){
        String name = loadHabit();

        // make sure name is correct
        assertTrue(((TextView)solo.getView(R.id.EditHabitName)).getText().toString().equals(name));
    }

    public void testDelete() throws NoSuchFieldException, IllegalAccessException {

        Field field = solo.getCurrentActivity().getClass().getDeclaredField("user");
        field.setAccessible(true);
        int count = ((Profile)field.get(solo.getCurrentActivity())).getHabitsCount();

        loadHabit();

        solo.clickOnButton("Delete");
        solo.sleep(1000);
        // confirm deletion dialog
        solo.clickOnView(solo.getView(android.R.id.button1));
        solo.sleep(3000);
        solo.assertCurrentActivity("wrong activity", HomePageActivity.class);

        // make sure habit was removed
        assertTrue(((Profile)field.get(solo.getCurrentActivity())).getHabitsCount() == count-1);
    }

    public void testEdit(){
        loadHabit();
        solo.clickOnButton("Edit");
        solo.sleep(1000);
        solo.assertCurrentActivity("wrong activity", EditHabitActivity.class);

        // should not save because no days of week selected
        solo.clickOnButton("Save");
        solo.sleep(600);
        solo.assertCurrentActivity("wrong activity", EditHabitActivity.class);

        // select wednesday
        solo.clickOnView(solo.getView(R.id.day_picker));
        solo.sleep(600);

        solo.clearEditText(0);
        solo.sleep(600);

        // should not save because no title
        solo.clickOnButton("Save");
        solo.sleep(600);
        solo.assertCurrentActivity("wrong activity", EditHabitActivity.class);

        solo.enterText(0, "newname");
        solo.sleep(600);

        solo.clearEditText(1);
        solo.sleep(600);
        solo.enterText(1, "newreason");
        solo.sleep(600);

        // successful save
        solo.clickOnButton("Save");
        solo.sleep(1500);
        solo.assertCurrentActivity("wrong activity", HabitViewActivity.class);

        // make sure changes took effect here
        assertTrue(((TextView)solo.getView(R.id.EditHabitName)).getText().toString().equals("newname"));
        assertTrue(((TextView)solo.getView(R.id.EditHabitReason)).getText().toString().equals("newreason"));
        assertTrue(((TextView)solo.getView(R.id.HabitFrequency)).getText().toString().equals("Wednesday\n"));

        // return to home page
        solo.goBack();
        solo.sleep(3000);
        solo.assertCurrentActivity("wrong activity", HomePageActivity.class);

        // make sure changes took effect in the home page
        String name = loadHabit();
        assertTrue(name.equals("newname"));
        assertTrue(((TextView)solo.getView(R.id.EditHabitName)).getText().toString().equals("newname"));
        assertTrue(((TextView)solo.getView(R.id.EditHabitReason)).getText().toString().equals("newreason"));
        assertTrue(((TextView)solo.getView(R.id.HabitFrequency)).getText().toString().equals("Wednesday\n"));
    }

}
