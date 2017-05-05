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

public class BoardView extends SurfaceView implements SurfaceHolder.Callback{

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

        gameLogic();
        //I'm putting gameLogic() here because I can't figure out how else to get access to the thread
        //that creates the boardview; Trying to force an onDraw() by calling invalidate() from a different thread does not work. :(
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
        //checkingforcombos();

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

    public void gameLogic(){

        runAnimations();


        invalidate();
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

        int combo[] = candyTable.checkRow(x, y, candyTable.candyBoard); // gives number of combos there is
        if (combo[1] > 3){
            candyTable.popCandies(x,y,combo[1]);
        }






    }
}

