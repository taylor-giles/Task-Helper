package giles.taskhelper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CollapsedTaskView extends LinearLayout {
  private TextView taskLabel;
  private TextView timeDisplay;
  private ImageButton expandButton;
  private Task task;

  public CollapsedTaskView(Context context) {
    super(context);
    this.setOrientation(HORIZONTAL);
  }

  public CollapsedTaskView(Context context, @NonNull Task task){
    super(context);
    this.setOrientation(HORIZONTAL);
    this.task = task;
    taskLabel = new TextView(context);
    timeDisplay = new TextView(context);
    taskLabel.setText(task.getName());

    //Time spent label

  }
}