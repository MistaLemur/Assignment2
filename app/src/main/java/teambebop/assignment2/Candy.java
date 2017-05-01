package teambebop.assignment2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.content.Context;


/**
 * Created by Byron on 4/23/2017.
 */

public class Candy {

    String color;
    Rect rect;
    Rect rect1;
    Bitmap icons[];


    public Candy(String _color, Rect _rect1, Rect _rect2, Bitmap _icon, Context _context){
        this.color = _color;
        this.rect = _rect1;
        this.rect1 = _rect2;

        // assigning 4 icons for now
        icons[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(),R.mipmap.bean_blue);
        icons[1] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(),R.mipmap.bean_orange);
        icons[2] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(),R.mipmap.bean_yellow);
        icons[3] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(),R.mipmap.bean_purple);


    }



    // getting touched



}
