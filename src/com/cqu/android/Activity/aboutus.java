package com.cqu.android.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public class aboutus extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
		setTitle("关于小Y");
		

	}
	
	
public boolean onKeyDown(int keyCode, KeyEvent event) {

	// 按下键盘上返回按钮

	if (keyCode == KeyEvent.KEYCODE_BACK) {
		Intent intent_return = new Intent();
		intent_return.setClass(aboutus.this, mainPage.class);
		startActivity(intent_return);
		aboutus.this.finish();
	}
	return true;
}

	
	

}
