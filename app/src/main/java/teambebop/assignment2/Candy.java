/*
Author: Byron and Anthony
*/

package teambebop.assignment2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.content.Context;
import android.graphics.Canvas;


public class Candy {

    Rect iconRect; // inside the rect drawing candy
    Rect touchRect; // the big square

    int type = -1;
    /*
    type is just an int that corresponds to the sprite bitmap. The values represent the following
    1. blue
    2. red
    3. yellow
    4. purple
    5. orange
    */

    int x,y;

    Animation anim;


    public static Bitmap icons[]; //global variable in the class
    public static int numTypes = 5;


    public Candy(){ //constructors
    }

    public Candy(int idType, Context _context){ //create new candy --- constructors
        type = idType;// coresspond to color
        loadSprites(_context); // call code
        initRects(); // create new rectangles

    }

    public Candy(Rect _rect1, Rect _rect2, int idType, Context _context){
        this.iconRect = _rect1;
        this.touchRect = _rect2;

        type = idType;

        // assigning 4 icons for now
        loadSprites(_context);
        initRects();


    }

    public void initRects(){
        iconRect = new Rect();
        touchRect = new Rect();
    }

    public static void loadSprites(Context _context){
        //This function initializes the array of possible candy icons.
        if(icons == null) {
            icons = new Bitmap[5];
            icons[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.jelly_teal);
            icons[1] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.swirl_red);
            icons[2] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.bean_yellow);
            icons[3] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.mm_purple);
            icons[4] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.candycorn);
        }
    }

    public void flush(){ // only called when deleting candy
        if(anim != null){
            iconRect = anim.fin;
            anim.candy = null;
            anim = null;
        }
    }

    public void drawToCanvas(Canvas canvas){ //draw candy to canvas
        if(icons != null && iconRect != null)
            canvas.drawBitmap(icons[type], null, iconRect, null); // called from candy table
    }

    public void debugTap(){ //debugging   just tells where it touched.
        System.out.println("CANDY: (" + type + ") <"+x+", "+y+">");
        System.out.println(iconRect.flattenToString());
    }

    public void setAnimation(Animation newAnim){
        //This is a simple setter function for animations
        anim = newAnim;
        anim.candy = this;
    }

    public void newAnimation(Rect start, Rect finish, int numFrames){
        //This will create a new animation with the given parameters and set it for this candy.
        //If the candy already has an animation, it will get removed.
        new Animation(this, start, finish, numFrames);
    }
}
