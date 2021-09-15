package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

class MyAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Cursor cursor;
    private LinearLayout layout;


    @Override
    public int getCount() {
        return cursor.getCount(); }

    public MyAdapter(Context context,Cursor cursor) {
        this.mInflater = LayoutInflater.from(context);
        this.cursor=cursor;
    }


    @Override
    public Object getItem(int position) {
        return cursor.getPosition(); }

    @Override
    public long getItemId(int position) {
        return position; }
        class ViewHolder{
        TextView tvOne;
        TextView tvTwo;
        ImageView iv;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.item,null);
            viewHolder = new ViewHolder();
            viewHolder.tvOne = convertView.findViewById(R.id.mainTitle);
            viewHolder.tvTwo = convertView.findViewById(R.id.mainTime);
            viewHolder.iv = convertView.findViewById(R.id.mainPicture);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

       cursor.moveToPosition(position);
        String title=cursor.getString(cursor.getColumnIndex("title"));
        String time=cursor.getString(cursor.getColumnIndex("time"));
        String url=cursor.getString(cursor.getColumnIndex("photo"));
        viewHolder.tvOne.setText(title);
        viewHolder.tvTwo.setText(time);
        viewHolder.iv.setImageBitmap(getImageThumbnail(url,200,200));
        return convertView;
        }

        public Bitmap getImageThumbnail(String uri,int width,int height){
        Bitmap bitmap=null;
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        bitmap=BitmapFactory.decodeFile(uri,options);
        options.inJustDecodeBounds=false;
        int beWidth=options.outWidth/width;
        int beHeight=options.outHeight/height;
        int be=1;
        if(beWidth<beHeight){
            be=beWidth;
        }
        else{
            be=beHeight;
        }
        if(be<=0){
            be=1;
        }
        options.inSampleSize=be;
        bitmap=BitmapFactory.decodeFile(uri,options);
        bitmap= ThumbnailUtils.extractThumbnail(bitmap,width,height,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;

        }
}