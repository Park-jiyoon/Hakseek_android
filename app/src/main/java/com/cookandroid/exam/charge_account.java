package com.cookandroid.exam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class charge_account extends Activity {
    int money;
    int charge_money;
    String str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.charge);
        Intent intent=getIntent();
        str=intent.getStringExtra("money");
        money= Integer.parseInt(str);
    }

    //확인 버튼 클릭
    public void mOnClose(View v) {
        //데이터 전달하기
        Intent intent = new Intent(this,Menu_Activity.class);
        EditText txt = (EditText) findViewById(R.id.get_charge_money);
        charge_money= Integer.parseInt(txt.getText().toString());
        money+=charge_money;
        str= Integer.toString(money);
        intent.putExtra("money",str);
        Log.e("zzz",str);
        setResult(-2,intent);

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
