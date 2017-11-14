package com.robin.backtracking;


import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;


import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.github.mikephil.charting.data.Entry;
import com.robin.bean.ChartDateBean;
import com.robin.utils.UtilTools;

/**
 * amr音频处理
 *
 * @author hongfa.yy
 * @version 创建时间2012-11-21 下午4:33:28
 */
public class RecordManager {
	private final String TAG = "RecordManager";
	private MediaRecorder mMediaRecorder;
	public static final int MAX_LENGTH = 1000 * 60 * 60 * 24;// 最大录音时长24小时;
	private File file;
	private Handler reHandler;

	public RecordManager(File file,Handler handler) {
		this.file = file;
		this.reHandler=handler;
	}
	public RecordManager(File file) {
		this.file = file;
	}

	private long startTime;
	private long endTime;

	/**
	 * 开始录音 使用amr格式
	 *
	 *
	 *            录音文件
	 * @return
	 */
	public void startRecord() {
		// 开始录音
		/* ①Initial：实例化MediaRecorder对象 */
		if (mMediaRecorder == null)
			mMediaRecorder = new MediaRecorder();
		try {
			/* ②setAudioSource/setVedioSource */
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
			/*
			 * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
			 * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
			 */
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			/* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			/* ③准备 */
			mMediaRecorder.setOutputFile(file.getAbsolutePath());
			mMediaRecorder.setMaxDuration(MAX_LENGTH);
			mMediaRecorder.prepare();
			/* ④开始 */
			mMediaRecorder.start();
			// AudioRecord audioRecord.
			/* 获取开始时间* */
			startTime = System.currentTimeMillis();
			// pre=mMediaRecorder.getMaxAmplitude();
			updateMicStatus();
			Log.i("ACTION_START", "startTime" + startTime);
		} catch (IllegalStateException e) {
			Log.i(TAG,
					"call startAmr(File mRecAudioFile) failed!"
							+ e.getMessage());
		} catch (IOException e) {
			Log.i(TAG,
					"call startAmr(File mRecAudioFile) failed!"
							+ e.getMessage());
		}

	}

	/**
	 * 停止录音
	 */
	public long stopRecord() {
		if (mMediaRecorder == null)
			return 0L;
		endTime = System.currentTimeMillis();
		Log.i("ACTION_END", "endTime" + endTime);
		mMediaRecorder.stop();
		mMediaRecorder.reset();
		mMediaRecorder.release();
		mMediaRecorder = null;
		Log.i("ACTION_LENGTH", "Time" + (endTime - startTime));
		return endTime - startTime;
	}

	private Runnable mUpdateMicStatusTimer = new Runnable() {
		public void run() {
			updateMicStatus();
		}
	};

	/**
	 * 更新话筒状态 分贝是也就是相对响度 分贝的计算公式K=20lg(Vo/Vi) Vo当前振幅值 Vi基准值为600：我是怎么制定基准值的呢？ 当20
	 * * Math.log10(mMediaRecorder.getMaxAmplitude() / Vi)==0的时候vi就是我所需要的基准值
	 * 当我不对着麦克风说任何话的时候，测试获得的mMediaRecorder.getMaxAmplitude()值即为基准值。
	 * Log.i("mic_", "麦克风的基准值：" + mMediaRecorder.getMaxAmplitude());前提时不对麦克风说任何话
	 */
	private int BASE = 600;
	private int SPACE = 1000;// 间隔取样时间

	private void updateMicStatus() {
		if (mMediaRecorder != null) {
			int redioMax =  mMediaRecorder.getMaxAmplitude();
			//System.out.println("------------->>getMaxAmplitude() . " + redioMax);
			int ratio = redioMax/ BASE;
			//	System.out.println(" ------>>> ratio = " + ratio);
			double db = 0;// 分贝
			if (ratio > 1)
				db = ( Math.log10(ratio));
			System.out.println("分贝值："+db+"     "+Math.log10(ratio));
			setCharBean(db);
			reHandler.postDelayed(mUpdateMicStatusTimer,SPACE);
			/*
			 * if (db > 1) { vuSize = (int) (20 * Math.log10(db)); Log.i("mic_",
			 * "麦克风的音量的大小：" + vuSize); } else Log.i("mic_", "麦克风的音量的大小：" + 0);
			 */
		}
	}



	private void setCharBean(double dbfloat){
		//	dbfloat = Double.valueOf(String.format("%.2s",dbfloat));
		System.out.println(" dbloat = " + dbfloat);
		int db = 0;
		int distance = 0;
		if(dbfloat > 1.7){
			db = 10;
			distance = 20;
		}else if(dbfloat > 1.6){
			db = 9;
			distance = 30;
		}else if(dbfloat > 1.5){
			db = 8;
			distance = 40;
		}else if(dbfloat > 1.4){
			db = 7;
			distance = 50;
		}else if(dbfloat > 1.3){
			db = 6;
			distance = 60;
		}else if(dbfloat > 1.2){
			db = 5;
			distance = 70;
		}else if(dbfloat > 1.1){
			db = 4;
			distance = 80;
		}else if(dbfloat > 1){
			db = 3;
			distance = 90;
		}else if(dbfloat > 0.9){
			db = 2;
			distance = 100;
		}else if(dbfloat > 0.8){
			db = 1;
			distance = 110;
		}else{
			db = 0;
			distance = 120;
		}

		ChartDateBean  chartDateBean = new ChartDateBean();
		chartDateBean.setTime(UtilTools.getDate());
		chartDateBean.setDb(db);
        chartDateBean.setDitstance(distance);
		Message message = new Message();
		message.what = 0;
		message.obj = chartDateBean;
		System.out.println(" ----------->>> audio db = " + db);
		reHandler.sendMessage(message);
		//chartDateBean.setEntry(new Entry(db,));
		//Entry entry = new Entry();
		//entry.set
	}

}
