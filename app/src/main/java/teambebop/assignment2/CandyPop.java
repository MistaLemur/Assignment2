package teambebop.assignment2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by Admin on 5/5/2017.
 */

public class CandyPop {
    //A candypop is just the effect that remains on the screen after you pop a candy. It's very similar to the candy and animation class


    int type = -1;
    /*
    type is just an int that corresponds to the sprite bitmap. The values represent the following
    0. blue
    1. red
    2. yellow
    3. purple
    4. "orange"
    */

    Rect iconRect; // inside the rect drawing candy

    public static Bitmap allFrames[][]; //global variable in the class. this stores all of the frames of all possible animations for this.
    public static int numFrames = 5;
    public static int numTypes = 4;

    int frameCount = 0; //Thsi is # of frames that this has been running. NOT # OF PICTURE FRAMES IN THE ANIMATION.
    int animLength = 10;

    public Bitmap frames[]; //This is simply the specific frames for the color associated with this.

    public CandyPop(Candy candy, int framesLength, Context _context){

        loadSprites(_context);

        type = candy.type;
        if(type == 4) type = 3; //these share the same explosion animation.
        animLength = framesLength;
        iconRect = candy.iconRect;
        frames = allFrames[type];
    }

    public static void loadSprites(Context _context) {
        if(allFrames != null) return;

        allFrames = new Bitmap[numTypes][numFrames];
        //blue
        allFrames[0][0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosionblue01);
        allFrames[0][1] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosionblue02);
        allFrames[0][2] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosionblue03);
        allFrames[0][3] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosionblue04);
        allFrames[0][4] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosionblue05);

        //red
        allFrames[1][0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosionred01);
        allFrames[1][1] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosionred02);
        allFrames[1][2] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosionred03);
        allFrames[1][3] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosionred04);
        allFrames[1][4] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosionred05);

        //yellow
        allFrames[2][0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosiongreen01);
        allFrames[2][1] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosiongreen02);
        allFrames[2][2] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosiongreen03);
        allFrames[2][3] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosiongreen04);
        allFrames[2][4] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosiongreen05);

        //purple
        allFrames[3][0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosionpink01);
        allFrames[3][1] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosionpink02);
        allFrames[3][2] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosionpink03);
        allFrames[3][3] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosionpink04);
        allFrames[3][4] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.explosionpink05);
    }

    public void drawToCanvas(Canvas canvas){ //draw candy to canvas
        int frameNum = getFrame();
        if(frameNum >= numFrames) return;

        if(allFrames != null && iconRect != null)
            canvas.drawBitmap(allFrames[type][frameNum], null, iconRect, null); // called from candy table
    }

    public int getFrame(){
        return frameCount * numFrames / (animLength+1); //the +1 at the end is just for avoiding out of bounds error
    }

    public void nextFrame(){
        ++frameCount;
    }

}
