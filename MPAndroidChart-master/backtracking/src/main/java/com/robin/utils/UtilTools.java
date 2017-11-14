package com.robin.utils;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Robin on 2016/6/5.
 */
public class UtilTools {
    //返回文件夹路径
    public static final String BACKTRACKING = "/tracking/";
    public static final String FileCatalog = Environment.getExternalStorageState() + "/tracking/";
    public static String MusicFile = Environment.getExternalStorageDirectory() + "/tracking/sounds.amr";

    public static void createSDCardDir(String rounts) {
        // TODO Auto-generated method stub
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            // 创建一个文件夹对象，赋值为外部存储器的目录
            File sdcardDir =Environment.getExternalStorageDirectory();
            //得到一个路径，内容是sdcard的文件夹路径和名字
            String path=sdcardDir.getPath()+rounts;
            File path1 = new File(path);
            if (!path1.exists()) {
                //若不存在，创建目录，可以在应用启动的时候创建
                path1.mkdirs();
            }
        }
    }

    public static String getText(Context context,int id){
        return  context.getResources().getString(id);
    }
    public static String GetDir(String route) {
        String dir;
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            dir = Environment.getDataDirectory() + route;
        } else {
            // 获取SDCard目录
            dir = Environment.getExternalStorageDirectory() + route;
        }
        return dir;
    }

    //获取当前的时间
    public static String getDate(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
        }
        return flag;
    }

    /**
     * @创建者   ：robin
     * @创建时间 ：2015-11-17 上午10:05:35
     * @方法说明 ： TODO
     */
    public static int dp2px(Context context, int dp)
    {
        // TODO Auto-generated method stub
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        int px = (int) (dp * metrics.density);
        Log.d(" ---Dp---", "dp = " + dp +  "Dp  px = " +px);
        return px;
    }
}
