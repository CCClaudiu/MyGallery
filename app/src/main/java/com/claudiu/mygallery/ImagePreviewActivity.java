package com.claudiu.mygallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ImagePreviewActivity extends Activity {
    private ImageView mImageView;
    public static final String IMAGE_PATH ="IMAGE_PATH";
    public static final String IMAGE_ID ="IMAGE_ID";
    private int picId;
    private Bitmap mBitmap;
    private String[] picPaths =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        Intent it = getIntent();
        if(it == null){
            return;
        }
        picPaths = it.getStringArrayExtra(IMAGE_PATH);
        picId = it.getIntExtra(IMAGE_ID, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initUI();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupBitmap(picPaths[picId]);
            }
        }, 500);
    }

    @Override
    protected void onStop() {
        super.onStop();
        destoryBitmap();
    }
    private void initUI(){
        mImageView = findViewById(R.id.ivImagePrev);
        RelativeLayout ll=findViewById(R.id.rl);
        ll.setLongClickable(true);
    }
    private void setupBitmap(String path){
        if(mBitmap != null){
            mBitmap.recycle();
        }
        int reqW = mImageView.getWidth();
        int reqH = mImageView.getHeight();
        mBitmap = BitmapUtils.getOriginBitmap(path, reqW, reqH);
        mImageView.setImageBitmap(mBitmap);
    }
    private void destoryBitmap(){
        if(mBitmap != null){
            mBitmap.recycle();
        }
    }

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };
}
