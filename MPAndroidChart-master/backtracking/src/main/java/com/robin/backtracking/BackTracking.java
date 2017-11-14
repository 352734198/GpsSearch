package com.robin.backtracking;

import android.app.Activity;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.Utils;
import com.robin.bean.ChartDateBean;
import com.robin.bean.Topbar;
import com.robin.customeView.CustomDialog;
import com.robin.customeView.MyMarkerView;
import com.robin.utils.DatabaseHelper;
import com.robin.utils.UtilTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BackTracking extends Activity implements OnChartGestureListener,OnChartValueSelectedListener {
    private  LineChart lineChart;
    private ArrayList<String> xvalues = new ArrayList<>();
    private ArrayList<Entry>  yValue = new ArrayList<>();
    private ArrayList<ChartDateBean> listDateBean = new ArrayList<>();
    private RecordManager recordManager;
    private File sounfile;
    public static int Number = 0;   //用于全局的标识，没有采集到声音的次数
    private DatabaseHelper databaseHelper;

    private Button previewBtn;
    //  private Button saveBtn;
    private Topbar topbar;

    private  PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;
    private boolean HEADSET_PLUG = false;  // 判断是否有耳机插入的标识
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(" ------------->>> oncreate");
        powerManager = (PowerManager)this.getSystemService(this.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        setContentView(R.layout.activity_back_tracking);
        audioInitChart();
        initBtn();
        registerHeadsetPlugReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initBtn(){
        topbar = (Topbar) findViewById(R.id.main_topbar);


        topbar.setTitle(UtilTools.getText(this,R.string.main_text),
                UtilTools.getText(this,R.string.see_data));
        topbar.setLeftvisibility(false);
        topbar.setTopbarClickListen(new Topbar.topbarClickListen() {
            @Override
            public void onleftClick() {

            }

            @Override
            public void onrightClick() {
                Intent intent = new Intent(BackTracking.this,RecordListview.class);
                // recordManager.stopRecord();
                startActivity(intent);
            }
        });
        //默认打开是 开启预览 于保存数据的
        previewBtn = (Button) findViewById(R.id.preview_button);

        previewBtn.setActivated(false);
        previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!previewBtn.isActivated()&& !HEADSET_PLUG){
                    new SweetAlertDialog(BackTracking.this).setTitleText("提示").setConfirmText("确定")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .setContentText("请先插入探测器").show();
                    return;
                }
                previewBtn.setActivated(!previewBtn.isActivated());
                if(previewBtn.isActivated()){
                    previewBtn.setText(R.string.stop_monitor);
                    //recordManager.stopRecord();
                    if(xvalues.size() == 0){
                        showAlertDialoge();
                    }
                }else{
                    previewBtn.setText(R.string.start_monitor);
                }
            }
        });

        textView = (TextView) findViewById(R.id.model_change);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCanClick){
                    Toast.makeText(BackTracking.this,"切换过于频繁",Toast.LENGTH_SHORT).show();
                    return;
                }
                isCanClick = false;
                isModelDistance = !isModelDistance;
                xvalues.clear();
                yValue.clear();
                for(int i=0; i<listDateBean.size(); i++){
                    xvalues.add(listDateBean.get(i).getTime());

                    int size = yValue.size();    //获取列表的长度，
                    if(!isModelDistance){
                        yValue.add(new Entry(listDateBean.get(i).getDb(), size));
                    }else{
                        yValue.add(new Entry(listDateBean.get(i).getDitstance(), size));
                    }
                }

                if(!isModelDistance){
                    leftAxis.setAxisMaxValue(10f);
                    leftAxis.setAxisMinValue(0f);
                    textView.setText("切换模式(信号模式)");
                }else{
                    leftAxis.setAxisMaxValue(120f);
                    leftAxis.setAxisMinValue(20f);
                    textView.setText("切换模式(距离模式)");
                }

                handler.sendEmptyMessageDelayed(1,2000);
                setData(xvalues,yValue);
                lineChart.invalidate();
            }
        });

    }

    private boolean isModelDistance = false;
    private boolean isCanClick = true;
    //获取传过来的数据跟新Ui
    private void  updateData(Message message){
        if(!previewBtn.isActivated()){
            return;
        }
        ChartDateBean dateBean = (ChartDateBean) message.obj;
        int size = yValue.size();    //获取列表的长度，
        System.out.println("------------>>> dateBean.getDb = " + dateBean.getDb() + "  Distance  = " + dateBean.getDitstance());
        if(dateBean.getDb() < 1 && Number < 60 * 10){
            System.out.println("------------>> number = " + Number);
            if(Number < 2){
                if(size > 1){
                    if(yValue.get(size -2).getVal() < 1 &&yValue.get(size - 1).getVal() < 1){
                        Number ++ ;   //如果
                    }
                }
                System.out.println(" dateBean.getTime() = " + dateBean.getTime() +"  number = "  + Number);
                xvalues.add(dateBean.getTime());
                if(!isModelDistance){
                    yValue.add(new Entry(dateBean.getDb(), size));
                }else{
                    yValue.add(new Entry(dateBean.getDitstance(), size));
                }
            }else{
                if(size > 0){
                    xvalues.set(size -1,dateBean.getTime());
                    if(!isModelDistance){
                        yValue.set(size -1 ,new Entry(dateBean.getDb(), size -1));
                    }else{
                        yValue.set(size -1 ,new Entry(dateBean.getDitstance(), size -1));
                    }
                }
            }
            Number ++ ;
            return;
        }
        else{
            Number = 0;
        }
        listDateBean.add(dateBean);
        int length = yValue.size();  // 获取添加数据列表后的长度实际上也是之前的长度

        if(length < 2){
            insertDb(dateBean);
        }else{
            int lastdb  = (int) yValue.get(length- 1).getVal();
            int lasttwo = (int) yValue.get(length- 2).getVal();
            ChartDateBean lastBean;
            if(lastdb < 1 && lasttwo < 1){
                lastBean = new ChartDateBean();
                lastBean.setDb(0);
                lastBean.setTime(xvalues.get(length-2));
                insertDb(lastBean);

                lastBean = new ChartDateBean();
                lastBean.setDb(0);
                lastBean.setTime(xvalues.get(length-1));
                insertDb(lastBean);
            }else if(lastdb < 1){
                lastBean = new ChartDateBean();
                lastBean.setDb(0);
                lastBean.setTime(xvalues.get(length-1));
                insertDb(lastBean);
            }
        }

        if(dateBean.getDb() >= 1){
            insertDb(dateBean);
            xvalues.add(dateBean.getTime());
            if(!isModelDistance){
                yValue.add(new Entry(dateBean.getDb(), yValue.size()));
            }else{
                yValue.add(new Entry(dateBean.getDitstance(), yValue.size()));
            }

        }
        //如果按钮是激活的 则更新Ui // 停止预览
        //System.out.println(" ----------->>> preview Button  = " + previewBtn.isActivated() );
        //    if(previewBtn.isActivated()){
        setData(xvalues,yValue);
        lineChart.invalidate();
        //   }
    }


    //初始化，声音监听的
    private void initRm(){
        //先删除 目录下的所有文件
        UtilTools.delAllFile(UtilTools.FileCatalog);
        //如果目录不存在创建目录
        UtilTools.createSDCardDir(UtilTools.BACKTRACKING);
        //新建保存音频的文件
        String filePath = UtilTools.GetDir(UtilTools.BACKTRACKING);
        sounfile = new File(UtilTools.MusicFile);
        if(!sounfile.exists()){
            try {
                sounfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        recordManager = new RecordManager(sounfile,handler);
        recordManager.startRecord();
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    updateData(msg);
                    break;
                case 1:
                    isCanClick = true;
                    break;
                default:
                    break;
            }
        }
    };






    //将数据插入数据库
    private void insertDb(ChartDateBean dateBean)
    {

      /*  if(!saveBtn.isActivated()){
            return;
        }*/
        ContentValues values = new ContentValues();
        values.put("time", dateBean.getTime());
        values.put("dbvalue", dateBean.getDb());
        getDB().insert("AutoRecoder", values);
    }


    private YAxis leftAxis;
    private void audioInitChart(){
        lineChart = (LineChart) findViewById(R.id.audio_chart);
        lineChart.setOnChartGestureListener(this);
        lineChart.setOnChartGestureListener(this);
        lineChart.setOnChartValueSelectedListener(this);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription("");
        lineChart.setNoDataTextDescription("");
        lineChart.setTouchEnabled(true);

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);
        XAxis xAxis = lineChart.getXAxis();

        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines

        if(!isModelDistance){
            leftAxis.setAxisMaxValue(10f);
            leftAxis.setAxisMinValue(0f);
        }else{
            leftAxis.setAxisMaxValue(120f);
            leftAxis.setAxisMinValue(20f);
        }
     /*   leftAxis.setAxisMaxValue(120f);
        leftAxis.setAxisMinValue(0f);*/
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);


        leftAxis.setDrawLimitLinesBehindData(true);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
        // get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();

        l.setForm(Legend.LegendForm.LINE);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        lineChart.setMarkerView(mv);

    }

    //private voi

    private void setData(ArrayList<String> xValue, ArrayList<Entry> yValue) {
        LineDataSet set1;
        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)lineChart.getData().getDataSetByIndex(0);
            set1.setYVals(yValue);
            lineChart.getData().setXVals(xValue);
            lineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yValue, "DataSet 1");
            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(0.8f);
            set1.setCircleRadius(1.5f);
            set1.setDrawCircleHole(false);
            set1.setDrawValues(false);
            set1.setDrawFilled(true);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }


            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(xValue, dataSets);

            // set data
            lineChart.setData(data);
        }
    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            lineChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + lineChart.getLowestVisibleXIndex() + ", high: " + lineChart.getHighestVisibleXIndex());
        Log.i("MIN MAX", "xmin: " + lineChart.getXChartMin() + ", xmax: " + lineChart.getXChartMax() + ", ymin: " + lineChart.getYChartMin() + ", ymax: " + lineChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    public DatabaseHelper getDB() {
        if (databaseHelper == null) {
            synchronized (BackTracking.class) {
                databaseHelper = DatabaseHelper.getInstanece(getApplicationContext());
            }
        }
        return databaseHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recordManager.stopRecord();
        System.out.println(" ------------>> main destroy");
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_LONG).show();
                exitTime = System.currentTimeMillis();
            } else {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * @创建者   ：Robin
     * @创建时间 ：2015-12-30 下午2:43:08
     * @方法说明 ： 弹出确认保存Dialoge
     */
    private void showAlertDialoge() {
        // TODO Auto-generated method stub
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(R.string.notice_text);
        builder.setPositiveButton(R.string.start_monitor, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                initRm();
                String sql = "Delete from AutoRecoder";
                getDB().execSQL(sql);
                previewBtn.setText(R.string.stop_monitor);
                dialog.dismiss();
                //设置你的操作事项
            }
        });

        builder.setNegativeButton(R.string.cancel,
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        previewBtn.setActivated(!previewBtn.isActivated());
                        previewBtn.setText(R.string.start_monitor);
                        dialog.dismiss();
                    }});

        builder.create(false).show();
    }


    /*  private void registerHeadsetPlugReceiver() {
              IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
              this.registerReceiver(headsetPlugReceiver, intentFilter);
          }

          private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {

              @Override
              public void onReceive(Context context, Intent intent) {
                  String action = intent.getAction();
                  System.out.println("---------->>> action = " + action);
                  if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                      // handleHeadsetDisconnected();
                  }
              }
      };*/
    private void registerHeadsetPlugReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headsetPlugReceiver, intentFilter);


    }

    private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if ("android.intent.action.HEADSET_PLUG".equals(action)) {
                if (intent.hasExtra("state")) {
                    System.out.println("state  = " + intent.getIntExtra("state", 0));
                    if (intent.getIntExtra("state", 0) == 0) {
                        //handleHeadsetDisconnected();
                        if(previewBtn.isActivated()){
                            new SweetAlertDialog(BackTracking.this).setTitleText("提示").setConfirmText("确定")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                        }
                                    })
                                    .setContentText("探测器已经拔出").show();
                        }
                        HEADSET_PLUG = false;
                        previewBtn.setActivated(false);
                        previewBtn.setText(R.string.start_monitor);
                    }else{
                        HEADSET_PLUG = true;
                    }
                }
            }
        }

    };

}
