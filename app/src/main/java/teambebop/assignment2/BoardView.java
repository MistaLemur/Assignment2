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

import java.util.List;

//lol
/**
 * Created by d4rk3_000 on 4/23/2017.
 */

public class BoardView extends SurfaceView implements SurfaceHolder.Callback {

    Context thisContext;
    Bitmap icons[];

    List<Integer> indices;
    int prevX;
    int prevY;
    int startRowNum;
    int startColNum;

    CandyTable candyTable;

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
        System.out.println("onDraw()");
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
            candyTable = new CandyTable(9, 9, thisContext);
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
        //System.out.println("Touch event function");

        int currX; // currentX coor
        int currY;  // current Y coor
        int endRowNum = 0;
        int endColNum = 0;
        int width = getWidth();
        int height = getHeight();

        int columnWidth = width / 9;
        int rowHeight = height / 9;

        int action = event.getAction() & event.ACTION_MASK;

        //System.out.println("touch event: " + action);

        if (event.getAction() == MotionEvent.ACTION_DOWN) { //touchdown
            Rect rect = new Rect();

            prevX = (int) event.getX();
            prevY = (int) event.getY();

            startRowNum = prevY / rowHeight;
            startColNum = prevX / columnWidth;

            Candy candy = candyTable.candyBoard.get(startColNum).get(startRowNum);
            candy.debugTap();
        }

        else if (event.getAction() == MotionEvent.ACTION_UP) { //liftup
            currX = (int) event.getX();
            currY = (int) event.getY();

            endRowNum = currY / rowHeight;
            endColNum = currX / columnWidth;

            System.out.println("StartRowNum : " + startRowNum + " StartColNum: " + startColNum);
            System.out.println("EndRowNum: " + endRowNum + " EndColNum: " + endColNum);

            // continue 4301134
            if(startRowNum == endRowNum){
                if(startColNum >endColNum){
                    System.out.println("R to L"):
                    swapGrids(startRowNum, startColNum, endRowNum, )
                }
                else if(startColNum <endColNum){
                    System.out.println("L to R"):
                }else{
                    System.out.println("Action unspecified");
                }
            else if (startColNum == endColNum){
                    if(startColNum >endRowNum){
                        System.out.println("Bottom to Top"):
                    }
                    else if(startColNum <endRowNum){
                        System.out.println("Top to Bottom"):
                    }else{
                        System.out.println("Action unspecified");
                    }
                }
                else{
                    System.out.println("It doesn't make sense");

                }
            }

        return true;
    }

    private void swapGrids(int startRow, int startCol, int endRow, int endCol) {
        int src_index = startRow * 2 + startCol;
        int dest_index = endRow * 2 + endCol;

        int srcVal = indices.get(src_index);
        int destVal = indices.get(dest_index);

        indices.set(src_index, destVal);
        indices.set(dest_index, srcVal);
    }

}

