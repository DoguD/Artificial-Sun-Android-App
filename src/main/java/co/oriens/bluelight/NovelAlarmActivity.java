package co.oriens.bluelight;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

import static java.lang.Math.round;

public class NovelAlarmActivity extends AppCompatActivity {
    // VARIABLES
    // LAYOUT ELEMENTS
    Button buttonStopAlarm, buttonSquare;
    TextView textAlarmTimer, textAlarmIntro;
    // Telephone Screen
    Window window;
    // Realtive Layout
    RelativeLayout relativeLayout;
    // Layout Parameters
    RelativeLayout.LayoutParams layoutParams;

    // Random Object
    Random rnd = new Random();

    // dpi
    DisplayMetrics metrics ;
    float density;

    // Context
    Context ctx = this;

    // Timer Object and second integer
    CountDownTimer timer;
    int remainingSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_alarm);

        // SET LAYOUT ELEMENTS
        // Intro
        textAlarmIntro = (TextView) findViewById(R.id.textAlarmFullScreenIntro);
        // Timer
        textAlarmTimer = (TextView) findViewById(R.id.textAlarmTimer);
        // Stop alarm
        buttonStopAlarm = (Button) findViewById(R.id.buttonStopAlarm);
        // Square button for game
        buttonSquare = (Button) findViewById(R.id.buttonSquare);
        // Relative layout
        relativeLayout = (RelativeLayout) findViewById(R.id.alarmFullScreenLayout);
        // Layout Parameters
        layoutParams = new RelativeLayout.LayoutParams(buttonSquare.getLayoutParams());

        //Set window
        window = getWindow();

        // Dpi
        metrics = getApplicationContext().getResources().getDisplayMetrics();
        density = (float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT;

        // SET ON CLICK LISTENERS
        // Stop alarm and play game
        buttonStopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlarmAndStartGame();
            }
        });
        // Pressed square button inside game
        buttonSquare.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View view){
               squareButtonPressed();
           }
        });
    }

    // FUNCTIONS
    void stopAlarmAndStartGame(){
        // Arrange (in)visibility
        buttonStopAlarm.setVisibility(View.GONE);
        textAlarmIntro.setVisibility(View.GONE);
        buttonSquare.setVisibility(View.VISIBLE);
        textAlarmTimer.setVisibility(View.VISIBLE);

        // Start Timer
        remainingSeconds = 30;
        StartTimer();

        // Transfer square button to a random location
        transferToRandomLocation();
    }

    void squareButtonPressed(){
        transferToRandomLocation();
        // Reset its size
        layoutParams.width = (round(50*density));
        layoutParams.height = (round(50*density));
    }

    void StartTimer() {
        timer = new CountDownTimer(3000000, 1000) {//Yeni geri sayım methodu oluşturuluyor
            float targetBrigthness = 0f;
            public void onTick(long millisUntilFinished) { //Her saniye geçtiğinde çalışan method
                // Exit if the timer has reached 0
                if(remainingSeconds <= 0){
                    Log.d("NOVEL ALARM:","Shold the timer finsih");
                    textAlarmTimer.setText(getString(R.string.wake_up_session_finished));//Geri sayım metin kutusuna "ARTIK ENERJİKSİNİZ" yazısı atanıyor
                    buttonSquare.setVisibility(View.INVISIBLE);
                    timer.cancel();
                }
                else{
                    // Decrease remaining seconds for display
                    remainingSeconds -= 1;

                    // Display remaining time
                    textAlarmTimer.setText(remainingSeconds + " " + getString(R.string.wake_up_session_countdown)); //Metin kutusundaki kalan saniyeler güncelleniyor

                    //Kademeli olarak parlaklığı artırma koşulu
                /*int currentSecond=60;
                if( millisUntilFinished>=44000 && (millisUntilFinished / 1000) != currentSecond){ // Eğer 44 saniyeden fazla kalmışssa parlaklığı %5 artır (her saniye)
                    float targetBrigthness; // Hedef parlaklık değişkeni
                    targetBrigthness = (61-(millisUntilFinished/1000)) * 0.05f; // Her 4 saniyede bir hedef parlaklık %20 artırılıyor
                    MakeBright(targetBrigthness); //Parlaklığı artırma methodu çalıştırılıyor

                    currentSecond=(int)(millisUntilFinished / 1000); //Şu anki saniye değişkenini yenile
                }*/
                    // Increase brightness gradually
                    if(targetBrigthness < 1f){
                        targetBrigthness += 0.05f;
                        MakeBright(targetBrigthness);
                    }

                    // Execute shrink square method
                    shrinkSquare();
                }
            }

            //Empty method for the end of timer, because it is cancelled
            public void onFinish() {
            }
        }.start();
    }

    //Ekran parlaklığını arttırma methodu
    void MakeBright(float targetBrigthness) {
        WindowManager.LayoutParams layoutParams = window.getAttributes();//Telefon ekranı parametreleri alınıyor

        layoutParams.screenBrightness = targetBrigthness;//Parlaklık parametresi targetBrigthness değerine göre artırılıyor
        window.setAttributes(layoutParams);//Değişiklikler telefon ekranına uygulanıyor
    }

    // Transfer the square button to a random location
    void transferToRandomLocation(){
        // Generate random x and y
        int newX, newY;
        newX = rnd.nextInt(relativeLayout.getWidth()-(buttonSquare.getWidth()+50+(2*20))*round(density)); // 20 acts as a margin from borders
        newY = rnd.nextInt(relativeLayout.getHeight()-(buttonSquare.getHeight()+50+(2*20)
                +textAlarmTimer.getHeight()+16)*round(density));// 16 is the margin of textAlarmTimer

        // Transfer the square
        layoutParams.setMargins(20+newX,textAlarmTimer.getHeight()+16+50+20+newY,0,0);
        buttonSquare.setLayoutParams(layoutParams);

    }

    // Shrinking the square
    boolean shrinkSquare(){
        float newLength;
        boolean result = true;

        // Shrink if possible
        if((newLength = buttonSquare.getWidth())>30*density){
            newLength -= 10*density;
        }
        else{ // Create new square and add time to timer
            // To prevent a bug: timer cancels but more seconds are added here
            if(remainingSeconds>0){
                // Add time to timer
                remainingSeconds += 5;
                // Display remaining time
                textAlarmTimer.setText(remainingSeconds + " " + getString(R.string.wake_up_session_countdown)); //Metin kutusundaki kalan saniyeler güncelleniyor

            }
            
            // New square
            transferToRandomLocation();
            newLength = 50*density;
            result = false;
        }
        layoutParams.width = ((int) newLength);
        layoutParams.height = ((int)newLength);

        Log.d("NOVEL ALARM Pixel:",""+newLength);
        Log.d("NOVEL ALARM DP:",""+newLength/density);

        return result;
    }

    // Overriding methods for preventing going back
    @Override
    public void onBackPressed() {
    }

}
