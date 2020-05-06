package com.cookandroid.exam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;


public class PopActivity extends Activity {
    int cnt=1;
    byte[] arr;
    Bitmap bitmap;
    Bitmap sendBitmap;
    String str;
    String Price=new String();
    String Menu=new String();
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pop_layout);
        TextView txtText = (TextView)findViewById(R.id.Subject);
        img = (ImageView) findViewById(R.id.Menu_Img);
        Intent intent = getIntent();
        str=intent.getStringExtra("data");
        for(int i = 0 ; i < str.length(); i ++)
        {
            if(48 <= str.charAt(i) && str.charAt(i) <= 57)
                Price += str.charAt(i);
        }
        for (int i=0;i<str.length();i++)
        {
            if (str.charAt(i)==' ')
                break;
            Menu+=str.charAt(i);
        }
        Log.e("zz",Price);
        arr = getIntent().getByteArrayExtra("img");
        bitmap = (Bitmap) BitmapFactory.decodeByteArray(arr,0,arr.length);
        img.setImageBitmap(bitmap);
        txtText.setText(Menu);
    }

    //확인 버튼 클릭
    public void mOnClose(View v) {
        //데이터 전달하기
        Intent intent = new Intent(this,Menu_Activity.class);
        TextView txt = (TextView) findViewById(R.id.Menu_cnt);
        intent.putExtra("cnt", txt.getText().toString());
        intent.putExtra("data", Menu);
        intent.putExtra("price",Price);

        sendBitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        intent.putExtra("imgg",byteArray);
        setResult(-1,intent);

        //액티비티(팝업) 닫기
        finish();
    }
    public void sum_Click(View v)
    {
        cnt++;
        TextView txtText = (TextView)findViewById(R.id.Menu_cnt);
        txtText.setText(Integer.toString(cnt));
    }
    public void subBtn_Click(View v)
    {
        if (cnt>1)
            cnt--;
        TextView txtText = (TextView)findViewById(R.id.Menu_cnt);
        txtText.setText(Integer.toString(cnt));
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
