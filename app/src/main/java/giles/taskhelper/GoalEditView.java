package giles.taskhelper;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

public class GoalEditView extends LinearLayout {
  private TimeSelectionLayout timeSelector;
  private CheckBox[] checkBoxes = new CheckBox[7];
  private ImageButton removeButton;
  private EditText nameEdit;
  private boolean isActive = true;

  public GoalEditView(Context context) {
    super(context);
    this.setOrientation(VERTICAL);
    this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    this.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
    this.setPadding(0, 20, 0, 20);

    //Create title
    nameEdit = new EditText(context);
    nameEdit.setText("New Goal");
    nameEdit.setHint("Goal Name");
    nameEdit.setTextColor(ContextCompat.getColor(context, android.R.color.black));
    nameEdit.setTextSize(18);
    nameEdit.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 2));

    //Create remove button
    removeButton = new ImageButton(context);
    removeButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_delete_gray_24dp));
    removeButton.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
    removeButton.setOnClickListener(v -> {
      isActive = false;
      setVisibility(GONE);
    });
    removeButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

    //Create checkboxes
    LinearLayout checkboxLayout = new LinearLayout(context);
    checkboxLayout.setOrientation(VERTICAL);
    checkboxLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    for(int i = TaskGoal.SUNDAY; i <= TaskGoal.SATURDAY; i++){
      CheckBox checkbox = new CheckBox(context);
      checkbox.setChecked(true);
      checkBoxes[i] = checkbox;
      switch(i){
        case TaskGoal.SUNDAY: checkbox.setText("Sunday"); break;
        case TaskGoal.MONDAY: checkbox.setText("Monday"); break;
        case TaskGoal.TUESDAY: checkbox.setText("Tuesday"); break;
        case TaskGoal.WEDNESDAY: checkbox.setText("Wednesday"); break;
        case TaskGoal.THURSDAY: checkbox.setText("Thursday"); break;
        case TaskGoal.FRIDAY: checkbox.setText("Friday"); break;
        case TaskGoal.SATURDAY: checkbox.setText("Saturday"); break;
      }
      checkboxLayout.addView(checkbox);
    }

    //Time Selector
    timeSelector = new TimeSelectionLayout(context);

    //Spacers
    Space space1 = new Space(context);
    space1.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
    Space space2 = new Space(context);
    space2.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
    Space space3 = new Space(context);
    space3.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
    View divider = new View(context);
    divider.setLayoutParams(new LayoutParams(1, LayoutParams.MATCH_PARENT));
    divider.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));

    //Add views
    LinearLayout topLayout = new LinearLayout(context);
    topLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    topLayout.setOrientation(HORIZONTAL);
    topLayout.setPadding(10, 10, 10, 10);
    topLayout.setGravity(Gravity.CENTER_VERTICAL);
    topLayout.addView(nameEdit);
    topLayout.addView(space1);
    topLayout.addView(removeButton);

    LinearLayout contentLayout = new LinearLayout(context);
    contentLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    contentLayout.setOrientation(HORIZONTAL);
    contentLayout.setGravity(Gravity.CENTER);
    contentLayout.addView(checkboxLayout);
    contentLayout.addView(space2);
    contentLayout.addView(divider);
    contentLayout.addView(space3);
    contentLayout.addView(timeSelector);

    this.addView(topLayout);
    this.addView(contentLayout);
  }


  /**
   * Builds and returns a <code>TaskGoal</code> based on the selections made in this view
   * @return A new <code>TaskGoal</code> reflecting the choices made in this view
   */
  public TaskGoal getGoal(){
    if(isActive){
      boolean[] days = new boolean[7];
      for(int i = 0; i < checkBoxes.length; i++){
        days[i] = checkBoxes[i].isChecked();
      }
      String name = "New Goal";
      if(!nameEdit.getText().toString().equals("")){
        name = nameEdit.getText().toString();
      }
      return new TaskGoal(TaskGoal.MORE, timeSelector.getHours() * 60 + timeSelector.getMinutes(), days, name);
    } else {
      return new TaskGoal(TaskGoal.NONE, 0);
    }
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
    if(!active){
      this.setVisibility(GONE);
    } else {
      this.setVisibility(VISIBLE);
    }
  }
}
