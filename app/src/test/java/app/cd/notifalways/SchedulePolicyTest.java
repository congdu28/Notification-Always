package app.cd.notifalways;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class SchedulePolicyTest {
    @Test public void lowBatteryBoundaryAndUnknownLevel() {
        assertEquals(120 * 60_000L, SchedulePolicy.delay(false, 14, false, true));
        assertEquals(60 * 60_000L, SchedulePolicy.delay(false, 15, false, true));
        assertEquals(60 * 60_000L, SchedulePolicy.delay(false, -1, false, true));
    }

    @Test public void chargingHasPriorityOverBatterySaver() {
        assertEquals(120 * 60_000L, SchedulePolicy.delay(true, 5, true, true));
    }

    @Test public void powerSaverAndOfflineChooseLongerIntervals() {
        assertEquals(120 * 60_000L, SchedulePolicy.delay(false, 80, true, true));
        assertEquals(120 * 60_000L, SchedulePolicy.delay(false, 80, false, false));
        assertEquals(30 * 60_000L, SchedulePolicy.delay(true, 80, false, true));
    }
}
