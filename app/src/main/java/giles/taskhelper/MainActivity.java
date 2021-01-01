package giles.taskhelper;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

  //Request codes
  public static final int ADD_TASK_REQUEST = 1;

  //Data
  private ArrayList<Task> tasks = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setVisible(true);
  }

  public void openEdit(View view){
    Intent openEdit = new Intent(this, EditTaskActivity.class);
    startActivityForResult(openEdit, ADD_TASK_REQUEST);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_CANCELED){
      if(resultCode == Activity.RESULT_OK) {

        //Add new task
        if (requestCode == ADD_TASK_REQUEST) {
          tasks.add(new Task(data.getStringExtra("name"),
                  data.getIntExtra("color", ContextCompat.getColor(this, R.color.grey_600))));
        }
      }
    }


  }




}
