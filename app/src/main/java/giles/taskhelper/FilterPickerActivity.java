package giles.taskhelper;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Activity for changing the time filter - presented as a dialog
 */
public class FilterPickerActivity extends AppCompatActivity implements DatePickerDialog.DatePickerDialogListener {
  private EditText editStart;
  private EditText editEnd;
  private Button cancelButton;
  private Button finishButton;

  private Date startDate;
  private Date endDate;

  /**
   * Set before calling DatePickerDialog.
   * Used to determine which date should be changed
   * when the dialog returns.
   * If true, start date should be changed. If false,
   * end date should be changed.
   */
  private boolean applyingStart = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_filter_picker);
    setTitle("Select Time Frame");
    setVisible(true);

    editStart = findViewById(R.id.edit_start_date);
    editEnd = findViewById(R.id.edit_end_date);
    cancelButton = findViewById(R.id.button_filter_cancel);
    finishButton = findViewById(R.id.button_filter_finish);

    //Get data from main activity - end date then start date
    applyingStart = false;
    applyDate(getIntent().getLongExtra("endMillis", System.currentTimeMillis()));
    applyingStart = true;
    applyDate(getIntent().getLongExtra("startMillis", System.currentTimeMillis()));

    //Cancel button behavior
    cancelButton.setOnClickListener(v -> {
      //Return to main activity
      setResult(Activity.RESULT_CANCELED, new Intent());
      finish();
    });

    //Finish button behavior
    finishButton.setOnClickListener(v -> {
      Intent returnIntent = new Intent();

      //Set return data
      returnIntent.putExtra("startMillis", startDate.getTime());
      returnIntent.putExtra("endMillis", endDate.getTime());

      //Return to main activity
      setResult(Activity.RESULT_OK, returnIntent);
      finish();
    });
  }

  public void openDatePickerDialog(View view){
    if(view.equals(editStart)){
      applyingStart = true;
      DatePickerDialog dialog = DatePickerDialog.newInstance(startDate.getTime(), "Start Date");
      dialog.show(getSupportFragmentManager(), "choose date");
    } else if (view.equals(editEnd)){
      applyingStart = false;
      DatePickerDialog dialog = DatePickerDialog.newInstance(endDate.getTime(), "End Date");
      dialog.show(getSupportFragmentManager(), "choose date");
    }
  }

  @Override
  public void applyDate(long millis) {
    //Construct text for date
    Date date = new Date(millis);
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

    //Apply changes
    if(applyingStart){
      startDate = date;
      editStart.setText(dateText.toString());
    } else {
      endDate = date;
      editEnd.setText(dateText.toString());
    }
  }
}
