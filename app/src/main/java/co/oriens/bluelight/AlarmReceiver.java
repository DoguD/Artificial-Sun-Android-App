package co.oriens.bluelight;

//Gerekli eklentiler import ediliyor
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import layout.SetAlarmFragment;


public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) { //Çağrı geldiğinde

        SetAlarmFragment.instance().StartAlarmActivity(); //Alarm aktivitesi başlatılıyor

        //Bildirim gönderiliyor
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }

}
