package com.example.admin.candycrushclone;

import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import java.util.ArrayList;

public class BoardView extends SurfaceView implements SurfaceHolder.Callback {
    //This class represents how we interact with the touchscreen surface.
    //This class can capture touch events, and also controls draw events with the surface.

    BoardView(Context context) {
        //constructor for the boardview.

        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setWillNotDraw(false);
        System.out.println("Constructor");
    }


    @Override
    public void onDraw(Canvas canvas) {
        //Draw event

        //First draw a flat white background
        System.out.println("onDraw()");
        canvas.drawColor(Color.WHITE);
        int width = getWidth();
        int height = getHeight();
        /*
        int rowSize = width/3;
        int columnSize = height/4;
        */

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Surface initialization
        System.out.println("SurfaceCreated()");
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //surfaceChanged, like for when the phone goes sideways.

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Touchscreen input event
        System.out.println("touch event");
        //if (event.getAction() == MotionEvent.ACTION_DOWN) {
        //}

        return false;
    }
}
