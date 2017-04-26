package teambebop.assignment2;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;
/**
 * Created by d4rk3_000 on 4/23/2017.
 */

public class CandyTable {

    int sizeX = 9;
    int sizeY = 9;

    public ArrayList<Candy> candyList = new ArrayList<Candy>();
    //This is a 1d arraylist of candy references, just to make stuff easier to iterate through.

    public ArrayList<ArrayList<Candy>> candyBoard = new ArrayList<ArrayList<Candy>>();
    //The candyBoard is an arraylist of arraylists, kind of like a 2d array.
    //To read from this arraylist, do...
    // candyBoard.get(X).get(Y);


    double score = 0;

    public CandyTable(){
        generateNewBoard();
    }

    public CandyTable(int x, int y){
        sizeX = x;
        sizeY = y;
        generateNewBoard();
    }

    public void generateNewBoard(){
        //This fills the board with new candies
        //Cleanup any preexisting stuffs.
        for(ArrayList A:candyBoard){
            A.clear();
        }
        candyBoard.clear();

        for(Candy C:candyList){
            C.flush(); //cleanup any extra memory allocated to the candies.
        }

        candyList.clear();

        //now build the new board's arraylists.
        for(int i = 0; i < sizeX; i++){
            //build the column list
            ArrayList<Candy> column = new ArrayList<Candy>();

            for(int j=0; j < sizeY; j++){
                //fill in the columns.
                Candy candy = generateNewCandy(i, j);
                //do candy initialization shit here.
                //maybe add animations here too?
                //Maybe a board should have an initial animation of all of the columns falling.
                column.add(candy);
            }

            candyBoard.add(column);
        }
    }

    public void inputSwap(int x, int y, int dx, int dy){
        //This function attempts to swap the candy at x,y in the given direction
        /*
         * dx and dy are just delta variables that indicate the swapping direction
         *
         * ::ALGORITHM::
         * 1. Perform the swapping operation
         * 2. Check for any newly created rows.
         * 3. If there are newly created rows, then delete all candies in the row and award points
         * 4. Otherwise, undo the swapping operation.
         * 5. Apply any animations if necessary.
         */

        int newX = x + dx;
        int newY = y + dy;

        //bounds checking for swapping parameters
        if(x < 0 || x >= sizeX) return;
        if(y < 0 || y >= sizeY) return;
        if(newX < 0 || newX >= sizeX) return;
        if(newY < 0 || newY >= sizeY) return;

        //swap the pieces here
        Candy initial = candyBoard.get(x).get(y);
        Candy swapped = candyBoard.get(newX).get(newY);
        candyBoard.get(x).set(y, swapped);
        candyBoard.get(newX).set(newY, initial);

        int[] rowLengths = checkRow(newX, newY, candyBoard);
        if(rowLengths[0] >= 3 || rowLengths[1] >= 3){ //successful swap
            //successful swap.
            initial.x = newX;
            initial.y = newY;
            swapped.x = x;
            swapped.y = y;

            //Find all candies in the rows to pop
            popCandies(x, y);

            //swapping animation
            //popping animation

            //regenerate new candies


        }else{ //failed swap
            //do an animation for failed swap??
        }

    }

    public void popCandies(int x, int y, int combo){
        //This pops all candies in rows that include the given coordinates.
        if(x < 0 || x >= sizeX) return;
        if(y < 0 || y >= sizeY) return;

        Candy initial = candyBoard.get(x).get(y);
        ArrayList<Candy> candiesToPop = new ArrayList<Candy>();
        getCandiesInRow(x, y,  0,1,  initial, candyBoard, candiesToPop);
        getCandiesInRow(x, y,  0,-1,  initial, candyBoard, candiesToPop);
        getCandiesInRow(x, y,  1,0,  initial, candyBoard, candiesToPop);
        getCandiesInRow(x, y,  -1,0,  initial, candyBoard, candiesToPop);

        double points = candiesToPop.size() * candiesToPop.size() * combo * combo / 4.0d;
        //points awarded for popping candies is nonlinear.
        //This is to promote strategic play to set up big combos!
        addScore(points);

        for(Candy candy:candiesToPop){
            //kill the candy here.
            removeCandy(candy);

            //shift candies
            shiftCandyColumn(candy.x, candy.y);

            //make new candy at top of column
            Candy newCandy = generateNewCandy(candy.x, sizeY-1);
            //apply any animations here...

        }
    }

    public void shiftCandyColumn(int x, int y){
        //This is for shifting candies after a row has been cleared at position x,y.
        //This means x,y is now empty, and everything above y must now be moved downwards.
        ArrayList<Candy> column = candyBoard.get(x);
        for(int i = y; y < sizeY - 1; y++){
            Candy candy = column.get(i+1);
            Candy.y = i;
            column.set(i, candy);
        }
        column.set(sizeY - 1, null); //for the time being, put a null into the topmost position.
    }

    public void removeCandy(Candy candy){
        candyList.remove(candy);
        candyBoard.get(candy.x).set(candy.y, null);
    }

    public void getCandiesInRow(int x, int y, int dx, int dy, Candy initial,
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


        }
    }

    public int[] checkRow(int x, int y, ArrayList<ArrayList<Candy>> board){
        /*
         This function checks for any rows that include the given position at x,y.
         It checks for both vertical and horizontal rows.
         It's return format is: {vertical row length, horizontal row length}
         */
        int vertical = 0;
        int horizontal = 0;

        if(x > -1 && x < sizeX && y > -1 && y < sizeY) {
            Candy initial = board.get(x).get(y);
            //north
            vertical += checkRowRecursive(x, y, 0, 1, 1, initial, board);
            //south
            vertical += checkRowRecursive(x, y, 0, -1, 1, initial, board);

            //east
            horizontal += checkRowRecursive(x, y, 1, 0, 1, initial, board);
            //west
            horizontal += checkRowRecursive(x, y, -1, 0, 1, initial, board);
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
        if(current.equals(initialCandy)){
            return checkRowRecursive(x + dx, y + dy, dx, dy, depth+1, initialCandy, board);
        }else{
            return depth;
        }
    }

    public int computeRemainingMoves() {
        //This function computes and saves possible moves that yields points.
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
                boardSwapCandies(x, y,  0,1,  clone);
                rowLengths = checkRow(x, y, clone);
                if(rowLengths[0] > 0 || rowLengths[1] > 0){
                    //successful move
                    movesLeft ++;
                    //maybe save the move here...
                }
                boardSwapCandies(x, y,  0,1,  clone); //undo the swap


                //test swapping south
                boardSwapCandies(x, y,  0,-1,  clone);
                rowLengths = checkRow(x, y, clone);
                if(rowLengths[0] > 0 || rowLengths[1] > 0){
                    //successful move
                    movesLeft ++;
                    //maybe save the move here...
                }
                boardSwapCandies(x, y,  0,-1,  clone); //undo the swap


                //test swapping east
                boardSwapCandies(x, y,  1,0,  clone);
                rowLengths = checkRow(x, y, clone);
                if(rowLengths[0] > 0 || rowLengths[1] > 0){
                    //successful move
                    movesLeft ++;
                    //maybe save the move here...
                }
                boardSwapCandies(x, y,  1,0,  clone); //undo the swap


                //test swapping west
                boardSwapCandies(x, y,  -1,0,  clone);
                rowLengths = checkRow(x, y, clone);
                if(rowLengths[0] > 0 || rowLengths[1] > 0){
                    //successful move
                    movesLeft ++;
                    //maybe save the move here...
                }
                boardSwapCandies(x, y,  -1,0,  clone); //undo the swap
            }
        }
        return movesLeft;
    }

    public ArrayList<ArrayList<Candy>> cloneBoard(){
        //This function makes a shallow copy of the board. This is useful for checking potential moves.
        ArrayList<ArrayList<Candy>> clone = new ArrayList<ArrayList<Candy>>();
        for(ArrayList<Candy> column:candyBoard){
            clone.add((ArrayList<Candy>) column.clone());
        }
        return clone;
    }

    public void boardSwapCandies(int x, int y, int dx, int dy, ArrayList<ArrayList<Candy>> board ){
        //This function swaps two candies for the given board.
        //It does not process any row detection or any score processing.

        int newX = x + dx;
        int newY = y + dy;

        Candy initial = board.get(x).get(y);
        Candy swapped = board.get(newX).get(newY);
        board.get(x).set(y, swapped);
        board.get(newX).set(newY, initial);
    }

    public void addScore(double amount){
        //This is just for incrementing the score.
        score += amount;
    }

    public int[] screenCoordsToGridCoords(double x, double y){
        //This function converts screen percentage coordinates to grid index coordinates.
        //So the function input, x and y, should both ONLY range from 0.0 to 1.0

        int gridX = (int)(x * sizeX);
        int gridY = (int)(y * sizeY);
        int[] coords = new int[]{gridX, gridY};
        return coords;
    }

    public Candy generateNewCandy(int x, int y){
        Candy candy = new Candy();
        //Generate a random new candy, with array position x and y.
        candy.x = x;
        candy.y = y;
        candyBoard.get(x).set(y, candy);
        candyList.add(candy);
        return candy;
    }

    public void drawToCanvas(Canvas canvas){
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
