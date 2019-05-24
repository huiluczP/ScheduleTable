package com.demo.schedulealarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.demo.schedulealarm.CalendarAlarm.AlarmAdapter;
import com.demo.schedulealarm.Dao.ScheduleDbAdapter;
import com.demo.schedulealarm.Element.ScheduleInfo;

public class CreateActivity extends Activity {

    private TextView showtime;
    private EditText titleadd;
    private EditText contentadd;
    private Button confirmadd;
    private TimePicker timepicker;
    private Button back;

    private int[] time;
    private ScheduleDbAdapter dbAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_schedule);

        //初始化
        init();
        initButton();
    }

    private void init(){
        titleadd=(EditText)findViewById(R.id.titleadd);
        contentadd=(EditText)findViewById(R.id.contentadd);
        showtime=(TextView)findViewById(R.id.showaddtime);
        confirmadd=(Button)findViewById(R.id.confirmadd);
        timepicker=(TimePicker)findViewById(R.id.timepicker);
        back=(Button)findViewById(R.id.goback);

        //显示当前欲插入年月日
        Intent intent=getIntent();
        time=intent.getIntArrayExtra("timeinfo");
        showtime.setText("日程插入时间为"+time[0]+"年"+time[1]+"月"+time[2]+"日");

        //初始化数据库连接
        dbAdapter=new ScheduleDbAdapter(CreateActivity.this);
        dbAdapter.open();
    }

    private void initButton(){
        confirmadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=titleadd.getText().toString();
                String content=contentadd.getText().toString();
                int hour=timepicker.getHour();
                int minute=timepicker.getMinute();

                ScheduleInfo info=new ScheduleInfo();
                info.setTitle(title);
                info.setContent(content);
                info.setYear(time[0]);
                info.setMounth(time[1]);
                info.setDate(time[2]);
                info.setHour(hour);
                info.setMinute(minute);
                dbAdapter.insert(info);

                AlarmAdapter.addCalendarEvent(CreateActivity.this,null,null,0,0,info);
                Toast.makeText(CreateActivity.this, "成功创建日程", Toast.LENGTH_SHORT).show();
                /*
                Intent intent=new Intent(CreateActivity.this,MainActivity.class);
                startActivity(intent);
                */
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CreateActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
