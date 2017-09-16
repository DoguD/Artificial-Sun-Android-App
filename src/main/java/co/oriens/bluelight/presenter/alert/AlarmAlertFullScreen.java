/*
 * Copyright (C) 2009 The Android Open Source Project
 * Copyright (C) 2012 Yuriy Kulikov yuriy.kulikov.87@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * NV : NOVEL ALARM
 */

package co.oriens.bluelight.presenter.alert;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

import co.github.androidutils.logger.Logger;
import co.oriens.bluelight.R;
import co.oriens.bluelight.model.AlarmsManager;
import co.oriens.bluelight.model.interfaces.Alarm;
import co.oriens.bluelight.model.interfaces.AlarmNotFoundException;
import co.oriens.bluelight.model.interfaces.IAlarmsManager;
import co.oriens.bluelight.model.interfaces.Intents;
import co.oriens.bluelight.presenter.DynamicThemeHandler;
import co.oriens.bluelight.presenter.SettingsActivity;
import co.oriens.bluelight.presenter.TimePickerDialogFragment;

import static android.view.View.GONE;
import static java.lang.Math.round;

/**
 * Alarm Clock alarm alert: pops visible indicator and plays alarm tone. This
 * activity is the full screen version which shows over the lock screen with the
 * wallpaper as the background.
 */
public class AlarmAlertFullScreen extends Activity implements TimePickerDialogFragment.AlarmTimePickerDialogHandler,
        TimePickerDialogFragment.OnAlarmTimePickerCanceledListener {
    private static final boolean LONGCLICK_DISMISS_DEFAULT = false;
    private static final String LONGCLICK_DISMISS_KEY = "longclick_dismiss_key";
    private static final String DEFAULT_VOLUME_BEHAVIOR = "2";
    protected static final String SCREEN_OFF = "screen_off";

    protected Alarm mAlarm;
    private int mVolumeBehavior;
    boolean mFullscreenStyle;

    private IAlarmsManager alarmsManager;

    private boolean longClickToDismiss;

    /**NV**/
    // VARIABLES
    // LAYOUT ELEMENTS
    Button buttonStopAlarm, buttonSquare;
    TextView textAlarmTimer, textAlarmIntro, textAlarmFinished;
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

    /**
     * Receives Intents from the model
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int id = intent.getIntExtra(Intents.EXTRA_ID, -1);
            if (action.equals(Intents.ALARM_SNOOZE_ACTION)) {
                if (mAlarm.getId() == id) {
                    finish();
                }
            } else if (action.equals(Intents.ALARM_DISMISS_ACTION)) {
                if (mAlarm.getId() == id) {
                    finish();
                }
            } else if (action.equals(Intents.ACTION_SOUND_EXPIRED)) {
                if (mAlarm.getId() == id) {
                    // if sound has expired there is no need to keep the screen
                    // on
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }
        }
    };
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle icicle) {
        setTheme(DynamicThemeHandler.getInstance().getIdForName(getClassName()));
        super.onCreate(icicle);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (getResources().getBoolean(R.bool.isTablet)) {
            // preserve initial rotation and disable rotation change
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(getRequestedOrientation());
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        alarmsManager = AlarmsManager.getAlarmsManager();

        int id = getIntent().getIntExtra(Intents.EXTRA_ID, -1);
        try {
            mAlarm = alarmsManager.getAlarm(id);

            final String vol = sp.getString(SettingsActivity.KEY_VOLUME_BEHAVIOR, DEFAULT_VOLUME_BEHAVIOR);
            mVolumeBehavior = Integer.parseInt(vol);

            final Window win = getWindow();
            win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            // Turn on the screen unless we are being launched from the
            // AlarmAlert
            // subclass as a result of the screen turning off.
            if (!getIntent().getBooleanExtra(SCREEN_OFF, false)) {
                win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
            }

            updateLayout();
            //Increase brightness
            MakeBright(1f,win);

            // Register to get the alarm killed/snooze/dismiss intent.
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intents.ALARM_SNOOZE_ACTION);
            filter.addAction(Intents.ALARM_DISMISS_ACTION);
            filter.addAction(Intents.ACTION_CANCEL_SNOOZE);
            filter.addAction(Intents.ACTION_SOUND_EXPIRED);
            registerReceiver(mReceiver, filter);
        } catch (AlarmNotFoundException e) {
            Logger.getDefaultLogger().d("Alarm not found");
        }

        /**NV**/
        // SET LAYOUT ELEMENTS
        // Intro
        textAlarmIntro = (TextView) findViewById(R.id.textAlarmFullScreenIntro);
        // Finished
        textAlarmFinished = (TextView) findViewById(R.id.textAlarmFullScreenEnergized);
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

    //Method for increasing brightness
    void MakeBright(float targetBrigthness, Window window) {
        WindowManager.LayoutParams layoutParams = window.getAttributes();//Telefon ekranı parametreleri alınıyor

        layoutParams.screenBrightness = targetBrigthness;//Parlaklık parametresi targetBrigthness değerine göre artırılıyor
        window.setAttributes(layoutParams);//Değişiklikler telefon ekranına uygulanıyor
    }

    private void setTitle() {
        final String titleText = mAlarm.getLabelOrDefault(this);
        setTitle(titleText);
        TextView textView = (TextView) findViewById(R.id.alarm_alert_label);
        textView.setText(titleText);

        if (getLayoutResId() == R.layout.alert || getString(R.string.default_label).equals(titleText)) {
            // in non-full screen mode we already see the label in the title.
            // Therefore we hade the views with an additional label
            // also, if the label is default, do not show it
            textView.setVisibility(GONE);
            findViewById(R.id.alert_label_divider).setVisibility(GONE);
        }
    }

    protected int getLayoutResId() {
        return R.layout.alert_fullscreen;
    }

    protected String getClassName() {
        return AlarmAlertFullScreen.class.getName();
    }

    private void updateLayout() {
        LayoutInflater inflater = LayoutInflater.from(this);

        setContentView(inflater.inflate(getLayoutResId(), null));

        /*
         * snooze behavior: pop a snooze confirmation view, kick alarm manager.
         */
        final Button snooze = (Button) findViewById(R.id.alert_button_snooze);
        snooze.requestFocus();
        snooze.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                snoozeIfEnabledInSettings();
            }
        });

        snooze.setOnLongClickListener(new Button.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isSnoozeEnabled()) {
                    TimePickerDialogFragment.showTimePicker(getFragmentManager());
                    AlarmAlertFullScreen.this.sendBroadcast(new Intent(Intents.ACTION_MUTE));
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //TODO think about removing this or whatevar
                            AlarmAlertFullScreen.this.sendBroadcast(new Intent(Intents.ACTION_DEMUTE));
                        }
                    }, 10000);
                }
                return true;
            }
        });

        /* dismiss button: close notification */
        final Button dismissButton = (Button) findViewById(R.id.alert_button_dismiss);
        dismissButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longClickToDismiss) {
                    dismissButton.setText(getString(R.string.alarm_alert_hold_the_button_text));
                } else {
                    dismiss();
                }
            }
        });

        dismissButton.setOnLongClickListener(new Button.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dismiss();
                return true;
            }
        });

        /* Set the title from the passed in alarm */
        setTitle();
    }

    // Attempt to snooze this alert.
    private void snoozeIfEnabledInSettings() {
        if (isSnoozeEnabled()) {
            alarmsManager.snooze(mAlarm);
        }
    }

    // Dismiss the alarm.
    private void dismiss() {
        alarmsManager.dismiss(mAlarm);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

    }

    private boolean isSnoozeEnabled() {
        return Integer.parseInt(sp.getString("snooze_duration", "-1")) != -1;
    }

    /**
     * this is called when a second alarm is triggered while a previous alert
     * window is still active.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Logger.getDefaultLogger().d("AlarmAlert.OnNewIntent()");

        int id = intent.getIntExtra(Intents.EXTRA_ID, -1);
        try {
            mAlarm = alarmsManager.getAlarm(id);
            setTitle();
        } catch (AlarmNotFoundException e) {
            Logger.getDefaultLogger().d("Alarm not found");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        longClickToDismiss = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(LONGCLICK_DISMISS_KEY,
                LONGCLICK_DISMISS_DEFAULT);

        Button snooze = (Button) findViewById(R.id.alert_button_snooze);
        snooze.setEnabled(isSnoozeEnabled());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.getDefaultLogger().d("AlarmAlert.onDestroy()");
        // No longer care about the alarm being killed.
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Do this on key down to handle a few of the system keys.
        boolean up = event.getAction() == KeyEvent.ACTION_UP;
        switch (event.getKeyCode()) {
        // Volume keys and camera keys dismiss the alarm
        case KeyEvent.KEYCODE_VOLUME_UP:
        case KeyEvent.KEYCODE_VOLUME_DOWN:
        case KeyEvent.KEYCODE_VOLUME_MUTE:
        case KeyEvent.KEYCODE_CAMERA:
        case KeyEvent.KEYCODE_FOCUS:
            if (up) {
                switch (mVolumeBehavior) {
                case 1:
                    snoozeIfEnabledInSettings();
                    break;

                case 2:
                    dismiss();
                    break;

                default:
                    break;
                }
            }
            return true;
        default:
            break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        // Don't allow back to dismiss. This method is overriden by AlarmAlert
        // so that the dialog is dismissed.
        return;
    }

    @Override
    public void onDialogTimeSet(int hourOfDay, int minute) {
        mAlarm.snooze(hourOfDay, minute);

    }

    @Override
    public void onTimePickerCanceled() {
        AlarmAlertFullScreen.this.sendBroadcast(new Intent(Intents.ACTION_DEMUTE));
    }

    /**NV**/
    // FUNCTIONS
    void stopAlarmAndStartGame(){
        // Arrange (in)visibility
        buttonStopAlarm.setVisibility(GONE);
        textAlarmIntro.setVisibility(GONE);
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
                    textAlarmIntro.setVisibility(GONE);
                    textAlarmFinished.setVisibility(View.VISIBLE);
                    //textAlarmTimer.setText(getString(R.string.wake_up_session_finished));//Geri sayım metin kutusuna "ARTIK ENERJİKSİNİZ" yazısı atanıyor
                    buttonSquare.setVisibility(View.INVISIBLE);
                    timer.cancel();

                    // Wait one second and dismiss the alarm
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                        }
                    }, 1000);
                }
                else{
                    // Decrease remaining seconds for display
                    remainingSeconds -= 1;

                    // Display remaining time
                    textAlarmTimer.setText(remainingSeconds + " " + getString(R.string.wake_up_session_countdown)); //Metin kutusundaki kalan saniyeler güncelleniyor

                    // Increase brightness gradually
                    /*if(targetBrigthness < 1f){
                        targetBrigthness += 0.05f;
                        MakeBright(targetBrigthness);
                    }*/

                    // Execute shrink square method
                    shrinkSquare();
                }
            }

            //Empty method for the end of timer, because it is cancelled
            public void onFinish() {
            }
        }.start();
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
}
