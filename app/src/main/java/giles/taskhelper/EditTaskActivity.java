package giles.taskhelper;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class EditTaskActivity extends AppCompatActivity {

  private ColorSelectionLayout selectionLayout;
  private NumberPicker hoursPicker;
  private NumberPicker minutesPicker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_task);
    selectionLayout = new ColorSelectionLayout(this);

    //Set range for pickers
    hoursPicker = findViewById(R.id.picker_hours);
    minutesPicker = findViewById(R.id.picker_mins);
    hoursPicker.setMaxValue(23);
    hoursPicker.setMinValue(0);
    minutesPicker.setMaxValue(59);
    minutesPicker.setMinValue(0);
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
        //Send data back to main activity
        Intent returnIntent = new Intent();
        returnIntent.putExtra("name", nameView.getText().toString());
        returnIntent.putExtra("color", selectionLayout.getSelectedColor());

        //TaskGoal data
        returnIntent.putExtra("goalType", TaskGoal.NONE);
        if(findViewById(R.id.radio_greater).isSelected()){
          returnIntent.putExtra("goalType", TaskGoal.MORE);
        } else if(findViewById(R.id.radio_less).isSelected()){
          returnIntent.putExtra("goalType", TaskGoal.LESS);
        }
        returnIntent.putExtra("goalMinutes", hoursPicker.getValue() * 60 + minutesPicker.getValue());

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
