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
    int destX;
    int destY;

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
            System.out.println("Generating new candytable");
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
        int action = event.getAction() & event.ACTION_MASK;

        int width = getWidth();
        int height = getHeight();
        int colWidth = getWidth() / candyTable.sizeX;
        int colHeight = getHeight() / candyTable.sizeY;

        if (event.getAction() == MotionEvent.ACTION_DOWN) { //touchdown
            System.out.println("Touch down detected");
            prevX = (int) event.getX() / colWidth; //X that was touched
            prevY = (int) event.getY() / colHeight; // Y that was touched
            candyTable.candyBoard.get(prevX).get(prevY).debugTap();
        }

        else if (event.getAction() == MotionEvent.ACTION_UP) { //liftup
            System.out.println("Touch up detected");
            //swapping candy
            destX = (int) event.getX() / colWidth;
            destY = (int) event.getY() / colHeight;

            //deltas for x and y
            int dX = destX - prevX;
            int dY = destY - prevY;

            //If you're trying to swap nonadjacent candies... return
            if(Math.abs(dX) > 1 || Math.abs(dY) > 1 || (dX == 0 && dY == 0) ||
                    (dX != 0 && dY != 0)) {
                System.out.println("Cannot swap: " + dX + ", " + dY);
                return false;
            }

            candyTable.inputSwap( prevX,prevY,destX,destY);
            // check if candy match

            // if candies dont match

            /*

            if ("candies match function") {

                candyTable.generateNewCandy();
                candyTable.shiftCandyColumn();
                candyTable.addScore();
                candyTable.setScore();
            }
            */

            }

            return true;

        }

  /*  private void swapGrids(int startRow, int startCol, int endRow, int endCol) {
        int src_index = startRow * 2 + startCol;
        int dest_index = endRow * 2 + endCol;

        int srcVal = indices.get(src_index);
        int destVal = indices.get(dest_index);

        indices.set(src_index, destVal);
        indices.set(dest_index, srcVal);
    }
*/
}

