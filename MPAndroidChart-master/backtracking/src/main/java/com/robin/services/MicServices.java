package com.robin.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Robin on 2016/6/15.
 */
public class MicServices  extends Service{



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
