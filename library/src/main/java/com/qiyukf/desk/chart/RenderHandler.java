package com.qiyukf.desk.chart;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by zhoujianghua on 2016/10/9.
 */

public class RenderHandler {
    private static Handler renderHandler;
    public static Handler get() {
        if (renderHandler == null) {
            synchronized (RenderHandler.class) {
                HandlerThread thread = new HandlerThread("Render-Chart-Thread");
                thread.start();
                renderHandler = new Handler(thread.getLooper());
            }
        }
        return renderHandler;
    }
}
