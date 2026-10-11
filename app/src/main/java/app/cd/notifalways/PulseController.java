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

/** Schedules optional, unverified signals to Google Play services. */
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
        return storage(context).getBoolean("active", false);
    }

    static void setActive(Context context, boolean active) {
        storage(context).edit().putBoolean("active", active).apply();
        if (active) {
            sendConnectionNudge(context);
            scheduleNext(context);
        } else {
            cancelAlarm(context);
            storage(context).edit().remove("paused_no_gms").remove("schedule_failed").apply();
        }
    }

    static void sendConnectionNudge(Context context) {
        String skipped = !DeviceDiagnostics.playServicesEnabled(context) ? "Google Play Services chưa bật"
                : !isOnline(context) ? "thiết bị không có mạng" : null;
        if (skipped != null) {
            storage(context).edit().putString("last_check_status", skipped)
                    .putLong("last_check_at", System.currentTimeMillis()).apply();
            return;
        }
        boolean rejected = false;
        for (String action : CONNECTION_ACTIONS) {
            try {
                context.sendBroadcast(new Intent(action).setPackage(GMS_PACKAGE));
            } catch (RuntimeException ignored) {
                // These internal actions are not a supported connection health API.
                rejected = true;
            }
        }
        storage(context).edit()
                .putLong("signal_sent_at", System.currentTimeMillis())
                .putLong("last_check_at", System.currentTimeMillis())
                .putString("last_check_status", rejected
                        ? "Android từ chối ít nhất một yêu cầu"
                        : "đã gửi yêu cầu; chưa xác nhận GMS xử lý")
                .apply();
    }

    static void requestImmediateCheck(Context context) {
        sendConnectionNudge(context);
        scheduleNext(context);
    }

    static void ensureScheduled(Context context) {
        if (!isActive(context)) return;
        long planned = storage(context).getLong("planned_for", 0);
        if (planned > System.currentTimeMillis()
                && !storage(context).getBoolean("paused_no_gms", false)) return;
        scheduleNext(context);
    }

    static void scheduleNext(Context context) {
        if (!isActive(context)) return;
        if (!DeviceDiagnostics.playServicesEnabled(context)) {
            cancelAlarm(context);
            storage(context).edit().putBoolean("paused_no_gms", true)
                    .putBoolean("schedule_failed", false).apply();
            return;
        }
        boolean online = isOnline(context);
        long wait = chooseDelay(context, online);
        storage(context).edit().putBoolean("paused_no_gms", false).apply();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager == null) {
            storage(context).edit().remove("planned_for").putBoolean("schedule_failed", true).apply();
            return;
        }
        try {
            if (online && wait < 120 * 60_000L) {
                manager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + wait, alarmToken(context));
            } else {
                manager.set(AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime() + wait, alarmToken(context));
            }
            storage(context).edit()
                    .putLong("planned_for", System.currentTimeMillis() + wait)
                    .putBoolean("paused_no_gms", false)
                    .putBoolean("schedule_failed", false)
                    .apply();
        } catch (RuntimeException ignored) {
            storage(context).edit().remove("planned_for").putBoolean("schedule_failed", true).apply();
        }
    }

    static void handleAlarm(Context context) {
        if (!isActive(context)) return;
        storage(context).edit().putLong("alarm_fired_at", System.currentTimeMillis()).apply();
        sendConnectionNudge(context);
        scheduleNext(context);
    }

    static String delayLabel(Context context) {
        return (chooseDelay(context, isOnline(context)) / 60_000L) + " phút";
    }

    private static PendingIntent alarmToken(Context context) {
        Intent intent = new Intent(context, PulseReceiver.class).setAction(SIGNAL);
        return PendingIntent.getBroadcast(context, 41, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private static void cancelAlarm(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            try {
                manager.cancel(alarmToken(context));
            } catch (RuntimeException ignored) {
                // A late alarm still checks the stored active flag before doing any work.
            }
        }
        storage(context).edit().remove("planned_for").apply();
    }

    private static boolean isOnline(Context context) {
        try {
            ConnectivityManager network = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (network == null) return false;
            android.net.Network activeNetwork = network.getActiveNetwork();
            if (activeNetwork == null) return false;
            NetworkCapabilities capabilities =
                    network.getNetworkCapabilities(activeNetwork);
            return capabilities != null
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private static boolean isPowerSaving(Context context) {
        try {
            PowerManager power = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            return power != null && power.isPowerSaveMode();
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private static long chooseDelay(Context context, boolean online) {
        BatteryManager battery =
                (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        boolean charging = false;
        int level = -1;
        try {
            if (battery != null) {
                charging = battery.isCharging();
                level = battery.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            }
        } catch (RuntimeException ignored) {
            // Keep the default interval if the ROM does not expose battery state reliably.
        }
        return SchedulePolicy.delay(charging, level, isPowerSaving(context), online);
    }
}
