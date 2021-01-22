package giles.taskhelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LogTimeDialog.LogTimeDialogListener, DatePickerDialog.DatePickerDialogListener {
  //Files
  private static final String TASKS_FILE = "tasks.obj";

  //Request codes
  public static final int ADD_TASK_REQUEST = 1;
  public static final int EDIT_TASK_REQUEST = 2;

  //Data
  private List<Task> tasks = new ArrayList<>();
  private List<TaskView> taskViews = new ArrayList<>();
  private TimeFilter currentFilter = new TimeFilter(TimeFilter.ONE_WEEK);

  //Layout elements
  private ImageButton logButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    tasks = loadTasks();
    setContentView(R.layout.activity_main);
    setVisible(true);

    //Log Time button
    logButton = findViewById(R.id.button_log_time);
    logButton.setOnClickListener(v -> openLogDialog(null));
    if(tasks.isEmpty()){
      logButton.setVisibility(View.GONE);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    //Hide log button if and only if there are no tasks
    if(tasks.isEmpty()){
      logButton.setVisibility(View.GONE);
    } else {
      logButton.setVisibility(View.VISIBLE);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_CANCELED){
      if(resultCode == Activity.RESULT_OK) {
        //Add new task
        if (requestCode == ADD_TASK_REQUEST) {
          //Build and add new task
          Task newTask = new Task(data.getStringExtra("name"),
                  data.getIntExtra("color", ContextCompat.getColor(this, R.color.grey_600)),
                  new TaskGoal(data.getIntExtra("goalType", TaskGoal.NONE), data.getIntExtra("goalMinutes", 0)));
          tasks.add(newTask);

          saveToFile(TASKS_FILE, tasks);

          //Build and add new TaskView
          TaskView newView = new TaskView(this, newTask, currentFilter);
          taskViews.add(newView);
          ((LinearLayout)findViewById(R.id.layout_scroll_main)).addView(newView);

          //There is at least one task now, so show log button
          logButton.setVisibility(View.VISIBLE);
        }
      }
    }
  }


  /**
   * Opens the EditTask Activity to add a new task
   * @param view The view that was pressed to activate this method
   */
  public void openEditToAdd(View view){
    Intent openEdit = new Intent(this, EditTaskActivity.class);
    startActivityForResult(openEdit, ADD_TASK_REQUEST);
  }


  /**
   * Opens a LogTimeDialog to log time for a task
   */
  public void openLogDialog(Task task){
    LogTimeDialog dialog = LogTimeDialog.newInstance((ArrayList<Task>)tasks, task);
    dialog.show(getSupportFragmentManager(), "log time");
  }


  /**
   * Serializes the given <code>Object</code> and saves it to the specified file
   * @param filename The location of the file to save to
   * @param obj The <code>Object</code> to serialize and save
   */
  private void saveToFile(String filename, Object obj){
    try {
      FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE);
      ObjectOutputStream os = new ObjectOutputStream(fos);
      os.writeObject(obj);
      os.close();
      fos.close();
    } catch (IOException e) {
      Toast.makeText(this, "An error occurred when saving data", Toast.LENGTH_LONG).show();
    }
  }


  /**
   * Reads in a serialized <code>Object</code> from the specified file
   * @param filename The location of the file to read from
   * @return The <code>Object</code> read in from file
   */
  private Object loadFromFile(String filename){
    try {
      FileInputStream fis = this.openFileInput(filename);
      ObjectInputStream is = new ObjectInputStream(fis);
      Object obj = is.readObject();
      is.close();
      fis.close();
      return obj;
    } catch(FileNotFoundException e){
      //Create file if it does not already exist
      saveToFile(filename, null);
    } catch(IOException | ClassNotFoundException e){
      e.printStackTrace();
      Toast.makeText(this, "An error occurred when reading data from file", Toast.LENGTH_LONG).show();
    }
    return null;
  }


  /**
   * Gets the tasks object from file
   * @return The object stored at TASKS_FILE
   */
  private ArrayList<Task> loadTasks(){
    try {
      ArrayList<Task> newTasks = (ArrayList<Task>) loadFromFile(TASKS_FILE);
      if(newTasks == null){
        return new ArrayList<>();
      }
      return newTasks;
    } catch(ClassCastException e) {
      e.printStackTrace();
      Toast.makeText(this, "Error processing data from file", Toast.LENGTH_SHORT).show();
      return new ArrayList<>();
    }
  }


  /**
   * Adds the specified amount of time spent to the specified task
   * @param task The <code>Task</code> of interest
   * @param minutesLogged The amount of time, in minutes, to add to the given <code>Task</code>
   */
  @Override
  public void applyLogTime(Task task, int minutesLogged) {
    task.addTime(minutesLogged);

    //Update the necessary view
    for(TaskView view : taskViews){
      if(view.getTask().equals(task)){
        view.update();
      }
    }
  }

  @Override
  public void applyDate(long date) {

  }
}
