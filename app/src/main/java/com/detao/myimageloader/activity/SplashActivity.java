package com.detao.myimageloader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.detao.myimageloader.MainActivity;
import com.detao.myimageloader.R;

/**
 * Created by shaoronggang on 2017/2/15.
 */

public class SplashActivity extends Activity {
    private RelativeLayout relativeLayout;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏通知栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        initView();
    }

    private void initView() {
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_splash);
        //启动动画
        Animation splash_anim = AnimationUtils.loadAnimation(this,R.anim.splash_activity);
        relativeLayout.startAnimation(splash_anim);
        //发送延迟消息，进入主界面
        handler.sendEmptyMessageDelayed(1,2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); //移除多余未处理的消息
    }

}
