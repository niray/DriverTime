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

    WindowManager wm = null;
    WindowManager.LayoutParams wmParams = null;
    View view;
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
            wm.updateViewLayout(view, wmParams);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        view = LayoutInflater.from(this).inflate(R.layout.floating, null);
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        iv_close = view.findViewById(R.id.iv_close);
        dataRefresh();
        createView();
        handler.postDelayed(task, delaytime);
    }

    private void toLog(String msg) {
        Log.i("Drive", msg);
    }

    private void createView() {
        wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        wmParams = ((MyApplication) getApplication()).getMywmParams();
        wmParams.type = 2002;
        wmParams.flags |= 8;
        wmParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        wmParams.x = 0;
        wmParams.y = 370;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.format = 1;

        wm.addView(view, wmParams);

        view.setOnTouchListener(new OnTouchListener() {
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
        wm.updateViewLayout(view, wmParams);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(task);
        wm.removeView(view);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
