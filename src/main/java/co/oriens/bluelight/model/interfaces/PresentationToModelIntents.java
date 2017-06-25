package co.oriens.bluelight.model.interfaces;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import co.oriens.bluelight.BuildConfig;
import co.oriens.bluelight.model.AlarmsService;

public class PresentationToModelIntents {

    public static final String ACTION_REQUEST_SNOOZE = BuildConfig.APPLICATION_ID + ".model.interfaces.ServiceIntents.ACTION_REQUEST_SNOOZE";
    public static final String ACTION_REQUEST_DISMISS = BuildConfig.APPLICATION_ID + ".model.interfaces.ServiceIntents.ACTION_REQUEST_DISMISS";

    public static PendingIntent createPendingIntent(Context context, String action, int id) {
        Intent intent = new Intent(action);
        intent.putExtra(Intents.EXTRA_ID, id);
        intent.setClass(context, AlarmsService.class);
        return PendingIntent.getService(context, id, intent, 0);
    }
}
