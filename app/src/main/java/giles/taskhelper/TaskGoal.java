package giles.taskhelper;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A class to represent a goal associated with a task
 */
public class TaskGoal implements Serializable {
  public static final int NONE = 0;
  public static final int MORE = 1;
  public static final int LESS = -1;

  public static final int SUNDAY = 0;
  public static final int MONDAY = 1;
  public static final int TUESDAY = 2;
  public static final int WEDNESDAY = 3;
  public static final int THURSDAY = 4;
  public static final int FRIDAY = 5;
  public static final int SATURDAY = 6;

  private int type = NONE;
  private int mins = 0;
  private boolean[] days = new boolean[7];
  private String name = "Goal";

  public TaskGoal() {
  }

  public TaskGoal(int type, int minutes) {
    this(type, minutes, new boolean[]{true, true, true, true, true, true, true});
  }

  public TaskGoal(int type, int minutes, boolean[] days){
    this(type, minutes, days, "Goal");
  }

  public TaskGoal(int type, int minutes, boolean[] days, String name) {
    setType(type);
    setMinutes(minutes);
    setDays(days);
    setName(name);
  }


  public int getType() {
    return type;
  }

  public void setType(int type) {
    if (type == NONE || type == MORE || type == LESS) {
      this.type = type;
    } else {
      throw new IllegalArgumentException("Type must be one of TaskGoal.NONE, TaskGoal.MORE, TaskGoal.LESS");
    }
  }

  public int getMinutes() {
    return mins;
  }

  public void setMinutes(int minutes) {
    if (minutes >= 0) {
      this.mins = minutes;
    } else {
      throw new IllegalArgumentException("Minutes must not be negative");
    }
  }

  public boolean[] getDays() {
    return days;
  }

  public void setDays(boolean[] days) {
    if(days.length == 7){
      this.days = days;
    } else {
      throw new IllegalArgumentException("Number of days must be equal to seven");
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Determines whether the goal described by this object
   * was met on the given day if the given number of minutes was spent
   *
   * @param minutes The number of minutes spent on this task
   * @param day The day of interest: should be one of TaskGoal.SUNDAY - TaskGoal.SATURDAY
   * @return The number of minutes still needed to meet the goal
   */
  public int minsToMeet(int minutes, int day) {
    if(checkDay(day)) {
      return (minutes * type) + (type * -1 * mins);
    } else {
      return 0;
    }
  }


  /**
   * Determines whether the goal described by this object
   * was met on the given <code>Date</code> if the given
   * number of minutes was spent
   *
   * @param minutes The number of minutes spent on this task during the given <code>Date</code>
   * @param date The <code>Date</code> of interest
   * @return The number of minutes still needed to meet the goal
   */
  public int minsToMeet(int minutes, Date date) {
    if(checkDate(date)) {
      return (minutes * type) + (type * -1 * mins);
    } else {
      return 0;
    }
  }


  /**
   * Determines whether or not this goal is active on the given day of the week
   * @param day The day of interest. Must be one of TaskGoal.SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, or SATURDAY
   * @return <code>true</code> if this <code>TaskGoal</code> is active on the given day, <code>false</code> otherwise.
   */
  public boolean checkDay(int day){
    if(day < SUNDAY || day > SATURDAY){
      throw new IllegalArgumentException("Invalid day given. Must be one of TaskGoal.SUNDAY - TaskGoal.SATURDAY");
    } else {
      return days[day];
    }
  }


  /**
   * Determines whether or not this goal is active on the given date
   * @param date The <code>Date</code> of interest
   * @return <code>true</code> if this <code>TaskGoal</code> is active on the given <code>Date</code>, <code>false</code> otherwise.
   */
  public boolean checkDate(Date date){
    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    switch(cal.get(Calendar.DAY_OF_WEEK)){
      case Calendar.SUNDAY: return checkDay(SUNDAY);
      case Calendar.MONDAY: return checkDay(MONDAY);
      case Calendar.TUESDAY: return checkDay(TUESDAY);
      case Calendar.WEDNESDAY: return checkDay(WEDNESDAY);
      case Calendar.THURSDAY: return checkDay(THURSDAY);
      case Calendar.FRIDAY: return checkDay(FRIDAY);
      case Calendar.SATURDAY: return checkDay(SATURDAY);
    }
    return false;
  }


  /**
   * Determines whether or not this goal was met on the given day if
   * the given amount of time was spent on its task
   * @param date The <code>Date</code> of interest
   * @param minutesSpent The amount of time, in minutes, spent on this goal's task on the given day
   * @return <code>true</code> if the goal was active and met on the given day, <code>false</code> otherwise
   */
  public boolean isMet(Date date, int minutesSpent){
    if(checkDate(date)){
      return (minutesSpent >= mins);
    } else {
      return false;
    }
  }
}

