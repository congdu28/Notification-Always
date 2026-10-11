package app.cd.notifalways;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

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
            int status = GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(context);
            return version + " · " + availabilityLabel(status);
        } catch (RuntimeException ignored) {
            return version + " · chưa xác định khả dụng";
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

    private static String availabilityLabel(int status) {
        switch (status) {
            case ConnectionResult.SUCCESS:
                return "API Google Play khả dụng";
            case ConnectionResult.SERVICE_MISSING:
                return "thiếu dịch vụ";
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                return "cần cập nhật";
            case ConnectionResult.SERVICE_DISABLED:
                return "đang bị tắt";
            case ConnectionResult.SERVICE_INVALID:
                return "không hợp lệ";
            case ConnectionResult.SERVICE_UPDATING:
                return "đang cập nhật";
            default:
                return "API Google Play báo lỗi " + status;
        }
    }
}
