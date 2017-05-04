package teambebop.assignment2;


import android.graphics.Canvas;
import java.util.Random;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Rect;
/**
 * Created by d4rk3_000 on 4/23/2017.
 */

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


    public ArrayList<Candy> candyList = new ArrayList<Candy>();
    //This is a 1d arraylist of candy references, just to make stuff easier to iterate through.

    public ArrayList<ArrayList<Candy>> candyBoard = new ArrayList<ArrayList<Candy>>(); // list of array list. in columns.
    //The candyBoard is an arraylist of arraylists, kind of like a 2d array.
    //To read from this arraylist, do...
    // candyBoard.get(X).get(Y);


    double score = 0;

    public CandyTable(){  //constructor
        generateNewBoard();
    }

    public CandyTable(int x, int y, Context context){ // context from candy create cndy tables of any size

        sizeX = x;
        sizeY = y;
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

        //now build the new board's arraylists.
        for(int i = 0; i < sizeX; i++){ //# of columns
            //build the column list
            ArrayList<Candy> column = new ArrayList<Candy>(9);

            for(int j=0; j < sizeY; j++){ //# of rows
                //fill in the columns.
                Candy candy = generateNewCandy(i, j); //new object
                //do candy initialization shit here.
                //maybe add animations here too?
                //Maybe a board should have an initial animation of all of the columns falling.
                column.add(candy);
            }

            candyBoard.add(column); //adding columns to the candy board
        }
    }

    public void inputSwap(int x, int y, int newX, int newY){
        //This function attempts to swap the candy at x,y in the given direction
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
        if(x < 0 || x >= sizeX) return;
        if(y < 0 || y >= sizeY) return;
        if(newX < 0 || newX >= sizeX) return;
        if(newY < 0 || newY >= sizeY) return;

        System.out.println("Attempting swap");

        //swap the pieces here
        Candy initial = candyBoard.get(x).get(y); // initial position
        Candy swapped = candyBoard.get(newX).get(newY); // destination position
        candyBoard.get(x).set(y, swapped); //initi
        candyBoard.get(newX).set(newY, initial); // swapping the other

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


            //Find all candies in the rows to pop
            popCandies(x, y, combo);


            //swapping animation
            //popping animation

            //regenerate new candies


        }else{ //failed swap
            candyBoard.get(x).set(y, initial); //initi
            candyBoard.get(newX).set(newY, swapped); // swapping the other
            //do an animation for failed swap??
        }

    }

    public void popCandies(int x, int y, int combo){
        //This pops all candies in rows that include the given coordinates.
        /*
        This gets all of the candies in the row directions, and then
        awards points based on how many candies are found.
        Then it removes all of the candies found and shifts columns as necessary.
         */
        if(x < 0 || x >= sizeX) return;
        if(y < 0 || y >= sizeY) return;

        Candy initial = candyBoard.get(x).get(y); // it saves the initial
        ArrayList<Candy> candiesToPop = new ArrayList<Candy>();
        getCandiesInRow(x, y,  0,1,  initial, candyBoard, candiesToPop); // you have the board
        getCandiesInRow(x, y,  0,-1,  initial, candyBoard, candiesToPop);// checks position. saves candy if correct color and it continues the rest of the row or column
        getCandiesInRow(x, y,  1,0,  initial, candyBoard, candiesToPop);// calls it four times for each direction
        getCandiesInRow(x, y,  -1,0,  initial, candyBoard, candiesToPop);

        double points = candiesToPop.size() * candiesToPop.size() * combo * combo / 4.0d;
        //points awarded for popping candies is nonlinear.
        //This is to promote strategic play to set up big combos!
        addScore(points);

        for(Candy candy:candiesToPop){
            //kill the candy here.
            removeCandy(candy);

            //shift candies
            //shiftCandyColumn(candy.x, candy.y); // when candy falls

            //make new candy at top of column
            Candy newCandy = generateNewCandy(candy.x, sizeY-1);



            // Assign new candies here
            setCandyXY( newCandy,x , y); // (Candy candy, x,y );


            //shift candies again.
            shiftCandyColumn(candy.x, candy.y); // when candy falls

        }
    }

    public void shiftCandyColumn(int x, int y){
        //This is for shifting candies after a row has been cleared at position x,y.
        //This means x,y is now empty, and everything above y must now be moved downwards.
        ArrayList<Candy> column = candyBoard.get(x);
        for(int i = y; y < sizeY - 1; y++){
            Candy candy = column.get(i+1);
            candy.y = i;
            column.set(i, candy);
        }
        column.set(sizeY - 1, null); //for the time being, put a null into the topmost position.
    }

    public void removeCandy(Candy candy){
        candy.flush();
        candyList.remove(candy);
        candyBoard.get(candy.x).set(candy.y, null);
    }

    public void getCandiesInRow(int x, int y, int dx, int dy, Candy initial, //not finished
            ArrayList<ArrayList<Candy>> board, ArrayList<Candy> candies) {

        //This will add any found candies to the given candies list, since objects are passed by reference in java.
        if (x < 0 || x >= sizeX) return;
        if (y < 0 || y >= sizeY) return;

        Candy current = board.get(x).get(y);
        if (current.equals(initial)) {
            if(!candies.contains(current)) candies.add(current);
            return;
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
        if(current.type == initialCandy.type){
            return checkRowRecursive(x + dx, y + dy, dx, dy, depth+1, initialCandy, board);
        }else{
            return depth;
        }
    }

    public int computeRemainingMoves() { // copies table and test every single possible move. to end the game
        //This function computes and saves possible moves that yields points.
        //it returns the # of possible moves.
        //The move class just stores x and y, and a move direction.

        /*
        * ::ALGORITHM::
        * 1. Do a shallow clone of the table of candies.
        * 2. For each candy in the given list
        *   a) Perform swapping operations of that candy in the cloned table
        *   b) Check if any rows can be created from that swapping operation
        *   c) If so, then make note of the move performed and save it.
        *   d) undo the swapping operation and move onto the next candy.
        */

        ArrayList<ArrayList<Candy>> clone = cloneBoard();
        int[] rowLengths = new int[2];
        int movesLeft = 0;
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
                    if (rowLengths[0] > 0 || rowLengths[1] > 0) {
                        //successful move
                        movesLeft++;
                        //maybe save the move here...
                    }
                    boardSwapCandies(x, y, 0, 1, clone); //undo the swap
                }


                //test swapping south
                if(y > 0) {
                    boardSwapCandies(x, y, 0, -1, clone);
                    rowLengths = checkRow(x, y - 1, clone);
                    if (rowLengths[0] > 0 || rowLengths[1] > 0) {
                        //successful move
                        movesLeft++;
                        //maybe save the move here...
                    }
                    boardSwapCandies(x, y, 0, -1, clone); //undo the swap
                }


                //test swapping east
                if(x < sizeX-1) {
                    boardSwapCandies(x, y, 1, 0, clone);
                    rowLengths = checkRow(x + 1, y, clone);
                    if (rowLengths[0] > 0 || rowLengths[1] > 0) {
                        //successful move
                        movesLeft++;
                        //maybe save the move here...
                    }
                    boardSwapCandies(x, y, 1, 0, clone); //undo the swap
                }


                //test swapping west
                if(x > 0) {
                    boardSwapCandies(x, y, -1, 0, clone);
                    rowLengths = checkRow(x - 1, y, clone);
                    if (rowLengths[0] > 0 || rowLengths[1] > 0) {
                        //successful move
                        movesLeft++;
                        //maybe save the move here...
                    }
                    boardSwapCandies(x, y, -1, 0, clone); //undo the swap
                }
            }
        }
        return movesLeft;
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

    public void setScore(double newScore){
        score = newScore;
    }

    public int[] screenCoordsToGridCoords(double x, double y){ // not needed
        //This function converts screen percentage coordinates to grid index coordinates.
        //So the function input, x and y, should both ONLY range from 0.0 to 1.0

        int gridX = (int)(x * sizeX);
        int gridY = (int)(y * sizeY);
        int[] coords = new int[]{gridX, gridY};

        return coords;
    }

    public Candy generateNewCandy(int x, int y){// creates new candy.
        Random rand = new Random();
        int type = rand.nextInt(Candy.numTypes);

        Candy candy = new Candy(type, appContext);
        //Generate a random new candy, with array position x and y.
        setCandyXY(candy, x, y);
        candyList.add(candy);
        return candy;
    }

    public void setCandyXY(Candy candy, int x, int y){ //making smaller square in rectangle
        /*
        This function is for setting the x,y coordinates of the candy in the table.
        This also updates the candy's drawing position and touching position.
        It's necessary that candies have a separate drawing rect and touching rect, because
        the touch box might be of a different aspect ratio from the drawing box.
         */

        //create two new rects here!
        //one is for the sprites
        //the other is for touch
        int colWidth = screenWidth / sizeX;
        int rowHeight = screenHeight / sizeY;

        //first create touch rect
        Rect touchRect = new Rect();
        touchRect.set(x * colWidth + offX, y * rowHeight + offY,
                (x+1) * colWidth + offX, (y+1) * rowHeight + offY);

        //then create the draw rect as a square centered in the touch rect.
        int cX = touchRect.centerX();
        int cY = touchRect.centerY();
        int width = touchRect.width();
        int height = touchRect.height();

        int borderGap = 2;
        int drawWidth = width-borderGap;
        if(height < width) drawWidth = height-borderGap;
        int xOff = (width - drawWidth)/2;
        int yOff = (height - drawWidth)/2;

        Rect drawRect = new Rect();
        drawRect.set(cX - width/2 + xOff + offX, cY - height/2 + yOff + offY,
                cX - width/2 + xOff + offX + drawWidth, cY - width/2 + yOff + offY + drawWidth);

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

        if(screenWidth != newWidth || screenHeight != newHeight) changed = true;
        screenWidth = newWidth-screenBorder;
        screenHeight = newHeight-screenBorder;
        offX = screenBorder/2;
        offY = screenBorder/2;

        if(changed) {
            for (Candy candy : candyList) {
                setCandyXY(candy, candy.x, candy.y);
            }
        }
    }

    public void drawToCanvas(Canvas canvas){ // drawing  candy to canvas. write down score.
        //This is called by the board view; canvas is also obtained from the board view.

        //Draw any border or background here. any kind of fanciness.
        //This might have to scale to screen or be centered in some way

        //Draw all of the candy pieces.
        for(Candy candy:candyList){
            candy.drawToCanvas(canvas);
        }

        //Draw the score


        //Draw anything else here?


    }
}
