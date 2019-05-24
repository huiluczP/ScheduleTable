package com.demo.schedulealarm.Element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.demo.schedulealarm.R;

import java.util.List;

public class ScheduleAdapter extends ArrayAdapter<ScheduleInfo> {
    private int resourceId;
    public ScheduleAdapter(Context context, int resource,List<ScheduleInfo>objects) {
        super(context, resource,objects);
        resourceId=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScheduleInfo sche=getItem(position);
        //获取布局文件
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

        //设置显示内容
        TextView title=(TextView)view.findViewById(R.id.show_title);
        TextView content=(TextView)view.findViewById(R.id.show_content);
        TextView time=(TextView)view.findViewById(R.id.show_time);

        int hour=sche.getHour();
        int minute=sche.getMinute();
        String hourstr=hour+"";
        String minutestr=minute+"";
        if(hour<10){
            hourstr="0"+hour;
        }
        if(minute<10){
            minutestr="0"+minute;
        }

        title.setText(sche.getTitle());
        content.setText(sche.getContent());
        time.setText(hourstr+":"+minutestr);

        return view;
    }
}
