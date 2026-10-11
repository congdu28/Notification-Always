package app.cd.notifalways;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;

/** Read-only checks. None of these checks confirms that FCM is receiving messages. */
final class DeviceDiagnostics {
    private static final String PLAY_STORE = "com.android.vending";

    private DeviceDiagnostics() {}

    static String playServices(Context context) {
        String version;
        try {
            PackageInfo info = context.getPackageManager()
                    .getPackageInfo(PulseController.gmsPackage(), 0);
            version = info.versionName == null ? "đã cài" : "bản " + info.versionName;
        } catch (PackageManager.NameNotFoundException absent) {
            return "chưa cài";
        } catch (RuntimeException ignored) {
            return "không xác định";
        }

        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(PulseController.gmsPackage(), 0);
            return version + (info.enabled ? " · đang bật" : " · đang tắt");
        } catch (PackageManager.NameNotFoundException absent) {
            return "chưa cài";
        } catch (RuntimeException ignored) {
            return version + " · chưa xác định trạng thái";
        }
    }

    static String playStore(Context context) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(PLAY_STORE, 0);
            return info.enabled ? "đã cài và bật" : "đã cài nhưng bị tắt";
        } catch (PackageManager.NameNotFoundException absent) {
            return "chưa cài";
        } catch (RuntimeException ignored) {
            return "không xác định";
        }
    }

    static String backgroundRestriction(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return "Android chưa hỗ trợ kiểm tra";
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) return "không xác định";
        try {
            return manager.isBackgroundRestricted()
                    ? "đang bị Android hạn chế"
                    : "Android không báo hạn chế";
        } catch (RuntimeException ignored) {
            return "không xác định";
        }
    }

    static String batteryOptimization(Context context) {
        PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (manager == null) return "không xác định";
        try {
            return manager.isIgnoringBatteryOptimizations(context.getPackageName())
                    ? "đã bỏ qua"
                    : "đang áp dụng";
        } catch (RuntimeException ignored) {
            return "không xác định";
        }
    }
}
