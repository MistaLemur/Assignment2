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

    int combo = 1;


    /*
     For delayed checking of row popping, I have to save the x and y coordinates.
     This is necessary because I have to wait for animations to complete before executing anything else.
     */
    boolean shouldCheckPop = false;

    /*
     Combo checking for after candies fall.
     */
    boolean shouldCheckCombos = false;

    /*
    For delayed end condition checking, the same is true; I have to have a flag to keep track of if
    it should check or not whether the game has reached end conditions.
     */
    boolean shouldCheckEnd = false;

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
        //System.out.println("DRAW");
        canvas.drawColor(Color.argb(255, 32, 32, 32));

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
        System.out.println("TOUCH EVENT: " + action);

        int width = getWidth();
        int height = getHeight();
        int colWidth = getWidth() / candyTable.sizeX;
        int colHeight = getHeight() / candyTable.sizeY;

        boolean shouldReadInput = !(hasAnimation || shouldCheckPop || shouldCheckCombos || shouldCheckEnd);

        int coords[] = candyTable.screenCoordsToGridCoords((int)event.getX(), (int)event.getY());

        if (event.getAction() == MotionEvent.ACTION_DOWN && shouldReadInput) { //touchdown
            prevX = coords[0];
            prevY = coords[1];
            if(prevX <0 || prevY <0) return true;
            candyTable.candyBoard.get(prevX).get(prevY).debugTap();
            canSwap = true;
            //System.out.println("Touch down detected: " + prevX + ", "+ prevY);
        }

        else if (event.getAction() == MotionEvent.ACTION_MOVE && canSwap && shouldReadInput) { //dragging.
            //swapping candy
            destX = coords[0];
            destY = coords[1];
            if(destY <0 || destX <0) return true;

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

            shouldCheckPop = candyTable.inputSwap( prevX,prevY,destX,destY);
            System.out.println("SWAPPED? :" + shouldCheckPop);
            canSwap = false;
            combo = 1;
            runAnimations();

        }

        return true;
    }

    public void runAnimations(){
        hasAnimation = false;

        if(candyTable != null){
            if(candyTable.candyList != null && candyTable.candyList.size() > 0) {

                for (Candy candy : candyTable.candyList) {
                    Animation anim = candy.anim;
                    if (anim == null) continue;

                    anim.nextFrame();

                    //System.out.println("RUNNING ANIMATION! " + anim.frameCount);

                    if (anim.frameCount >= anim.numFrames)
                        anim.flush();
                    else hasAnimation = true;
                }
            }

            if(candyTable.candyPopList != null && candyTable.candyPopList.size() > 0){
                for(int i = 0; i < candyTable.candyPopList.size(); i++){
                    CandyPop pop = candyTable.candyPopList.get(i);
                    if(pop == null) continue;

                    pop.nextFrame();
                    if(pop.frameCount > pop.animLength){
                        candyTable.candyPopList.remove(pop);
                        i--;
                    } else hasAnimation = true;
                }
            }

        }

    }

    public void gameLogic(){
        if(!hasAnimation){
            // When check for combos match
            //checkingforcombos();


            //Thsi is a crude state machine for game state checking.
            //Default state for the game is essentially just running animations or awaiting inputs.
            if(shouldCheckPop){

                System.out.println("SHOULDCHECKPOP: " + prevX + ", "+ prevY);
                System.out.println("SHOULDCHECKPOP: " + destX + ", "+ destY);

                candyTable.popCandies(prevX, prevY, combo);
                candyTable.popCandies(destX, destY, combo);

                shouldCheckCombos = true;
                shouldCheckPop = false;

            }else if(shouldCheckCombos){


                shouldCheckCombos = false;
                shouldCheckEnd = true;

            }else if(shouldCheckEnd){

                shouldCheckEnd = false;
            }
        }



        runAnimations();

        invalidate();
    }

    public void checkingforcombos(int xx, int yy, Candy candyList ){// To Anthony-chan, What should the arguments be?

        // check all candies to see if in row

         // gives number of combos there is
        for(int x =0; x> 9; x++) {
            for (int y =0; y > 9;y++) {
                int combo[] = candyTable.checkRow(x, y, candyTable.candyBoard);
                if (combo[2] >= 3) {
                    candyTable.popCandies(x, y, combo[1]);
                }
            }
        }
//for(Candy candy:candyList)



    }
}

