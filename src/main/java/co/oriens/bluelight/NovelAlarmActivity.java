package co.oriens.bluelight;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class NovelAlarmActivity extends AppCompatActivity {
    // VARIABLES
    // LAYOUT ELEMENTS
    Button buttonStopAlarm, buttonSquare;
    TextView textAlarmTimer, textAlarmIntro;
    // Telephone Screen
    Window window;

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

        //Set window
        window = getWindow();

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
        StartTimer();
    }

    void squareButtonPressed(){

    }

    void StartTimer() {
        new CountDownTimer(60000, 1000) {//Yeni geri sayım methodu oluşturuluyor

            public void onTick(long millisUntilFinished) { //Her saniye geçtiğinde çalışan method
                textAlarmTimer.setText((millisUntilFinished / 1000) + " " + getString(R.string.wake_up_session_countdown)); //Metin kutusundaki kalan saniyeler güncelleniyor

                //Kademeli olarak parlaklığı artırma koşulu
                int currentSecond=60;
                if( millisUntilFinished>=44000 && (millisUntilFinished / 1000) != currentSecond){ // Eğer 44 saniyeden fazla kalmışssa parlaklığı %5 artır (her saniye)
                    float targetBrigthness; // Hedef parlaklık değişkeni
                    targetBrigthness = (61-(millisUntilFinished/1000)) * 0.05f; // Her 4 saniyede bir hedef parlaklık %20 artırılıyor
                    MakeBright(targetBrigthness); //Parlaklığı artırma methodu çalıştırılıyor

                    currentSecond=(int)(millisUntilFinished / 1000); //Şu anki saniye değişkenini yenile
                }
            }

            //Geri sayım bittiğinde uygulanacaklar methodu
            public void onFinish() {
                textAlarmTimer.setText(getString(R.string.wake_up_session_finished));//Geri sayım metin kutusuna "ARTIK ENERJİKSİNİZ" yazısı atanıyor
            }
        }.start();
    }

    //Ekran parlaklığını arttırma methodu
    void MakeBright(float targetBrigthness) {
        WindowManager.LayoutParams layoutParams = window.getAttributes();//Telefon ekranı parametreleri alınıyor

        layoutParams.screenBrightness = targetBrigthness;//Parlaklık parametresi targetBrigthness değerine göre artırılıyor
        window.setAttributes(layoutParams);//Değişiklikler telefon ekranına uygulanıyor
    }
}
