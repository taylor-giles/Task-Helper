package giles.taskhelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LogTimeDialog extends AppCompatDialogFragment implements DatePickerDialog.DatePickerDialogListener {
  private TimeSelectionLayout timeSelector;
  private Spinner spinner;
  private LogTimeDialogListener listener;
  private long date = System.currentTimeMillis();
  private EditText dateText;

  @Override @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View view = inflater.inflate(R.layout.layout_log_time, null);
    timeSelector = view.findViewById(R.id.time_selection_log);
    dateText = view.findViewById(R.id.edit_date_log);

    //Set up spinner
    spinner = view.findViewById(R.id.spinner_task_log);
    ArrayList<Task> allTasks = (ArrayList<Task>)getArguments().getSerializable("tasks");
    ArrayList<String> taskNames = new ArrayList<>();
    for(Task task : allTasks){
      taskNames.add(task.getName());
    }
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
            android.R.layout.simple_spinner_dropdown_item, taskNames);
    spinner.setAdapter(adapter);

    //Auto-fill spinner if task is given
    Task task = (Task)getArguments().getSerializable("task");
    if(task != null){
      spinner.setSelection(taskNames.indexOf(task.getName()));
    }

    //Set up date selection
    date = System.currentTimeMillis();
    dateText.setText(new Date(date).toString());
    dateText.setOnClickListener(v -> openDatePicker(dateText));

    //Set up dialog
    builder.setView(view)
            .setTitle("Log Time")
            .setNegativeButton("Cancel", (dialog, which) -> {
              //Do nothing (cancel)
            })
            .setPositiveButton("Done", (dialog, which) -> {
              Task chosenTask = null;
              String taskName = taskNames.get(spinner.getSelectedItemPosition());
              for(Task t : allTasks){
                if(t.getName().equals(taskName)){
                  chosenTask = t;
                }
              }
              int minutesLogged = timeSelector.getHours() * 60 + timeSelector.getMinutes();
              listener.applyLogTime(chosenTask, minutesLogged);
            });

    return builder.create();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    //Guarantee that the context implements LogTimeDialogListener
    try {
      listener = (LogTimeDialogListener) context;
    } catch(ClassCastException e){
      throw new ClassCastException(context.toString() +
              " must implement LogTimeDialogListener");
    }
  }

  public void openDatePicker(View view){
    DatePickerDialog dialog = DatePickerDialog.newInstance(date);
    dialog.show(getActivity().getSupportFragmentManager(), "choose date");
  }

  static public LogTimeDialog newInstance(ArrayList<Task> allTasks, Task task){
    LogTimeDialog dialog = new LogTimeDialog();
    Bundle args = new Bundle();
    args.putSerializable("tasks", allTasks);
    args.putSerializable("task", task);
    dialog.setArguments(args);
    return dialog;
  }

  static public LogTimeDialog newInstance(ArrayList<Task> allTasks){
    return newInstance(allTasks, null);
  }

  @Override
  public void applyDate(long date) {
    this.date = date;
  }

  //Interface for returning data to host
  public interface LogTimeDialogListener{
    void applyLogTime(Task task, int minutesLogged);
  }
}
