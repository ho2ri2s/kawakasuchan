package com.kurus.kawakasuchan;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    //Frame per second
    static  final long FPS = 30;
    static final long FRAME_TIME = 1000 / FPS;
    private SurfaceHolder surfaceHolder;
    private Bitmap imgCharacter, imgDryer;
    private int viewWidth, viewHeight;
    private float dryerX, dryerY;
    Thread thread;


    public MainSurfaceView(Context context, SurfaceView surfaceView){
        super(context);
        init(context, surfaceView);
    }
    public MainSurfaceView(Context context, SurfaceView surfaceView, DragEvent event){
        super(context);
        init(context, surfaceView);
        dryerX = event.getX();
        dryerY = event.getY();
    }

    public void init(Context context, SurfaceView surfaceView){
        this.surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        Resources resources = context.getResources();
        imgCharacter = BitmapFactory.decodeResource(resources, R.drawable.girl);
        imgDryer = BitmapFactory.decodeResource(resources, R.drawable.dryer);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        canvas.drawBitmap(imgCharacter, 0,0, null);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        viewWidth = width;
        viewHeight = height;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
    }

    @Override
    public void run() {
        if(thread != null) {
            // TODO: 2019/04/06 ドライヤーやキャラクターの描画処理
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawBitmap(imgCharacter, 0, 0, null);
            canvas.drawBitmap(imgDryer, dryerX - imgDryer.getWidth() / 2,
                    dryerY - imgDryer.getHeight(), null);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
        try {
            thread.sleep(FRAME_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }
}
