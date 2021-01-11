package giles.taskhelper;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Space;

import java.util.ArrayList;

public class ColorSelectionLayout extends LinearLayout {
  public static final int ERROR = -1;
  public final int RED = ContextCompat.getColor(this.getContext(), R.color.red_custom);
  public final int DEEP_ORANGE = ContextCompat.getColor(this.getContext(), R.color.deep_orange_600);
  public final int ORANGE = ContextCompat.getColor(this.getContext(), R.color.orange_600);
  public final int AMBER = ContextCompat.getColor(this.getContext(), R.color.amber_600);
  public final int YELLOW = ContextCompat.getColor(this.getContext(), R.color.yellow_600);
  public final int LIME = ContextCompat.getColor(this.getContext(), R.color.lime_600);
  public final int LIGHT_GREEN = ContextCompat.getColor(this.getContext(), R.color.light_green_600);
  public final int GREEN = ContextCompat.getColor(this.getContext(), R.color.green_600);
  public final int TEAL = ContextCompat.getColor(this.getContext(), R.color.teal_600);
  public final int CYAN = ContextCompat.getColor(this.getContext(), R.color.cyan_600);
  public final int LIGHT_BLUE = ContextCompat.getColor(this.getContext(), R.color.light_blue_600);
  public final int BLUE = ContextCompat.getColor(this.getContext(), R.color.blue_600);
  public final int INDIGO = ContextCompat.getColor(this.getContext(), R.color.indigo_600);
  public final int DEEP_PURPLE = ContextCompat.getColor(this.getContext(), R.color.deep_purple_600);
  public final int PURPLE = ContextCompat.getColor(this.getContext(), R.color.purple_600);
  public final int PINK = ContextCompat.getColor(this.getContext(), R.color.pink_custom);
  public final int BROWN = ContextCompat.getColor(this.getContext(), R.color.brown_600);
  public final int GREY = ContextCompat.getColor(this.getContext(), R.color.grey_600);

  private final int SPACE_SIZE = 15;

  //Defaults
  private int[] colors = {RED, DEEP_ORANGE, ORANGE, AMBER, YELLOW, LIME, LIGHT_GREEN, GREEN, TEAL,
          CYAN, LIGHT_BLUE, BLUE, INDIGO, DEEP_PURPLE, PURPLE, PINK, GREY, BROWN};
  private int numRows = 3;
  private ArrayList<ColorView> colorViews = new ArrayList<>();


  //Constructors
  public ColorSelectionLayout(Context context) {
    super(context);

    //Build layout
    buildLayout(context);
  }
  public ColorSelectionLayout(Context context, int[] colors) {
    super(context);
    this.colors = colors;

    //Build layout
    buildLayout(context);
  }
  public ColorSelectionLayout(Context context, int numRows){
    super(context);
    this.numRows = numRows;

    //Build layout
    buildLayout(context);
  }
  public ColorSelectionLayout(Context context, int[] colors, int numRows) {
    super(context);
    this.numRows = numRows;
    this.colors = colors;

    //Build layout
    buildLayout(context);
  }


  /**
   * Builds the layout by placing color views into <code>LinearLayout</code>s within this layout
   * @param context The context to build within
   */
  private void buildLayout(Context context){
    this.setOrientation(VERTICAL);
    LayoutParams hSpaceParams = new LayoutParams(SPACE_SIZE, LayoutParams.MATCH_PARENT);
    LayoutParams vSpaceParams = new LayoutParams(LayoutParams.MATCH_PARENT, SPACE_SIZE);

    //Construct each row
    for(int i = 0; i < numRows; i++){
      LinearLayout layout = new LinearLayout(context);
      layout.setOrientation(HORIZONTAL);
      for(int j = 0; j < colors.length; j++) {
        int color = colors[j];
        ColorView colorView = new ColorView(context, color);

        //Add color to row if it belongs there
        if(j % numRows == i){
          colorViews.add(colorView);
          layout.addView(colorView);
          Space space = new Space(context);
          space.setLayoutParams(hSpaceParams);
          layout.addView(space);
        }
      }
      layout.removeViewAt(layout.getChildCount() - 1); //Remove extra space

      //Add row and space under row to the layout
      this.addView(layout);
      Space verticalSpace = new Space(context);
      verticalSpace.setLayoutParams(vSpaceParams);
      this.addView(verticalSpace);
    }
    this.removeViewAt(this.getChildCount() - 1); //Remove extra space

    //Add onClickListeners to color views
    for(ColorView view : colorViews){
      view.setOnClickListener(v -> {
        colorViews.forEach((x) -> x.setSelected(false));
        v.setSelected(true);
      });
    }
  }

  //Getters and setters
  public int[] getColors() {
    return colors;
  }
  public void setColors(int[] colors) {
    this.colors = colors;
    buildLayout(this.getContext());
  }
  public int getNumRows() {
    return numRows;
  }
  public void setNumRows(int numRows) {
    this.numRows = numRows;
    buildLayout(this.getContext());
  }
  public ArrayList<ColorView> getColorViews() {
    return colorViews;
  }
  public ColorView getSelectedView(){
    for(ColorView view : colorViews){
      if(view.isSelected()){
        return view;
      }
    }
    return null;
  }
  public int getSelectedColor(){
    if(getSelectedView() == null){
      return ERROR;
    } else {
      return getSelectedView().getColor();
    }
  }

  //Inner ColorView class
  public class ColorView extends View {
    private int color;
    private boolean isSelected = false;
    private LayoutParams params = new LayoutParams(0, 0);

    public ColorView(Context context, int color){
      super(context);
      params.weight = 20;
      this.setLayoutParams(params);
      this.color = color;
      setBackgroundColor(color);
    }

    /**
     * Force this view to be the largest possible square
     * @param widthMeasureSpec Measure width argument
     * @param heightMeasureSpec Measure height argument
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      int width = MeasureSpec.getSize(widthMeasureSpec);
      int height = MeasureSpec.getSize(heightMeasureSpec);
      int size = width < height ? height : width;
      setMeasuredDimension(size, size);
    }

    public int getColor() {
      return color;
    }
    public void setColor(int color) {
      this.color = color;
      setBackgroundColor(color);
    }

    @Override
    public boolean isSelected() {
      return isSelected;
    }

    @Override
    public void setSelected(boolean selected) {
      isSelected = selected;
      if(isSelected){
        setForeground(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_check_white_24dp));
      } else {
        setForeground(null);
      }
    }
  }
}
