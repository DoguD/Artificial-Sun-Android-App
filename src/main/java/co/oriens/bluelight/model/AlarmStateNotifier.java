package co.oriens.bluelight.model;

import android.content.Context;
import android.content.Intent;

import co.oriens.bluelight.model.interfaces.Intents;

/**
 * Broadcasts alarm state with an intent
 *
 */
public class AlarmStateNotifier implements AlarmCore.IStateNotifier {

    private final Context mContext;

    public AlarmStateNotifier(Context context) {
        mContext = context;
    }

    @Override
    public void broadcastAlarmState(int id, String action) {
        Intent intent = new Intent(action);
        intent.putExtra(Intents.EXTRA_ID, id);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void broadcastAlarmState(int id, String action, long millis) {
        Intent intent = new Intent(action);
        intent.putExtra(Intents.EXTRA_ID, id);
        intent.putExtra(Intents.EXTRA_NEXT_NORMAL_TIME_IN_MILLIS, millis);
        mContext.sendBroadcast(intent);
    }
}
