package giles.taskhelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerDialog extends AppCompatDialogFragment {
  DatePickerDialogListener listener;

  @Override @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState){
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View view = inflater.inflate(R.layout.layout_date_picker, null);

    //Set up calendarView
    CalendarView calendarView = view.findViewById(R.id.calendar);
    calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
      Calendar calendar = Calendar.getInstance();
      calendar.set(year, month, dayOfMonth);
      calendarView.setDate(calendar.getTimeInMillis());
    });
    if (getArguments() != null) {
      calendarView.setDate(getArguments().getLong("date"));
    } else {
      calendarView.setDate(System.currentTimeMillis());
    }

    //Set up dialog
    builder.setView(view)
            .setTitle("Choose Date")
            .setNegativeButton("Cancel", (dialog, which) -> {
              //Do nothing (cancel)
            })
            .setPositiveButton("Done", (dialog, which) -> listener.applyDate(calendarView.getDate()));

    return builder.create();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    //Guarantee that the context implements DatePickerDialogListener
    try {
      listener = (DatePickerDialogListener) context;
    } catch(ClassCastException e){
      throw new ClassCastException(context.toString() +
              " must implement DatePickerDialogListener");
    }
  }

  static public DatePickerDialog newInstance(long date){
    DatePickerDialog dialog = new DatePickerDialog();
    Bundle args = new Bundle();
    args.putLong("date", date);
    dialog.setArguments(args);
    return dialog;
  }

  //Interface for returning data to host
  public interface DatePickerDialogListener {
    void applyDate(long millis);
  }
}
