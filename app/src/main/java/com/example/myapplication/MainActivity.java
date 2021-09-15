package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MyDatabaseHelper myDatabaseHelper;
    private SQLiteDatabase dbReader;
    private MyAdapter adapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDatabaseHelper=new MyDatabaseHelper(MainActivity.this,"notes",null,3);
        dbReader=myDatabaseHelper.getReadableDatabase();
        lv= (ListView) findViewById(R.id.lv);
        Button toAdd=findViewById(R.id.toAdd);
        toAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);

            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor=dbReader.query(MyDatabaseHelper.TABLE_NAME,null,null,null,null,null,null);
                cursor.moveToPosition(position);
                Intent intent=new Intent(MainActivity.this,SelectActivity.class);
                intent.putExtra(MyDatabaseHelper.ID,cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.ID)));
                intent.putExtra(MyDatabaseHelper.TITLE,cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.TITLE)));
                intent.putExtra(MyDatabaseHelper.PHOTO,cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.PHOTO)));
                intent.putExtra(MyDatabaseHelper.CONTENT,cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.CONTENT)));

                startActivity(intent);
            }
        });
    }
    public String getTime(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date=new Date();
        String str=simpleDateFormat.format(date);
        return str;
    }
    public void selectDB(){
        Cursor cursor= dbReader.query(MyDatabaseHelper.TABLE_NAME,null,null,null,null,null,null);
        adapter=new MyAdapter(MainActivity.this,cursor);
        lv.setAdapter(adapter);

    }
    @Override
    protected void onResume(){
        super.onResume();
        selectDB();

    }

    class Main{
        private String title;
        private Date time;
        private String location;
        private int picture;

        public Main() {
        }

        public Main(String title, Date time, String location, int picture) {
            this.title = title;
            this.time = time;
            this.location = location;
            this.picture = picture;
        }



        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public String getLocation(String mainLocation) {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getPicture() {
            return picture;
        }

        public void setPicture(int picture) {
            this.picture = picture;
        }



    }

}