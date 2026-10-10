package app.cd.notifalways;

import android.graphics.Insets;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;

/** APIs introduced in Android 11, isolated so older Android versions can load HomeActivity. */
final class Api30Insets {
    private Api30Insets() {}

    static void apply(Window window, View content) {
        window.setDecorFitsSystemWindows(false);
        content.setOnApplyWindowInsetsListener((view, windowInsets) -> {
            Insets safeInsets = windowInsets.getInsets(
                    WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
            view.setPadding(safeInsets.left, safeInsets.top, safeInsets.right, safeInsets.bottom);
            return windowInsets;
        });
    }
}
