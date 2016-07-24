package com.cqu.android.allservice.monitoring;

import java.math.BigDecimal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;

public class TrafficMonitoring {
	Context context;
	ConnectivityManager cm ;
	NetworkInfo nwi;
	long lastTraffic = 0;
	long currentTraffic;

	// 构造函数
	public TrafficMonitoring() {
	}
	/**Context-------
	 * Interface to global information about an application environment.  This is
	 * an abstract class whose implementation is provided by
	 * the Android system.  It
	 * allows access to application-specific resources and classes, as well as
	 * up-calls for application-level operations such as launching activities,
	 * broadcasting and receiving intents, etc.
	 */

	public TrafficMonitoring(Context context) {
		this.context = context;
		/**
	     * Return the handle to a system-level service by name. The class of the
	     * returned object varies by the requested name. Currently available names
	     * are:
	     */
		
		cm =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);	
		
		/**
	     * Returns details about the currently active data network. When connected,
	     * this network is the default route for outgoing connections. You should
	     * always check {@link NetworkInfo#isConnected()} before initiating network
	     * traffic. This may return {@code null} when no networks are available.
	     * <p>This method requires the caller to hold the permission
	     * {@link android.Manifest.permission#ACCESS_NETWORK_STATE}.
	     */
		nwi = cm.getActiveNetworkInfo();
	}

	// 获取当前手机的联网类型，返回String
	public int getNetType() {
		if(nwi != null){
			 /**
		     * Return a human-readable name describe the type of the network,
		     * for example "WIFI" or "MOBILE".
		     * @return the name of the network type
		     */
			String net = nwi.getTypeName();
			if(net.equals("WIFI")){
				return 0;
			}else {
				return 1;
			}
		}else {
			return -1;
		}
	}	

	// 查询手机总流量
	public static long traffic_Monitoring() {
		long recive_Total = TrafficStats.getTotalRxBytes();
		long send_Total = TrafficStats.getTotalTxBytes();
		long total = recive_Total + send_Total;
		return total;
	}

	//查询手机的Mobile上行流量
	public static long mReceive(){
		return  TrafficStats.getMobileRxBytes();
	}
	
	//查询手机的Mobile下行流量
	public static long mSend(){
		return  TrafficStats.getMobileTxBytes();
	}
	
	//查询手机的WIFI下行流量
	public static long wSend(){
		return  TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes();
	}
	
	//查询手机Wifi的上行流量
	public static long wReceive(){
		return TrafficStats.getTotalTxBytes() - TrafficStats.getMobileRxBytes();
	}

	// 查询某个Uid的下行值
	public static long monitoringEachApplicationReceive(int uid) {
		return TrafficStats.getUidRxBytes(uid);
	}

	// 查询某个Uid的上行值
	public static long monitoringEachApplicationSend(int uid) {
		return TrafficStats.getUidTxBytes(uid);
	}

	// 流量转化
	public static String convertTraffic(long traffic) {
		BigDecimal trafficKB;
		BigDecimal trafficMB;
		BigDecimal trafficGB;
		
	/**
		 * This class represents immutable(不可改变) integer numbers of arbitrary（任意） length. Large
		 * numbers are typically used in security applications and therefore BigIntegers
		 * offer dedicated functionality（专用的功能） like the generation of large prime numbers（产生素数） or
		 * the computation of modular inverse（模的逆运算）.
		 */

		BigDecimal temp = new BigDecimal(traffic);
		BigDecimal divide = new BigDecimal(1000);
//  ctrl+/   Returns a new {@code BigDecimal} whose value is {@code this / divisor}. 
//  参数divisor  scale精度2位    舍入模式
		/**
	     * Rounding mode where the values are rounded towards zero.
	     *
	     * @see RoundingMode#DOWN      public static final int ROUND_DOWN = 1;
	     * 
	     * ROUND_HALF_UP: 遇到.5的情况时往上近似,例: 1.5 ->;2
           ROUND_HALF_DOWN : 遇到.5的情况时往下近似,例: 1.5 ->;1

                BigDecimal a = new BigDecimal(1.5);
                System.out.println("down="+a.setScale(0,BigDecimal.ROUND_HALF_DOWN)+"/tup="+a.setScale(0,BigDecimal.ROUND_HALF_UP));
                                              结果:down=1  up=2
                                            看这个例子就明白了!

                                           其他参数说明

 

ROUND_CEILING     
  如果   BigDecimal   是正的，则做   ROUND_UP   操作；如果为负，则做   ROUND_DOWN   操作。     
  ROUND_DOWN     
  从不在舍弃(即截断)的小数之前增加数字。     
  ROUND_FLOOR     
  如果   BigDecimal   为正，则作   ROUND_UP   ；如果为负，则作   ROUND_DOWN   。     
  ROUND_HALF_DOWN     
  若舍弃部分>   .5，则作   ROUND_UP；否则，作   ROUND_DOWN   。     
  ROUND_HALF_EVEN     
  如果舍弃部分左边的数字为奇数，则作   ROUND_HALF_UP   ；如果它为偶数，则作   ROUND_HALF_DOWN   。     
  ROUND_HALF_UP     
  若舍弃部分>=.5，则作   ROUND_UP   ；否则，作   ROUND_DOWN   。     
  ROUND_UNNECESSARY     
  该“伪舍入模式”实际是指明所要求的操作必须是精确的，，因此不需要舍入操作。     
  ROUND_UP     
  总是在非   0   舍弃小数(即截断)之前增加数字。     
	     */
	   
		trafficKB = temp.divide(divide, 2, 1);
		//和divide做差   大于零返回1  等于零返回0 小于零返回-1
		//除1000后仍大于1000则继续运算直至小于1000   分成GB MB KB
		if (trafficKB.compareTo(divide) > 0) {
			trafficMB = trafficKB.divide(divide, 2, 1);
		} else {
			return trafficKB.doubleValue()+"KB";
		}
			
		if (trafficMB.compareTo(divide) > 0) {
				trafficGB = trafficMB.divide(divide, 2, 1);
				return trafficGB.doubleValue()+"GB";
		} else {
				
			return trafficMB.doubleValue()+"MB";
		}
		
	}
}
