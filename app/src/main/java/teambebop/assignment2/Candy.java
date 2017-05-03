package teambebop.assignment2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.content.Context;
import android.graphics.Canvas;


/**
 * Created by Byron on 4/23/2017.
 */

public class Candy {

    String color;
    Rect iconRect; // inside the rect drawing candy
    Rect touchRect; // the big square

    int type = -1;
    /*
    type is just an int that corresponds to the sprite. for color
    */

    int x,y;


    public static Bitmap icons[]; //global variable in the class
    public static int numTypes = 4;


    public Candy(){ //constructors
    }

    public Candy(int idType, Context _context){ //create new candy --- constructors
        type = idType;// coresspond to color
        loadSprites(_context); // call code
        initRects(); // create new rectangles

    }

    public Candy(String _color, Rect _rect1, Rect _rect2, int idType, Context _context){
        this.color = _color;
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
            icons = new Bitmap[4];
            icons[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.mipmap.bean_blue);
            icons[1] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.mipmap.bean_orange);
            icons[2] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.mipmap.bean_yellow);
            icons[3] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.mipmap.bean_purple);
        }
    }

    public void flush(){ // only called when deleted candy. empty for now

    }

    public void drawToCanvas(Canvas canvas){ //draw candy to canvas
        if(icons != null && iconRect != null)
            canvas.drawBitmap(icons[type], null, iconRect, null); // called from candy table
    }

    public void debugTap(){ //debugging   just tells where it touched.
        System.out.println("CANDY: (" + type + ") <"+x+", "+y+">");
        System.out.println(iconRect.flattenToString());
    }




}
