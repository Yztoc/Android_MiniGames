package tj.project.esir.progmobproject.ball_games;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import tj.project.esir.progmobproject.MainActivity;
import tj.project.esir.progmobproject.R;

public class Balls extends AppCompatActivity {

    GameView gameView;

    boolean isMoving = false;
    int nbCol = 0;
    int widthBall = 100;
    int heightBall = 100;
    float ballX;
    float ballY;
    float width;
    float height;
    double distance = 0;
    double angle = 0;
    boolean direction = false; // false = left rigth = true
    double m;
    double p;
    long fps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ball);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        ballX = size.x/2-widthBall;
        ballY = size.y-heightBall;
        hideSystemUI();

        // Initialize gameView and set it as the view
        gameView = new GameView(this);
        setContentView(gameView);
        gameView.setOnTouchListener(handleTouch);
    }


    class GameView extends SurfaceView implements Runnable {

        Thread gameThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playing;
        Canvas canvas;
        Paint paint;
        private long timeThisFrame;

        // Declare an object of type Bitmap
        Bitmap bitmapBob;
        float walkSpeedPerSecond = 150;

        public GameView(Context context) {
            super(context);

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            // Load Bob from his .png file
            bitmapBob = BitmapFactory.decodeResource(this.getResources(), R.drawable.ball_blue);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                    bitmapBob, widthBall, heightBall, false);
            bitmapBob = resizedBitmap;

            // Set our boolean to true - game on!
            playing = true;

        }

        @Override
        public void run() {
            while (playing) {

                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();
                update();
                draw();
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        public void update() {
            if(isMoving){

                for(int i=0;i<10;i++){

                    deplacement(collisition(ballX,ballY));

                }
            }
        }

        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                canvas.drawColor(Color.argb(255,  26, 128, 182));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255,  249, 129, 0));

                // Make the text a bit bigger
                paint.setTextSize(45);

                // Display the current fps on the screen
                canvas.drawText("FPS:" + fps, 20, 40, paint);


                canvas.drawBitmap(bitmapBob, ballX, ballY, paint);

                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }


    }
    @Override
    protected void onResume() {
        super.onResume();

        gameView.resume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    /* detecte si la balle touche un mur
    - col = 1 ---> colision droite
    - col = 2 ---> colision gauche
    - col = 3 ---> colision haut
    - col = 4 ---> colision bas

    - col = 0 ---> aucune droite
    */
    public int collisition(float x, float y){
        if(x + widthBall>=width){
            System.out.println("COLISION DROITE");
            nbCol++;
            return 1;
        }else if(x-widthBall<= 0){
            System.out.println("COLISION GAUCHE");
            nbCol++;
            return 2;
        }else if(y <= 0) {
            System.out.println("COLISION HAUT");
            nbCol++;
            return 3;
        }else if(y-heightBall >= height){
            System.out.println("COLISION BAS");
            nbCol++;
            return 4;
        }
        else{
            return 0;
        }
    }

    public void deplacement(int col){
        if(col == 0) {
            if (direction) {
                System.out.println("BALL X AFTER: " + ballX);
                ballX = ballX - (((float) distance / 4) / fps);
                System.out.println("BALL X AFTER: " + ballX);
                m = -m;
            } else {
                ballX = ballX + (((float) distance / 4) / fps);
            }
            ballY = (float) (((ballX) * m) + p);
        }else{

        }
    }

    // class defini dans le package permet de calculer la distance entre le point de depart et le point d arrivé
    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        int xStart = 0;
        int yStart = 0;
        int xEnd = 0;
        int yEnd = 0;


        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    xStart = (int) event.getX();
                    yStart = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i("TAG", "moving: (" + xStart + ", " + yStart + ")");
                    break;
                case MotionEvent.ACTION_UP:
                    xEnd= (int) event.getX();
                    yEnd = (int) event.getY();

                    if(yStart < yEnd){
                        isMoving = true;
                        double h = yEnd - yStart;
                        double base = 0;

                        if(xStart < xEnd){
                            base = xEnd - xStart;
                            direction = true;
                        }else{
                            base = xStart - xEnd;
                            direction = false;
                        }

                        // hypothenus du tringle (ditance entre le debut et la fin du slide
                        distance = Math.sqrt(Math.pow(base,2) + Math.pow(h,2));
                        angle = Math.toDegrees(Math.atan(h/base));

                        System.out.println("Hauteur : " + h);
                        System.out.println("Base : " + base);
                        System.out.println("Distance : " + distance);
                        System.out.println("Angle : " + angle);


                        // calcul la hauteur à laquelle la ball va toucher pour la premiere fois le mur
                        double yEnd =  (width - ballX) * Math.tan((float)angle);

                        // y = mx + p
                        //calcul coeficient de la droite
                        m  = (yEnd - ballY) / (width - ballX);

                        //calcul p
                        p = yEnd - (width*m);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                isMoving = false;
                            }
                        }, 5000);

                    }else{
                        System.out.println("NOT good");
                    }

                    break;
            }

            return true;
        }
    };







    public void onBackPressed() {
        Intent home = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(home);
        finish();
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


}