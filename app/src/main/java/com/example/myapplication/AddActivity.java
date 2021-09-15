package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    private MyDatabaseHelper myDatabaseHelper;
    private SQLiteDatabase dbWriter;
    private Button toMain;
    private Button toMenu;
    private Button toHide;
    private Button toCommit;
    private String imagePath=null;
    private PopupWindow menu;
    private Uri imageUri;
    private ImageView picture;
    private EditText title;
    private EditText text;
    private  File outputImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        toMain = findViewById(R.id.toMain);
        title=findViewById(R.id.addTitle);
        text=findViewById(R.id.addText);
        toCommit=findViewById(R.id.toCommit);
        myDatabaseHelper=new MyDatabaseHelper(AddActivity.this,"notes",null,3);
        dbWriter=myDatabaseHelper.getWritableDatabase();
        toCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDB();
                finish();
            }
        });

        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toMenu=findViewById(R.id.toMenu);
        toMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view=getLayoutInflater().inflate(R.layout.activity_menu,null);
                toHide=view.findViewById(R.id.toHide);
                picture=findViewById(R.id.picture);
                menu=new PopupWindow(view,200, ViewGroup.LayoutParams.WRAP_CONTENT);
                toHide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menu.dismiss();
                        Toast.makeText(getApplicationContext(), "该文件已设为私密！", Toast.LENGTH_SHORT).show();
                    }
                });
                Button toInsertPicture=view.findViewById(R.id.toInsertPicture);
                toInsertPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        picture.setVisibility(View.VISIBLE);
                        AlertDialog builder=new AlertDialog.Builder(AddActivity.this).setTitle("请选择获取图片的方式").setPositiveButton("相机拍摄", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                outputImage=new File(getExternalCacheDir(),getTime()+".outputImage.jpg");
                                try {
                                    if(outputImage.exists()){
                                        outputImage.delete();
                                    }
                                    outputImage.createNewFile();
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                                imageUri= FileProvider.getUriForFile(AddActivity.this,"com.example.myapplication.fileprovider",outputImage);
                                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(intent,TAKE_PHOTO);
                            }
                        }).setNeutralButton("手机相册", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(AddActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                }
                                else{
                                    openAlbum();
                                }
                            }
                        }).show();
                    }
                });
                menu.setOutsideTouchable(true);
                menu.setFocusable(true);
                menu.showAsDropDown(toMenu);
            }
        });
    }
    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK){
//                    try{
//                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//                        picture.setImageBitmap(bitmap);
//                    }catch (FileNotFoundException e){
//                        e.printStackTrace();
//                    }
                    Bitmap bitmap=BitmapFactory.decodeFile(outputImage.getAbsolutePath());
                    picture.setImageBitmap(bitmap);
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    handleImageOnKitKat(data);
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @Nullable String[] permissions,@Nullable  int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();                                                                                                                                  openAlbum();
                }else{
                    Toast.makeText(this,"申请权限失败！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
    @TargetApi(29)
    private void handleImageOnKitKat(Intent data){
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.provider.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }

        disPlayImage(imagePath);
    }
    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void disPlayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        }else{
            Toast.makeText(AddActivity.this,"不能得到此图片！",Toast.LENGTH_SHORT).show();
        }
    }
    public void addDB(){
        ContentValues contentValues=new ContentValues();
        contentValues.put(MyDatabaseHelper.TITLE,title.getText().toString());
        contentValues.put(MyDatabaseHelper.CONTENT,text.getText().toString());
        if(outputImage!=null)
            contentValues.put(MyDatabaseHelper.PHOTO,outputImage+"");
        else
            contentValues.put(MyDatabaseHelper.PHOTO,imagePath);
        contentValues.put(MyDatabaseHelper.TIME,getTime());
        dbWriter.insert(myDatabaseHelper.TABLE_NAME,null,contentValues);

    }
    public String getTime(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date=new Date();
        String str=simpleDateFormat.format(date);
        return str;
    }

}