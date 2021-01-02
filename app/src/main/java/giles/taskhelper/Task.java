package giles.taskhelper;

import android.support.annotation.Nullable;

import java.util.ArrayList;

public class Task {
    private String name;
    private int color;
    private ArrayList<TaskEntry> entries;

    /**
     * Constructors
     */
    public Task(String name, int color){
        this.name = name;
        this.color = color;
        entries = new ArrayList<>();
    }
    public Task(String name, int color, ArrayList<TaskEntry> entries){
        this.name = name;
        this.color = color;
        this.entries = new ArrayList<>(entries);
    }

    //Getters and setters
    public ArrayList<TaskEntry> getEntries() {
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


    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Task &&
                ((this.name.equals(((Task) obj).getName())) &&
                        (this.color == ((Task) obj).getColor()));
    }

}
