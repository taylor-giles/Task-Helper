package giles.taskhelper;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditTaskActivity extends AppCompatActivity {

  private ColorSelectionLayout selectionLayout;
  private ArrayList<GoalEditView> goalViews = new ArrayList<>();
  private Button addGoalButton;
  private LinearLayout goalLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_task);
    goalLayout = findViewById(R.id.layout_goals);

    //Set up button onClick
    addGoalButton = findViewById(R.id.button_add_goal);
    addGoalButton.setOnClickListener(v -> {
      GoalEditView goalView = new GoalEditView(this);
      goalViews.add(goalView);
      goalLayout.addView(goalView);

      //Divider
      View divider = new View(this);
      divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
      divider.setBackgroundColor(ContextCompat.getColor(this, android.R.color.tab_indicator_text));
      goalLayout.addView(divider);
    });

    //Add ColorSelectionLayout
    selectionLayout = new ColorSelectionLayout(this);
    ((LinearLayout)findViewById(R.id.layout_color)).addView(selectionLayout);
  }

  public void done(View view){
    TextView nameView = findViewById(R.id.text_edit_name);
    if(nameView.getText().toString().equals("") || nameView.getText() == null){
      Toast.makeText(this, "Please provide a name for your task", Toast.LENGTH_LONG).show();
    } else {
      if(selectionLayout.getSelectedColor() == ColorSelectionLayout.ERROR){
        Toast.makeText(this, "Please provide a color for your task", Toast.LENGTH_LONG).show();
      } else {
        Intent returnIntent = new Intent();

        //Build and return task
        ArrayList<TaskGoal> goals = new ArrayList<>();
        for(GoalEditView goalView : goalViews){
          if(goalView.isActive()) {
            goals.add(goalView.getGoal());
          }
        }
        Task task = new Task(nameView.getText().toString(), selectionLayout.getSelectedColor(), goals);
        returnIntent.putExtra("task", task);

        //Return to main activity
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
      }
    }
  }

  public void cancel(View view){
    Intent returnIntent = new Intent();
    setResult(Activity.RESULT_CANCELED, returnIntent);
    finish();
  }
}
