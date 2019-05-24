package com.demo.schedulealarm.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.demo.schedulealarm.Element.ScheduleInfo;

//数据存储辅助类
public class ScheduleDbAdapter {
    private static final String DB_NAME="schedule.db";//数据库名
    private static final int DB_VERSION=1;//创建模式
    private static final String TABLE_NAME="scheduleinfo";//表名

    public static final String KEY_ID="id";
    public static final String KEY_TITLE="title";
    public static final String KEY_CONTENT="content";
    public static final String KEY_YEAR="year";
    public static final String KEY_MONTH="month";
    public static final String KEY_DATE="date";
    public static final String KEY_HOUR="hour";
    public static final String KEY_MINUTE="minute";

    private SQLiteDatabase db;
    private Context context;
    private DBopenHelper openhelper;

    //私有类协助进行数据库创建和打开
    private static class DBopenHelper extends SQLiteOpenHelper
    {
        //private final static String dbcreatesql="create table "+TABLE_NAME;
        private static final String DB_CREATE = "create table " +
                TABLE_NAME + " (" + KEY_ID + " integer primary key autoincrement, " +
                KEY_TITLE+" text, "+KEY_CONTENT+" text, "+KEY_YEAR+" integer, "+
                KEY_MONTH+" integer, "+KEY_DATE+" integer, "+KEY_HOUR+" integer, "+
                KEY_MINUTE+" integer);";

        public DBopenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        //创建数据表
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        //数据库升级
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(db);
        }
    }

    public ScheduleDbAdapter(Context context){
        this.context=context;
    }

    //打开数据库
    public void open() throws SQLiteException {
        openhelper = new DBopenHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = openhelper.getWritableDatabase();
        }
        catch(SQLiteException ex){
            ex.toString();
        }
    }
    //关闭数据库
    public void close(){
        if(db!=null){
            db.close();
            db=null;
        }
    }

    //插入日程信息
    public long insert(ScheduleInfo info){
        ContentValues newValues=new ContentValues();
        newValues.put(KEY_TITLE,info.getTitle());
        newValues.put(KEY_CONTENT,info.getContent());
        newValues.put(KEY_YEAR,info.getYear());
        newValues.put(KEY_MONTH,info.getMounth());
        newValues.put(KEY_DATE,info.getDate());
        newValues.put(KEY_HOUR,info.getHour());
        newValues.put(KEY_MINUTE,info.getMinute());
        return db.insert(TABLE_NAME,null,newValues);
    }

    //工具方法，将指针还原为具体数据类
    private ScheduleInfo[] ConvertToClass(Cursor cursor) {
        int count=cursor.getCount();
        if(count==0||!cursor.moveToFirst()){
            return null;
        }
        ScheduleInfo[] schdules=new ScheduleInfo[count];
        for(int i=0;i<count;i++){
            schdules[i]=new ScheduleInfo();
            schdules[i].setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            schdules[i].setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
            schdules[i].setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
            schdules[i].setYear(cursor.getInt(cursor.getColumnIndex(KEY_YEAR)));
            schdules[i].setMounth(cursor.getInt(cursor.getColumnIndex(KEY_MONTH)));
            schdules[i].setDate(cursor.getInt(cursor.getColumnIndex(KEY_DATE)));
            schdules[i].setHour(cursor.getInt(cursor.getColumnIndex(KEY_HOUR)));
            schdules[i].setMinute(cursor.getInt(cursor.getColumnIndex(KEY_MINUTE)));
            cursor.moveToNext();
        }
        return schdules;
    }

    //返回对应日期日程信息
    public ScheduleInfo[] searchByDate(int year,int month,int date){
        String[] colums={KEY_ID,KEY_TITLE,KEY_CONTENT,KEY_YEAR,KEY_MONTH,KEY_DATE,KEY_HOUR,KEY_MINUTE};
        //按条件查询，使用小时来排序
        Cursor result=db.query(TABLE_NAME,colums,KEY_YEAR+"="+year+" and "+KEY_MONTH+"="+month+" and "+KEY_DATE+"="+date,null,null,null,KEY_HOUR);
        return ConvertToClass(result);
    }

    //根据id更新数据
    public long updateById(int id,ScheduleInfo info){
        ContentValues newValues=new ContentValues();
        newValues.put(KEY_TITLE,info.getTitle());
        newValues.put(KEY_CONTENT,info.getContent());
        newValues.put(KEY_YEAR,info.getYear());
        newValues.put(KEY_MONTH,info.getMounth());
        newValues.put(KEY_DATE,info.getDate());
        newValues.put(KEY_HOUR,info.getHour());
        newValues.put(KEY_MINUTE,info.getMinute());
        return db.update(TABLE_NAME,newValues,KEY_ID+"="+id,null);
    }

    //根据id删除
    public long deleteById(int id){
        return db.delete(TABLE_NAME,KEY_ID+"="+id,null);
    }
}
