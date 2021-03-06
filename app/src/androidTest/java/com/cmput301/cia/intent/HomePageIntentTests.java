/*
 * Copyright (c) 2017 CMPUT301F17T15. This project is distributed under the MIT license.
 */

package com.cmput301.cia.intent;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import com.cmput301.cia.R;
import com.cmput301.cia.TestProfile;
import com.cmput301.cia.activities.habits.CreateHabitActivity;
import com.cmput301.cia.activities.events.HistoryActivity;
import com.cmput301.cia.activities.HomePageActivity;
import com.cmput301.cia.activities.MainActivity;
import com.cmput301.cia.activities.users.UserProfileActivity;
import com.cmput301.cia.models.Habit;
import com.cmput301.cia.models.Profile;
import com.robotium.solo.Solo;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Version 2
 * Author: Adil Malik
 * Date: Nov 13 2017
 *
 * This class tests the UI for the features on the home page of the activity
 */

public class HomePageIntentTests extends ActivityInstrumentationTestCase2<HomePageActivity> {

    private Solo solo;

    public HomePageIntentTests() {
        super(com.cmput301.cia.activities.HomePageActivity.class);
    }

    public void setUp() throws Exception{

        Profile profile = new TestProfile("xyz");
        Intent intent = new Intent();
        intent.putExtra(HomePageActivity.ID_PROFILE, profile);
        setActivityIntent(intent);

        solo = new Solo(getInstrumentation(), getActivity());
        Log.d("SETUP", "setUp()");
    }

    public void testNavigation(){
        // select profile option in menu
        solo.clickOnActionBarItem(R.id.menu_button_My_Profile);
        solo.clickOnMenuItem("My Profile");
        solo.sleep(1000);
        solo.assertCurrentActivity("wrong activity", UserProfileActivity.class);
        solo.goBackToActivity("HomePageActivity");
        solo.sleep(300);
        solo.assertCurrentActivity("wrong activity", HomePageActivity.class);

        // select add new habit
        solo.clickOnActionBarItem(R.id.menu_button_Add_New_Habit);
        solo.clickOnMenuItem("Add New Habit");
        solo.sleep(1000);
        solo.assertCurrentActivity("wrong activity", CreateHabitActivity.class);
        solo.goBackToActivity("HomePageActivity");
        solo.sleep(300);
        solo.assertCurrentActivity("wrong activity", HomePageActivity.class);

        // select habit history
        solo.clickOnActionBarItem(R.id.menu_button_Habit_History);
        solo.clickOnMenuItem("Habit History");
        solo.sleep(1000);
        solo.assertCurrentActivity("wrong activity", HistoryActivity.class);
        solo.goBack();
        solo.sleep(3000);
        solo.assertCurrentActivity("wrong activity", HomePageActivity.class);

        // TODO: power rankings

        // TODO: overall rankings

        // TODO: followed users

        // TODO: whatever else is in the menu bar

    }


    public void testTodaysHabits() throws NoSuchFieldException, IllegalAccessException {
        Field field = solo.getCurrentActivity().getClass().getDeclaredField("user");
        field.setAccessible(true);
        Profile user = (Profile) field.get(solo.getCurrentActivity());

        ListView todaysHabits = (ListView)solo.getView(R.id.TodayToDoListView);
        assertTrue(user.getTodaysHabits().size() == todaysHabits.getAdapter().getCount());
    }


    @Override
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }

}