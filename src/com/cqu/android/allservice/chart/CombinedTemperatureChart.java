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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.BubbleChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.model.XYValueSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.cqu.android.db.DatabaseAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Combined temperature demo chart.
 */
public class CombinedTemperatureChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "每月流量统计";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "每月流量统计";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    String[] titles = new String[] { "每月流量趋势" };
    List<double[]> x = new ArrayList<double[]>();
    for (int i = 0; i < titles.length; i++) {
      x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
    }
    DatabaseAdapter dbAdapter = new DatabaseAdapter(context);
    dbAdapter.open();
    Calendar calendar = Calendar.getInstance();
    double[] month = new double[12];
    XYValueSeries sunSeries = new XYValueSeries("流量点状趋势");
    XYSeries waterSeries = new XYSeries("每月流量柱状图            ");
    for(int i = 0; i<=11;i++){
    	long temp=dbAdapter.calculateForMonth(calendar.get(Calendar.YEAR),i+1, 1);
    	if(temp!=0){
    		month[i] = new BigDecimal(temp).divide(new BigDecimal(1000000),1,1).doubleValue();
    		sunSeries.add(i+1, 35,month[i]);
    		waterSeries.add(i+1,month[i]);
    	}else{
    		month[i]=0;
    		sunSeries.add(i+1, 35,0);
    		waterSeries.add(i+1,0);
    	}
    }
    List<double[]> values = new ArrayList<double[]>();
    values.add(month);
//    values.add(month);
    int[] colors = new int[] { Color.GREEN};
    PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE};
    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
    renderer.setPointSize(5.5f);
    int length = renderer.getSeriesRendererCount();

    for (int i = 0; i < length; i++) {
      XYSeriesRenderer r = (XYSeriesRenderer) renderer.getSeriesRendererAt(i);
      r.setLineWidth(5);
      r.setFillPoints(true);
    }
    setChartSettings(renderer, "每月流量统计图", "Month", "流量值（MB）", 0.5, 12.5, 0, 40,
        Color.LTGRAY, Color.LTGRAY);

    renderer.setXLabels(12);
    renderer.setYLabels(10);
    renderer.setShowGrid(true);
    renderer.setXLabelsAlign(Align.RIGHT);
    renderer.setYLabelsAlign(Align.RIGHT);
    renderer.setZoomButtonsVisible(true);
    renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
    renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });

//    XYValueSeries sunSeries = new XYValueSeries("使用流量的月份");
//    sunSeries.add(1, 35, 4.3);
//    sunSeries.add(2, 35, 4.9);
//    sunSeries.add(3, 35, 5.9);
//    sunSeries.add(4, 35, 8.8);
//    sunSeries.add(5, 35, 10.8);
//    sunSeries.add(6, 35, 11.9);
//    sunSeries.add(7, 35, 13.6);
//    sunSeries.add(8, 35, 12.8);
//    sunSeries.add(9, 35, 11.4);
//    sunSeries.add(10, 35, 9.5);
//    sunSeries.add(11, 35, 7.5);
//    sunSeries.add(12, 35, 5.5);
    XYSeriesRenderer lightRenderer = new XYSeriesRenderer();
    lightRenderer.setColor(Color.YELLOW);

//    XYSeries waterSeries = new XYSeries("每月流量柱状图");
//    waterSeries.add(1, 16);
//    waterSeries.add(2, 15);
//    waterSeries.add(3, 16);
//    waterSeries.add(4, 17);
//    waterSeries.add(5, 20);
//    waterSeries.add(6, 23);
//    waterSeries.add(7, 25);
//    waterSeries.add(8, 25.5);
//    waterSeries.add(9, 26.5);
//    waterSeries.add(10, 24);
//    waterSeries.add(11, 22);
//    waterSeries.add(12, 18);
    renderer.setBarSpacing(0.5);
    XYSeriesRenderer waterRenderer = new XYSeriesRenderer();
    waterRenderer.setColor(Color.argb(250, 0, 210, 250));

    XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);
    dataset.addSeries(0, sunSeries);
    dataset.addSeries(0, waterSeries);
    renderer.addSeriesRenderer(0, lightRenderer);
    renderer.addSeriesRenderer(0, waterRenderer);
    waterRenderer.setDisplayChartValues(true);
    waterRenderer.setChartValuesTextSize(10);
    renderer.setLegendTextSize(20);

    String[] types = new String[] { BarChart.TYPE, BubbleChart.TYPE, LineChart.TYPE};
    Intent intent = ChartFactory.getCombinedXYChartIntent(context, dataset, renderer, types,
        "每月流量统计");
    return intent;
  }

}
