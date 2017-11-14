package com.robin.com.robin.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.robin.backtracking.R;
import com.robin.bean.ListviewBean;

import java.util.ArrayList;

/**
 * Created by Robin on 2016/6/11.
 */
public class AutoAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ListviewBean> listBean;


    public AutoAdapter(Context context , ArrayList<ListviewBean> been){
        this.context = context;
        this.listBean = been;
    }

    @Override
    public int getCount() {
        return listBean.size();
    }

    @Override
    public Object getItem(int i) {
        return listBean.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListviewBean viewBean = listBean.get(position);
        if(convertView == null){
            convertView = View.inflate(context, R.layout.auto_list_layout,null);
        }
        TextView timeText = (TextView) convertView.findViewById(R.id.text_starttime);
        TextView value = (TextView) convertView.findViewById(R.id.dbvalue);
        TextView distance = (TextView) convertView.findViewById(R.id.text_distance);
        String timeStr = viewBean.getStartTime() + " — " + viewBean.getEndTime();


        distance.setText(viewBean.getDistance());
        distance.setTextColor(context.getResources().getColor(R.color.red_text));
        timeText.setText(timeStr);
        value.setText(String.valueOf(viewBean.getDbvalue()));
        if(viewBean.getDbvalue() >= 1){
            timeText.setTextColor(context.getResources().getColor(R.color.red_text));
            value.setTextColor(context.getResources().getColor(R.color.red_text));
        }else{
            timeText.setTextColor(context.getResources().getColor(R.color.main_text_color));
            value.setTextColor(context.getResources().getColor(R.color.main_text_color));
        }
        return convertView;
    }
}
