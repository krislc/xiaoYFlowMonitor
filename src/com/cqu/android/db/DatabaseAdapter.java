package com.cqu.android.db;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {
	
	// 数据库名名称
	private final static String DATABASE_NAME = "Flow.db";
	// 表名
	private final static String TABLE_NAME = "table1"; 
	//主键，ID
	private final static String TABLE_ID = "FlowID";
	//上行流量，单位byte
	private final static String TABLE_UPF = "UpFlow";
	//下载流量，单位byte
	private final static String TABLE_DPF = "DownFlow";
	//储存日期
	//格式：YYYY-MM-DD HH:MM:SS
	private final static String TABLE_TIME = "Time";
	//联网类型，有WIFI和GPRS
	private final static String TABLE_WEB = "WebType";
	//数据库版本号
	private final static int DB_VERSION = 1;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM");
	
	private Context mContext = null;
	
	//创建表的语句，用于第一次创建数据库时，创建表     1 主键id integer自增      2上行下行流量long  3联网类型integer 4时间datetime类型
	private final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
			+ " (" + TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ TABLE_UPF + " Long," + TABLE_DPF
			+ " Long," + TABLE_WEB + " INTEGER," + TABLE_TIME + " DATETIME)";
	// 数据库对象
		private SQLiteDatabase mSQLiteDatabase = null;

	//数据库adapter，用于创建数据库
	private DatabaseHelper mDatabaseHelper = null;
    //自定义数据库Helper   继承了抽象类helper  实现了方法
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DB_VERSION);
		}
		
		/*
		 * 创建数据库
		 * 创建表
		 * */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(CREATE_TABLE);
//			String insertData = " INSERT INTO " + TABLE_NAME + " (" + TABLE_ID
//					+  ", " + TABLE_UPF + ", " + TABLE_DPF
//					+ "," + TABLE_WEB + "," + TABLE_TIME + " ) values(" + 1
//					+ ",' '," + 0 + ", " + 0 + "," + 3 + ","
//					+ new java.sql.Date(1) + ");";
//			db.execSQL(insertData);
		}

		/*
		 * 数据库跟新
		 *删除表并重新创建新表 
		 * */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}

	
	//外部函数
	/*外部类 构造函数 获取context */
	public DatabaseAdapter(Context context) {
		mContext = context;
	}

	// 打开数据库，返回数据库对象
	public void open() throws SQLException {
		mDatabaseHelper = new DatabaseHelper(mContext);
		 /**
	     * Create and/or open a database that will be used for reading and writing.
	     * The first time this is called, the database will be opened and
	     * {@link #onCreate}, {@link #onUpgrade} and/or {@link #onOpen} will be
	     * called.     public SQLiteDatabase getWritableDatabase() 
	     */
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}

	// 关闭数据库
	public void close() {
		mDatabaseHelper.close();
	}

	/* 插入一条数据 ，插入信息以日为单位*/
	public void insertData(long UpFlow, long DownFlow,
			int WebType, Date date) {
		String dataString = sdf.format(date);
		String insertData = " INSERT INTO " + TABLE_NAME + " ("
		+ TABLE_UPF + ", " + TABLE_DPF + "," + TABLE_WEB + ","
				+ TABLE_TIME + " ) values(" + UpFlow + ", "
				+ DownFlow + "," + WebType + "," + "datetime('" + dataString
				+ "'));";
		mSQLiteDatabase.execSQL(insertData);
		
	}
	
	//////////////更新数据
	
	 public void updateData(long upFlow,long downFlow, int webType, Date date){
			String dataString = sdf.format(date);
			String updateData = " UPDATE " + TABLE_NAME + " SET "+ TABLE_UPF+"=" +upFlow+" , " +TABLE_DPF+"="+downFlow+
	        " WHERE " + TABLE_WEB+"=" + webType+" and "+ TABLE_TIME +" like '"+dataString+"%'"; 
			mSQLiteDatabase.execSQL(updateData);
	 }
			
			
	/////////////////
   
	 /*检查是否存在该条数据*/
		public Cursor check( int netType, Date date) {
			String dataString = sdf.format(date);
			//Query the given table, returning a {@link Cursor} over the result set.
			/* public Cursor query(String table, String[] columns, String selection,
            String[] selectionArgs, String groupBy, String having,
            String orderBy, String limit) */

			Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
					TABLE_UPF+" AS upPro",TABLE_DPF+" AS dwPro"},  TABLE_WEB + "=" + netType+" and "+ TABLE_TIME +" like '"+dataString+"%'", null, null,
					null, null, null);// date转化
			return mCursor;
		}
///////////////////////////////	

		/* 查询今日流量数据 */
		public Cursor fetchDayFlow(int year, int month, int day, int netType) {
			StringBuffer date = new StringBuffer();
			date.append(String.valueOf(year) + "-");
			if (month < 10) {
				date.append("0" + String.valueOf(month) + "-");
			} else {
				date.append(String.valueOf(month) + "-");
			}
			if (day < 10) {
				date.append("0" + String.valueOf(day));
			} else {
				date.append(String.valueOf(day));
			}
			Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
					"sum(" + TABLE_UPF + ") AS sumUp",
					"sum(" + TABLE_DPF + ") as sumDw" }, TABLE_WEB + "=" + netType
					+ " and " + TABLE_TIME + " LIKE '" + date.toString() + "%'",
					null, null, null, null, null);
			return mCursor;
		}

	/* 查询每月流量 可用于月报表和月流量统计 */
	public Cursor fetchMonthFlow(int year, int Month, int netType) {
		StringBuffer date = new StringBuffer();
		date.append(String.valueOf(year) + "-");
		if (Month < 10) {
			date.append("0" + String.valueOf(Month) + "-");
		} else {
			date.append(String.valueOf(Month) + "-");
		}
		Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
				"sum(" + TABLE_UPF + ") AS monthUp",
				"sum(" + TABLE_DPF + ") as monthDw" }, TABLE_WEB + "="
				+ netType + " and " + TABLE_TIME + " LIKE '" + date.toString()
				+ "%'", null, null, null, null, null);
//		mCursor.close();
		return mCursor;
	}
   //计算某天的上传流量
	public long getProFlowUp(int netType, Date date){
		//由于插入数据以日为单位，故每一行的数据都是一天的流量信息
		Cursor cur = check( netType, date);
		long UP=0 ;
     /**
	   * This interface provides random read-write access to the result set returned
	   * by a database query.
	   *
	   * Cursor implementations are not required to be synchronized so code using a Cursor from multiple
	   * threads should perform its own synchronization when using the Cursor.
	   */
		  /**
	     * Returns the current position of the cursor in the row set.
	     * The value is zero-based. When the row set is first returned the cursor
	     * will be at positon -1, which is before the first row. After the
	     * last row is returned another call to next() will leave the cursor past
	     * the last entry, at a position of count().
	     *
	     * @return the current cursor position.      int getPosition();
	     */
	

		if(cur.moveToNext()){
			
			/**
		     * Returns the zero-based index for the given column name, or -1 if the column doesn't exist.
		     * If you expect the column to exist use {@link #getColumnIndexOrThrow(String)} instead, which
		     * will make the error more clear.
		     *
		     * @param columnName the name of the target column.
		     * @return the zero-based column index for the given column name, or -1 if
		     * the column name does not exist.
		     * @see #getColumnIndexOrThrow(String)
		     */
			int up = cur.getColumnIndex("upPro");
			
			 /**
		     * Returns the value of the requested column as a long.
		     *
		     * <p>The result and whether this method throws an exception when the
		     * column value is null, the column type is not an integral type, or the
		     * integer value is outside the range [<code>Long.MIN_VALUE</code>,
		     * <code>Long.MAX_VALUE</code>] is implementation-defined.
		     *
		     * @param columnIndex the zero-based index of the target column.
		     * @return the value of that column as a long.   long getLong(int columnIndex);
		     */
		    
			UP = cur.getLong(up);
		}	
		cur.close();
		return UP ;
		
	}
	//计算某天的下载流量
	public long getProFlowDw(int netType, Date date){
		Cursor cur = check( netType, date);
		long UP=0 ;
		if(cur.moveToNext()){
			int up = cur.getColumnIndex("dwPro");
			UP = cur.getLong(up);
		}
		cur.close();
			return UP ;
	}
	/* 计算每日的流量 */
	public long calculate(int year, int month, int day, int netType) {
		Cursor calCurso = fetchDayFlow(year, month, day, netType);
		long sum = 0;
		if (calCurso != null) {
			if (calCurso.moveToFirst()) {
				do {
					int upColumn = calCurso.getColumnIndex("sumUp");
					int dwColumn = calCurso.getColumnIndex("sumDw");
					sum = calCurso.getLong(upColumn)
							+ calCurso.getLong(dwColumn);
				} while (calCurso.moveToNext());
			}
		}
//		calCurso.close();
		return sum;
	}
//////////计算本周上传流量/////////////////////////////////
	public long weekUpFloew(int netType){
		/**
	     * Constructs a new {@code GregorianCalendar} initialized to the current date and
	     * time with the default {@code Locale} and {@code TimeZone}.
	     *  public static synchronized Calendar getInstance() {
                   return new GregorianCalendar();
             }
              public GregorianCalendar() {
                 this(TimeZone.getDefault(), Locale.getDefault());
            }

	     */
		//获取系统日期
		Calendar date1 = Calendar.getInstance();
		/*set(f, value) 将日历字段 f 更改为 value。此外，它设置了一个内部成员变量，
		以指示日历字段 f 已经被更改。尽管日历字段 f 是立即更改的，
		但是直到下次调用 get()、getTime()、getTimeInMillis()、add()
		或 roll() 时才会重新计算日历的时间值（以毫秒为单位）。
		因此，多次调用 set() 不会触发多次不必要的计算。
		
		*add(f, delta) 将 delta 添加到 f 字段中。
		*这等同于调用 set(f, get(f) + delta)，但要带以下两个调整：

         Add 规则 1。调用后 f 字段的值减去调用前 f 字段的值等于 delta，
                          以字段 f 中发生的任何溢出为模。溢出发生在字段值超出其范围时，
                          结果，下一个更大的字段会递增或递减，并将字段值调整回其范围内。

         Add 规则 2。
                        如果期望某一个更小的字段是不变的，但让它等于以前的值是不可能的，
                        因为在字段 f 发生更改之后，或者在出现其他约束之后，比如时区偏移量发生更改，
                        它的最大值和最小值也在发生更改，然后它的值被调整为尽量接近于所期望的值。
                        更小的字段表示一个更小的时间单元。HOUR 是一个比 DAY_OF_MONTH 小的字段。
                        对于不期望是不变字段的更小字段，无需进行任何调整。日历系统会确定期望不变的那些字段。

                           此外，与 set() 不同，add() 强迫日历系统立即重新计算日历的毫秒数和所有字段。

                        示例：假定 GregorianCalendar 最初被设置为 1999 年 8 月 31 日。
                        调用 add(Calendar.MONTH, 13) 将日历设置为 2000 年 9 月 30 日。
        Add 规则 1 将 MONTH 字段设置为 September，
                       因为向 August 添加 13 个月得出的就是下一年的 September。
                       因为在 GregorianCalendar 中，DAY_OF_MONTH 不可能是 9 月 31 日，
                       所以 add 规则 2 将 DAY_OF_MONTH 设置为 30，即最可能的值。
                       尽管它是一个更小的字段，但不能根据规则 2 调整 DAY_OF_WEEK，
                       因为在 GregorianCalendar 中的月份发生变化时，该值也需要发生变化。
		*/
		//将日期设为本周的第一天
		date1.set(Calendar.DAY_OF_WEEK, date1.getFirstDayOfWeek());
		long flowUp = 0 ;
		for (int i=0 ; i<7 ; i++){
			
			int y = date1.get(Calendar.YEAR);
			int m = date1.get(Calendar.MONTH)+1;
			int d = date1.get(Calendar.DAY_OF_MONTH); 
			flowUp +=calculateUp(y, m, d,  netType);
			date1.add(Calendar.DAY_OF_WEEK, 1);     
			
		}
		return flowUp ;
	}
	
	//计算本周下载流量
	
	public long weekDownFloew(int netType){
		Calendar date1 = Calendar.getInstance();//得到现在的日期
		date1.set(Calendar.DAY_OF_WEEK, date1.getFirstDayOfWeek());//将日期改为今天所在周的第一天
		long flowDw = 0 ;
		for (int i=0 ; i<7 ; i++){
			
			int y = date1.get(Calendar.YEAR);
			int m = date1.get(Calendar.MONTH)+1;
			int d = date1.get(Calendar.DAY_OF_MONTH);
			flowDw +=calculateDw(y, m, d,  netType);
			date1.add(Calendar.DAY_OF_WEEK, 1);   //date1加一天  	
		}
		
		return flowDw ;
	}

	/////////////////////////////////////////////
	//计算每月上传流量
	public long calculateUpForMonth(int year, int Month, int netType) {
		Cursor lCursor = fetchMonthFlow(year, Month, netType);
		long sum = 0;
		
			if (lCursor != null) {
				if (lCursor.moveToFirst()) {
					do {
						int upColumn = lCursor.getColumnIndex("monthUp");
						sum += lCursor.getLong(upColumn);
					} while (lCursor.moveToNext());
				}
				lCursor.close();
		}
		return sum;
	}
	//计算每月下载流量
	public long calculateDnForMonth(int year, int Month, int netType) {
		Cursor lCursor = fetchMonthFlow(year, Month, netType);
		long sum =0;
		
			if (lCursor != null) {
				if (lCursor.moveToFirst()) {
					do {
						int dwColumn = lCursor.getColumnIndex("monthDw");
						sum += lCursor.getLong(dwColumn);
					} while (lCursor.moveToNext());
				}
				lCursor.close();
		}
		return sum;
	}
	/* 计算某月的流量 */
	public long calculateForMonth(int year, int Month, int netType) {
		Cursor lCursor = fetchMonthFlow(year, Month, netType);
		long sum;
		long monthSum = 0;
		
			if (lCursor != null) {
				if (lCursor.moveToFirst()) {
					do {
						int upColumn = lCursor.getColumnIndex("monthUp");
						int dwColumn = lCursor.getColumnIndex("monthDw");
						sum = lCursor.getLong(upColumn) + lCursor.getLong(dwColumn);
						monthSum += sum;
					} while (lCursor.moveToNext());
				}
				lCursor.close();
		}
		return monthSum;
	}



/* 计算每日的上传流量 */
	public long calculateUp(int year, int month, int day, int netType) {
		Cursor calCurso = fetchDayFlow(year, month, day, netType);
		long sum = 0;
		if (calCurso != null) {
			if (calCurso.moveToFirst()) {
				do {
					int upColumn = calCurso.getColumnIndex("sumUp");
					sum = calCurso.getLong(upColumn);
				} while (calCurso.moveToNext());
			}
		}
//		calCurso.close();
		return sum;
	}
	/* 计算每日的xiazai流量 */
	public long calculateDw(int year, int month, int day, int netType) {
		Cursor calCurso = fetchDayFlow(year, month, day, netType);
		long sum = 0;
		if (calCurso != null) {
			if (calCurso.moveToFirst()) {
				do {
					int dwColumn = calCurso.getColumnIndex("sumDw");
					sum = calCurso.getLong(dwColumn);
				} while (calCurso.moveToNext());
			}
		}
//		calCurso.close();
		return sum;
	}

	
	/* 计算每个程序的流量 */
//	public Cursor programmeCur(String proName, int netType) {
//		Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
//				TABLE_PRO, "sum(" + TABLE_UPF + ") AS upPro",
//				"sum(" + TABLE_DPF + ") AS dwPro" }, TABLE_PRO + "= '"
//				+ proName + "' and " + TABLE_WEB + "=" + netType, null, null,
//				null, null, null);// date转化
//		return mCursor;
//	}

	/* 计算每个程序的流量 */
//	public long calPro(String proName, int netType) {
//		Cursor cursor = programmeCur(proName, netType);
//		long upFlow;
//		long downFlow;
//		long flow = 0;
//		long countFlow = 0;
//		if (cursor.moveToFirst()) {
//			do {
//				int upCol = cursor.getColumnIndex("upPro");
//				int downCol = cursor.getColumnIndex("dwPro");
//				upFlow = cursor.getLong(upCol);
//				downFlow = cursor.getLong(downCol);
//				flow = upFlow + downFlow;
//				countFlow += flow;
//			} while (cursor.moveToNext());
//		}
//		cursor.close();
//		return countFlow;
//	}

	/* 计算所有程序的流量 */
//	public Cursor allProgrammeCur(int netType) {
//		Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
//				TABLE_PRO, "sum(" + TABLE_UPF + ") AS upPro",
//				"sum(" + TABLE_DPF + ") AS dwPro" }, TABLE_WEB + "=" + netType,
//				null, null, TABLE_PRO, null, null);// date转化
//		return mCursor;
//	}
//
//	public ProgrammeHolder[] calAllPro(int netType) {
//		Cursor cursor = allProgrammeCur(netType);
//		int count = cursor.getCount();
//		ProgrammeHolder[] programmeHolder = new ProgrammeHolder[count];
//		long upFlow;
//		long downFlow;
//		long flow;
//		String proName;
//		ProgrammeHolder ph ;
//		int  i =0;
//		if (cursor.moveToFirst()) {
//			do {
//				flow =0;
//				
//				int proNameId = cursor.getColumnIndex(TABLE_PRO);
//				int upCol = cursor.getColumnIndex("upPro");
//				int downCol = cursor.getColumnIndex("dwPro");
//				
//				proName  = cursor.getString(proNameId);
//				upFlow = cursor.getLong(upCol);
//				downFlow = cursor.getLong(downCol);
//				flow = upFlow + downFlow;
//				
//				ph = new ProgrammeHolder();
//				ph.setTraffic(flow);
//				ph.setName(proName);
//				
//				programmeHolder[i] = ph;
//				i++;
//			} while (cursor.moveToNext());
//		}
//		cursor.close();
//		return programmeHolder;
//	}

	/* 更新一条数据 以后扩展用 */

	/* 清空数据 */
	public void deleteAll() {
		mSQLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME);
	}
	public void clear(){
		mSQLiteDatabase.delete(TABLE_NAME, null, null);
	}
}
