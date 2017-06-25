package co.oriens.bluelight;

import android.content.Context;
import android.content.SharedPreferences;

//
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    //Shared Pref Mode
    int PRIVATE_MODE=0;

    //Shared Preferences File Name
    private static final String PREF_NAME = "androidhive-welcom";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_ALARM_SET = "IsAlarmSet";
    private static final String ALARM_HOUR = "alarmHour";
    private static final String ALARM_MINUTE = "alarmMinute";

    //Pref Manager
    public PrefManager(Context context){
        this._context=context;
        pref=_context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor=pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTimeLaunch){
        editor.putBoolean(IS_FIRST_TIME_LAUNCH,isFirstTimeLaunch);
        editor.commit();
    }

    public boolean isFirstTimeLaunch(){
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH,true);
    }

    //ALARM
    //isSet
    public void setIsAlarmSet(boolean isAlarmSet){
        editor.putBoolean(IS_ALARM_SET,isAlarmSet);
        editor.commit();
    }

    public boolean isAlarmSet(){
        return pref.getBoolean(IS_ALARM_SET,false);
    }

    //Hour
    public void setAlarmHour(int alarmHour) {
        editor.putInt(ALARM_HOUR, alarmHour);
        editor.commit();
    }
    public int alarmHour(){
        return pref.getInt(ALARM_HOUR,7);
    }

    //Minute
    public void setAlarmMinute(int alarmMinute){
        editor.putInt(ALARM_MINUTE,alarmMinute);
        editor.commit();
    }
    public int alarmMinute(){
        return pref.getInt(ALARM_MINUTE,0);
    }

}
