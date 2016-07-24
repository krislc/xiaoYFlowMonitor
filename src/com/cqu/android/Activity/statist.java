package com.cqu.android.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;


import com.cqu.android.allservice.chart.CombinedTemperatureChart;

import com.cqu.android.allservice.chart.TemperatureChart;
import com.cqu.android.allservice.chart.WeightDialChart;

public class statist extends Activity {
	
		
	private TemperatureChart at= new TemperatureChart();
	private WeightDialChart weekchart = new WeightDialChart();
	private CombinedTemperatureChart monthchart = new CombinedTemperatureChart();
	private ImageView dayChartButton = null;
	private ImageView weekChartButton = null;
	private ImageView monthChartButton = null;
				
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistchart);
		this.setTitle("小Y流量统计");	    
		dayChartButton = (ImageView)findViewById(R.id.dayChart);
		weekChartButton = (ImageView)findViewById(R.id.weekChart);
		monthChartButton = (ImageView)findViewById(R.id.monthChart);
		dayChartButton.setOnClickListener(new DayChartListener());
		weekChartButton.setOnClickListener(new WeekChartListener());
		monthChartButton.setOnClickListener(new MonthChartListener());
		
	}
	
	class DayChartListener implements OnClickListener
	{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = null;
			intent = at.execute(statist.this);

			startActivity(intent);
		}
		
	}
	class WeekChartListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = null;
			intent = weekchart.execute(statist.this);

			startActivity(intent);
		}
		
	}
	
	class MonthChartListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = null;
			intent = monthchart.execute(statist.this);

			startActivity(intent);
		}
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// 按下键盘上返回按钮

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			Intent intent1 = new Intent();
			intent1.setClass(statist.this, mainPage.class);
			startActivity(intent1);
			statist.this.finish();
		}
		return true;

	}
}
