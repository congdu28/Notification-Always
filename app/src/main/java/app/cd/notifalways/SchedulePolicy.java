package app.cd.notifalways;

/** Chooses the next experimental signal interval from current device conditions. */
final class SchedulePolicy {
    private static final long MINUTE = 60_000L;

    private SchedulePolicy() {}

    static long delay(boolean charging, int batteryLevel, boolean powerSave, boolean online) {
        if (!online || powerSave || (batteryLevel >= 0 && batteryLevel < 15)) {
            return 120 * MINUTE;
        }
        return charging ? 30 * MINUTE : 60 * MINUTE;
    }
}
