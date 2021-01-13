package giles.taskhelper;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class Task {
  private String name;
  private int color;
  private ArrayList<TaskEntry> entries;
  private TaskGoal goal;

  /**
   * Constructors
   */
  public Task(String name, int color){
    this(name, color, new TaskGoal(), new ArrayList<>());
  }
  public Task(String name, int color, TaskGoal goal){
    this(name, color, goal, new ArrayList<>());
  }
  public Task(String name, int color, TaskGoal goal, ArrayList<TaskEntry> entries){
    this.name = name;
    this.color = color;
    this.goal = goal;
    this.entries = new ArrayList<>(entries);
  }

  //Getters and setters
  public ArrayList<TaskEntry> getAllEntries() {
    return entries;
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

  public TaskGoal getGoal() {
    return goal;
  }

  public void setGoal(TaskGoal goal) {
    this.goal = goal;
  }

  public long getTotalTimeSpent(){
    return getTimeSpent(new TimeFilter(TimeFilter.ALL_TIME));
  }

  /**
   * Determines the amount of time spent on this task during the period specified by the given
   * <code>TimeFilter</code>
   * @param filter The <code>TimeFilter</code> that dictates the period of interest
   * @return The amount of time, in minutes, spent on this task
   */
  public long getTimeSpent(TimeFilter filter){
    long sum = 0;
    for(TaskEntry entry : entries.stream().filter(filter).collect(Collectors.toList())){
      sum += entry.getDuration();
    }
    return sum;
  }

  public long getTimeSpent(Date date){
    return getTimeSpent(new TimeFilter(date, 1));
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    return obj instanceof Task &&
            ((this.name.equals(((Task) obj).getName())) &&
                    (this.color == ((Task) obj).getColor()));
  }

}
