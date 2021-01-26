package giles.taskhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Activity for logging time for tasks - will be presented as a dialog
 */
public class LogTimeActivity extends AppCompatActivity implements DatePickerDialog.DatePickerDialogListener {
  private TimeSelectionLayout timeSelector;
  private Spinner spinner;
  private long dateMillis = System.currentTimeMillis();
  private EditText dateView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_log_time);
    setTitle("Log Time");
    setVisible(true);
    timeSelector = findViewById(R.id.time_selection_log);
    dateView = findViewById(R.id.text_date_log);

    //Set up spinner
    spinner = findViewById(R.id.spinner_task_log);
    String[] taskNames = getIntent().getStringArrayExtra("taskNames");
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, taskNames);
    spinner.setAdapter(adapter);

    //Auto-fill spinner if task is given
    String taskName = getIntent().getStringExtra("taskName");
    if(taskName != null) {
      for (int i = 0; i < taskNames.length; i++) {
        if (taskNames[i].equals(taskName)) {
          spinner.setSelection(i);
          break;
        }
      }
    }

    //Set up dateMillis selection
    applyDate(System.currentTimeMillis());
    dateView.setOnClickListener(v -> openDatePicker(dateView));

    //Cancel button
    Button cancelButton = findViewById(R.id.button_log_cancel);
    cancelButton.setOnClickListener(v -> {
      //Return to main activity
      setResult(Activity.RESULT_CANCELED, new Intent());
      finish();
    });

    //Done button
    Button doneButton = findViewById(R.id.button_log_finish);
    doneButton.setOnClickListener(v -> {
      Intent returnIntent = new Intent();

      //Set return data
      Log.d("log", taskNames[spinner.getSelectedItemPosition()]);
      returnIntent.putExtra("taskName", taskNames[spinner.getSelectedItemPosition()]);
      returnIntent.putExtra("dateMillis", dateMillis);
      returnIntent.putExtra("time", timeSelector.getHours() * 60 + timeSelector.getMinutes());

      //Return to main activity
      setResult(Activity.RESULT_OK, returnIntent);
      finish();
    });
  }

  public void openDatePicker(View view){
    DatePickerDialog dialog = DatePickerDialog.newInstance(dateMillis);
    dialog.show(getSupportFragmentManager(), "choose date");
  }

  @Override
  public void applyDate(long millis) {
    //Set new date millis
    this.dateMillis = millis;

    //Set date display
    Date date = new Date(dateMillis);
    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    StringBuilder dateText = new StringBuilder();
    switch(cal.get(Calendar.DAY_OF_WEEK)){
      case Calendar.SUNDAY: dateText.append("Sunday, "); break;
      case Calendar.MONDAY: dateText.append("Monday, "); break;
      case Calendar.TUESDAY: dateText.append("Tuesday, "); break;
      case Calendar.WEDNESDAY: dateText.append("Wednesday, "); break;
      case Calendar.THURSDAY: dateText.append("Thursday, "); break;
      case Calendar.FRIDAY: dateText.append("Friday, "); break;
      case Calendar.SATURDAY: dateText.append("Saturday, "); break;
    }
    dateText.append(cal.get(Calendar.MONTH) + 1);
    dateText.append("/");
    dateText.append(cal.get(Calendar.DATE));
    dateText.append("/");
    dateText.append(cal.get(Calendar.YEAR));
    dateView.setText(dateText.toString());
  }
}
