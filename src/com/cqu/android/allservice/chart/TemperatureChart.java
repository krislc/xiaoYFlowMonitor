/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cqu.android.allservice.chart;

import java.math.BigDecimal;
import java.util.Calendar;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.RangeCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import com.cqu.android.db.DatabaseAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Temperature demo range chart.
 */
public class TemperatureChart extends AbstractDemoChart {

  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "每日流量统计";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "每日流量统计";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    double[] minValues = new double[] {0,0,0,0,0,0,0};
//    double[] maxValues = new double[] {7,12,24,28,33,35,37};
    DatabaseAdapter db = new DatabaseAdapter(context);
   	db.open();
   	Calendar calendar = Calendar.getInstance();
   	int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
       double[] maxValues = new double[7];
       int day =  calendar.get(Calendar.DATE);
       int Sunday = day - weekDay + 1;
       int month = calendar.get(Calendar.MONTH)+1;
       int year = calendar.get(Calendar.YEAR);
       
       for(int i = 0; i<7;i++){
       	Long temp = db.calculate(year, month, ++Sunday, 1);
       	maxValues[i] = new BigDecimal(temp).divide(new BigDecimal(1000),1,1).doubleValue();
       }

    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    RangeCategorySeries series = new RangeCategorySeries("每日流量统计");
    int length = minValues.length;
    for (int k = 0; k < length; k++) {
      series.add(minValues[k], maxValues[k]);
    }
    dataset.addSeries(series.toXYSeries());
    int[] colors = new int[] { Color.CYAN };
    XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
    setChartSettings(renderer, "每日流量统计", "天", "流量值（KB）",0,8,
        0, 4000, Color.GRAY, Color.LTGRAY);
    renderer.setBarSpacing(0.5);
    renderer.setXLabels(0);
    renderer.setYLabels(10);
   renderer.setMargins(new int[] {20, 45, 10, 0});
    renderer.setXLabelsAlign(Align.RIGHT);
    renderer.setYLabelsAlign(Align.RIGHT);
    SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
    r.setDisplayChartValues(true);
    r.setChartValuesTextSize(12);
    r.setChartValuesSpacing(3);
    r.setGradientEnabled(true);
    r.setGradientStart(-20, Color.BLUE);
    r.setGradientStop(20, Color.GREEN);
    renderer.addXTextLabel(1, "  周一");
    renderer.addXTextLabel(2, "  周二");
    renderer.addXTextLabel(3, "  周三");
    renderer.addXTextLabel(4, "  周四");
    renderer.addXTextLabel(5,"  周五");
    renderer.addXTextLabel(6,"  周六");
    renderer.addXTextLabel(7,"  周日");
   
    renderer.addYTextLabel(30000, "流量超出");
    renderer.setShowGrid(true);
    renderer.setZoomButtonsVisible(true);
    renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
    renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
   
    return ChartFactory.getRangeBarChartIntent(context, dataset, renderer, Type.DEFAULT,
        "每日流量统计");
  }

}
