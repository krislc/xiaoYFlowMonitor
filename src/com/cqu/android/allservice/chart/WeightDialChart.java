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
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DialRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.DialRenderer.Type;

import com.cqu.android.db.DatabaseAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * Budget demo pie chart.
 */
public class WeightDialChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * @return the chart name
   */
  public String getName() {
    return "本月流量统计";
  }
  
  /**
   * Returns the chart description.
   * @return the chart description
   */
  public String getDesc() {
    return "统计本月的总流量，并以饼状图表示";
  }
  
  /**
   * Executes the chart demo.
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    CategorySeries category = new CategorySeries("本月流量统计（MB）");
    DatabaseAdapter db = new DatabaseAdapter(context);
   	db.open();
 	Calendar calendar = Calendar.getInstance();
    int month = calendar.get(Calendar.MONTH)+1;
    int year = calendar.get(Calendar.YEAR);
    long monthT=db.calculateForMonth(year, month, 1);
    double sum=new BigDecimal(monthT).divide(new BigDecimal(1000000),1,1).doubleValue();
    category.add("本月使用流量"+":"+sum+"MB", sum);
    category.add(" ", 0);
    category.add("  最大可用值"+":"+30+"MB", 30);
    DialRenderer renderer = new DialRenderer();
    renderer.setChartTitleTextSize(20);
    renderer.setLabelsTextSize(20);
    renderer.setLegendTextSize(20);
    renderer.setMargins(new int[] {20, 30, 15, 0});
    SimpleSeriesRenderer r = new SimpleSeriesRenderer();
    r.setColor(Color.BLUE);
    renderer.addSeriesRenderer(r);
    r = new SimpleSeriesRenderer();
    r.setColor(Color.rgb(0, 150, 0));
    renderer.addSeriesRenderer(r);
    r = new SimpleSeriesRenderer();
    r.setColor(Color.GREEN);
    //显示当前流量值
    r.setDisplayChartValues(true);
    renderer.addSeriesRenderer(r);
    renderer.setLabelsTextSize(10);
    renderer.setLabelsColor(Color.WHITE);
    renderer.setShowLabels(true);
    renderer.setVisualTypes(new DialRenderer.Type[] {Type.ARROW, Type.NEEDLE, Type.NEEDLE});
    renderer.setMinValue(0);
    renderer.setMaxValue(40);
    renderer.setZoomButtonsVisible(true);
   
    return ChartFactory.getDialChartIntent(context, category, renderer, "本月流量统计");
  }

}
