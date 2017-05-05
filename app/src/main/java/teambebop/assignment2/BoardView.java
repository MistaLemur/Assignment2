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
import java.lang.Thread;

import java.util.List;

//lol
/**
 * Created by d4rk3_000 on 4/23/2017.
 */

public class BoardView extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    Context thisContext;

    int prevX;
    int prevY;
    int destX;
    int destY;

    boolean canSwap = false;

    public static CandyTable candyTable;
    public static boolean hasAnimation = false;

    BoardView(Context context) {
        //Constructor for the surface
        super(context);
        thisContext = context;
        getHolder().addCallback(this);
        setFocusable(true);
        setWillNotDraw(false);
        System.out.println("Constructor");
    }

    @Override
    public void onDraw(Canvas canvas) {
        //Called when drawing shit.
        System.out.println("DRAW");
        canvas.drawColor(Color.WHITE);

        int width = getWidth();
        int height = getHeight();

        if(candyTable != null) {
            candyTable.updateScreenDims(width, height);
            candyTable.drawToCanvas(canvas);
        }
        /*
        //deprecated example code from discussion
       // Drawing the whole screen. until we get one screen into another
        Rect rect = new Rect();

        int width = getWidth();
        int height = getHeight();

        int rowHeight = height / 9;//3
        int columnWidth = width / 9;//2

        for(int i = 0; i < 9; ++i) { //3
            for(int j = 0; j < 9; ++j) { //2
                rect.set(j * columnWidth, i * rowHeight, (j + 1) * columnWidth, (i + 1) * rowHeight);
                canvas.drawBitmap(icons[indices.get(i * 2 + j)], null, rect, null);
            }
        }
        */

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) { // when you start your app created new candytable
        //Initialization for when the surface is created
        System.out.println("SurfaceCreated()");
        //icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        if(candyTable == null) {
            System.out.println("Generating new candytable");
            candyTable = new CandyTable(9, 9, getWidth(), getHeight(), thisContext);
        }
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
    public boolean onTouchEvent(MotionEvent event) { // unchanged  DO swapping
        //Event listening
        int action = event.getAction() & event.ACTION_MASK;

        int width = getWidth();
        int height = getHeight();
        int colWidth = getWidth() / candyTable.sizeX;
        int colHeight = getHeight() / candyTable.sizeY;

        if (event.getAction() == MotionEvent.ACTION_DOWN && !hasAnimation) { //touchdown
            prevX = (int) event.getX() / colWidth; //X that was touched
            prevY = (int) event.getY() / colHeight; // Y that was touched
            candyTable.candyBoard.get(prevX).get(prevY).debugTap();
            canSwap = true;
            //System.out.println("Touch down detected: " + prevX + ", "+ prevY);
        }

        else if (event.getAction() == MotionEvent.ACTION_MOVE && canSwap && !hasAnimation) { //dragging.
            //swapping candy
            destX = (int) event.getX() / colWidth;
            destY = (int) event.getY() / colHeight;

            //deltas for x and y
            int dX = destX - prevX;
            int dY = destY - prevY;

            //System.out.println("Touch up detected: " + destX + ", " + destY);
            //If you're trying to swap nonadjacent candies... return
            if(Math.abs(dX) > 1 || Math.abs(dY) > 1 || (dX == 0 && dY == 0) ||
                    (dX != 0 && dY != 0)) {
                //System.out.println("Cannot swap: " + dX + ", " + dY);
                return true;
            }

            candyTable.inputSwap( prevX,prevY,destX,destY);
            canSwap = false;
        }

        runAnimations();
        // When check for combos match
        checkingforcombos();

        invalidate();
        return true;

    }

    public void runAnimations(){
        hasAnimation = false;

        if(candyTable != null && candyTable.candyList != null && candyTable.candyList.size() > 0) {

            for (Candy candy : candyTable.candyList) {
                Animation anim = candy.anim;
                if (anim == null) continue;

                anim.nextFrame();
                hasAnimation = true;

                //System.out.println("RUNNING ANIMATION! " + anim.frameCount);

                if (anim.frameCount >= anim.numFrames)
                    anim.flush();
            }
        }

    }

    @Override
    public void run(){
        while(true){
            runAnimations();
            //invalidate(); I have no idea how to access this thing from this other thread. :(

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkingforcombos(int x, int y, Candy candyList ){

        // needs to check for combos
        // checks for combos

        // check
        // if after it matches  is there any other combos?

        // Check every candy after first one matches.
        // check
        // if after it matches  is there any other combos?

        // Check every candy after first one matches.

        int combo[] = candyTable.checkRow(x, y, board); // gives number of combos there is
        if (combo > 3){
            candyTable.popcandies(x,y,combo);


        }






    }
}

