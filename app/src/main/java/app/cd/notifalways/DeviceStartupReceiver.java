package app.cd.notifalways;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Rebuilds the next check after Android starts the device or replaces the application. */
public final class DeviceStartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (PulseController.isActive(context)) {
            PulseController.sendConnectionNudge(context);
            PulseController.scheduleNext(context);
        }
    }
}
