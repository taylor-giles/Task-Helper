package giles.taskhelper;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.function.Predicate;

/**
 * A <code>Predicate</code> that filters for <code>TaskEntry</code>s that represent entries made
 * within the time period specified by this filter
 */
public class TimeFilter implements Predicate<TaskEntry>, Serializable {
  public static final int ALL_TIME = -1;
  public static final int TODAY = 0;
  public static final int ONE_WEEK = 6;
  public static final int TWO_WEEKS = 13;
  private static final long MILLIS_IN_DAY = 86400000;
  private Date earliest;
  private Date latest;

  /**
   * Constructor
   * @param earliest The earliest time to accept with this filter
   * @param latest The latest time to accept with this filter
   */
  public TimeFilter(Date earliest, Date latest){
    this.earliest = earliest;
    this.latest = latest;
  }

  /**
   * Recommended Constructor
   * @param numDays The number days into the past that this filter should span, including today
   */
  public TimeFilter(int numDays){
    //Set latest to 11:59:59.999 PM today (to include all of today)
    this.latest = new Date(System.currentTimeMillis() - (System.currentTimeMillis() % MILLIS_IN_DAY) - 1 + MILLIS_IN_DAY);
    Calendar latestCal = new GregorianCalendar();
    latestCal.setTime(new Date(System.currentTimeMillis()));
    latestCal.set(Calendar.HOUR_OF_DAY, 23);
    latestCal.set(Calendar.MINUTE, 59);
    latestCal.set(Calendar.SECOND, 59);
    latestCal.set(Calendar.MILLISECOND, 999);
    this.latest = latestCal.getTime();
    Log.d("log", latest.toString());

    //Set earliest to 12:00:00.001 AM on desired day
    if(numDays == ALL_TIME){
      this.earliest = new Date(0);
    } else if(numDays >= 0){
      Calendar midnightEarliest = new GregorianCalendar();
      midnightEarliest.setTime(new Date(System.currentTimeMillis() - (numDays * MILLIS_IN_DAY)));
      midnightEarliest.set(Calendar.HOUR_OF_DAY, 0);
      midnightEarliest.set(Calendar.MINUTE, 0);
      midnightEarliest.set(Calendar.SECOND, 0);
      midnightEarliest.set(Calendar.MILLISECOND, 1);
      this.earliest = midnightEarliest.getTime();
    } else {
      //Handle negative input with exception
      throw new IllegalArgumentException("Number of days must be a positive integer");
    }
  }

  /**
   * Constructor
   * @param earliest The earliest day to include in this filter
   * @param numDays The number of days after the earliest day to include in the filter
   */
  public TimeFilter(Date earliest, int numDays){
    this(earliest, new Date(earliest.getTime() + (numDays * MILLIS_IN_DAY)));
  }

  @Override
  public boolean test(TaskEntry taskEntry) {
    return (taskEntry.getDate().equals(earliest) || taskEntry.getDate().equals(latest)) ||
            (taskEntry.getDate().before(latest) && taskEntry.getDate().after(earliest));
  }


  /**
   * Returns a list of the <code>Date</code>s that this <code>TimeFilter</code> spans
   * @return An <code>ArrayList</code> of <code>Date</code>s spanned by this <code>TimeFilter</code>
   */
  public ArrayList<Date> getDates(){
    ArrayList<Date> output = new ArrayList<>();
    Date temp = new Date(earliest.getTime());
    while(temp.before(latest) || temp.equals(latest)){
      output.add(new Date(temp.getTime()));
      temp.setTime(temp.getTime() + MILLIS_IN_DAY);
    }
    return output;
  }


  /**
   * Gets the current date, in the same format as other TimeFilters will have it
   * @return A <code>Date</code> representing the current day at 12:00:00.001
   */
  public static Date getToday(){
    return (new TimeFilter(TODAY).getDates().get(0));
  }
}
