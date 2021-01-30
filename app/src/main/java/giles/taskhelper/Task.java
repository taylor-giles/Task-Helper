package giles.taskhelper;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

public class Task implements Serializable {
  private String name;
  private int color;
  private ArrayList<TaskEntry> entries;
  private ArrayList<TaskGoal> goals;

  /**
   * Constructors
   */
  public Task(String name, int color){
    this(name, color, new ArrayList<>(), new ArrayList<>());
  }
  public Task(String name, int color, ArrayList<TaskGoal> goals){
    this(name, color, goals, new ArrayList<>());
  }
  public Task(String name, int color, ArrayList<TaskGoal> goals, ArrayList<TaskEntry> entries){
    this.name = name;
    this.color = color;
    this.goals = goals;
    this.entries = new ArrayList<>(entries);
  }


  /**
   * Returns a list of all the <code>TaskEntry</code>s that fit within the given time filter
   * @param filter The <code>TimeFilter</code> used to determine the desired <code>TaskEntry</code>s
   * @return An <code>ArrayList</code> of <code>TaskEntry</code> objects
   */
  public ArrayList<TaskEntry> getMatchingEntries(TimeFilter filter){
    return (ArrayList<TaskEntry>) entries.stream().filter(filter).collect(Collectors.toList());
  }


  /**
   * Returns a list of all the <code>TaskEntry</code>s that occurred on the given <code>Date</code>
   * @param date The <code>Date</code> of interest
   * @return An <code>ArrayList</code> of <code>TaskEntry</code> objects
   */
  public ArrayList<TaskEntry> getMatchingEntries(Date date){
    return (ArrayList<TaskEntry>) entries.stream().filter(new TimeFilter(date, date)).collect(Collectors.toList());
  }


  /**
   * Determines the total amount of time spent on this task since its creation
   * @return The amount of time spent on this task
   */
  public long getTotalTimeSpent(){
    return getTimeSpent(new TimeFilter(TimeFilter.ALL_TIME));
  }


  /**
   * Determines the amount of time spent on this task during the period specified by the given
   * <code>TimeFilter</code>
   * @param filter The <code>TimeFilter</code> that dictates the period of interest
   * @return The amount of time, in minutes, spent on this task
   */
  public int getTimeSpent(TimeFilter filter){
    int sum = 0;
    for(TaskEntry entry : entries.stream().filter(filter).collect(Collectors.toList())){
      sum += entry.getDuration();
    }
    return sum;
  }


  /**
   * Determines the amount of time spent on this task during the specified <code>Date</code>
   * @param date The <code>Date</code> of interest
   * @return The amount of time spent on this task during the given <code>Date</code>
   */
  public int getTimeSpent(Date date){
    return getTimeSpent(new TimeFilter(date, 1));
  }


  /**
   * Creates a new <code>TaskEntry</code> to associate with this task with the specified
   * amount of time
   * @param minutes The number of minutes to add to the time spent on this task
   */
  public void addTime(int minutes){
    entries.add(new TaskEntry(this, new Date(), minutes));
  }


  /**
   * Creates a new <code>TaskEntry</code> to associate with this task with the specified
   * date and amount of time
   * @param date The data to apply to the new <code>TaskEntry</code>
   * @param minutes The number of minutes to add to the time spent on this task
   */
  public void addTime(Date date, int minutes){
    entries.add(new TaskEntry(this, date, minutes));
  }


  /**
   * Adds a new <code>TaskEntry</code> to associate with this task
   * @param entry The entry to add to this task
   */
  public void addEntry(TaskEntry entry){
    entries.add(entry);
  }


  /**
   * Adds a new <code>TaskGoal</code> to be associated with this <code>Task</code>
   * @param goal The <code>TaskGoal</code> to add to this task
   */
  public void addGoal(TaskGoal goal){
    this.goals.add(goal);
  }


  /**
   * Determines which goals associated with this <code>Task</code> apply on the given <code>Date</code>.
   * @param date The <code>Date</code> of interest
   * @return A list of this task's <code>TaskGoal</code>s that apply on the given date
   */
  public ArrayList<TaskGoal> getGoals(Date date){
    ArrayList<TaskGoal> output = new ArrayList<>();
    for(TaskGoal goal : goals){
      if(goal.checkDate(date)){
        output.add(goal);
      }
    }
    return output;
  }


  /**
   * Two tasks are equal if their names and colors are equal
   * @param obj The object to test for equality with this <code>Task</code>
   * @return <code>true</code> iff this <code>Task</code> is equal to the given <code>Object</code>
   */
  @Override
  public boolean equals(@Nullable Object obj) {
    return obj instanceof Task &&
            ((this.name.equals(((Task) obj).getName())) &&
                    (this.color == ((Task) obj).getColor()));
  }


  //Getters and setters
  public ArrayList<TaskEntry> getAllEntries() {
    return entries;
  }
  public void setEntries(ArrayList<TaskEntry> entries) {
    this.entries = entries;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public int getColor() {
    return color;
  }
  public void setColor(int color) {
    this.color = color;
  }
  public ArrayList<TaskGoal> getAllGoals() {
    return goals;
  }
  public void setGoal(ArrayList<TaskGoal> goals) {
    this.goals = goals;
  }
}
