package app.cd.notifalways;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
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
        content.addView(makeText("Kiểm tra các điều kiện ảnh hưởng tới thông báo trên ROM nội địa và hướng dẫn chỉnh cài đặt nền.", 15, SECONDARY), withTop(12));

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

        addHeader(content, "Chẩn đoán trên máy");
        report = makeText("", 14, SECONDARY);
        content.addView(report, withTop(4));
        addAction(content, "Làm mới chẩn đoán", view -> updatePage());

        addHeader(content, "Thiết lập thông báo");
        content.addView(makeText("1. Kiểm tra Google Play Services và Google Play Store bên trên. Bật Google Play Services, cho phép chạy nền và chọn pin Không hạn chế nếu ROM có tùy chọn.", 14, SECONDARY), withTop(4));
        addAction(content, "Cài đặt Google Play Services", view ->
                launch(DeviceGuidance.appInfo(this, PulseController.gmsPackage())));

        content.addView(makeText("2. Với từng ứng dụng chat, email hoặc ngân hàng: bật quyền Thông báo, cho phép chạy nền và Tự khởi chạy trong cài đặt ROM.", 14, SECONDARY), withTop(12));
        addAction(content, "Mở mục Tự khởi chạy", view -> openStartup());

        content.addView(makeText("3. Nếu dùng thử nghiệm định kỳ, bạn có thể cho phép Notification Always bỏ qua tối ưu pin để alarm ít bị trì hoãn hơn. Tùy chọn này có thể làm tăng tiêu thụ pin.", 14, SECONDARY), withTop(12));
        addAction(content, "Cho phép bỏ qua tối ưu pin", view -> launch(DeviceGuidance.batteryAccess(this)));

        addHeader(content, "Thử nghiệm tín hiệu");
        content.addView(makeText("App gửi yêu cầu tới Google Play Services nhưng không nhận xác nhận kết nối FCM. Hãy so sánh độ trễ thông báo trên cùng điện thoại khi bật và tắt thử nghiệm.", 14, SECONDARY), withTop(4));
        addAction(content, "Thử gửi tín hiệu ngay", view -> {
            PulseController.requestImmediateCheck(this);
            updatePage();
            Toast.makeText(this, "Đã thử gửi yêu cầu; chưa xác nhận Google Play Services xử lý.", Toast.LENGTH_LONG).show();
        });

        content.addView(makeText("Alarm có thể bị Android/ROM trì hoãn hoặc chặn. App không đọc thông báo của ứng dụng khác và không bảo đảm thông báo đến tức thời.", 13, SECONDARY), withTop(9));
        content.addView(makeText("• App không yêu cầu quyền Internet và không gửi dữ liệu chẩn đoán ra máy chủ.", 13, SECONDARY), withTop(9));

        scroller.addView(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Api30Insets.apply(getWindow(), scroller);
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
        // Keep the screen usable even when an OEM ROM blocks alarm scheduling.
        try {
            PulseController.scheduleNext(this);
        } catch (RuntimeException ignored) {
            // Scheduling can be retried after the user changes battery/background settings.
        }
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
            toggleButton.setText(active ? "Tắt thử nghiệm định kỳ" : "Bật thử nghiệm định kỳ");
            toggleButton.setTextColor(active ? ACCENT : TEXT);
            toggleButton.setBackgroundTintList(ColorStateList.valueOf(
                    active ? Color.WHITE : INACTIVE_BUTTON));
            toggleButton.setContentDescription(active
                    ? "Đang bật. Chạm để tắt thử nghiệm gửi tín hiệu định kỳ."
                    : "Đang tắt. Chạm để bật thử nghiệm gửi tín hiệu định kỳ.");
        }

        long next = PulseController.storage(this).getLong("planned_for", 0);
        stateLine.setText(active ? "Thử nghiệm tín hiệu đang bật" : "Thử nghiệm tín hiệu đang tắt");
        schedule.setText(active
                ? (PulseController.storage(this).getBoolean("schedule_failed", false)
                    ? "Không đặt được alarm trên thiết bị này."
                    : "Alarm dự kiến: " + (next == 0 ? "đang xếp lịch" : DateFormat.format("dd/MM HH:mm", next))
                        + " · Chu kỳ " + PulseController.delayLabel(this))
                : "Không gửi tín hiệu định kỳ. Bật nếu muốn thử nghiệm.");

        long lastSignal = PulseController.storage(this).getLong("signal_sent_at", 0);
        long lastAlarm = PulseController.storage(this).getLong("alarm_fired_at", 0);
        String signalStatus = lastSignal == 0 ? "chưa thử"
                : PulseController.storage(this).getBoolean("signal_rejected", false)
                    ? "Android từ chối ít nhất một yêu cầu"
                    : "đã gửi yêu cầu; chưa xác nhận GMS xử lý";

        report.setText("Thiết bị: " + DeviceGuidance.manufacturerLabel()
                + "\nModel: " + android.os.Build.MODEL + " · Android " + android.os.Build.VERSION.RELEASE
                + "\nGoogle Play Services: " + DeviceDiagnostics.playServices(this)
                + "\nGoogle Play Store: " + DeviceDiagnostics.playStore(this)
                + "\nHạn chế nền của app: " + DeviceDiagnostics.backgroundRestriction(this)
                + "\nTối ưu pin cho app: " + DeviceDiagnostics.batteryOptimization(this)
                + "\nAlarm chạy gần nhất: " + (lastAlarm == 0 ? "chưa có" : DateFormat.format("dd/MM HH:mm:ss", lastAlarm))
                + "\nThử gửi tín hiệu: " + (lastSignal == 0 ? "chưa có" : DateFormat.format("dd/MM HH:mm:ss", lastSignal))
                + "\nKết quả gửi: " + signalStatus
                + "\nLưu ý: các trạng thái trên không xác nhận FCM nhận được tin.");
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
