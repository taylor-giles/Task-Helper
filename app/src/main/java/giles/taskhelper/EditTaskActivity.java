package giles.taskhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class EditTaskActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_task);
    ((LinearLayout)findViewById(R.id.layout_color)).addView(new ColorSelectionLayout(this));
  }
}
