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

import java.util.ArrayList;
import java.util.List;

//lol
/**
 * Created by d4rk3_000 on 4/23/2017.
 */

public class BoardView extends SurfaceView implements SurfaceHolder.Callback{

    /*
        HOW THIS APPLICATION IS STRUCTURED:
        the BoardView class handles the "main loop" and the I/O events (like drawing to screen and touch input).
            Since we couldn't figure out how to call invalidate() from a separate thread,
            and we couldn't figure out where to hook into the main thread that runs the application...
            onDraw() sort of acts as the main loop for our application, since we are effectively making it call invalidate() on itself.
            For the very basics of this assignment, a main loop isn't needed... But to run any animations whatsoever, a main loop is required.

            The onTouch() event does trigger candy swapping, but it does not trigger any candy row "popping".
            All "popping" of candy rows takes place within the gameLogic() function.

        the CandyTable class manages the game itself. It contains the grid that holes the candies, and has all of the functions necessary for
            row checks, candy removal, candy generation, and so forth.

        the Candy class just represents a singular candy object in the grid. It has a type variable, which is used for row checking and such.
            It has two rects. iconRect is for drawing, and touchRect is for recognizing touch events.
            When a touch event is registered, the application iterates through all candies and checks to see if the point exists within the candy's touchRect.
            This is how we determine which candy is being touched and dragged.
            This is necessary since the candy grid does not draw to a constant position;
            the position of the grid can change depending on the size of the screen and the orientation of the screen,
            so checking every candy's touchRect is a simpler solution to find the corresponding candy.

            It's graphics are all initialized into a static array, because it makes little sense to generate bitmaps for each candy object.
            It has an x and y coordinate that refers to its position in the grid. These aren't really used anywhere unfortunately...
            Lastly, it has a reference to an animation object.

        the Animation class is a quick and simple class to handle tweening type of animations.
            Essentially, you give it a number of in-game frames to last, and an initial and a final rect (for drawing sprites) and it will interpolate the rect in-between.
            Hence, tweening (short for in-betweening).
            This class has a number of easing or interpolating functions that can model the behavior of various physical effects.
            For example, a quadratic easing function can be used to model constant acceleration, like falling due to gravity.

        the CandyPop class is another quick and simple class that does a different type of animation.
            It cycles through a set of bitmaps in sequence, kind of like a flipbook.
            This is used for the popping effects when a row of candies is formed.

     */

    Context thisContext;

    //prevXY and destXY are the grid coordinates used when input swapping.
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
        //System.out.println("TOUCH EVENT: " + action);

        int width = getWidth();
        int height = getHeight();
        int colWidth = getWidth() / candyTable.sizeX;
        int colHeight = getHeight() / candyTable.sizeY;

        boolean shouldReadInput = !(hasAnimation || shouldCheckPop || shouldCheckCombos || shouldCheckEnd || (candyTable.gameEnd != 0));

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

            if(Math.abs(dX) > 1 || Math.abs(dY) > 1 || (dX == 0 && dY == 0) ||
                    (dX != 0 && dY != 0)) {
                return true;
            }

            shouldCheckPop = candyTable.inputSwap( prevX,prevY,destX,destY);
            canSwap = false;
            combo = 1;
            runAnimations();

        }

        return true;
    }

    public void runAnimations(){
        //This function makes every animation and every candyPop advance by 1 frame.
        hasAnimation = false;

        if(candyTable != null){
            if(candyTable.candyList != null && candyTable.candyList.size() > 0) {

                for (Candy candy : candyTable.candyList) {
                    Animation anim = candy.anim;
                    if (anim == null) continue;

                    anim.nextFrame();

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
            //This is a crude state machine for running the game logic.
            //Default state for the game is essentially just running animations or awaiting inputs.
            //Otherwise, it will be specifically running checks for different states of the game, such as if a cascading combo is running.
            if(shouldCheckPop){
                //shouldcheckpop state is specifically for checking if the VERY previously swapped candies created rows to pop

                shouldCheckCombos = true;
                shouldCheckPop = false;

                //Get the candies from X and Y that forms rows
                ArrayList<Candy> popCandiesList = candyTable.getCandiesToPop(prevX, prevY);
                popCandiesList.addAll(candyTable.getCandiesToPop(destX, destY));

                //Pop the candies that were found
                candyTable.popCandies(popCandiesList, combo);


            }else if(shouldCheckCombos){
                //This state is for checking when newly falling candies spontaneously form new rows.
                //Like a combo, or a cascade.

                shouldCheckCombos = false;
                shouldCheckEnd = true;

                combo++;
                checkingforcombos();


            }else if(shouldCheckEnd){
                //This state is for checking the game end conditions.
                // //Note that this doesn't process if the game is still processing combos.

                shouldCheckEnd = false;

                candyTable.updateGameEnd();
            }
        }

        runAnimations();

        invalidate();
    }

    public void checkingforcombos(){
        /*
         This function checks the whole board to see if there are any rows and columns of 3 that exist, and pops them if they do.

         A better solution would be to check only the locations directly around the affected shifted columns,
         but that's a bit more complex to implement;
         This simpler solution doesn't run quite as quickly, but it gets the job done.

         Algorithm:
         1. For every candy in the candy board:
            a. Check to see if it forms any rows, horizontally or vertically
            b. If it does form rows, then add add the candy to the popCandiesList
         2. If there are any candies in the popCandiesList, pop all of them.
         */



        ArrayList<Candy> popCandiesList = new ArrayList<Candy>();
         // gives number of combos there is
        for(int x =0; x< candyTable.sizeX; x++) {
            for (int y =0; y < candyTable.sizeY;y++) {

                int rowLengths[] = candyTable.checkRow(x, y, candyTable.candyBoard);

                if (rowLengths[0] >= 3 || rowLengths[1] >= 3 ) {
                    popCandiesList.addAll(candyTable.getCandiesToPop(x, y));
                }

            }
        }

        if(popCandiesList.size() > 0){
            shouldCheckCombos = true;
            candyTable.popCandies(popCandiesList, combo);
        }
    }
}

