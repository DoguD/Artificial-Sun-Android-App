package co.oriens.bluelight;

//Gerekli eklentiler import ediliyor

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FullScreenWork extends AppCompatActivity {

    //Objeler yaratılıyor
    ImageButton stopButton; //Seansı bitirme butonu
    Window window; //Telefon ekranı
    RelativeLayout relativeLayout; //Aktivitenin layoutu
    TextView timerTextView; //Geri sayım için metin kutusu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_work);

        //Objeler tanımlanyor
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout); //Layout
        timerTextView = (TextView) findViewById(R.id.timerTextView);//
        window = getWindow();
        stopButton = (ImageButton) findViewById(R.id.pauseButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StopButtonPressed();
            }
        });

        MakeBright();
        StartTimer();
    }

    //Change Brightness Method
    void MakeBright() {
        //Set the layout parameters for window
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        //Change brignetss layout parameter to 1(%100)
        layoutParams.screenBrightness = 1;
        //Effect screen brightness
        window.setAttributes(layoutParams);
    }

    //Start Timer Method
    void StartTimer() {
        timerTextView.setVisibility(View.VISIBLE);

        new CountDownTimer(1500000, 1000) {

            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished - minutes * 60000) / 1000;
                timerTextView.setText(minutes + " " + getString(R.string.work_session_countdown_minutes) + " " + seconds + " " + getString(R.string.work_session_countdown_seconds));
            }

            public void onFinish() {
                timerTextView.setText(getString(R.string.work_session_finished));
                stopButton.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    //Stop Button Pressed
    void StopButtonPressed() {
        finish();
    }

    //Girilen dalga boyunu önce RGB sonrasında da HEX renk koduna çeviren metod
    public String waveLengthToHEX(double wavelength) {//girdi olarak alınan sayı wavelength değişkenine atanıyor
        double IntensityMax = 255; //RGB kodunda renklerin alabileceği maksimum değer tanımlanıyor
        double R, G, B; //RGB renk kodunun değerleri için 3 adet değişken oluşturuluyor
        int[] rgb = new int[3];//RGB renk kodu için 3 elemanlı tam sayı dizisi oluşturuluyor
        String HexValue; //HEX renk kodu için bir string değişkeni oluştruluyor

        //IF ELSE zinciriyle girilen dalga boyunun hangi bant aralığında olduğu anlaşılır
        if ((wavelength >= 380) && (wavelength < 440)) {
            R = -(wavelength - 440) / (440 - 380);
            G = 0.0;
            B = 1.0;
        } else if ((wavelength >= 440) && (wavelength < 490)) { //Girdimiz olan mavi ışığın (480nm)'nin düştüğü aralık
            R = 0.0;
            G = (wavelength - 440) / (490 - 440);
            B = 1.0;
        } else if ((wavelength >= 490) && (wavelength < 510)) {
            R = 0.0;
            G = 1.0;
            B = -(wavelength - 510) / (510 - 490);
        } else if ((wavelength >= 510) && (wavelength < 580)) {
            R = (wavelength - 510) / (580 - 510);
            G = 1.0;
            B = 0.0;
        } else if ((wavelength >= 580) && (wavelength < 645)) {
            R = 1.0;
            G = -(wavelength - 645) / (645 - 580);
            B = 0.0;
        } else if ((wavelength >= 645) && (wavelength < 781)) {
            R = 1.0;
            G = 0.0;
            B = 0.0;
        } else {
            R = 0.0;
            G = 0.0;
            B = 0.0;
        }
        rgb[0] = (int) Math.round(R * IntensityMax);//Dizinin 1. elemanına R(kırmızı) değişkeni atanıyor
        rgb[1] = (int) Math.round(G * IntensityMax);//Dizinin 2. elemanına G(yeşil) değişkeni atanıyor
        rgb[2] = (int) Math.round(B * IntensityMax);//Dizinin 3. elemanına B(mavi) değişkeni atanıyor
        /*Son olarak HexValue değişkenine rgb dizisinin elemanları, teker teker hexadecimal değerlerine çevirilerek ekleniyor
        * Uygulama arka planında kullanılabilecek bir String değeri elde ediliyor*/
        HexValue = "#" + Integer.toHexString(rgb[0]) + Integer.toHexString(rgb[1]) + Integer.toHexString(rgb[2]);
        return HexValue;
    }
}
