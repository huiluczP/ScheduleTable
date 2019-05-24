package com.demo.schedulealarm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.schedulealarm.Dao.ScheduleDbAdapter;
import com.demo.schedulealarm.Element.ScheduleAdapter;
import com.demo.schedulealarm.Element.ScheduleInfo;
import com.haibin.calendarview.CalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendar;
    private ListView schedulelist;
    private List<ScheduleInfo> sches;
    private ScheduleAdapter adapter;
    private TextView add;
    private ScheduleDbAdapter dbAdapter;
    private SwipeRefreshLayout reflash;

    //测试用
    private ScheduleInfo s1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbAdapter=new ScheduleDbAdapter(MainActivity.this);
        schedulelist=(ListView)findViewById(R.id.schelist);
        calendar=(CalendarView)findViewById(R.id.calendarView);
        add=(TextView)findViewById(R.id.addtext);
        reflash=(SwipeRefreshLayout)findViewById(R.id.swiperereshlayout);

        dbAdapter.open();

        init();
        initListView();
        initCalendar();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,CreateActivity.class);
                if(calendar.getSelectedCalendar()!=null){
                    com.haibin.calendarview.Calendar c=calendar.getSelectedCalendar();
                    int info[]={c.getYear(),c.getMonth(),c.getDay()};
                    intent.putExtra("timeinfo",info);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this, "请先选择对应日期", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //测试用初始化
    private void init(){
        s1=new ScheduleInfo();
        s1.setTitle("当天工作");
        s1.setContent("待办工作");

        Calendar c=Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH);
        int day=c.get(Calendar.DAY_OF_MONTH);
        int hour=c.get(Calendar.HOUR);
        int minute=c.get(Calendar.MINUTE);

        s1.setYear(year);
        s1.setMounth(month);
        s1.setDate(day);
        s1.setHour(hour);
        s1.setMinute(minute);

        sches=new ArrayList<ScheduleInfo>();
        sches.add(s1);
    }

    //初始化listview,下拉刷新
    @SuppressLint("ResourceAsColor")
    private void initListView(){

        Log.i("+++before adapter+++",sches.get(0).getTitle()+sches.size());

        //设置显示模式
        adapter=new ScheduleAdapter(this,R.layout.showscheitem_layout,sches);
        schedulelist.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //点击弹出编辑页面,将原信息传回去
        schedulelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //点击跳转到编辑界面
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,sches.get(position).getTitle(),Toast.LENGTH_SHORT).show();
                ScheduleInfo info=sches.get(position);
                Intent intent=new Intent(MainActivity.this,UpdateActivity.class);
                String []stringinfo={info.getTitle(),info.getContent()};
                int []timeinfo={info.getYear(),info.getMounth(),info.getDate(),info.getHour(),info.getMinute()};
                int infoid=info.getId();
                intent.putExtra("stringscheduleinfo",stringinfo);
                intent.putExtra("timeinfo",timeinfo);
                intent.putExtra("id",infoid);
                startActivity(intent);
            }
        });

        //下拉刷新设置
        reflash.setColorSchemeColors(R.color.blue,R.color.orange,R.color.pink);
        reflash.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            //刷新时调用当前数据库,延迟两秒
            @Override
            public void onRefresh() {
                handle.postDelayed(new Runnable() {
                    //调用数据库方法获取当天日程信息（calendar进行对应）
                    @Override
                    public void run() {
                        com.haibin.calendarview.Calendar c=calendar.getSelectedCalendar();

                        int year=c.getYear();
                        int month=c.getMonth();
                        int day=c.getDay();

                        ScheduleInfo[] infos=dbAdapter.searchByDate(year,month,day);
                        sches.clear();
                        if(infos!=null) {
                            for(int i=0;i<infos.length;i++)
                                sches.add(infos[i]);
                        }
                        else
                            sches.add(s1);
                        adapter.notifyDataSetChanged();
                        //停止刷新状态
                        reflash.setRefreshing(false);
                    }
                },500);
            }
        });
    }

    private Handler handle=new Handler();
    //初始化日历控件，设置子线程更新对应list
    private void initCalendar(){
        calendar.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(final com.haibin.calendarview.Calendar calendar, boolean isClick) {
                handle.post(new Runnable() {
                    //调用数据库方法获取当天日程信息（calendar进行对应）
                    @Override
                    public void run() {
                        int year=calendar.getYear();
                        int month=calendar.getMonth();
                        int day=calendar.getDay();

                        ScheduleInfo[] infos=dbAdapter.searchByDate(year,month,day);
                        sches.clear();
                        if(infos!=null) {
                            for(int i=0;i<infos.length;i++)
                                sches.add(infos[i]);
                        }
                        else
                            sches.add(s1);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


}
