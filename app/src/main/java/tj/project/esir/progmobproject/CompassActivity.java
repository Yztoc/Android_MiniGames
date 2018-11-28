package tj.project.esir.progmobproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import tj.project.esir.progmobproject.ball_games.Balls;
import tj.project.esir.progmobproject.ball_games.MenuParam;



public class CompassActivity extends AppCompatActivity {

    private int mAzimuth = 0; // degree

    private SensorManager mSensorManager;
    private boolean volumeOn;

    private Sensor mGravity;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    private boolean haveGravity = false;
    private boolean haveAccelerometer = false;
    private boolean haveMagnetometer = false;

    private float currentDegree = 0f;
    private TextView text;
    private TextView timeText;
    private int randomDegree;

    private ImageView image;

    CountDownTimer cTimer = null;
    float time = 0;


    private MediaPlayer mediaPlayerUnlock;
    private MediaPlayer mediaPlayerClick;
    private int clickDegree;

    static final float ALPHA = 0.25f;


    private int score = 0;
    private int scoreBall = 0;

    private int timeParam = 20000; //seconde niveau de difficulté à modifé lors de l envoie du client


    private SensorEventListener mSensorEventListener = new SensorEventListener() {

        float[]gData = new float[3]; // gravity
        float[] mData = new float[3]; // magnetometer
        float[] aData = new float[3]; // accelerometer
        float[] rMat = new float[9];
        float[] iMat = new float[9];
        float[] orientation = new float[3];

        public void onAccuracyChanged( Sensor sensor, int accuracy ) {}

        @Override
        public void onSensorChanged( SensorEvent event ) {
            switch ( event.sensor.getType() ) {
                case Sensor.TYPE_GRAVITY:
                    gData = lowPass(event.values.clone(), gData);
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    gData = lowPass(event.values.clone(), aData);
                    aData = gData;
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mData = lowPass(event.values.clone(), mData);
                    break;
                default: return;
            }


            if ( SensorManager.getRotationMatrix( rMat, iMat, gData, mData ) ) {
                mAzimuth= (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;

                // create a rotation animation (reverse turn degree degrees)
                RotateAnimation ra = new RotateAnimation(
                        currentDegree,
                        -mAzimuth,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f);

                // how long the animation will take place
                ra.setDuration(210);

                // set the animation after the end of the reservation status
                ra.setFillAfter(true);

                // Start the animation
                 image.startAnimation(ra);
                 if(mAzimuth == randomDegree){
                     // FAIRE SCORE //
                     dialogFinish();
                     mediaPlayerUnlock.start();

                 }
                 else if(Math.abs(clickDegree-mAzimuth)>5){
                     if(volumeOn)
                        calculVolume(mAzimuth, randomDegree);
                     mediaPlayerClick.start();
                     clickDegree = mAzimuth;
                 }
                currentDegree = -mAzimuth;
                text.setText("Degres : " + mAzimuth+"");
            }
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        volumeOn = true;
        setContentView(R.layout.activity_compass);
        image = findViewById(R.id.middle_lock);
        text = findViewById(R.id.text);
        timeText = findViewById(R.id.time);
        randomDegree = (int)(Math.random()*360);
        mediaPlayerUnlock = MediaPlayer.create(this,R.raw.unlock_locker);
        mediaPlayerClick = MediaPlayer.create(this,R.raw.click_locker);
        clickDegree = 0;
        startTimer(timeParam);

        // recoit le score de l'activity précédente
        Intent iin= getIntent();
        Bundle c = iin.getExtras();

        if(c!=null){
            scoreBall = (int) c.get("scoreBall");
        }

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        this.mGravity = this.mSensorManager.getDefaultSensor( Sensor.TYPE_GRAVITY );
        this.haveGravity = this.mSensorManager.registerListener( mSensorEventListener, this.mGravity, SensorManager.SENSOR_DELAY_UI );

        this.mAccelerometer = this.mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        this.haveAccelerometer = this.mSensorManager.registerListener( mSensorEventListener, this.mAccelerometer, SensorManager.SENSOR_DELAY_UI );

        this.mMagnetometer = this.mSensorManager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD );
        this.haveMagnetometer = this.mSensorManager.registerListener( mSensorEventListener, this.mMagnetometer, SensorManager.SENSOR_DELAY_UI  );

        // if there is a gravity sensor we do not need the accelerometer
        if( this.haveGravity )
            this.mSensorManager.unregisterListener( this.mSensorEventListener, this.mAccelerometer );

        if ( ( haveGravity || haveAccelerometer ) && haveMagnetometer ) {
            // ready to go
        } else {
            Toast.makeText(this, "No gyroscope detected on this device", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

    }


    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    protected void onPause(){
        super.onPause();
        volumeOn = false;
        mediaPlayerUnlock.setVolume(0,0);
        mediaPlayerClick.setVolume(0,0);
    }

    @Override
    protected void onStop(){
        super.onStop();
        volumeOn = false;
        mediaPlayerUnlock.setVolume(0,0);
        mediaPlayerClick.setVolume(0,0);
    }

    @Override
    protected void onStart(){
        super.onStart();
        volumeOn = true;
        mediaPlayerUnlock.setVolume(1,1);
        mediaPlayerClick.setVolume(1,1);
    }

    @Override
    public void onBackPressed() {
        mediaPlayerUnlock.setVolume(0,0);
        mediaPlayerClick.setVolume(0,0);
        Intent home = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(home);
        finish();
    }


    void startTimer(int minTime) {
        cTimer = new CountDownTimer(minTime, 100) {
            public void onTick(long millisUntilFinished) {
                time = millisUntilFinished/1000;
                timeText.setText("Time : " + time);
            }
            public void onFinish() {
            }
        };
        cTimer.start();
    }

    void dialogFinish(){
        Runnable second_Task = new Runnable() {
            public void run() {
                CompassActivity.this.runOnUiThread(new Runnable() {
                    public void run() {

                        final AlertDialog.Builder alert = new AlertDialog.Builder(CompassActivity.this);
                        alert.setTitle("Terminé ! ");
                        alert.setMessage(Html.fromHtml("Vous avez fini avec les stats suivant : "
                                + "<br><b><h3>Score Final : " + 10 + "</h3></b>"));

                        alert.setPositiveButton("Jeux suivant", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent quizz = new Intent(getApplicationContext(), QuizzActivity.class);
                                quizz.putExtra("scoreBall", scoreBall);
                                quizz.putExtra("scoreCompass", score);
                                startActivity(quizz);
                                overridePendingTransition(R.anim.slide,R.anim.slide_out);
                            }
                        });

                        alert.setNegativeButton("Rejouer", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                dialog.dismiss();
                            }
                        });

                        alert.show();
                    }
                });

            }
        };

        second_Task.run();
    }

    public void calculVolume(int currentDegree, int unlockDegree){
        int positionDifference = Math.abs(currentDegree - unlockDegree);
        float newVolume = 1f;
        if(positionDifference < 180){
            newVolume = 1 - ( (float) positionDifference /180); // position différence ne peut pas etre égal à 0 donc on est bon
            System.out.println("new Volume 1 :" +newVolume);
        }
        else{
            float diffPositionSoundValue = currentDegree >= unlockDegree ? (float)(unlockDegree + (360 - currentDegree))/180 : (float)(currentDegree + (360 - unlockDegree))/180;
            newVolume = 1- diffPositionSoundValue ;
            System.out.println("new Volume 2 :" +newVolume);
        }
        mediaPlayerClick.setVolume(newVolume,newVolume);
    }

}
