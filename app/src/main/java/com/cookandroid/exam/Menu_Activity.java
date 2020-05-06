package com.cookandroid.exam;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Menu_Activity extends AppCompatActivity  {

    Intent Pop_intent;
    Intent Charge_intent;
    Intent Payment_intent;
    String loginD;
    String money;
    int Sum=0;
    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;
    private class ViewHolder {
        public ImageView mIcon;

        public TextView mText;

        public TextView mDate;
    }
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        mListView = (ListView) findViewById(R.id.Menu_list);
        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);
        Pop_intent = new Intent(this,PopActivity.class);
        Charge_intent = new Intent(this,charge_account.class);
        Payment_intent=new Intent(this,Payment.class);



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("Request",Integer.toString(requestCode));
        Log.e("resultdoe",Integer.toString(resultCode));
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                //데이터 받기
                String Price=data.getStringExtra("price");
                String result = data.getStringExtra("data");
                String Cnt = data.getStringExtra("cnt");
                Sum+=(Integer.parseInt(Price)*Integer.parseInt(Cnt));
                byte[] arr = data.getByteArrayExtra("imgg");
                Bitmap bitmap = (Bitmap) BitmapFactory.decodeByteArray(arr,0,arr.length);
                Drawable drawable = new BitmapDrawable(bitmap);
                long now =System.currentTimeMillis();
                Date date=new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String getTime = sdf.format(date);
                mAdapter.addItem(drawable,result+" "+Cnt+"개",getTime);
                dataChange();
                TextView t=(TextView)findViewById(R.id.Total_Sum);
                t.setText("금액 : "+Sum);
            }
            else if (resultCode==-2)
            {
                String money=data.getStringExtra("money");
                TextView txt=(TextView)findViewById(R.id.Account_money);
                txt.setText(money);
            }
            else if (resultCode==-4)
            {
                money=data.getStringExtra("money");
                TextView txt=(TextView)findViewById(R.id.Account_money);
                txt.setText(money);
                for (int i=0;i<mAdapter.getCount();i++)
                    mAdapter.remove(i);
                Sum=0;
            }writeNewUser();


    }}
    private void writeNewUser() {
        TextView txt = (TextView) findViewById(R.id.Account_money);
        int Amoney=Integer.parseInt(txt.getText().toString());
        Intent intent = getIntent();
        String uid = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        loginD = intent.getStringExtra("loginD");
        User user = new User(name,Amoney);
        databaseReference.child(loginD+"_user").child(uid).setValue(user);
        }
    public class User {

        public String username;
        public int pay;

        public User() {

        }

        public User(String username, int pay) {
            this.username = username;
            this.pay = pay;
        }

    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ListData> mListData = new ArrayList<ListData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        public void addItem(Drawable icon, String mTitle, String mDate){
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mIcon = icon;
            addInfo.mTitle = mTitle;
            addInfo.mDate = mDate;
            mListData.add(addInfo);
        }
        public void remove(int position){
            mListData.remove(position);
            dataChange();
        }
        public void sort(){
            Collections.sort(mListData, ListData.ALPHA_COMPARATOR);
            dataChange();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.menu_list_view, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.mImage);
                holder.mText = (TextView) convertView.findViewById(R.id.mText);
                holder.mDate = (TextView) convertView.findViewById(R.id.mDate);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            ListData mData = mListData.get(position);

            if (mData.mIcon != null) {
                holder.mIcon.setVisibility(View.VISIBLE);
                holder.mIcon.setImageDrawable(mData.mIcon);
            }else{
                holder.mIcon.setVisibility(View.GONE);
            }

            holder.mText.setText(mData.mTitle);
            holder.mDate.setText(mData.mDate);

            return convertView;
        }
    }

    public void dataChange(){
        mAdapter.notifyDataSetChanged();
    }
    public void M1_1Click(View v)
    {
        TextView txt =(TextView)findViewById(R.id.textView2);
        Bitmap sendBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o_2800);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Pop_intent.putExtra("data", txt.getText().toString());
        Pop_intent.putExtra("img",byteArray);
        startActivityForResult(Pop_intent,1);

    }

    public void M1_2Click(View v)
    {
        TextView txt =(TextView)findViewById(R.id.textView3);
        Bitmap sendBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dak_4000);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Pop_intent.putExtra("data", txt.getText().toString());
        Pop_intent.putExtra("img",byteArray);
        startActivityForResult(Pop_intent,1);
    }
    public void M1_3Click(View v)
    {
        TextView txt =(TextView)findViewById(R.id.textView4);
        Bitmap sendBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.menu_saetr);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Pop_intent.putExtra("data", txt.getText().toString());
        Pop_intent.putExtra("img",byteArray);
        startActivityForResult(Pop_intent,1);
    }

    public void M2_1Click(View v)
    {
        TextView txt =(TextView)findViewById(R.id.M2_T1);
        Bitmap sendBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.s_o_3800);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Pop_intent.putExtra("data", txt.getText().toString());
        Pop_intent.putExtra("img",byteArray);
        startActivityForResult(Pop_intent,1);
    }
    public void M2_2Click(View v)
    {
        TextView txt =(TextView)findViewById(R.id.M2_T2);
        Bitmap sendBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.g_o_3800);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Pop_intent.putExtra("data", txt.getText().toString());
        Pop_intent.putExtra("img",byteArray);
        startActivityForResult(Pop_intent,1);
    }
    public void M2_3Click(View v)
    {
        TextView txt =(TextView)findViewById(R.id.M2_T3);
        Bitmap sendBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.h_o_4000);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Pop_intent.putExtra("data", txt.getText().toString());
        Pop_intent.putExtra("img",byteArray);
        startActivityForResult(Pop_intent,1);
    }



    public void M3_1Click(View v)
    {
        TextView txt =(TextView)findViewById(R.id.M2_T3);
        Bitmap sendBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.don_o_4500);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Pop_intent.putExtra("data", txt.getText().toString());
        Pop_intent.putExtra("img",byteArray);
        startActivityForResult(Pop_intent,1);
    }
    public void M3_2Click(View v)
    {
        TextView txt =(TextView)findViewById(R.id.M3_T2);
        Bitmap sendBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bul_o_4500);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Pop_intent.putExtra("data", txt.getText().toString());
        Pop_intent.putExtra("img",byteArray);
        startActivityForResult(Pop_intent,1);
    }
    public void M3_3Click(View v)
    {
        TextView txt =(TextView)findViewById(R.id.M3_T3);
        Bitmap sendBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.c_b_4300);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Pop_intent.putExtra("data", txt.getText().toString());
        Pop_intent.putExtra("img",byteArray);
        startActivityForResult(Pop_intent,1);
    }
    public void Charge_Account(View v)
    {
        TextView txt = (TextView) findViewById(R.id.Account_money);
        Charge_intent.putExtra("money",txt.getText().toString());
        startActivityForResult(Charge_intent,1);

    }
    public void Payment(View v)
    {
        TextView txt = (TextView) findViewById(R.id.Account_money);
        int Amoney=Integer.parseInt(txt.getText().toString());

        Log.e("돈",Integer.toString(Amoney));
        if (Sum>=Amoney)
        {
            Payment_intent.putExtra("money", "9");
            Toast.makeText(this,"잔액이부족합니다",Toast.LENGTH_SHORT);
            startActivityForResult(Payment_intent,1);
        }
        else {
            Payment_intent.putExtra("money", Integer.toString(Sum));
            Payment_intent.putExtra("Amoney", txt.getText().toString());
            startActivityForResult(Payment_intent,1);
        }


    }

    }




