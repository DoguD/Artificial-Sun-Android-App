package co.oriens.bluelight;

//gerekli eklentiler import ediliyor
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import static android.os.SystemClock.sleep;

public class AlarmFullScreenActivity extends AppCompatActivity {

    Window window; //Window (Pencere) öğesi oluşturuluyor
    Button wakeUpButton; //Buton oluşturuluyor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_full_screen); //Görünüm activity_alarm_full_screen.xml dosyasından alınıyor

        window = getWindow(); //window'a telefon ekranı atanıyor(parlaklığı değiştimek amaçlı)
        wakeUpButton=(Button) findViewById(R.id.buttonAlarmWakeUp); //wakeUpButton'a layout'un buttonAlarmWakeUp objesi atanıyor

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM); //Varsayılan alarm zil sesi alarmUri'ye atanıyor
        if (alarmUri == null) { //Eğer varsayılan larm zil sesi yoksa
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); //Varsayılan bildirim zil sesi alarmUri'ye atanıyor
        }
        final Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);

        MakeBright(); //Ekran parlaklığını arttıran method çalıştırılıyor
        sleep(300000); // Odaya ışık vermesi için zil sesi çalmadan önce 5 dakika bekle
        ringtone.play(); //Zil sesi başlatılıyor

        //wakeUpButton'a OnClickListener(tıklanabilme özelliği) ekleniyor
        wakeUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtone.stop(); //Zil sesi durduruluyor
                AlarmFullScreenActivity.this.startActivity(new Intent(AlarmFullScreenActivity.this, FullScreenWakeUp.class)); //1 dakikalık mavi ışık sesansı açılıyor
            }
        });

    }

    //Ekran parlaklığını arttırma methodu
    void MakeBright() {
        WindowManager.LayoutParams layoutParams = window.getAttributes(); //window için layout parametreleri alınıyor
        layoutParams.screenBrightness = 1; //Parlaklık parametresi 1'e (%100) çıkarılıyor
        window.setAttributes(layoutParams); //Parametre telefon ekranına uygulanıyor
    }

    //Geri butonuna basılarak çıkılmasını engelleme methodu
    @Override
    public void onBackPressed() {
        //hiçbir şey yapma
    }
}