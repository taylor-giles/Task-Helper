package giles.taskhelper;

import java.util.Calendar;
import java.util.Date;

class TaskEntry {
    private Task task;
    private Date date;
    private long duration; //The amount of time represented by this entry, in minutes

    public TaskEntry(Task task){
        this.task = task;
        this.date = Calendar.getInstance().getTime();
    }

    public TaskEntry(Task task, Date date){
        this.task = task;
        this.date = date;
    }

    public TaskEntry(Task task, Date date, long duration){
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

    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Returns the number of hours represented by this <code>TaskEntry</code>
     * @return The number of hours represented by this <code>TaskEntry</code>
     */
    public long getHours(){
        return (int)(duration / 60);
    }

    /**
     * Returns the number of minutes left over after hours calculation
     * @return The number of minutes left over after hours calculation
     */
    public long getMinutes(){
        return duration - (60 * getHours());
    }
}
