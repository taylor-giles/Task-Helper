package giles.taskhelper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

public class TimeSelectionLayout extends LinearLayout {
  public TimeSelectionLayout(Context context) {
    super(context);
    init();
  }

  public TimeSelectionLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init(){
    inflate(getContext(), R.layout.layout_time_selection, this);
    NumberPicker hoursPicker = findViewById(R.id.picker_hours);
    NumberPicker minutesPicker = findViewById(R.id.picker_minutes);
    hoursPicker.setMaxValue(23);
    hoursPicker.setMinValue(0);
    minutesPicker.setMaxValue(59);
    minutesPicker.setMinValue(0);
  }

  public int getHours(){
    return ((NumberPicker)this.findViewById(R.id.picker_hours)).getValue();
  }

  public int getMinutes(){
    return ((NumberPicker)this.findViewById(R.id.picker_minutes)).getValue();
  }
}
