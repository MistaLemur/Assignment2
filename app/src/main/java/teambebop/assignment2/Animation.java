package teambebop.assignment2;


import android.graphics.Rect;
/**
 * Created by Admin on 5/4/2017.
 */

public class Animation {
    Candy candy;

    //Initial and final keyframes for this animation
    Rect ini;
    Rect fin;

    int frameCount = 0;
    int numFrames = 0;

    //Animation types!
    int animType = 0;
    //0 for linear interpolation.
    //1 for quadratic failed swapping (forward and then back)


    public Animation(Candy newCandy, Rect start, Rect finish, int frames){
        candy = newCandy;

        if(candy.anim != null) {
            candy.anim.numFrames += frames/2;
            candy.anim.fin = finish;
            return;
        }

        candy.anim = this;
        ini = start;
        fin = finish;
        numFrames = frames;
    }

    public void nextFrame(){
        candy.iconRect = getFrame(++frameCount);
    }

    public void flush(){
        if(candy != null) {

            if(fin!= null) {
                candy.iconRect = fin;
                if(animType == 1) candy.iconRect = ini;
            }

            candy.anim = null;
            candy = null;
        }
    }

    public Rect getFrame(int frameNum){
        //This will get the tweened rectangle that corresponds to the current frame
        double u = tween((frameNum+1.0d)/numFrames);
        double v = 1 - u;

        Rect newRect = new Rect();
        newRect.bottom = (int) (ini.bottom * v + fin.bottom * u);
        newRect.top =    (int) (ini.top * v + fin.top * u);
        newRect.left =   (int) (ini.left * v + fin.left * u);
        newRect.right =  (int) (ini.right * v + fin.right * u);

        //System.out.println("animating " + newRect.flattenToString());

        return newRect;
    }

    public double lerp(double time){
        //linear interpolation for tweening.
        return time;
    }

    public double quadFailedSwap(double time){
        //This is the animation for a failed swap.
        //It's a quadratic easing curve centered about 0.5.
        return 1 - (time - 0.5) * (time - 0.5);
    }

    public double tween(double time){
        //This function simply switch()'s the animation type with the tweening function.
        switch(animType){
            case 1: return quadFailedSwap(time);
            default: return lerp(time);
        }
    }
}
