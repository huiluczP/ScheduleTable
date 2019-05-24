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

public class UpdateActivity extends Activity {

    private EditText titleupdate;
    private EditText contentupdate;
    private Button confirmupdate;
    private Button confirmdelete;
    private TimePicker timepicker;
    private Button back;

    private ScheduleInfo former;
    private ScheduleDbAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_schedule);

        //初始化
        init();
    }

    //将intent中信息填到页面中
    private void init(){
        titleupdate=(EditText)findViewById(R.id.titleupdate);
        contentupdate=(EditText)findViewById(R.id.contentupdate);
        confirmupdate=(Button)findViewById(R.id.confirmupdate);
        confirmdelete=(Button)findViewById(R.id.confirmdelete);
        timepicker=(TimePicker)findViewById(R.id.timeupdatepicker);
        back=(Button)findViewById(R.id.goback);

        dbAdapter=new ScheduleDbAdapter(this);
        dbAdapter.open();
        Intent intent=getIntent();
        String stringinfo[]=intent.getStringArrayExtra("stringscheduleinfo");
        int timeinfo[]=intent.getIntArrayExtra("timeinfo");
        int id=intent.getIntExtra("id",0);
        former=new ScheduleInfo();

        former.setId(id);
        former.setTitle(stringinfo[0]);
        former.setContent(stringinfo[1]);
        former.setYear(timeinfo[0]);
        former.setMounth(timeinfo[1]);
        former.setDate(timeinfo[2]);
        former.setHour(timeinfo[3]);

        titleupdate.setText(former.getTitle());
        contentupdate.setText(former.getContent());
        timepicker.setHour(former.getHour());
        timepicker.setMinute(former.getMinute());

        confirmupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=titleupdate.getText().toString();
                String content=contentupdate.getText().toString();
                int hour=timepicker.getHour();
                int minute=timepicker.getMinute();

                former.setTitle(title);
                former.setContent(content);
                former.setHour(hour);
                former.setMinute(minute);

                dbAdapter.updateById(former.getId(),former);
                Toast.makeText(UpdateActivity.this, "成功修改日程", Toast.LENGTH_SHORT).show();
                 /*
                Intent intent=new Intent(UpdateActivity.this,MainActivity.class);
                startActivity(intent);
                */
            }
        });

        confirmdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAdapter.deleteById(former.getId());
                //删除提醒
                AlarmAdapter.deleteCalendarEvent(UpdateActivity.this,former.getTitle());
                Toast.makeText(UpdateActivity.this, "成功删除日程", Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(UpdateActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UpdateActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
