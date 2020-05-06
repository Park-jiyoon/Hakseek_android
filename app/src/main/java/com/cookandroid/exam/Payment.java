package com.cookandroid.exam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class Payment extends Activity {
    int money;
    int charge_money;
    String str;
    String str2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.payment_layout);
        Intent intent=getIntent();
        str = intent.getStringExtra("money");
        str2 = intent.getStringExtra("Amoney");
        money = Integer.parseInt(str);

        TextView txt=(TextView)findViewById(R.id.payment_text);
        if (str.equals("9"))
        {
            txt.setText("페이가 부족합니다");
            intent.putExtra("money",-3);
            setResult(-3,intent);
            finish();
        }
        else {
            charge_money = Integer.parseInt(str2);
            txt.setText(money + "원을 결제합니다");
        }
    }

    //확인 버튼 클릭
    public void mOnClose(View v) {
        //데이터 전달하기
        Intent intent = new Intent(this, Menu_Activity.class);
        charge_money-=money;
        String s=Integer.toString(charge_money);
        intent.putExtra("money",s);
        setResult(-4,intent);
       // startActivity(intent);
        finish();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;

    }
}