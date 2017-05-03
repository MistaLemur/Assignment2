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
    Rect iconRect;
    Rect touchRect;

    int type = -1;
    /*
    type is just an int that corresponds to the sprite.
    */

    int x,y;

    Bitmap icon;

    public static Bitmap icons[];
    public static int numTypes = 4;


    public Candy(){
    }

    public Candy(int idType, Context _context){
        type = idType;
        loadSprites(_context);
        initRects();
        icon = icons[type];
    }

    public Candy(String _color, Rect _rect1, Rect _rect2, int idType, Context _context){
        this.color = _color;
        this.iconRect = _rect1;
        this.touchRect = _rect2;

        type = idType;

        // assigning 4 icons for now
        loadSprites(_context);
        initRects();

        icon = icons[type];
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

    public void flush(){

    }

    public void drawToCanvas(Canvas canvas){
        if(icon != null && iconRect != null)
            canvas.drawBitmap(icons[type], null, iconRect, null);
    }

    public void debugTap(){
        System.out.println("CANDY: (" + type + ") <"+x+", "+y+">");
        System.out.println(iconRect.flattenToString());
    }




}
