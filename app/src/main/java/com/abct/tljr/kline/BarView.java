package com.abct.tljr.kline;
 
import com.abct.tljr.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.utils.ValueFormatter;
 
import android.graphics.Color;
import android.view.MenuItem;
import android.widget.Toast;
 
public class BarView implements OptionsItemSelected {
   BarChart chart;
 
   public BarView(BarChart chart) {
       // TODO Auto-generated constructor stub
       this.chart = chart;
       initBarChart();
   }
 
   public BarChart getChart() {
       return chart;
   }
 
   private void initBarChart() {
//       MyYAxisRender yMyRender = new MyYAxisRender(chart.getViewPortHandler(),
//               chart.getAxisLeft(), chart.getTransformer(AxisDependency.LEFT));
//       chart.setRendererLeftYAxis(yMyRender);
 
       chart.setDrawBarShadow(false);
 
       chart.setDescription("");
       chart.setMaxVisibleValueCount(60);
 
       chart.setPinchZoom(false);
       
       ValueFormatter custom = new MyValueFormatter(4);
 
       YAxis leftAxis = chart.getAxisLeft();
       leftAxis.setLabelCount(2);
       leftAxis.setValueFormatter(custom);
       leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
       leftAxis.setSpaceTop(15f);
       leftAxis.setStartAtZero(false);
       leftAxis.setTextColor(Color.rgb(139, 148, 153));
 
       YAxis rightAxis = chart.getAxisRight();
       rightAxis.setDrawGridLines(false);
       rightAxis.setLabelCount(2);
       rightAxis.setStartAtZero(false);
       rightAxis.setValueFormatter(custom);
       rightAxis.setSpaceTop(15f);
       
       chart.setDrawGridBackground(false);
       chart.getLegend().setEnabled(false);
 
   }
 
   @Override
   public void optionsItemSelected(MenuItem item) {
 
       switch (item.getItemId()) {
       case R.id.actionToggleValues: {
           for (DataSet<?> set : chart.getData().getDataSets())
               set.setDrawValues(!set.isDrawValuesEnabled());
 
           chart.invalidate();
           break;
       }
       case R.id.actionToggleHighlight: {
           if (chart.isHighlightEnabled())
               chart.setHighlightEnabled(false);
           else
               chart.setHighlightEnabled(true);
           chart.invalidate();
           break;
       }
       case R.id.actionTogglePinch: {
           if (chart.isPinchZoomEnabled())
               chart.setPinchZoom(false);
           else
               chart.setPinchZoom(true);
 
           chart.invalidate();
           break;
       }
       case R.id.actionToggleHighlightArrow: {
           if (chart.isDrawHighlightArrowEnabled())
               chart.setDrawHighlightArrow(false);
           else
               chart.setDrawHighlightArrow(true);
           chart.invalidate();
           break;
       }
       case R.id.actionToggleStartzero: {
           chart.getAxisLeft().setStartAtZero(
                   !chart.getAxisLeft().isStartAtZeroEnabled());
           chart.getAxisRight().setStartAtZero(
                   !chart.getAxisRight().isStartAtZeroEnabled());
           chart.notifyDataSetChanged();
           chart.invalidate();
           break;
       }
       case R.id.animateX: {
           chart.animateX(3000);
           break;
       }
       case R.id.animateY: {
           chart.animateY(3000);
           break;
       }
       case R.id.animateXY: {
 
           chart.animateXY(3000, 3000);
           break;
       }
       case R.id.actionToggleFilter: {
 
           Approximator a = new Approximator(ApproximatorType.DOUGLAS_PEUCKER,
                   25);
 
           if (!chart.isFilteringEnabled()) {
               chart.enableFiltering(a);
           } else {
               chart.disableFiltering();
           }
           chart.invalidate();
           break;
       }
       case R.id.actionSave: {
           if (chart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
               Toast.makeText(chart.getContext().getApplicationContext(),
                       "Saving SUCCESSFUL!", Toast.LENGTH_SHORT).show();
           } else
               Toast.makeText(chart.getContext().getApplicationContext(),
                       "Saving FAILED!", Toast.LENGTH_SHORT).show();
           break;
       }
       }
   }
 
}