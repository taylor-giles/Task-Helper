package giles.taskhelper;

import java.io.Serializable;

public class TaskGoal implements Serializable {
  public static final int NONE = 0;
  public static final int MORE = 1;
  public static final int LESS = -1;

  private int type = NONE;
  private int mins = 0;

  public TaskGoal(){ }

  public TaskGoal(int type, int minutes) {
    if (type == NONE || type == MORE || type == LESS) {
      this.type = type;
    } else {
      throw new IllegalArgumentException("Type must be one of TaskGoal.NONE, TaskGoal.MORE, TaskGoal.LESS");
    }

    if (minutes >= 0) {
      this.mins = minutes;
    } else {
      throw new IllegalArgumentException("Minutes must not be negative");
    }
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getMinutes() {
    return mins;
  }

  public void setMinutes(int minutes) {
    this.mins = minutes;
  }

  /**
   * Determines whether the goal described by this object
   * was met if the given number of minutes was spent
   *
   * @param minutes The number of minutes spent on this task
   * @return The number of minutes still needed to meet the goal
   */
  public int minsToMeet(int minutes) {
    return (minutes * type) + (type * -1 * mins);
  }
}

