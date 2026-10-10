package app.cd.notifalways;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Receives the scheduled wake-up and hands the next check back to Android. */
public final class PulseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PulseController.handleAlarm(context);
    }
}
