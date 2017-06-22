package layout;

//Gerekli eklentiler import ediliyor

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import co.oriens.bluelight.Alarm.AlarmFullScreenActivity;
import co.oriens.bluelight.Alarm.AlarmReceiver;
import co.oriens.bluelight.PrefManager;
import co.oriens.bluelight.R;
import static android.content.Context.ALARM_SERVICE;


public class SetAlarmFragment extends Fragment {
    //AlarmManager ve pendingIntent oluşturuluyor
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    //Layout objeleri oluşturuluyor
    private TimePicker alarmTimePicker; //Zaman seçicisi
    private TextView alarmTextView; //Alarmın kurulu olup olmadığını belirten metin kutusu
    private Button setAlarmButton; //Alarm kurma butonu
    private Button setAlarmButtonFinal;

    //Diğer sınıflardan bu sınıfa ulaşmak için instance oluşturuluyor
    private static SetAlarmFragment instance;
    public static SetAlarmFragment instance() {
        return instance;
    }

    //Pref Manager Declare
    private PrefManager prefManager;

    @Override
    public void onStart() {
        super.onStart();
        if(instance == null) {
            instance = this; //instance tanımlanıyor
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_set_alarm, container, false);

        //Layout elementleri tanımlanıyor
        alarmTimePicker = (TimePicker) layout.findViewById(R.id.alarmTimePicker);

        //Setting the Alarm Manager
        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE); //Alarm Yöneticisi(AlarmManager) tanımlanıyor

        UpdateLayout();

        setAlarmButton.setOnClickListener(new View.OnClickListener() { //Alarm kurma butonuna tıklanabilme özelliği ekleniyor
            @Override
            public void onClick(View v) { //Alarm kurma butonuna tıklandığında
                if(!prefManager.isAlarmSet()){
                    alarmTimePicker.setVisibility(View.VISIBLE);
                    setAlarmButtonFinal.setVisibility(View.VISIBLE);
                    alarmTextView.setVisibility(View.GONE);
                    setAlarmButton.setVisibility(View.GONE);
                }
                else{
                    alarmManager.cancel(pendingIntent);
                    prefManager.setIsAlarmSet(false);
                    UpdateLayout();
                }
            }
        });

        setAlarmButtonFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetAlarm(); //Alarm kurma methodu çalışıyor
                alarmTimePicker.setVisibility(View.GONE);
                setAlarmButtonFinal.setVisibility(View.GONE);
                alarmTextView.setVisibility(View.VISIBLE);
                setAlarmButton.setVisibility(View.VISIBLE);
                UpdateLayout();
            }
        });

        return layout;
    }

    //Arayüzü güncelleme
    public void UpdateLayout(){
        //Alarm Kurulu mu? Testi
        prefManager = new PrefManager(this.getContext());
        if(prefManager.isAlarmSet()){ // Kurulu ise
            alarmTextView.setText(getString(R.string.alarm_is_set_to)+prefManager.alarmHour()+":"+prefManager.alarmMinute()+getString(R.string.alarm_is_set_to_2));
            setAlarmButton.setText(getString(R.string.cancel_alarm));
        }
        else{
            alarmTextView.setText(getString(R.string.is_alarm_set));
            setAlarmButton.setText(getString(R.string.set_alarm_button));
        }
    }

    //Alarm kurma methodu
    public void SetAlarm() {
        prefManager.setIsAlarmSet(true);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour()); //Zaman seçicisinden saat alınıyor
        calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute()); //Zaman seçicisinden dakika alınıyor

        prefManager.setAlarmHour(alarmTimePicker.getCurrentHour());
        prefManager.setAlarmMinute(alarmTimePicker.getCurrentMinute());

        //Alarm alıcısına(AlarmReciever.class) alarmın bilgileri gönderiliyor
        Intent myIntent = new Intent(getActivity(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, myIntent, 0);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000 * 60, pendingIntent);
    }

    //Zamanı geldiğinde tam ekran alarm ekranını açan method
    public void StartAlarmActivity() {
        SetAlarmFragment.this.startActivity(new Intent(getActivity(), AlarmFullScreenActivity.class));
    }
}
