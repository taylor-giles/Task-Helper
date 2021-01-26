package giles.taskhelper;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
  //Files
  private static final String TASKS_FILE = "tasks.obj";

  //Request codes
  public static final int ADD_TASK_REQUEST = 1;
  public static final int EDIT_TASK_REQUEST = 2;
  public static final int LOG_TIME_REQUEST = 3;

  //Data
  private ArrayList<Task> tasks = new ArrayList<>(); //Necessary for reading/writing files
  private HashMap<String, TaskView> taskViews = new HashMap<>(); //Stores all TaskViews as values associated with the name of their Task
  private TimeFilter currentFilter = new TimeFilter(TimeFilter.ONE_WEEK);

  //Layout elements
  private ImageButton logButton;
  private LinearLayout scrollLayout;
  private ScrollView scrollView;
  private LineChart lineChart;
  private PieChart pieChart;
  private TextView averageTimeView;
  private TextView totalTimeView;
  private TextView goalsMetView;
  private TextView motivatorView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setVisible(true);
    scrollLayout = findViewById(R.id.layout_scroll_main);
    scrollView = findViewById(R.id.scroll_main);
    lineChart = findViewById(R.id.line_chart);
    pieChart = findViewById(R.id.pie_chart);
    averageTimeView = findViewById(R.id.text_avg_time);
    totalTimeView = findViewById(R.id.text_time_spent);
    goalsMetView = findViewById(R.id.text_goals);
    motivatorView = findViewById(R.id.text_motivator);

    //Set up tasks and TaskViews
    tasks = loadTasks();
    for(Task task : tasks){
      TaskView view = new TaskView(this, task, currentFilter);
      addTaskView(task, view);
    }

    //Log Time button
    logButton = findViewById(R.id.button_log_time);
    logButton.setOnClickListener(v -> openLogActivity(null));
    if(tasks.isEmpty()){
      logButton.setVisibility(View.GONE);
    }

    //Initialize charts
    lineChart.getDescription().setEnabled(false);
    lineChart.setNoDataText("Nothing to see here... yet!");
    lineChart.setNoDataTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    pieChart.getDescription().setEnabled(false);
    pieChart.setNoDataText("Nothing to see here... yet!");
    pieChart.setNoDataTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    updateOverview();
  }

  @Override
  protected void onResume() {
    super.onResume();

    //Hide log button if and only if there are no tasks
    if(tasks.isEmpty()){
      logButton.setVisibility(View.GONE);
    } else {
      logButton.setVisibility(View.VISIBLE);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_CANCELED){
      if(resultCode == Activity.RESULT_OK) {
        switch(requestCode){
          //Add new task
          case ADD_TASK_REQUEST:
            //Build and add new task
            Task newTask = new Task(data.getStringExtra("name"),
                    data.getIntExtra("color", ContextCompat.getColor(this, R.color.grey_600)),
                    new TaskGoal(data.getIntExtra("goalType", TaskGoal.NONE), data.getIntExtra("goalMinutes", 0)));
            tasks.add(newTask);
            saveToFile(TASKS_FILE, tasks);

            //Build and add new TaskView
            addTaskView(newTask, new TaskView(this, newTask, currentFilter));

            //There is at least one task now, so show log button
            logButton.setVisibility(View.VISIBLE);

            updateOverview();
            break;

          //Log time
          case LOG_TIME_REQUEST:
            //Get the task name, date, and time
            String taskName = data.getStringExtra("taskName");
            Date date = new Date(data.getLongExtra("dateMillis", System.currentTimeMillis()));
            int duration = data.getIntExtra("time", 0);

            //Find the task
            Task task = new Task(taskName, R.color.grey_600);
            for(Task t : tasks){
              if(t.getName().equals(taskName)){
                task = t;
              }
            }

            //Add time to task
            task.addTime(date, duration);

            //Update the associated TaskView
            TaskView view = taskViews.get(task.getName());
            if(view == null){
              addTaskView(task, new TaskView(this, task, currentFilter));
            } else {
              view.update();
            }
            saveToFile(TASKS_FILE, tasks);
            updateOverview();
            break;
        }
      }
    }
  }


  /**
   * Adds a <code>TaskView</code> to this activity
   * @param task The task to associate with the added view
   * @param view The <code>TaskView</code> to add
   */
  private void addTaskView(Task task, TaskView view){
    taskViews.put(task.getName(), view);
    scrollLayout.addView(view);
  }


  /**
   * Opens the EditTask Activity to add a new task
   * @param view The view that was clicked to activate this method
   */
  public void openEditToAdd(View view){
    Intent openEdit = new Intent(this, EditTaskActivity.class);
    startActivityForResult(openEdit, ADD_TASK_REQUEST);
  }


  /**
   * Opens a LogTimeActivity to log time for a task
   */
  public void openLogActivity(Task task){
    Intent openLog = new Intent(this, LogTimeActivity.class);

    //Put task name
    if(task != null) {
      openLog.putExtra("taskName", task.getName());
    } else {
      openLog.putExtra("taskName", (String)null);
    }

    //Put all task names
    String[] taskNames = new String[tasks.size()];
    for(int i = 0; i < tasks.size(); i++){
      taskNames[i] = tasks.get(i).getName();
    }
    openLog.putExtra("taskNames", taskNames);
    startActivityForResult(openLog, LOG_TIME_REQUEST);
  }


  /**
   * Serializes the given <code>Object</code> and saves it to the specified file
   * @param filename The location of the file to save to
   * @param obj The <code>Object</code> to serialize and save
   */
  private void saveToFile(String filename, Object obj){
    try {
      FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE);
      ObjectOutputStream os = new ObjectOutputStream(fos);
      os.writeObject(obj);
      os.close();
      fos.close();
    } catch (IOException e) {
      Toast.makeText(this, "An error occurred when saving data", Toast.LENGTH_LONG).show();
    }
  }


  /**
   * Reads in a serialized <code>Object</code> from the specified file
   * @param filename The location of the file to read from
   * @return The <code>Object</code> read in from file
   */
  private Object loadFromFile(String filename){
    try {
      FileInputStream fis = this.openFileInput(filename);
      ObjectInputStream is = new ObjectInputStream(fis);
      Object obj = is.readObject();
      is.close();
      fis.close();
      return obj;
    } catch(FileNotFoundException e){
      //Create file if it does not already exist
      saveToFile(filename, null);
    } catch(IOException | ClassNotFoundException e){
      e.printStackTrace();
      Toast.makeText(this, "An error occurred when reading data from file", Toast.LENGTH_LONG).show();
    }
    return null;
  }


  /**
   * Gets the tasks object from file
   * @return The object stored at TASKS_FILE
   */
  private ArrayList<Task> loadTasks(){
    try {
      ArrayList<Task> newTasks = (ArrayList<Task>) loadFromFile(TASKS_FILE);
      if(newTasks == null){
        return new ArrayList<>();
      }
      return newTasks;
    } catch(ClassCastException e) {
      e.printStackTrace();
      Toast.makeText(this, "Error processing data from file", Toast.LENGTH_SHORT).show();
      return new ArrayList<>();
    }
  }


  /**
   * Updates the charts and "stats" on the overview page
   */
  private void updateOverview(){
    //Update the total time display
    int sum = 0;
    for(Task t : tasks){
      sum += t.getTimeSpent(currentFilter);
    }
    totalTimeView.setText((sum / 60) + "h " + (sum % 60) + "m");

    //Update the average time display
    int avg = sum / currentFilter.getDates().size();
    averageTimeView.setText((avg / 60) + "h " + (avg % 60) + "m per day");

    //Update charts
    updateLineChart();
    updatePieChart();

    //Update motivator
    updateMotivator();
  }


  /**
   * Updates the pie chart on the overview screen
   */
  private void updatePieChart(){
    Map<String, Integer> pieMap = new LinkedHashMap<>(); //LinkedHashMap to preserve order
    ArrayList<Integer> pieColors = new ArrayList<>();

    //Add data to pie map
    for(Task t : tasks){
      if(t.getTimeSpent(currentFilter) > 0) {
        pieMap.put(t.getName(), t.getTimeSpent(currentFilter));
        pieColors.add(t.getColor());
      }
    }

    //Do not continue if there is no data to put in the pie chart
    if(pieMap.isEmpty()){
      return;
    }

    //Set up entries
    ArrayList<PieEntry> pieEntries = new ArrayList<>();
    for(String taskName : pieMap.keySet()){
      Integer value = pieMap.get(taskName);
      if(value != null) {
        pieEntries.add(new PieEntry(value.floatValue(), taskName));
      }
    }

    //Set up data set
    PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
    pieDataSet.setValueTextSize(12f);
    pieDataSet.setColors(pieColors);

    //Set up labels
    pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
    pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
    PieData pieData = new PieData(pieDataSet);
    pieData.setValueFormatter(new ValueFormatter() {
      @Override
      public String getFormattedValue(float value) {
        if(value == 0){
          return "";
        } else {
          return Math.round(value) + "%";
        }
      }
    });
    pieChart.setDrawEntryLabels(true); //Show task names if true
    pieChart.setEntryLabelColor(ContextCompat.getColor(this, android.R.color.tab_indicator_text));
    pieData.setValueTextColor(ContextCompat.getColor(this, android.R.color.tab_indicator_text));
    pieDataSet.setUsingSliceColorAsValueLineColor(true);
    pieDataSet.setValueLinePart2Length(1.3f);
    pieChart.setUsePercentValues(true);
    pieChart.setDrawSlicesUnderHole(false);
    pieData.setDrawValues(true);

    //Auto-scroll when clicked
    pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
      @Override
      public void onValueSelected(Entry e, Highlight h) {
        String taskName = ((PieEntry)e).getLabel();
        TaskView selectedView = taskViews.get(taskName);

        //Scroll to view
        if(selectedView != null) {
          selectedView.expand();
          ObjectAnimator.ofInt(scrollView, "scrollY", selectedView.getTop() - scrollView.getScrollY()).setDuration(500).start();
        }
      }

      @Override
      public void onNothingSelected() {
        //Do nothing
      }
    });

    pieChart.getLegend().setEnabled(false); //Disable legend
    pieChart.setRotationEnabled(false);
    pieChart.setData(pieData);
    pieChart.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 500));
    pieChart.setHoleRadius(50);
    pieChart.invalidate();
  }


  /**
   * Updates the line chart on the overview screen
   */
  private void updateLineChart(){
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    for(Task t : tasks) {
      ArrayList<Entry> lineEntries = new ArrayList<>();

      //Use index for x-value so that labels line up right
      for(int i = 0; i < currentFilter.getDates().size(); i++){
        lineEntries.add(new Entry(i, t.getTimeSpent(currentFilter.getDates().get(i))));
      }

      //Set data and appearance
      LineDataSet lineDataSet = new LineDataSet(lineEntries, t.getName());
      lineDataSet.setColor(t.getColor());
      lineDataSet.setCircleColor(t.getColor());
      lineDataSet.setLineWidth(1.8f);

      //Set labels above points
      lineDataSet.setValueTextSize(9f);
      lineDataSet.setValueFormatter(new ValueFormatter() {
        @Override
        public String getFormattedValue(float value) {
          if(value == 0){
            return "";
          }
          StringBuilder output = new StringBuilder();
          if((int)(value / 60) > 0){
            output.append((int) (value / 60)).append("h ");
          }
          output.append((int)(value) % 60).append("m");
          return output.toString();
        }
      });
      dataSets.add(lineDataSet);
    }

    //X-Axis labeling
    lineChart.getXAxis().setLabelRotationAngle(-30);
    lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
      @Override
      public String getFormattedValue(float value) {
        Calendar cal = new GregorianCalendar();
        cal.setTime((currentFilter.getDates().get((int)value)));
        StringBuilder output = new StringBuilder();

        //Get day of week
        switch(cal.get(Calendar.DAY_OF_WEEK)){
          case Calendar.SUNDAY: output.append("Sun, "); break;
          case Calendar.MONDAY: output.append("Mon, "); break;
          case Calendar.TUESDAY: output.append("Tue, "); break;
          case Calendar.WEDNESDAY: output.append("Wed, "); break;
          case Calendar.THURSDAY: output.append("Thu, "); break;
          case Calendar.FRIDAY: output.append("Fri, "); break;
          case Calendar.SATURDAY: output.append("Sat, "); break;
        }

        //Construct MM/DD/YY date string
        output.append(cal.get(Calendar.MONTH) + 1);
        output.append("/");
        output.append(cal.get(Calendar.DATE));
        output.append("/");
        output.append(("" + cal.get(Calendar.YEAR)).substring(2));
        return output.toString();
      }
    });

    //Y-Axis Labeling
    lineChart.getAxisRight().setDrawLabels(false);
    lineChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
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
    lineChart.getAxisLeft().setAxisMinimum(0);

    lineChart.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 800));
    LineData lineData = new LineData(dataSets);
    lineChart.setPinchZoom(false);
    lineChart.setData(lineData);
    lineChart.invalidate();
  }


  /**
   * Chooses a motivator and displays it based on the percentage of
   * the time towards their goals the user has completed, with random behavior.
   */
  private void updateMotivator() {
    final int NUM_CATEGORIES = 4;
    final int NUM_IN_EACH = 3;
    final String[][] MOTIVATORS = new String[][]{
            {getString(R.string.motivator_best1),
                    getString(R.string.motivator_best2),
                    getString(R.string.motivator_best3)},
            {getString(R.string.motivator_better1),
                    getString(R.string.motivator_better2),
                    getString(R.string.motivator_better3)},
            {getString(R.string.motivator_good1),
                    getString(R.string.motivator_good2),
                    getString(R.string.motivator_good3)},
            {getString(R.string.motivator_bad1),
                    getString(R.string.motivator_bad2),
                    getString(R.string.motivator_bad3)}
    };
    final String[] ONGOING_MOTIVATORS = new String[]{
            getString(R.string.motivator_ongoing_good),
            getString(R.string.motivator_ongoing_better),
            getString(R.string.motivator_ongoing_neutral1),
            getString(R.string.motivator_ongoing_neutral2),
            getString(R.string.motivator_ongoing_bad)
    };
    final double INCREMENT_VAL = 1 / (double)NUM_CATEGORIES;

    int allGoals = 0;
    for(Task t : tasks){
      if(t.getGoal().getType() != TaskGoal.NONE){
        allGoals++;
      }
    }
    if(allGoals == 0){
      motivatorView.setText(getString(R.string.motivator_none));
    } else {
      double totalTimeToMeet = 0.0;
      double totalTimeOfGoals = 0.0;
      for(Task t : tasks){
        totalTimeToMeet += t.getGoal().minsToMeet(t.getTimeSpent(currentFilter));
        totalTimeOfGoals += t.getGoal().getMinutes();
      }
      double percentMet = totalTimeToMeet / totalTimeOfGoals;

      //Get normally distributed sample with sigma = halfway between categories, mean = percentMet
      double normalDist = new Random().nextGaussian() * (INCREMENT_VAL / 2) + percentMet;
      int random = new Random().nextInt(NUM_IN_EACH);

      //Decide on and show a motivator
      int c = 0;
      for(double i = 1; i > 0; i-=INCREMENT_VAL){
        //If the normal dist val is (the center val for this motivator category)+/-(INCREMENT_VAL/2), use this category
        if (normalDist > (INCREMENT_VAL - (INCREMENT_VAL / 2)) && normalDist < (INCREMENT_VAL + (INCREMENT_VAL / 2))) {
          motivatorView.setText(MOTIVATORS[c][random]);
        }
        c++;
      }
    }
  }
}




