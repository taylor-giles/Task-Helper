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

import java.util.Date;

public class DatePickerDialog extends AppCompatDialogFragment {
  DatePickerDialogListener listener;

  @Override @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState){
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View view = inflater.inflate(R.layout.layout_date_picker, null);

    //Set default date to date given by host, or today
    CalendarView calendar = view.findViewById(R.id.calendar);
    if (getArguments() != null) {
      calendar.setDate(getArguments().getLong("date"));
    } else {
      calendar.setDate(System.currentTimeMillis());
    }

    //Set up dialog
    builder.setView(view)
            .setTitle("Date")
            .setNegativeButton("Cancel", (dialog, which) -> {
              //Do nothing (cancel)
            })
            .setPositiveButton("Done", (dialog, which) -> {
              listener.applyDate(calendar.getDate());
            });

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
    void applyDate(long date);
  }
}
