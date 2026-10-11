package app.cd.notifalways;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class SchedulePolicyTest {
    @Test public void lowBatteryBoundaryAndUnknownLevel() {
        assertEquals(30 * 60_000L, SchedulePolicy.delay(false, 14, false, false));
        assertEquals(20 * 60_000L, SchedulePolicy.delay(false, 15, false, false));
        assertEquals(20 * 60_000L, SchedulePolicy.delay(false, -1, false, false));
    }

    @Test public void chargingHasPriorityOverBatterySaver() {
        assertEquals(15 * 60_000L, SchedulePolicy.delay(true, 5, true, false));
    }

    @Test public void powerSaverAndCellularChooseExpectedIntervals() {
        assertEquals(30 * 60_000L, SchedulePolicy.delay(false, 80, true, true));
        assertEquals(15 * 60_000L, SchedulePolicy.delay(false, 80, false, true));
    }
}
