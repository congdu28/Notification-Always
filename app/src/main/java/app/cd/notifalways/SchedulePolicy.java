package app.cd.notifalways;

/** Chooses the next experimental signal interval from current device conditions. */
final class SchedulePolicy {
    private static final long MINUTE = 60_000L;

    private SchedulePolicy() {}

    static long delay(boolean charging, int batteryLevel, boolean powerSave, boolean cellular) {
        if (charging) return 15 * MINUTE;
        if ((batteryLevel >= 0 && batteryLevel < 15) || powerSave) return 30 * MINUTE;
        if (cellular) return 15 * MINUTE;
        return 20 * MINUTE;
    }
}
