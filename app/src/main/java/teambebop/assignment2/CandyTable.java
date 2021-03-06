/*
Author: Anthony SuVasquez

This file declares the CandyTable class. 
This represents the board of candy objects, and has many functions for swapping candies, checking for lines, and creating new candies.
*/

package teambebop.assignment2;

import android.graphics.Canvas;
import java.util.Random;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Paint;

public class CandyTable {

    //grid size variables
    int sizeX = 9;
    int sizeY = 9;

    //screen size variables
    int screenWidth;
    int screenHeight;

    //screen pixel offset variables for drawing
    int offX = 0; // for a new window screen corner to candy corner
    int offY = 0;

    //border width of the screen
    int screenBorder = 0; //the width

    Context appContext; // get drawing of bitmaps

    int animLength = 20;


    public ArrayList<Candy> candyList = new ArrayList<Candy>();
    //This is a 1d arraylist of candy references, just to make stuff easier to iterate through.

    public ArrayList<CandyPop> candyPopList = new ArrayList<CandyPop>();
    //This is a 1d arraylist of candypop effect references.

    public ArrayList<ArrayList<Candy>> candyBoard = new ArrayList<ArrayList<Candy>>(); // list of array list. in columns.
    //The candyBoard is an arraylist of arraylists, kind of like a 2d array.
    //To read from this arraylist, do...
    // candyBoard.get(X).get(Y);

    double score = 0;
    double winningScore = 1000000;
    int gameEnd = 0; //0 for game hasn't ended, -1 for game lost, 1 for game won.

    public CandyTable(){  //basic constructor
        generateNewBoard();
    }

    public CandyTable(int x, int y, int sWidth, int sHeight, Context context){ // context from candy create cndy tables of any size

        sizeX = x;
        sizeY = y;
        screenWidth = sWidth;
        screenHeight = sHeight;

        appContext = context;
        generateNewBoard();
    }

    public void generateNewBoard(){
        //This fills the board with new candies
        //Cleanup any preexisting stuffs.
        for(ArrayList A:candyBoard){ //cleans everything empty each column
            A.clear();
        }
        candyBoard.clear(); // erase the actual column

        for(Candy C:candyList){
            C.flush(); //cleanup any extra memory allocated to the candies.
        }

        candyList.clear(); //there should be nothing that reference to candy

        //now build the new board's arraylists columns first
        for(int i = 0; i < sizeX; i++) { //# of columns
            //build the column list
            ArrayList<Candy> column = new ArrayList<Candy>(sizeY);

            for(int j=0; j < sizeY; j++) { //# of rows
                column.add(null);
            }

            candyBoard.add(column); //adding columns to the candy board
        }

        //now generate the candies
        for(int i = 0; i < sizeX; i++){ //# of columns
            //build the column list
            ArrayList<Candy> column = candyBoard.get(i);

            for(int j=0; j < sizeY; j++){ //# of rows
                //fill in the columns.
                Candy candy = generateNewCandy(i, j); //new object at i,j

                column.add(candy);
            }

        }
    }

    public boolean inputSwap(int x, int y, int newX, int newY){
        //This function attempts to swap the candy at x,y in the given direction
        //Returns true, if success, and false, if failed move.
        /*
         * dx and dy are just delta variables that indicate the swapping direction
         *
         * ::ALGORITHM::
         * 1. Perform the swapping operation
         * 2. Check for any newly created rows.
         * 3. If there are newly created rows, then delete all candies in the row and award points popcandy
         * 4. Otherwise, undo the swapping operation.
         * 5. Apply any animations if necessary.
         */

        //bounds checking for swapping parameters
        if(x < 0 || x >= sizeX) return false;
        if(y < 0 || y >= sizeY) return false;
        if(newX < 0 || newX >= sizeX) return false;
        if(newY < 0 || newY >= sizeY) return false;

        System.out.println("Attempting swap");

        //swap the pieces here
        Candy initial = candyBoard.get(x).get(y); // initial position
        Candy swapped = candyBoard.get(newX).get(newY); // destination position
        candyBoard.get(x).set(y, swapped); //initi
        candyBoard.get(newX).set(newY, initial); // swapping the other

        Rect start = new Rect(initial.iconRect);
        Rect end = new Rect(swapped.iconRect);

        int[] rowLengths = checkRow(newX, newY, candyBoard); // chcking the rows
        System.out.println("Found rows: " + rowLengths[0] + ", " + rowLengths[1]);
        boolean success = false;
        int combo = 1;
        if(rowLengths[0] < 3 && rowLengths[1] < 3) { //successful swap to check // 0 horizontal // 1 vertical
            rowLengths = checkRow(x,y,candyBoard);
        }

        if(rowLengths[0] >= 3 || rowLengths[1] >= 3){
            //successful swap.
            System.out.println("Successful Candy Swap: updating sprite positions...");


            setCandyXY(initial, newX, newY);
            setCandyXY(swapped, x, y);

            initial.newAnimation(start, initial.iconRect, animLength);
            initial.anim.animType = 3;
            swapped.newAnimation(end, swapped.iconRect, animLength);
            swapped.anim.animType = 3;
            return true;


        }else{ //failed swap
            candyBoard.get(x).set(y, initial); //initi
            candyBoard.get(newX).set(newY, swapped); // swapping the other
            //do an animation for failed swap??
            initial.newAnimation(start, end, animLength*2);
            initial.anim.animType = 1;

            swapped.newAnimation(end, start, animLength*2);
            swapped.anim.animType = 1;

            return false;
        }

    }

    public ArrayList<Candy> getCandiesToPop(int x, int y){
        //this function returns a list of candies that make up the rows and columns of 3

        if(x < 0 || x >= sizeX) return null;
        if(y < 0 || y >= sizeY) return null;

        Candy initial = candyBoard.get(x).get(y); // it saves the initial
        ArrayList<Candy> candiesToPop = new ArrayList<Candy>();

        int left = 0, right = 0, up = 0, down = 0;
        left = checkRowRecursive( x-1, y,   -1, 0,   0, initial, candyBoard);
        right = checkRowRecursive(x+1, y,   1, 0,   0, initial, candyBoard);
        up = checkRowRecursive(   x, y-1,   0, -1,   0, initial, candyBoard);
        down = checkRowRecursive( x, y+1,   0, 1,   0, initial, candyBoard);

        if(left + right + 1 >= 3){
            getCandiesInRow(x, y,  1,0,  initial, candyBoard, candiesToPop);
            getCandiesInRow(x, y,  -1,0,  initial, candyBoard, candiesToPop);
        }
        if(up + down + 1 >= 3){
            getCandiesInRow(x, y,  0,1,  initial, candyBoard, candiesToPop);
            getCandiesInRow(x, y,  0,-1,  initial, candyBoard, candiesToPop);

        }

        return candiesToPop;
    }

    public void popCandies(ArrayList<Candy> candiesToPop, int combo){
        /*
        This function takes an arraylist of candies, and removes all of them from the grid.
         It also awards points, based on how many candies and the combo number.
         */
        double points = candiesToPop.size() * combo * combo * 200;
        //points awarded for popping candies is nonlinear.
        //This is to promote strategic play to set up big combos!
        addScore(points);

        for(Candy candy:candiesToPop){
            //add effects here. Thsi is added here because of an order of operations problem with this and column shifting.
            candyPopList.add( new CandyPop(candy, animLength/2, appContext));
        }
        for(Candy candy:candiesToPop){
            if(!candyList.contains(candy)){
                //This candy has already been removed...?
                continue;
            }

            //kill the candy here.
            removeCandy(candy);

            //shift candies
            shiftCandyColumn(candy.x, candy.y); // when candy falls

            //make new candy at top of column
            Candy newCandy = generateNewCandy(candy.x, 0);
            setCandyXY(newCandy, candy.x, 0);

            Rect oldRect = new Rect(newCandy.iconRect);
            oldRect.top -= newCandy.iconRect.height();
            oldRect.bottom -= newCandy.iconRect.height();

            newCandy.newAnimation(oldRect, newCandy.iconRect, animLength);

        }
    }

    public void shiftCandyColumn(int x, int y){
        //This is for shifting candies after a row has been cleared at position x,y.
        //This means x,y is now empty, and everything above y must now be moved downwards.
        ArrayList<Candy> column = candyBoard.get(x);
        for(int i = y; i > 0; i--){
            Candy candy = column.get(i-1);

            Rect oldRect = new Rect(candy.iconRect);
            setCandyXY(candy, x, i);

            column.set(i, candy);

            candy.newAnimation(oldRect, candy.iconRect, animLength);
            candy.anim.animType = 2;
        }
        column.set(0, null); //for the time being, put a null into the topmost position.
    }

    public void removeCandy(Candy candy){
        candy.flush();
        candyList.remove(candy);
        if(candyBoard.get(candy.x).get(candy.y) == candy)
            candyBoard.get(candy.x).set(candy.y, null);
    }

    public void getCandiesInRow(int x, int y, int dx, int dy, Candy initial,
            ArrayList<ArrayList<Candy>> board, ArrayList<Candy> candies) {

        //This will add any found candies to the given candies list, since objects are passed by reference in java.
        if (x < 0 || x >= sizeX) return;
        if (y < 0 || y >= sizeY) return;

        Candy current = board.get(x).get(y);
        if (current.type == initial.type) {
            if(!candies.contains(current)) candies.add(current);
            getCandiesInRow(x+dx, y+dy,  dx, dy,  initial,  board, candies);
        } else {
            return;


        }//not finshed
    }

    public int[] checkRow(int x, int y, ArrayList<ArrayList<Candy>> board){ // to count how many candies of the same colors.
        /*
         This function checks for any rows that include the given position at x,y.
         It checks for both vertical and horizontal rows.
         It's return format is: {vertical row length, horizontal row length}
         */
        int vertical = 1;
        int horizontal = 1;

        if(x > -1 && x < sizeX && y > -1 && y < sizeY) {
            Candy initial = board.get(x).get(y);
                //north
            vertical += checkRowRecursive(x, y+1, 0, 1, 0, initial, board);
            //south
            vertical += checkRowRecursive(x, y-1, 0, -1, 0, initial, board);

            //east
            horizontal += checkRowRecursive(x+1, y, 1, 0, 0, initial, board);
            //west
            horizontal += checkRowRecursive(x-1, y, -1, 0, 0, initial, board);
        }

        int[] rows = new int[]{vertical, horizontal};
        return rows;
    }

    public int checkRowRecursive(int x, int y, int dx, int dy, int depth, Candy initialCandy, ArrayList<ArrayList<Candy>> board){
        //This is a recursive function that searches for candies in the same direction;
        //It will return the number of candies it found in that direction.
        if(x < 0 || x >= sizeX) return depth;
        if(y < 0 || y >= sizeY) return depth;

        Candy current = board.get(x).get(y);
        if(current == null) return depth;

        if(current.type == initialCandy.type){
            return checkRowRecursive(x + dx, y + dy, dx, dy, depth+1, initialCandy, board);
        }else{
            return depth;
        }
    }

    public boolean hasRemainingMove() {
        //This function searching for any possible move that can be made.
        //IF one is found, then returns true. Otherwise, false.

        /*
        * ::ALGORITHM::
        * 1. Do a shallow clone of the table of candies.
        * 2. For each candy in the given list
        *   a) Perform swapping operations of that candy in the cloned table
        *   b) Check if any rows can be created from that swapping operation
        *   e) If a successful move was found, then return true
        *   d) undo the swapping operation and move onto the next candy.
        */

        ArrayList<ArrayList<Candy>> clone = cloneBoard();
        int[] rowLengths = new int[2];
        for(int x = 0; x < sizeX; x++){
            for(int y = 0; y < sizeY; y++){
                //each test swapping operations is
                //1. perform the swap
                //2. check rows and process success/fail
                //3. undo the swap

                //test swapping north
                if(y < sizeY-1) {
                    boardSwapCandies(x, y, 0, 1, clone);
                    rowLengths = checkRow(x, y + 1, clone);
                    if (rowLengths[0] >= 3 || rowLengths[1] >= 3) {
                        //successful move
                        return true;
                        //maybe save the move here...
                    }
                    boardSwapCandies(x, y, 0, 1, clone); //undo the swap
                }


                //test swapping south
                if(y > 0) {
                    boardSwapCandies(x, y, 0, -1, clone);
                    rowLengths = checkRow(x, y - 1, clone);
                    if (rowLengths[0] >= 3 || rowLengths[1] >= 3) {
                        //successful move
                        return true;
                        //maybe save the move here...
                    }
                    boardSwapCandies(x, y, 0, -1, clone); //undo the swap
                }


                //test swapping east
                if(x < sizeX-1) {
                    boardSwapCandies(x, y, 1, 0, clone);
                    rowLengths = checkRow(x + 1, y, clone);
                    if (rowLengths[0] >= 3 || rowLengths[1] >= 3) {
                        //successful move
                        return true;
                        //maybe save the move here...
                    }
                    boardSwapCandies(x, y, 1, 0, clone); //undo the swap
                }


                //test swapping west
                if(x > 0) {
                    boardSwapCandies(x, y, -1, 0, clone);
                    rowLengths = checkRow(x - 1, y, clone);
                    if (rowLengths[0] >= 3 || rowLengths[1] >= 3) {
                        //successful move
                        return true;
                        //maybe save the move here...
                    }
                    boardSwapCandies(x, y, -1, 0, clone); //undo the swap
                }
            }
        }
        return false;
    }

    public ArrayList<ArrayList<Candy>> cloneBoard(){ // just clones a board. ????
        //This function makes a shallow copy of the board. This is useful for checking potential moves.
        ArrayList<ArrayList<Candy>> clone = new ArrayList<ArrayList<Candy>>();
        for(ArrayList<Candy> column:candyBoard){
            clone.add((ArrayList<Candy>) column.clone());
        }
        return clone;
    }

    public void boardSwapCandies(int x, int y, int dx, int dy, ArrayList<ArrayList<Candy>> board ){ // swapping to any board
        //This function swaps two candies for the given board.
        //It does not process any row detection or any score processing.
        //This is useful for checking for potential moves.

        int newX = x + dx;
        int newY = y + dy;

        Candy initial = board.get(x).get(y);
        Candy swapped = board.get(newX).get(newY);
        board.get(x).set(y, swapped);
        board.get(newX).set(newY, initial);
    }

    public void addScore(double amount){ //
        //This is just for incrementing the score.
        score += amount;

    }

    public int[] screenCoordsToGridCoords(int x, int y){
        //This function converts screen pixel coordinates to grid index coordinates.
        //It returns grid index coordinates in the format {x, y}
        //If the coords are not within the grid, it returns {-1,-1}
        int coords[] = new int[2];
        coords[0] = -1;
        coords[1] = -1;

        for(Candy candy:candyList){
            if(candy.touchRect != null){
                if(candy.touchRect.contains(x,y)){
                    coords[0] = candy.x;
                    coords[1] = candy.y;
                    break;
                }
            }
        }

        return coords;
    }

    public Candy generateNewCandy(int x, int y){// creates new candy.
        Random rand = new Random();
        int type = rand.nextInt(Candy.numTypes);

        Candy candy = new Candy(type, appContext);
        //Generate a random new candy, with array position x and y.
        setCandyXY(candy, x, y);
        candyList.add(candy);


        do{
            int[] rowLengths = checkRow(x, y, candyBoard);
            if(rowLengths[0] < 3 && rowLengths[1] < 3)
                break;

            candy.type = rand.nextInt(Candy.numTypes);
        }while(true);


        return candy;
    }

    public void setCandyXY(Candy candy, int x, int y){ //making smaller square in rectangle
        /*
        This function is for setting the x,y coordinates of the candy in the table.
        This also updates the candy's drawing position and touching position.
        It's necessary that candies have a separate drawing rect and touching rect, because
        the touch box might be of a different aspect ratio from the drawing box.
         */
        if(candy == null) return;

        //create two new rects here!
        //one is for the sprites
        //the other is for touch
        int colWidth = screenWidth / sizeX;
        int rowHeight = screenHeight / sizeY;

        /*
        if(colWidth < rowHeight) rowHeight = colWidth;
        else colWidth = rowHeight;
        */

        //first create touch rect
        Rect touchRect = new Rect();
        touchRect.set(x * colWidth + offX, y * rowHeight + offY,
                (x+1) * colWidth + offX, (y+1) * rowHeight + offY);

        //then create the draw rect as a square centered in the touch rect.
        int cX = touchRect.centerX();
        int cY = touchRect.centerY();
        int width = touchRect.width();
        int height = touchRect.height();

        int borderGap = 10;
        int drawWidth = width-borderGap;
        if(height < width) drawWidth = height-borderGap;
        int xOff = (width - drawWidth)/2;
        int yOff = (height - drawWidth)/2;

        Rect drawRect = new Rect();
        drawRect.set(cX - width/2 + xOff, cY - height/2 + yOff,
                cX - width/2 + xOff + drawWidth, cY - height/2 + yOff + drawWidth);

        candy.touchRect = touchRect;
        candy.iconRect = drawRect;
        candy.x = x;
        candy.y = y;

        if(candyBoard.size() > x){
            if(candyBoard.get(x).size() > y){
                candyBoard.get(x).set(y, candy);
            }
        }
    }

    public void updateScreenDims(int newWidth, int newHeight){ // if screen has changed, recalculate the rect of candy
        //This updates all of the candies' rects when the screen dimensions change
        boolean changed = false;

        int nWidth = newWidth, nHeight = newHeight;

        if(newWidth > newHeight) nWidth = newHeight;
        else nHeight = newWidth;

        if(screenWidth != nWidth || screenHeight != nHeight) changed = true;

        screenWidth = nWidth-screenBorder;
        screenHeight = nHeight-screenBorder;
        offX = (newWidth - screenWidth)/2;
        offY = (newHeight - screenWidth)/2;

        if(changed) {
            for (Candy candy : candyList) {
                setCandyXY(candy, candy.x, candy.y);
            }
        }
    }

    public void updateGameEnd(){
        if(score >= winningScore){
            gameEnd = 1;
        }else if(!hasRemainingMove()){
            gameEnd = -1;
        }
    }

    public void drawToCanvas(Canvas canvas){ // drawing candy to canvas. write down score.
        //This is called by the board view; canvas is also obtained from the board view.

        //Draw any border or background here. any kind of fanciness.
        //This might have to scale to screen or be centered in some way

        //Draw all of the candy pieces.
        for(Candy candy:candyList){
            candy.drawToCanvas(canvas);
        }

        //Draw any candy pops here
        for(CandyPop pop:candyPopList){
            pop.drawToCanvas(canvas);
        }


        //Draw the score
        float textSize = 75;
        String scoreText = "Score: " + (int)score;
        Paint textPaint = new Paint();
        Paint bgPaint = new Paint();
        bgPaint.setARGB(128, 0, 0, 0);
        textPaint.setARGB(255, 255, 255, 255);

        textPaint.setTextSize(textSize);
        float textWidth = textPaint.measureText(scoreText) + 10;

        canvas.drawRect(0, 0, (int)textWidth, (int)(textSize*0.9), bgPaint);

        canvas.drawText(scoreText, 10, (int)(10 + textSize * 0.65), textPaint);

        //Draw anything else here?
        if(gameEnd != 0){
            bgPaint.setARGB(160, 0, 0, 0);

            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), bgPaint);
            String gameText1 = "";
            String gameText2 = "";
            if(gameEnd >0){
                gameText1 = "You reached "+ (int)(winningScore) + " points!";
                gameText2 = "Congrats on your diabeetus!";
            }else if(gameEnd < 0){
                gameText1 = "No possible moves remaining :(";
            }

            canvas.drawText(gameText1, 0, canvas.getHeight()/2 - textSize/2, textPaint);
            canvas.drawText(gameText2, 0, canvas.getHeight()/2 + textSize/2, textPaint);
        }

    }
}
