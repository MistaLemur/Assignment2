package teambebop.assignment2;

import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.content.Context;

/**
 * Created by d4rk3_000 on 4/23/2017.
 */

public class BoardView extends SurfaceView implements SurfaceHolder.Callback {



    BoardView(Context context) {
        //Constructor for the surface
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setWillNotDraw(false);
        System.out.println("Constructor");
    }

    @Override
    public void onDraw(Canvas canvas) {
        //Called when drawing shit.
        System.out.println("onDraw()");
        canvas.drawColor(Color.WHITE);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Initialization for when the surface is created
        System.out.println("SurfaceCreated()");
        //icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //Surface changed. Like when changing between landscape and portrait modes.

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //For clean up

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Event listening

        int action = event.getAction() & event.ACTION_MASK;

        System.out.println("touch event: " + action);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        }

        if (event.getAction() == MotionEvent.ACTION_UP) {

        }

        return true;
    }
}
