/*
Author: Anthony SuVasquez

This file contains the entry-point of this application.
*/

package teambebop.assignment2;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;

public class MainActivity extends Activity {

    BoardView bView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bView = new BoardView(this);
        setContentView(bView);

    }
}
