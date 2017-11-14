package com.robin.backtracking;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.robin.bean.ListviewBean;
import com.robin.bean.Topbar;
import com.robin.com.robin.view.adapter.AutoAdapter;
import com.robin.utils.DatabaseHelper;
import com.robin.utils.UtilTools;

import java.util.ArrayList;

/**
 * Created by Robin on 2016/6/11.
 */
public class RecordListview extends Activity {

    private ListView listView;
    private DatabaseHelper databaseHelper;
    private ArrayList<ListviewBean> listBean = new ArrayList<>();
    private AutoAdapter autoAdapter;
    private Topbar topbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_act);
        listView = (ListView) findViewById(R.id.record_listview);
        topbar = (Topbar) findViewById(R.id.list_topbar);

        topbar.setTitle(UtilTools.getText(this,R.string.data_list),"");
        topbar.setTopbarClickListen(new Topbar.topbarClickListen() {
            @Override
            public void onleftClick() {
                finish();
            }

            @Override
            public void onrightClick() {

            }
        });
        getDbDate();
        System.out.println(" listben ------------->>> " + listBean.size());
        autoAdapter = new AutoAdapter(this,listBean);
        listView.setAdapter(autoAdapter);
    }



    private int dbFlag = -1;
    private void getDbDate(){
        String sql = "Select * from AutoRecoder";
        Cursor cursor = getDB().query(sql);
        ListviewBean viewBean = null;
        String time = "";
        String lastTime = "";  //记住上一次 dbflag和db 相等的时间
        String distance = "";
        if(cursor.getCount() > 0){
          //  System.out.println(" ---------count = " + cursor.getCount());
            do {
                time = cursor.getString(1);
                int db = cursor.getInt(2);

                switch (db){
                    case 0:
                        distance = "S>110";
                        break;
                    case 1:
                        distance = "100<S<110";
                        break;
                    case 2:
                         distance = "90<S<100";
                        break;
                    case 3:
                        distance = "80<S<90";
                        break;

                    case 4:

                        distance = "70<S<80";
                        break;
                    case 5:
                        distance = "60<S<70";
                        break;

                    case 6:
                        distance = "50<S<60";
                        break;
                    case 7:
                        distance = "40<S<50";
                        break;
                    case 8:
                        distance = "30<S<40";
                        break;

                    case 9:
                        distance = "20<S<30";
                        break;

                    case 10:
                        distance = "0<S<20";
                        break;
                }

                if(db > 0){
                    db = 1;
                }
                System.out.println(" time == " + time + " db --> " + db);

                 if(dbFlag != db){
                    if(viewBean != null){
                      /*  if(lastTime.equals("")){
                            viewBean.setEndTime(time);
                        }else{
                            viewBean.setEndTime(lastTime);
                        }*/
                        viewBean.setEndTime(lastTime);
                        listBean.add(viewBean);
                        viewBean = new ListviewBean();

                      /*  if(lastTime.equals("")){
                            viewBean.setStartTime(time);
                        }else{
                            viewBean.setStartTime(lastTime);
                        }*/
                      //  viewBean.setStartTime(time);
                        viewBean.setStartTime(lastTime);
                        viewBean.setDbvalue(db);
                        viewBean.setDistance(distance);
                    }else{
                        viewBean = new ListviewBean();
                        viewBean.setStartTime(time);
                        viewBean.setDbvalue(db);
                        viewBean.setDistance(distance);
                    }
                }else{
                    // lastTime = time;
                     System.out.println(" dbflag = " + dbFlag + " db = " + db);
                 }
                lastTime = time;
                dbFlag = db;
            }while (cursor.moveToNext());
        }
        if(viewBean != null){
            viewBean.setEndTime(lastTime);
            listBean.add(viewBean);

        }
    }


    public DatabaseHelper getDB() {
        if (databaseHelper == null) {
            synchronized (RecordListview.class) {
                databaseHelper = DatabaseHelper.getInstanece(getApplicationContext());
            }
        }
        return databaseHelper;
    }
}
