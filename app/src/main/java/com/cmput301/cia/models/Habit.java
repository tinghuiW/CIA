/*
 * Copyright (c) 2017 CMPUT301F17T15. This project is distributed under the MIT license.
 */

package com.cmput301.cia.models;

import com.cmput301.cia.utilities.ElasticSearchUtilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Version 2
 * Author: Adil Malik
 * Date: Oct 14 2017
 *
 * This class represents a habit that a user has created
 * Keeps track of all habit events the user has completed/missed related to the habit
 */

public class Habit extends ElasticSearchable implements Serializable {

    public static final String TYPE_ID = "habit";

    // Constants for days of the week
    public static final int SUNDAY = 1, MONDAY = 2, TUESDAY = 3, WEDNESDAY = 4, THURSDAY = 5, FRIDAY = 6, SATURDAY = 7;

    // What category this habit falls under
    private String type;

    private String title;
    private String reason;
    private Date startDate;
    // Successfully completed events
    private List<HabitEvent> events;

    // The days of the week this habit should occur on
    private List<Integer> daysOfWeek;

    // The dates this event was missed on
    private List<Date> missedDates;

    /**

     * Construct a new habit object
     * @param title the name of the habit (not null)
     * @param reason the reason prompting the habit (not null)
     * @param startDate the date the user wants to start the habit (not null)
     * @param days the days of the week the event should occur on (Sunday = 1, Saturday = 7)
     * @param type the habit type category this habit falls under
     */
    public Habit(String title, String reason, Date startDate, List<Integer> days, String type) {
        this.type = type;
        this.title = title;
        this.reason = reason;
        this.startDate = startDate;
        events = new ArrayList<>();
        daysOfWeek = days;
        missedDates = new ArrayList<>();
    }

    /**
     * @return the days of the week this habit occurs on
     */
    public List<Integer> getDaysOfWeek() {
        return daysOfWeek;
    }

    /**
     * Set the days of the week this habit occurs on
     * @param daysOfWeek days this habit should occur on (Sunday = 1, Saturday = 7)
     */
    public void setDaysOfWeek(List<Integer> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    /**
     * Add a new habit event to the list of successfully completed ones for this habit
     * @param event the event to add
     */
    public void addHabitEvent(HabitEvent event){
        events.add(event);
    }

    /**
     * Remove a successfully completed habit event
     * @param event the event to remove
     */
    public void removeHabitEvent(HabitEvent event){
        events.remove(event);
    }

    /**
     * @return the habit's name
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the habit's name
     * @param title the new name for the habit
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the reason for creating this habit
     */
    public String getReason() {
        return reason;
    }

    /**
     * Set the reason for creating this habit
     * @param reason reason for creating this habit
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the date at which this habit should be started
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Set the date at which this habit should be started
     * @param startDate the new start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return all successfully completed events falling under this habit
     */
    public List<HabitEvent> getEvents() {
        return events;
    }

    /**
     * @param day the day number of the week (Sunday = 1, Saturday = 7)
     * @return whether this event occurs on the specified day
     */
    public boolean occursOn(int day){
        for (Integer integer : daysOfWeek){
            if (integer == day)
                return true;
        }
        return false;
    }

    /**
     * @return the number of times this habit was missed
     */
    public int getTimesMissed() {
        return missedDates.size();
    }

    /**
     * @return the number of times this habit was completed on time
     */
    public int getTimesCompleted() {
        return events.size();
    }

    /**
     * Increase the number of times this habit was missed
     * @param date is the date the event was missed on
     */
    public void miss(Date date){
        missedDates.add(date);
    }

    /**
     * @return the most recent event under this habit if exists (null otherwise)
     */
    public HabitEvent getMostRecentEvent(){
        if (events.size() == 0)
            return null;
        return events.get(events.size() - 1);
    }

    /**
     * @return the dates that this habit was missed
     */
    public List<Date> getMissedDates(){
        return missedDates;
    }

    /**
     * @return the last date this habit was succesfully completed on (null if there was none)
     */
    public Date getLastCompletionDate(){
        if (events.size() == 0)
            return null;
        return events.get(events.size() - 1).getDate();
    }

    /**
     * Set the habit's type category
     * @param typeStr the category
     */
    public void setType(String typeStr){ //edit by guanfang
        this.type = typeStr;
    }

    /**
     * @return the habit's type category
     */
    public String getType(){return this.type;}

    /**
     * @return the object's template type id
     */
    @Override
    public String getTypeId() {
        return TYPE_ID;
    }

    /**
     * Serialize this object to the ElasticSearch server
     */
    @Override
    public void save() {
        // TODO: for saving, maybe do it in Profile and then loop through each Habit stored in
        // the database and assign it's 'creator' parameter to the profile's getId() result

        // TODO: save the user who created it's ID as 'creator'
        ElasticSearchUtilities.save(this);

        // TODO: save the event's 'base' parameter as getId()
        for (HabitEvent event : events)
            ElasticSearchUtilities.save(event);
    }

    /**
     * Synchronize this object from the ElasticSearch server
     */
    @Override
    public void load() {

        Habit found = ElasticSearchUtilities.getObject(getTypeId(), Habit.class, getId());
        if (found != null){
            Map<String, String> params = new HashMap<>();
            params.put("base", getId());
            List<HabitEvent> foundEvents = ElasticSearchUtilities.getListOf(HabitEvent.TYPE_ID, HabitEvent.class, params);
            // TODO: copy from vars into this
        }
    }

    /**
     * Delete this object from the ElasticSearch server
     */
    @Override
    public void delete() {
        for (HabitEvent event : events)
            ElasticSearchUtilities.delete(event);
        ElasticSearchUtilities.delete(this);
    }

    @Override
    public String toString(){
        return title;
    }
}
