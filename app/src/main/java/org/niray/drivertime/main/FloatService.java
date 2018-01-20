package org.niray.drivertime.main;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import org.niray.drivertime.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class FloatService extends Service {

    WindowManager wManager = null;
    WindowManager.LayoutParams wmParams = null;
    View windowView;
    int state;
    TextView tv_time;
    View iv_close;
    int delaytime = 1 * 1000;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private float StartX;
    private float StartY;
    private Handler handler = new Handler();
    private SimpleDateFormat timeFormater = new SimpleDateFormat("HH:mm:ss");
    private Runnable task = new Runnable() {
        public void run() {
            dataRefresh();
            handler.postDelayed(this, delaytime);
            wManager.updateViewLayout(windowView, wmParams);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        windowView = LayoutInflater.from(this).inflate(R.layout.floating, null);
        tv_time = (TextView) windowView.findViewById(R.id.tv_time);
        iv_close = windowView.findViewById(R.id.iv_close);
        dataRefresh();
        createView();
        handler.postDelayed(task, delaytime);
    }


    private void toLog(String msg) {
        Log.i("Drive", msg);
    }

    private void createView() {
        wManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        wmParams = ((MyApplication) getApplication()).getWindowParams();
        wmParams.flags |= 8;
        wmParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        wmParams.x = 0;
        wmParams.y = 320;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.format = 1;

        wManager.addView(windowView, wmParams);

        windowView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getRawX() - 360;
                y = event.getRawY() - 25;
                toLog("currP" + "currX" + x + "====currY" + y);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        state = MotionEvent.ACTION_DOWN;
                        StartX = x;
                        StartY = y;
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        toLog("startP" + "startX" + mTouchStartX + "====startY" + mTouchStartY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        state = MotionEvent.ACTION_MOVE;
                        updateViewPosition();
                        break;

                    case MotionEvent.ACTION_UP:
                        state = MotionEvent.ACTION_UP;
                        updateViewPosition();
                        mTouchStartX = mTouchStartY = 0;
                        showImg();
                        break;
                }
                return true;
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceStop = new Intent();
                serviceStop.setClass(FloatService.this, FloatService.class);
                stopService(serviceStop);
            }
        });

    }

    public void showImg() {
        if (Math.abs(x - StartX) < 1.5 && Math.abs(y - StartY) < 1.5 && !iv_close.isShown()) {
            iv_close.setVisibility(View.VISIBLE);
        } else if (iv_close.isShown()) {
            iv_close.setVisibility(View.GONE);
        }
    }

    public void dataRefresh() {
        Date date = new Date();
        String str = timeFormater.format(date);
        tv_time.setText(str);
    }

    private void updateViewPosition() {
        wmParams.x = (int) (x - mTouchStartX);
        wmParams.y = (int) (y - mTouchStartY);
        wManager.updateViewLayout(windowView, wmParams);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(task);

        if (wManager != null && windowView != null) {
            wManager.removeView(windowView);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
