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
    Bitmap bgrd;
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
    double m;
    double p;
    long fps;
    int col;
    int score = 0;
    boolean finish = false;
    int level = 0;

    //test
    Block b = new Block(200,1000);


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

            Bitmap background = BitmapFactory.decodeResource(this.getResources(), R.drawable.back_ball_2);
             bgrd = Bitmap.createScaledBitmap(
                        background, (int)width, (int)height, false);

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


                canvas.drawBitmap(bgrd, 0, 0, paint);

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
    public int collisition(float x, float y) {


        int res = 0;
        for (int i=0;i<tabBlock.size();i++) {

            if(res !=0){
                return res;
            }

           if(x + widthBall>=width){
                nbCol++;
                res = 1;
            }else if(x<= 0){
                nbCol++;
                res = 2;
            }else if(y <= 0) {
                nbCol++;
               res = 3;
            }else if(y-heightBall*2 >= height){
                loose++;
                isMoving = false;
               res = 5;
            } else if((x >= tabBlock.get(i).getX() - widthBall && x<= tabBlock.get(i).getX()) && (y > (tabBlock.get(i).getY() - heightBall) && y < (tabBlock.get(i).getY() + tabBlock.get(i).getHeight() + heightBall))){ // si la balle tape le coté gauche d'un block
                nbCol++;
               res = 1;
            }else if((x <= tabBlock.get(i).getX() + tabBlock.get(i).getWidth() + widthBall/10 && x>= tabBlock.get(i).getX()+tabBlock.get(i).getWidth()) && (y > (tabBlock.get(i).getY() - heightBall) && y < (tabBlock.get(i).getY() + tabBlock.get(i).getHeight() + heightBall))){ // si la balle tape le coté droit d'un block
                nbCol++;
               res = 2;
            }else if((y  <= tabBlock.get(i).getY()+tabBlock.get(i).getHeight()+heightBall/10) && (y >= tabBlock.get(i).getY() +tabBlock.get(i). getHeight()) && (x>=(tabBlock.get(i).getX() - widthBall) && x<=(tabBlock.get(i).getX() + tabBlock.get(i).getWidth() + widthBall))) { // touche le haut block
                nbCol++;
               res = 3;
            }else if((y <= tabBlock.get(i).getY()) && (y >= tabBlock.get(i).getY() - heightBall) && (x>=(tabBlock.get(i).getX() - widthBall) && x<=(tabBlock.get(i).getX() + tabBlock.get(i).getWidth() + widthBall))){
                nbCol++;
               res = 4;
            }
            else{
               res = 0;
            }

        }
        return  res;

    }

    public void deplacement(){

        /*                 cote bas
                           col = 4
                        _________________
                        |                |
            cote droit  |                |  cote gauche
            col = 1     |________________|  col =  2

                            cote haut
                            col = 3
         */

        /**  COLLISION COTE DROIT  **/
        if(col == 1 && direction ==1) direction = 2;// si colision coté droit en monté alors deplacement à gauche en monté
        else if(col == 1 && direction == 4)direction = 3; // si colision coté droit en descente alors deplacement à gauche en descente


        /**  COLLISION COTE GAUCHE  **/
        if(col == 2 && direction ==2)direction = 1;// si colision coté gauche en monté alors deplacement à droite en monté
        else if(col == 2 && direction == 3)direction = 4;// si colision coté gauche en descente alors deplacement à droite en descente

        /**  COLLISION COTE HAUT   **/
        if(col == 3 && direction ==1) direction = 4; // si colision coté haut en monté vers la droite  alors deplacement à droite en descente
        else if(col == 3 && direction == 2)direction = 3; // si colision coté haut en monté vers la gauche alors déplacement à gauche en descente

        /**  COLLISION COTE BAS   **/
        if(col == 4 && direction == 4)direction = 1; // si colision coté bas en descente vers la droite  alors deplacement à droite en monté
        else if(col == 4 && direction == 3)direction = 2; // si colision coté bas en descente vers la gauche  alors deplacement à gauche en monté


        /**   DEPLACEMENT   **/
        if(direction == 1){ // monté vers la droite
            ballX = ballX + (((float) distance / 4) / fps);
            if(nbCol == 0){
                ballY = (float) (((ballX) * m) + p);
            }else{
                ballY = ballY - (((float) distance / 4) / fps);
            }
        }else if(direction == 2){ // monté vers la gauche
            ballX = ballX - (((float) distance / 4) / fps);
            if(nbCol == 0){
                ballY = height -  (((float) (((ballX) * m) + p)) - height);
            }else{
                ballY = ballY - (((float) distance / 4) / fps);
            }
        }else if(direction == 3){ // descente vers la gauche
            ballX = ballX - (((float) distance / 4) / fps);
            if(nbCol == 0){
                ballY = (float) (((ballX) * m) + p);
            }else{
                ballY = ballY + (((float) distance / 4) / fps);
            }
        }else if(direction == 4){ // descente vers la droite
            ballX = ballX + (((float) distance / 4) / fps);
            if(nbCol == 0){
                ballY = (float) (((ballX) * -m) - p);
            }else{
                ballY = ballY + (((float) distance / 4) / fps);
            }
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
                tabBlock.add(new Block(x,y*i*3/2));
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

}