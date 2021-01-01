package giles.taskhelper;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CollapsedTaskView extends LinearLayout {
  private TextView taskLabel;
  private TextView timeDisplay;
  private Task task;

  public CollapsedTaskView(Context context) {
    super(context);
    this.setOrientation(HORIZONTAL);
  }


}
