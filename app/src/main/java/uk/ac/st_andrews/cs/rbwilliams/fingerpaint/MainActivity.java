package uk.ac.st_andrews.cs.rbwilliams.fingerpaint;

import android.graphics.Color;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.Random;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;

import static uk.ac.st_andrews.cs.rbwilliams.fingerpaint.R.drawable.circle;
import static uk.ac.st_andrews.cs.rbwilliams.fingerpaint.R.drawable.paint;
import static uk.ac.st_andrews.cs.rbwilliams.fingerpaint.R.drawable.paint_pressed;

public class MainActivity extends AppCompatActivity implements OnClickListener,
                                                    GestureDetector.OnGestureListener,
                                                    GestureDetector.OnDoubleTapListener {

    private DrawingView drawView;
    private ImageButton currPaint;
    private float smallBrush;
    private float mediumBrush;
    private float largeBrush;

    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;

    private Matrix matrix = new Matrix();
    private float scale = 1f;
    private ScaleGestureDetector SGD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        drawView = (DrawingView)findViewById(R.id.drawing);

        // First paint color button
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);

        currPaint.setImageDrawable(ContextCompat.getDrawable(this, paint_pressed));

        // Code for setting brush size
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        ImageButton drawBtn = (ImageButton) findViewById(R.id.draw_btn);

        drawBtn.setOnClickListener(this);

        drawView.setBrushSize(mediumBrush);

        ImageButton eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        ImageButton newBtn = (ImageButton) findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        ImageButton saveBtn = (ImageButton) findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        ImageButton shapesBtn = (ImageButton) findViewById(R.id.shapes_btn);
        shapesBtn.setOnClickListener(this);

        ImageButton gestureBtn = (ImageButton) findViewById(R.id.gesture_btn);
        gestureBtn.setOnClickListener(this);


        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setOnDoubleTapListener((GestureDetector.OnDoubleTapListener) this);

        SGD = new ScaleGestureDetector(this,new ScaleListener());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void paintClicked(View view){
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());
        // use selected color
        if(view!=currPaint){
            //update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);

            imgView.setImageDrawable(ContextCompat.getDrawable(this, paint_pressed));
            currPaint.setImageDrawable(ContextCompat.getDrawable(this, paint));
            currPaint=(ImageButton)view;
        }
    }

    @Override
    public void onClick(View view){
        //respond to clicks
        if(view.getId()==R.id.draw_btn){

            //draw button clicked
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            // Listen for click on small brush
            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    drawView.setErase(false);
                    drawView.setShape("false");
                    drawView.setGesture("false");
                    drawView.setFreeHand(true);
                    brushDialog.dismiss();
                }
            });

            // Listen for click on medium brush
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    drawView.setErase(false);
                    drawView.setShape("false");
                    drawView.setGesture("false");
                    drawView.setFreeHand(true);
                    brushDialog.dismiss();
                }
            });

            // Listen for click on large brush
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    drawView.setErase(false);
                    drawView.setShape("false");
                    drawView.setGesture("false");
                    drawView.setFreeHand(true);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        } else if(view.getId()==R.id.erase_btn) {
            //switch to erase - choose size
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setShape("false");
                    drawView.setFreeHand(false);
                    drawView.setGesture("false");
                    drawView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setShape("false");
                    drawView.setFreeHand(false);
                    drawView.setGesture("false");
                    drawView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setShape("false");
                    drawView.setFreeHand(false);
                    drawView.setGesture("false");
                    drawView.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        } else if(view.getId() == R.id.new_btn){
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        } else if(view.getId()==R.id.save_btn){
            //save drawing
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    //save drawing
                    drawView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), drawView.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", "drawing");
                    if(imgSaved!=null){
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else{
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    drawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        } else if (view.getId() == R.id.shapes_btn){
            final Dialog shapesDialog = new Dialog(this);
            shapesDialog.setTitle("Choose a shape to draw:");
            shapesDialog.setContentView(R.layout.shapes_chooser);

            // Listen for click on small brush
            ImageButton smallBtn = (ImageButton)shapesDialog.findViewById(R.id.circle);
            smallBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setShape("circle");
                    drawView.setErase(false);
                    drawView.setFreeHand(false);
                    drawView.setGesture("false");
                    shapesDialog.dismiss();
                }
            });

            // Listen for click on medium brush
            ImageButton mediumBtn = (ImageButton)shapesDialog.findViewById(R.id.square);
            mediumBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setShape("square");
                    drawView.setErase(false);
                    drawView.setFreeHand(false);
                    drawView.setGesture("false");
                    shapesDialog.dismiss();
                }
            });

            // Listen for click on large brush
            ImageButton largeBtn = (ImageButton)shapesDialog.findViewById(R.id.triangle);
            largeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setShape("triangle");
                    drawView.setErase(false);
                    drawView.setFreeHand(false);
                    drawView.setGesture("false");
                    shapesDialog.dismiss();
                }
            });

            shapesDialog.show();
        } else if (view.getId() == R.id.gesture_btn) {
            final Dialog gestureDialog = new Dialog(this);
            gestureDialog.setTitle("Choose a gesture:");
            gestureDialog.setContentView(R.layout.gesture_chooser);

            // Listen for click on small brush
            ImageButton gesture = (ImageButton) gestureDialog.findViewById(R.id.gesture);
            gesture.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setShape("false");
                    drawView.setErase(false);
                    drawView.setFreeHand(false);
                    drawView.setGesture("gesture");
                    gestureDialog.dismiss();
                }
            });

            ImageButton pinch = (ImageButton) gestureDialog.findViewById(R.id.pinch);
            pinch.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setShape("false");
                    drawView.setErase(false);
                    drawView.setFreeHand(false);
                    drawView.setGesture("pinch");
                    gestureDialog.dismiss();
                }
            });

            ImageButton multiGesture = (ImageButton) gestureDialog.findViewById(R.id.multiGesture);
            multiGesture.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setShape("false");
                    drawView.setErase(false);
                    drawView.setFreeHand(false);
                    drawView.setGesture("multiGesture");
                    gestureDialog.dismiss();
                }
            });

            ImageButton multiTouch = (ImageButton) gestureDialog.findViewById(R.id.multiTouch);
            multiTouch.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setShape("false");
                    drawView.setErase(false);
                    drawView.setFreeHand(false);
                    drawView.setGesture("multiTouch");
                    gestureDialog.dismiss();
                }
            });
            gestureDialog.show();
        }

    }




    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (drawView.gesture){
            this.mDetector.onTouchEvent(event);
        } else if (drawView.pinch){
            SGD.onTouchEvent(event);
        } else if (drawView.multiGesture){
            //drawView.setRotation(rotation(event));
        }


        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    private class ScaleListener extends ScaleGestureDetector.

            SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(0.1f, Math.min(scale, 5.0f));


            drawView.setScaleX(scale);
            drawView.setScaleY(scale);
            return true;
        }
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(DEBUG_TAG,"onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
        drawView.setBackgroundColor(Color.WHITE);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(DEBUG_TAG, "onScroll: " + e1.toString() + e2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        Random rand = new Random();
        int  r = rand.nextInt(255) + 1;
        int  g = rand.nextInt(255) + 1;
        int  b = rand.nextInt(255) + 1;
        drawView.setBackgroundColor(Color.rgb(r,g,b));
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }

}
