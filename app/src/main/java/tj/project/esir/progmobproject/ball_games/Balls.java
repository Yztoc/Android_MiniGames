package tj.project.esir.progmobproject.ball_games;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
    Bitmap bitmapBob;
    Bitmap goal;
    Point size = new Point();
    boolean isMoving = false;
    int nbCol = 0;
    int widthBall = 100;
    int heightBall = 100;
    int widthGoal= 300;
    int heightGoal = 300;
    float ballX;
    float ballY;
    float goalX;
    float goalY;
    float width;
    float height;
    double distance = 0;
    double angle = 0;
    boolean direction = false; // false = left rigth = true
    double m;
    double p;
    long fps;
    int col;
    int score = 0;
    boolean finish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ball);

        Display display = getWindowManager().getDefaultDisplay();

        display.getSize(size);
        width = size.x;
        height = size.y;
        ballX = size.x/2-widthBall/2;
        ballY = size.y-heightBall;

        goalX = width/2-widthGoal/2;
        goalY = 200;


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

        float walkSpeedPerSecond = 150;

        public GameView(Context context) {
            super(context);

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            // Load Bob from his .png file
            bitmapBob = BitmapFactory.decodeResource(this.getResources(), R.drawable.ball_blue);
            goal = BitmapFactory.decodeResource(this.getResources(), R.drawable.trou);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                    bitmapBob, widthBall, heightBall, false);
            Bitmap resizedGoal = Bitmap.createScaledBitmap(
                    goal, widthGoal, heightGoal, false);

            bitmapBob = resizedBitmap;
            goal = resizedGoal;
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
                    col = collisition(ballX,ballY);
                    win(ballX,ballY);
                    deplacement();
                }
                //bitmapBob = rotation(bitmapBob,1);
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
                paint.setColor(Color.argb(255,  249, 0, 0));

                // Make the text a bit bigger
                paint.setTextSize(45);

                // Display the current fps on the screen
                canvas.drawText("FPS:" + fps, 20, 40, paint);
                canvas.drawText("NB COL :" + nbCol, 200, 40, paint);
                canvas.drawText("SCORE  :" + score, 440, 40, paint);


                canvas.drawBitmap(bitmapBob, ballX, ballY, paint);
                canvas.drawBitmap(goal, goalX, goalY, paint);

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

    public void reset(){
        //ballX = width/2-widthBall/2;
        //ballY = height-heightBall;
        ballX = 100;
        ballY = 100;
    }

    public void win(float x,float y){
        if(isMoving){
            if((goalY+10 > y && y < goalY + heightGoal) && (goalX < x && x < goalX + widthGoal/2) ){
                reset();
                isMoving = false;
                score++;
            }
        }

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
            nbCol++;
            return 1;
        }else if(x<= 0){
            nbCol++;
            return 2;
        }else if(y <= 0) {
            nbCol++;
            return 3;
        }else if(y-heightBall*2 >= height){
            nbCol++;
            return 4;
        }
        else{
            return 0;
        }
    }

    public void deplacement(){

        switch (col) {
            case 0 :
                if (direction) {
                    ballX = ballX - (((float) distance / 4) / fps);
                    if(nbCol == 0){
                        ballY = height -  (((float) (((ballX) * m) + p)) - height);
                    }else{
                        ballY =  (height - (- (float) (-((ballX) * m) - p)));
                    }
                }
                else {
                    ballX = ballX + (((float) distance / 4) / fps);

                    if(nbCol == 0){
                        ballY = (float) (((ballX) * m) + p);
                    }else{
                        ballY =  height - ((float) (((ballX) * -m) + p) -  height);

                    }

                }
                break;

            case 1 :
                direction = true; // monte en haut a gauche
               // col = 0;
                ballX = ballX - 1;
                ballY = ballY + 1;
                //ballY = height -  (((float) (((ballX) * m) + p)) - height);
                break;
            case 2 :

                direction = false; // monte en haut a gauche
                // col = 0;
                ballX = ballX + 1;
                ballY = ballY + 1;
                break;
        }

    }

    public static Bitmap rotation(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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

                    // detect si le trace est du haut vers le bas
                    if(yStart < yEnd){
                        isMoving = true;
                        double h = yEnd - yStart;
                        double base = 0;

                        // calcul la base du triangle du trace
                        if(xStart < xEnd){ // slide d en haut a gauche vers en bas à droite
                            base = xEnd - xStart;
                            direction = true;
                        }else{
                            base = xStart - xEnd;// slide d en haut a droite vers en bas à gauche
                            direction = false;
                        }

                        // hypothenus du tringle du trace (ditance entre le debut et la fin du slide
                        distance = Math.sqrt(Math.pow(base,2) + Math.pow(h,2));
                        angle = Math.toDegrees(Math.atan(h/base));


                        // calcul la hauteur à laquelle la ball va toucher pour la premiere fois le mur
                        double hauteurFinal =  (width - ballX) * Math.tan((float)angle);

                        // y = mx + p
                        //calcul coeficient de la droite
                        m  = (hauteurFinal - ballY) / (width - ballX);

                        //calcul p
                        p = hauteurFinal - (width*m);

                        System.out.println("Hauteur : " + h);
                        System.out.println("Base : " + base);
                        System.out.println("Distance : " + distance);
                        System.out.println("Angle : " + angle);
                        System.out.println("hauteurFinal : " + hauteurFinal);
                        System.out.println("M : " + m);
                        System.out.println("P : " + p);



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