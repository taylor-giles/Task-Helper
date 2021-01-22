package giles.taskhelper;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class TaskEntry implements Serializable {
    private Task task;
    private Date date;
    private int duration; //The amount of time represented by this entry, in minutes

    public TaskEntry(Task task){
        this.task = task;
        this.date = Calendar.getInstance().getTime();
    }

    public TaskEntry(Task task, Date date){
        this.task = task;
        this.date = date;
    }

    public TaskEntry(Task task, Date date, int duration){
        this.task = task;
        this.date = date;
        this.duration = duration;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Returns the number of hours represented by this <code>TaskEntry</code>
     * @return The number of hours represented by this <code>TaskEntry</code>
     */
    public int getHours(){
        return (duration / 60);
    }

    /**
     * Returns the number of minutes left over after hours calculation
     * @return The number of minutes left over after hours calculation
     */
    public int getMinutes(){
        return duration - (60 * getHours());
    }
}
