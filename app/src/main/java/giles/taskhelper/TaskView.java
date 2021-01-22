package giles.taskhelper;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TaskView extends LinearLayout {
  private Task task;
  private TimeFilter filter;
  private TaskViewHeader header;
  private LinearLayout expandedContent;
  private BarChart barChart;

  public TaskView(Context context) {
    super(context);
    setOrientation(VERTICAL);
    setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    setLayoutTransition(new LayoutTransition());
  }

  public TaskView(Context context, Task task, TimeFilter filter){
    this(context);
    this.task = task;
    this.filter = filter;
    this.header = new TaskViewHeader(context, task, filter);
    header.getExpandButton().setOnClickListener(v -> expand());
    header.getCollapseButton().setOnClickListener(v -> collapse());

    expandedContent = new LinearLayout(context);
    expandedContent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    expandedContent.setOrientation(VERTICAL);
    barChart = new BarChart(context);
    barChart.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 800));

    Space space = new Space(context);
    space.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 50));

    View divider = new View(context);
    divider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
    divider.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));

    expandedContent.addView(space);
    expandedContent.addView(barChart);

    collapse();
    update();
    addView(divider);
    addView(header);
    addView(expandedContent);
  }

  public void expand(){
    header.getExpandButton().setVisibility(View.GONE);
    header.getCollapseButton().setVisibility(View.VISIBLE);
    expandedContent.setVisibility(View.VISIBLE);
  }

  public void collapse(){
    header.getExpandButton().setVisibility(View.VISIBLE);
    header.getCollapseButton().setVisibility(View.GONE);
    expandedContent.setVisibility(View.GONE);
  }

  public void update(){
    header.update();
    ArrayList<BarEntry> barEntries = new ArrayList<>();

    //Update bar chart with index of task data from each day
    //NOTE - use index so that labels are aligned properly. Labeling is handled below
    for(int i = 0; i < filter.getDates().size(); i++){
      barEntries.add(new BarEntry(i, task.getTimeSpent(filter.getDates().get(i))));
    }
    BarDataSet barData = new BarDataSet(barEntries, "Time Spent on " + task.getName());
    barData.setColor(task.getColor());
    barChart.setData(new BarData(barData));
    barChart.getDescription().setEnabled(false); //Disable bottom-right description text

    //Format X-Axis labels as dates
    barChart.getXAxis().setValueFormatter(new ValueFormatter() {
      @Override
      public String getFormattedValue(float value) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(filter.getDates().get((int)value));
        StringBuilder output = new StringBuilder();
        switch(cal.get(Calendar.DAY_OF_WEEK)){
          case Calendar.SUNDAY: output.append("Sun, "); break;
          case Calendar.MONDAY: output.append("Mon, "); break;
          case Calendar.TUESDAY: output.append("Tue, "); break;
          case Calendar.WEDNESDAY: output.append("Wed, "); break;
          case Calendar.THURSDAY: output.append("Thu, "); break;
          case Calendar.FRIDAY: output.append("Fri, "); break;
          case Calendar.SATURDAY: output.append("Sat, "); break;
        }
        output.append(cal.get(Calendar.MONTH) + 1);
        output.append("/");
        output.append(cal.get(Calendar.DATE));
        output.append("/");
        output.append(("" + cal.get(Calendar.YEAR)).substring(2));
        return output.toString();
      }
    });
    barChart.getXAxis().setLabelCount(barData.getValues().size());
    barChart.getXAxis().setLabelRotationAngle(-30); //Rotate x-axis labels

    //Set Y-Axis labels
    barChart.getAxisRight().setDrawLabels(false);
    barChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
      @Override
      public String getFormattedValue(float value) {
        StringBuilder output = new StringBuilder();
        if((int)(value / 60) > 0){
          output.append((int) (value / 60)).append("h ");
        }
        output.append((int)(value) % 60).append("m");
        return output.toString();
      }
    });
    barChart.getAxisLeft().setAxisMinimum(0);
  }

  public TimeFilter getFilter() {
    return filter;
  }

  public void setFilter(TimeFilter filter) {
    this.filter = filter;
    header.setFilter(filter);
  }

  public Task getTask(){
    return task;
  }


  /**
   * A specialized <code>LinearLayout</code> used for the headers of <code>TaskView</code>s
   */
  private class TaskViewHeader extends LinearLayout {
    private final int TITLE_SIZE = 20;
    private final int TIME_SIZE = 16;
    private final int TITLE_COLOR = ContextCompat.getColor(this.getContext(), android.R.color.black);

    private TextView taskLabel;
    private TextView timeDisplay;
    private ImageButton expandButton;
    private ImageButton collapseButton;
    private Task task;
    private TimeFilter filter;
    private LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 150);


    public TaskViewHeader(Context context) {
      super(context);
      setOrientation(HORIZONTAL);
      setLayoutParams(params);
      setGravity(Gravity.CENTER);
      setLayoutTransition(new LayoutTransition());
    }

    public TaskViewHeader(Context context, Task task, TimeFilter filter){
      this(context);
      this.task = task;

      //Name (title)
      taskLabel = new TextView(context);
      taskLabel.setTextSize(TITLE_SIZE);
      taskLabel.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

      //Space separator
      Space space = new Space(context);
      LayoutParams spaceParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
      spaceParams.weight = 1;
      space.setLayoutParams(spaceParams);

      //Time display
      timeDisplay = new TextView(context);
      timeDisplay.setTextSize(TIME_SIZE);

      //Vertical divider
      View divider = new View(context);
      divider.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
      divider.setLayoutParams(new LayoutParams(1, 100));

      //Small space
      Space smallSpace = new Space(context);
      Space smallSpace2 = new Space(context);
      smallSpace.setLayoutParams(new LayoutParams(20, LayoutParams.MATCH_PARENT));
      smallSpace2.setLayoutParams(new LayoutParams(20, LayoutParams.MATCH_PARENT));

      //Expand and collapse buttons
      expandButton = new ImageButton(context);
      expandButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
      expandButton.setBackgroundColor(this.getSolidColor());
      expandButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_down_grey_24dp));
      collapseButton = new ImageButton(context);
      collapseButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
      collapseButton.setBackgroundColor(this.getSolidColor());
      collapseButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_up_grey_24dp));

      //Set filter and update
      setFilter(filter);

      addView(taskLabel);
      addView(space);
      addView(timeDisplay);
      addView(smallSpace);
      addView(divider);
      addView(smallSpace2);
      addView(expandButton);
      addView(collapseButton);
    }

    public Task getTask() {
      return task;
    }

    public void setTask(Task task) {
      this.task = task;
    }

    public TimeFilter getFilter() {
      return filter;
    }


    /**
     * Sets the time filter and updates the time view
     * @param filter The new <code>TimeFilter</code> to implement
     */
    public void setFilter(TimeFilter filter) {
      this.filter = filter;
      update();
    }

    public ImageButton getExpandButton(){
      return expandButton;
    }

    public ImageButton getCollapseButton() { return collapseButton; }

    @SuppressLint("SetTextI18n")
    public void update(){
      taskLabel.setText(task.getName());
      taskLabel.setTextColor(task.getColor());
      int timeSpent = task.getTimeSpent(filter);
      timeDisplay.setText(timeSpent / 60 + "h " + timeSpent % 60 + "m ");
      invalidate();
    }
  }
}
