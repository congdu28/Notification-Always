package app.cd.notifalways;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.os.SystemClock;

/** Schedules short connection checks without maintaining a permanent background service. */
final class PulseController {
    private static final String STORAGE = "notification_always";
    private static final String SIGNAL = "app.cd.notifalways.PULSE";
    private static final String GMS_PACKAGE = "com.google.android.gms";
    private static final String[] CONNECTION_ACTIONS = {
            "com.google.android.intent.action.GCM_RECONNECT",
            "com.google.android.intent.action.MCS_HEARTBEAT",
            "com.google.android.gms.gcm.ACTION_HEARTBEAT_NOW"
    };

    private PulseController() {}

    static String gmsPackage() {
        return GMS_PACKAGE;
    }

    static SharedPreferences storage(Context context) {
        return context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
    }

    static boolean isActive(Context context) {
        return storage(context).getBoolean("active", true);
    }

    static void setActive(Context context, boolean active) {
        storage(context).edit().putBoolean("active", active).apply();
        if (active) {
            sendConnectionNudge(context);
            scheduleNext(context);
        } else {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.cancel(alarmToken(context));
            storage(context).edit().remove("planned_for").apply();
        }
    }

    static void sendConnectionNudge(Context context) {
        for (String action : CONNECTION_ACTIONS) {
            context.sendBroadcast(new Intent(action).setPackage(GMS_PACKAGE));
        }
        storage(context).edit().putLong("checked_at", System.currentTimeMillis()).apply();
    }

    static void requestImmediateCheck(Context context) {
        sendConnectionNudge(context);
        scheduleNext(context);
    }

    static void scheduleNext(Context context) {
        if (!isActive(context)) return;
        long wait = chooseDelay(context);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + wait, alarmToken(context));
        storage(context).edit()
                .putLong("planned_for", System.currentTimeMillis() + wait)
                .apply();
    }

    static void handleAlarm(Context context) {
        if (!isActive(context)) return;
        sendConnectionNudge(context);
        scheduleNext(context);
    }

    static String delayLabel(Context context) {
        return (chooseDelay(context) / 60_000L) + " phút";
    }

    private static PendingIntent alarmToken(Context context) {
        Intent intent = new Intent(context, PulseReceiver.class).setAction(SIGNAL);
        return PendingIntent.getBroadcast(context, 41, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private static long chooseDelay(Context context) {
        BatteryManager battery =
                (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        PowerManager power = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        if (battery != null && battery.isCharging()) return 15 * 60_000L;
        int level = battery == null ? 100
                : battery.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        if ((level >= 0 && level < 15) || (power != null && power.isPowerSaveMode())) {
            return 30 * 60_000L;
        }

        ConnectivityManager network =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (network != null) {
            NetworkCapabilities active =
                    network.getNetworkCapabilities(network.getActiveNetwork());
            if (active != null && active.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return 15 * 60_000L;
            }
        }
        return 20 * 60_000L;
    }
}
