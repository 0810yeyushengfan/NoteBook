package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectActivity extends AppCompatActivity {

    private MyDatabaseHelper myDatabaseHelper;
    private SQLiteDatabase dbWriter;
    private Button main;
    private Button delete;
    private Button commit;
    private ImageView mainPicture;
    private EditText title;
    private EditText text;
    private File outputImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.select);
        main = findViewById(R.id.main);
        title=findViewById(R.id.title);
        text=findViewById(R.id.text);
        commit=findViewById(R.id.commit);
        mainPicture=findViewById(R.id.mainPicture);
        delete=findViewById(R.id.delete);
        myDatabaseHelper=new MyDatabaseHelper(SelectActivity.this,"notes",null,3);
        dbWriter=myDatabaseHelper.getWritableDatabase();
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDB();
                finish();
            }
        });

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDB();
                finish();
            }
        });
        if (ActivityCompat.checkSelfPermission(SelectActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SelectActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if(getIntent().getStringExtra(MyDatabaseHelper.PHOTO).equals("null")){
            mainPicture.setVisibility(View.GONE);
        }else{
            mainPicture.setVisibility(View.VISIBLE);
        }
        text.setText(getIntent().getStringExtra(MyDatabaseHelper.CONTENT));
        title.setText(getIntent().getStringExtra(MyDatabaseHelper.TITLE));
        Bitmap bitmap=BitmapFactory.decodeFile(getIntent().getStringExtra(MyDatabaseHelper.PHOTO));
        mainPicture.setImageBitmap(bitmap);
    }

    public String getTime(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date=new Date();
        String str=simpleDateFormat.format(date);
        return str;
    }
    public void updateDB(){
        ContentValues contentValues=new ContentValues();
        contentValues.put(MyDatabaseHelper.TITLE,title.getText().toString());
        contentValues.put(MyDatabaseHelper.CONTENT,text.getText().toString());
        contentValues.put(MyDatabaseHelper.TIME,getTime());
        dbWriter.update(MyDatabaseHelper.TABLE_NAME,contentValues,"ID = ? ",new String[]{getIntent().getIntExtra(MyDatabaseHelper.ID,0)+""});

    }
    public void deleteDB(){
        dbWriter.delete(MyDatabaseHelper.TABLE_NAME,"id="+getIntent().getIntExtra(MyDatabaseHelper.ID,0),null);


    }

}