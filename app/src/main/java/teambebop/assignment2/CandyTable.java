package teambebop.assignment2;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import java.util.ArrayList;
/**
 * Created by d4rk3_000 on 4/23/2017.
 */

public class CandyTable {

    int sizeX = 9;
    int sizeY = 9;

    double score = 0;

    public CandyTable(){
        generateNewBoard();
    }

    public CandyTable(int x, int y){
        sizeX = x;
        sizeY = y;
        generateNewBoard();
    }

    public void inputSwap(int x, int y, int direction){
        //This function attempts to swap the candy at x,y in the given direction
        /*
         * the direction int is a simple bit array:
         * 1 - Represents north
         * 2 - Represents south
         * 4 - Represents east
         * 8 - represents west
         *
         *
         * ::ALGORITHM::
         * 1. Perform the swapping operation
         * 2. Check for any newly created rows.
         * 3. If there are newly created rows, then delete all candies in the row and award points
         * 4. Otherwise, undo the swapping operation.
         */
    }

    public int checkRow(Candy candy){
        //This function searches for any rows, horizontal or vertical, that the given object is a part of.
        if(candy == null) return 0;
        /*
         * ::ALGORITHM::
         * 1. For each of the four directions in NORTH, SOUTH, EAST, WEST
         *  a) Take 1 step in that direction in the table of candies.
         *  b) If the candy in the current position is of the same type as the original candy
         *      Then increment the row counter, and continue stepping in the same direction.
         *  c) IF the candy in the current position is a different type
         *      Then stop checking in this direction, and move onto the next direction
         */

    }

    public void computeRemainingMoves(ArrayList<Candy> candies){
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
    }

    public void addScore(double amount){
        score += amount;
    }

    public Candy generateNewCandy(int x, int y){
        //Generate a random new candy, with array position x and y.
    }

    public void drawToCanvas(Canvas canvas){
        //This is called by the board view; canvas is also obtained from the board view.

        //Draw any border or background here. any kind of fanciness.
        //This might have to scale to screen or be centered in some way

        //Draw all of the candy pieces.

        //Draw the score

        //Draw anything else here?
    }

    public void generateNewBoard(){
        //This fills the board with new candies
    }
}
