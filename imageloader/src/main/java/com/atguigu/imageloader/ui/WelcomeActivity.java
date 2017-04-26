package com.atguigu.imageloader.ui;

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

import com.atguigu.imageloader.R;

public class WelcomeActivity extends Activity {

    private RelativeLayout rl_welcome;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1:
                    //启动新的界面
                    Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();//销毁当前的WelcomeActivity
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏通知栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        //初始化视图对象
        rl_welcome = (RelativeLayout) findViewById(R.id.rl_welcome);

        //启动动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha_welcome);
        rl_welcome.startAnimation(animation);

        //动画结束以后，启动MainActivity
        //方式一：给动画设置监听
        //方式二：发送延迟消息
        handler.sendEmptyMessageDelayed(1,2000);

    }

    /**
     * 当2s以内退出当前欢迎界面时，应该移除没有处理的消息
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
