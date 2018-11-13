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

import java.util.ArrayList;
import java.util.Random;

import tj.project.esir.progmobproject.MainActivity;
import tj.project.esir.progmobproject.R;

public class Balls extends AppCompatActivity {

    GameView gameView;
    Bitmap ball;
    Bitmap goal;
    Bitmap block;
    Point size = new Point();
    ArrayList<Block> tabBlock = new ArrayList<Block>();
    boolean isMoving = false;
    int nbCol = 0;
    int loose = 0;
    int widthBall = 100;
    int heightBall = 100;
    int widthGoal= 150;
    int heightGoal = 150;
    float ballX;
    float ballY;
    float goalX;
    float goalY;
    float width;
    float height;
    double distance = 0;
    double angle = 0;
    int direction;
    int directionTamp;
    boolean descente = false;
    double m;
    double p;
    long fps;
    int col;
    int score = 0;
    boolean finish = false;
    int level = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            level = (int) b.get("level");
        }

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

        generateMap();
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
            ball = BitmapFactory.decodeResource(this.getResources(), R.drawable.ball_blue);
            goal = BitmapFactory.decodeResource(this.getResources(), R.drawable.trou);
            block = BitmapFactory.decodeResource(this.getResources(), R.drawable.block);

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                    ball, widthBall, heightBall, false);
            Bitmap resizedGoal = Bitmap.createScaledBitmap(
                    goal, widthGoal, heightGoal, false);
            Bitmap resizedBlock = Bitmap.createScaledBitmap(
                    block, Block.width, Block.height, false);

            ball = resizedBitmap;
            goal = resizedGoal;
            block = resizedBlock;
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
                   // win(ballX,ballY);
                    deplacement();
                }
                //ball = rotation(ball,1);
            }else{
                reset();
            }
        }

        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                canvas.drawColor(Color.argb(255,  0, 0, 0));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255,  249, 0, 0));

                // Make the text a bit bigger
                paint.setTextSize(45);

                // Display the current fps on the screen
                canvas.drawText("FPS:" + fps, 20, 40, paint);
                canvas.drawText("BALL X : " + ballX + " BALL Y : " + ballY, 20, 100, paint);
                canvas.drawText("NB COL :" + nbCol, 200, 40, paint);
                canvas.drawText("SCORE  :" + score, 440, 40, paint);
                canvas.drawText("LOOSE  :" + loose, 680, 40, paint);


                canvas.drawBitmap(ball, ballX, ballY, paint);
                canvas.drawBitmap(goal, goalX, goalY, paint);

                for (Block element : tabBlock) {
                    canvas.drawBitmap(block, element.getX(), element.getY(), paint);
                }

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
        ballX = width/2-widthBall/2;
        ballY = height-heightBall;
        nbCol = 0;
        col = 0;
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
        /*
        for (Block element : tabBlock) {
            if(x + widthBall >= element.getX() && y > (element.getY() - heightBall) && y < (element.getY() + heightBall)){ // si la balle tape le coté gauche d'un block
                nbCol++;
                return 1;
            }else if(x<= element.getX()+element.getWidth() && y > (element.getY() - heightBall) && y < (element.getY() + heightBall) ){ // si la balle tape le coté droit d'un block
                nbCol++;
                return 2;
            }else if(y <= element.getY()+element.getHeight() && x>=(element.getX() - widthBall) && x<=(element.getX() + widthBall)) {
                nbCol++;
                return 3;
            }else if(y-heightBall*2 >= element.getY()-element.getHeight()){

                //return 4;
            }
            else{
                return 0;
            }
        }*/

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
            //loose++;
            //isMoving = false;
            return 4;
        }
        else{
            return 0;
        }
    }


    /*
     * direction == 1 ---> monté à droite
     * direction == 2 ---> monté à gauche
     * direction == 3 ---> descente à gauche
     * direction == 4 ---> descente à droite
     *
     * */
    public void deplacement(){

        float equationY = (float) (((ballX) * m) + p);
        float equationX = ballX + (((float) distance / 4) / fps);

        switch (col) {
            case 0 :
               // ballX = equationX;
               // ballY = equationY;

                if (direction == 1) { // MOINTÉ A DROITE &&  REBOND VERS DROITE
                    descente = false;
                    directionTamp = 1;
                    ballX = ballX + (((float) distance / 4) / fps);
                    if(nbCol == 0){ // Premier vers la droite
                        ballY = (float) (((ballX) * m) + p);
                    }else{ // rebond vers la droite
                        ballY =  height - ((float) (((ballX) * -m) + p) -  height);
                    }
                }else if(direction == 2){ // MOINTÉ A GAUCHE &&  REBOND VERS GAUCHE
                    descente = false;
                    directionTamp = 2;
                    ballX = ballX - (((float) distance / 4) / fps);
                    if(nbCol == 0){ // Premier vers la gauche
                        ballY = height -  (((float) (((ballX) * m) + p)) - height);
                    }else{ // rebond vers la gauche
                        /*************A CORRIGER  *************/
                        ballY = height + ((float) (((ballX) * -m) - (p)));
                    }
                }else if(direction == 3) {
                    // calcul la direction de descente (droite ou gauche);
                    if(directionTamp == 1){// descente en bas vers la gauche
                        ballX = ballX + (((float) distance / 4) / fps);
                        if(descente){
                            /*************A CORRIGER  *************/
                            ballY = -height - ((float) (((ballX) * m) - p));
                        }else{
                            ballY = -height - ((float) (((ballX) * m) - p));
                        }
                    }else if(directionTamp == 2){//descente en bas vers la droite
                        ballX = ballX - (((float) distance / 4) / fps);
                        if(descente){
                            ballY = -(height + ((float) (((ballX) * -m) - p)));
                        }else{
                            ballY = -(height + ((float) (((ballX) * -m) - p)));
                        }
                    }
                    descente = true;
                }else if(direction == 4){ // rebond sol (bas de plateforme

                    if(directionTamp == 1){// rebond vers la droite
                        ballX = ballX + (((float) distance / 4) / fps);

                        ballY =  height - ((float) (((ballX) * -m) + p) -  height);
                    }else if(directionTamp == 2){
                        ballY = height -  (((float) (((ballX) * m) + p)) - height);
                    }
                }
                break;

            case 1 :
                if(descente){
                    direction = 3; //descente
                    directionTamp = 2; // vers la gauche
                    ballX = ballX -1;
                    ballY = ballY + 1;
                }else{
                    direction = 2; // monte en haut a gauche
                    directionTamp = 2; // vers la gauche

                    ballX = ballX - 1;
                    ballY = ballY - 1;
                }

                break;
            case 2 :
                if(descente){ // COLISION A GAUCHE DE PUIS UNE DESCNETE A DROITE ---> RESULTAT DESCENTE A DROITE
                    direction = 3; //descente
                    directionTamp = 1; // vers la droite
                    ballX = ballX + 1;
                    ballY = ballY + 1;

                }else{
                    direction = 1; // monte en haut a droite
                    directionTamp = 1; // vers la droite

                    ballX = ballX + 1;
                    ballY = ballY - 1;
                }

                break;
            case 3:
                if(col==0){
                    descente = true;
                    if(direction == 1){
                        direction = 3; //descente
                        directionTamp = 1; // vers la droite
                        ballX = ballX + 1;
                        ballY = ballY + 1;

                    }else{
                        direction = 3; //descente
                        directionTamp = 2; // vers la gauche
                        ballX = ballX - 1;
                        ballY = ballY + 1;
                    }
                }else{
                    direction = 3; // descente
                    if(directionTamp == 1){
                        ballX = ballX + 10;
                        ballY = ballY + 10;

                    }
                    else if(directionTamp == 2)
                    {
                        ballY = ballY + 10;
                        ballX = ballX - 10;
                    }

                }
                break;
            case 4:
                direction = 4; // bas
                if(directionTamp == 1){
                    ballY = ballY + 10;
                }
                else if(directionTamp == 2) {
                    ballX = ballX - 10;
                }
                ballY = ballY - 1;
                break;
        }

    }

    public void generateMap(){
        int y = (int) goalY+heightGoal+20;
        int x = 100;
        Random rand1 = new Random();
        int startRange = 0, endRange = (int)width-(Block.width*2);
        int nbBlock = ((int) ((4) * rand1.nextDouble())) + 4;
        for(int i=1;i<=level*2;i++){
            for(int j=1;j<nbBlock;j++){
                x+=Block.width;
                tabBlock.add(new Block(x,y*i));
            }

            int offsetValue =  endRange - startRange + 1;
            int  baseValue = (int)  (offsetValue * rand1.nextDouble());
            int r =  baseValue + startRange;
            System.out.println("random : " + r);
            nbBlock = ((int) ((4) * rand1.nextDouble())) + 4;
            x=r;
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
                            direction = 2;
                        }else{
                            base = xStart - xEnd;// slide d en haut a droite vers en bas à gauche
                            direction = 1;
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
                        System.out.println("Geste dans le mauvais sens");
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