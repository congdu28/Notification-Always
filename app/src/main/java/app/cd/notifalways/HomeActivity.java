package app.cd.notifalways;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public final class HomeActivity extends Activity {
    private static final int PAGE = Color.rgb(243, 247, 246);
    private static final int TEXT = Color.rgb(24, 44, 41);
    private static final int SECONDARY = Color.rgb(99, 117, 113);
    private static final int ACCENT = Color.rgb(20, 125, 115);
    private static final int INACTIVE_CARD = Color.rgb(77, 91, 99);
    private static final int INACTIVE_BUTTON = Color.rgb(224, 242, 239);

    private TextView stateLine;
    private TextView report;
    private TextView schedule;
    private Button toggleButton;
    private LinearLayout statusCard;

    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        getWindow().setStatusBarColor(PAGE);
        getWindow().setNavigationBarColor(PAGE);
        int systemUiFlags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            systemUiFlags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        getWindow().getDecorView().setSystemUiVisibility(systemUiFlags);

        ScrollView scroller = new ScrollView(this);
        scroller.setFillViewport(true);
        scroller.setBackgroundColor(PAGE);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        int inset = dp(22);
        content.setPadding(inset, dp(20), inset, dp(32));

        TextView kicker = makeText("THÔNG BÁO TRÊN ANDROID NỘI ĐỊA", 11, ACCENT);
        kicker.setTypeface(null, Typeface.BOLD);
        content.addView(kicker);

        TextView heading = makeText("Notification Always", 29, TEXT);
        heading.setTypeface(null, Typeface.BOLD);
        content.addView(heading, withTop(4));
        content.addView(makeText("Hỗ trợ duy trì kết nối thông báo trên điện thoại Xiaomi, OPPO, vivo và các ROM quản lý pin mạnh.", 15, SECONDARY), withTop(12));

        statusCard = new LinearLayout(this);
        statusCard.setOrientation(LinearLayout.VERTICAL);
        statusCard.setPadding(dp(18), dp(18), dp(18), dp(18));
        content.addView(statusCard, withTop(16));
        stateLine = makeText("", 18, Color.WHITE);
        stateLine.setTypeface(null, Typeface.BOLD);
        statusCard.addView(stateLine);
        schedule = makeText("", 13, Color.rgb(217, 249, 242));
        statusCard.addView(schedule, withTop(7));

        toggleButton = new Button(this);
        toggleButton.setAllCaps(false);
        toggleButton.setTextSize(16);
        toggleButton.setTypeface(null, Typeface.BOLD);
        toggleButton.setMinHeight(dp(54));
        toggleButton.setPadding(dp(18), dp(8), dp(18), dp(8));
        toggleButton.setElevation(dp(2));
        toggleButton.setOnClickListener(view -> {
            PulseController.setActive(this, !PulseController.isActive(this));
            updatePage();
        });
        statusCard.addView(toggleButton, withTop(12));

        addHeader(content, "Thiết lập trên điện thoại");
        content.addView(makeText("1. Cho phép Notification Always chạy nền và tắt tối ưu pin cho ứng dụng.", 14, SECONDARY), withTop(4));
        addAction(content, "Mở cài đặt pin của ứng dụng", view -> launch(DeviceGuidance.batteryAccess(this)));
        addAction(content, "Mở mục Tự khởi chạy", view -> openStartup());

        content.addView(makeText("2. Mở phần thông tin Google Play Services, chọn pin Không hạn chế và cho phép tự khởi chạy nếu ROM có tùy chọn này.", 14, SECONDARY), withTop(12));
        addAction(content, "Cài đặt Google Play Services", view ->
                launch(DeviceGuidance.appInfo(this, PulseController.gmsPackage())));

        addHeader(content, "Trạng thái");
        report = makeText("", 14, SECONDARY);
        content.addView(report, withTop(4));
        addAction(content, "Kiểm tra ngay", view -> {
            PulseController.requestImmediateCheck(this);
            updatePage();
            Toast.makeText(this, "Đã yêu cầu Google Play Services kiểm tra kết nối.", Toast.LENGTH_SHORT).show();
        });

        addHeader(content, "Cách dùng");
        content.addView(makeText("Bật kiểm tra ở nút trạng thái phía trên. Cho phép tự khởi chạy các ứng dụng chat hoặc ngân hàng cần nhận tin, đồng thời khóa chúng trong màn hình đa nhiệm nếu điện thoại có tùy chọn này.", 14, SECONDARY), withTop(4));
        content.addView(makeText("Lịch kiểm tra tự điều chỉnh khoảng 15–30 phút theo pin và mạng. ROM vẫn có thể trì hoãn alarm nền hoặc đóng ứng dụng đích; ứng dụng không bảo đảm thông báo tức thời.", 13, SECONDARY), withTop(9));
        content.addView(makeText("Ứng dụng không yêu cầu quyền Internet và không gửi dữ liệu chẩn đoán ra ngoài điện thoại.", 13, SECONDARY), withTop(9));

        scroller.addView(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            scroller.setOnApplyWindowInsetsListener((view, windowInsets) -> {
                android.graphics.Insets safeInsets = windowInsets.getInsets(
                        WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                view.setPadding(safeInsets.left, safeInsets.top, safeInsets.right, safeInsets.bottom);
                return windowInsets;
            });
        }

        FrameLayout root = new FrameLayout(this);
        FrameLayout.LayoutParams scrollParams = new FrameLayout.LayoutParams(-1, -1);
        float screenWidthDp = getResources().getDisplayMetrics().widthPixels
                / getResources().getDisplayMetrics().density;
        if (screenWidthDp > 760f) {
            scrollParams.width = dp(720);
            scrollParams.gravity = Gravity.CENTER_HORIZONTAL;
        }
        root.addView(scroller, scrollParams);
        setContentView(root);
        PulseController.scheduleNext(this);
        updatePage();
    }

    @Override protected void onResume() {
        super.onResume();
        if (stateLine != null) updatePage();
    }

    private void updatePage() {
        boolean active = PulseController.isActive(this);
        if (statusCard != null) {
            statusCard.setBackgroundColor(active ? ACCENT : INACTIVE_CARD);
        }
        if (toggleButton != null) {
            toggleButton.setText(active ? "Tạm dừng kiểm tra" : "Bật kiểm tra");
            toggleButton.setTextColor(active ? ACCENT : TEXT);
            toggleButton.setBackgroundTintList(ColorStateList.valueOf(
                    active ? Color.WHITE : INACTIVE_BUTTON));
            toggleButton.setContentDescription(active
                    ? "Đang bật. Chạm để tạm dừng kiểm tra kết nối."
                    : "Đang tắt. Chạm để bật kiểm tra kết nối.");
        }

        long next = PulseController.storage(this).getLong("planned_for", 0);
        long last = PulseController.storage(this).getLong("checked_at", 0);
        stateLine.setText(active ? "Kiểm tra kết nối đang bật" : "Đang tạm dừng");
        schedule.setText(active
                ? "Dự kiến lần sau: " + (next == 0 ? "đang xếp lịch" : DateFormat.format("HH:mm", next))
                    + " · Chu kỳ " + PulseController.delayLabel(this)
                : "Bật lại để chạy kiểm tra định kỳ.");

        boolean excluded = false;
        PowerManager battery = (PowerManager) getSystemService(POWER_SERVICE);
        if (battery != null) excluded = battery.isIgnoringBatteryOptimizations(getPackageName());
        String servicesVersion;
        try {
            PackageInfo gms = getPackageManager().getPackageInfo(PulseController.gmsPackage(), 0);
            servicesVersion = gms.versionName == null ? "Đã cài" : gms.versionName;
        } catch (Exception absent) {
            servicesVersion = "Chưa phát hiện";
        }

        report.setText("Thiết bị: " + DeviceGuidance.manufacturerLabel()
                + "\nModel: " + android.os.Build.MODEL + " · Android " + android.os.Build.VERSION.RELEASE
                + "\nGoogle Play Services: " + servicesVersion
                + "\nTối ưu pin cho app: " + (excluded ? "đã bỏ qua" : "đang bật")
                + "\nKiểm tra gần nhất: " + (last == 0 ? "chưa có" : DateFormat.format("HH:mm:ss", last)));
    }

    private void openStartup() {
        for (Intent target : DeviceGuidance.startupScreens()) {
            if (tryLaunch(target)) return;
        }
        launch(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                android.net.Uri.parse("package:" + getPackageName())));
    }

    private void launch(Intent target) {
        if (!tryLaunch(target)) Toast.makeText(this, "Không tìm thấy trang cài đặt trên ROM này.", Toast.LENGTH_SHORT).show();
    }

    private boolean tryLaunch(Intent target) {
        try {
            target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(target);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void addHeader(LinearLayout parent, String title) {
        TextView text = makeText(title, 18, TEXT);
        text.setTypeface(null, Typeface.BOLD);
        parent.addView(text, withTop(20));
    }

    private void addAction(LinearLayout parent, String title, View.OnClickListener callback) {
        Button button = new Button(this);
        button.setText(title);
        button.setAllCaps(false);
        button.setTextColor(ACCENT);
        button.setOnClickListener(callback);
        parent.addView(button, withTop(6));
    }

    private TextView makeText(String value, int size, int color) {
        TextView field = new TextView(this);
        field.setText(value);
        field.setTextSize(size);
        field.setTextColor(color);
        field.setLineSpacing(dp(2), 1f);
        return field;
    }

    private LinearLayout.LayoutParams withTop(int margin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dp(margin);
        return params;
    }

    private int dp(int px) {
        return (int) (px * getResources().getDisplayMetrics().density + 0.5f);
    }
}
