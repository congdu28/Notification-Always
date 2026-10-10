package app.cd.notifalways;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class DeviceGuidance {
    private DeviceGuidance() {}

    static String manufacturerLabel() {
        String brand = (android.os.Build.MANUFACTURER + " " + android.os.Build.BRAND)
                .toLowerCase(Locale.ROOT);
        if (brand.contains("xiaomi") || brand.contains("redmi") || brand.contains("poco")) {
            return "Xiaomi / Redmi / POCO";
        }
        if (brand.contains("oppo") || brand.contains("realme") || brand.contains("oneplus")) {
            return "OPPO / realme / OnePlus";
        }
        if (brand.contains("vivo") || brand.contains("iqoo")) return "vivo / iQOO";
        if (brand.contains("honor")) return "HONOR";
        if (brand.contains("huawei")) return "Huawei";
        return android.os.Build.MANUFACTURER;
    }

    static Intent batteryAccess(Context context) {
        return new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                Uri.parse("package:" + context.getPackageName()));
    }

    static Intent appInfo(Context context, String packageName) {
        return new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + packageName));
    }

    static List<Intent> startupScreens() {
        String[][] components = {
                {"com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"},
                {"com.coloros.safecenter", "com.coloros.permissionstartup.PermissionStartupActivity"},
                {"com.oplus.safecenter", "com.oplus.safecenter.permission.startup.StartupAppListActivity"},
                {"com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"},
                {"com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"},
                {"com.hihonor.systemmanager", "com.hihonor.systemmanager.startupmgr.ui.StartupNormalAppListActivity"},
                {"com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"}
        };
        List<Intent> result = new ArrayList<>();
        for (String[] component : components) {
            result.add(new Intent().setComponent(new ComponentName(component[0], component[1])));
        }
        return result;
    }
}
