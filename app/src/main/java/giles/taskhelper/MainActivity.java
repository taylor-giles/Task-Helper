package giles.taskhelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  //Files
  private static final String TASKS_FILE = "tasks.obj";

  //Request codes
  public static final int ADD_TASK_REQUEST = 1;
  public static final int EDIT_TASK_REQUEST = 2;

  //Data
  private List<Task> tasks = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    tasks = loadTasks();
    setContentView(R.layout.activity_main);
    setVisible(true);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_CANCELED){
      if(resultCode == Activity.RESULT_OK) {
        //Add new task
        if (requestCode == ADD_TASK_REQUEST) {
          tasks.add(new Task(data.getStringExtra("name"),
                  data.getIntExtra("color", ContextCompat.getColor(this, R.color.grey_600)),
                  new Goal(data.getIntExtra("goalType", Goal.NONE), data.getIntExtra("goalMinutes", 0))));
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
}
