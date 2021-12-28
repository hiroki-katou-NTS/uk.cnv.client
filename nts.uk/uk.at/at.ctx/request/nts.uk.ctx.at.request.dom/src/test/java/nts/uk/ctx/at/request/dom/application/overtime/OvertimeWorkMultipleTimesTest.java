package nts.uk.ctx.at.request.dom.application.overtime;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.adapter.CalculationParams;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppStandardReasonCode;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.worktime.common.AbolishAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PrescribedTimezoneSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.TimezoneUse;
import nts.uk.ctx.at.shared.dom.worktime.predset.UseSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.*;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.shr.com.time.TimeWithDayAttr;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JMockit.class)
public class OvertimeWorkMultipleTimesTest {

    @Injectable
    private OvertimeWorkMultipleTimes.Require require;

    @Test
    public void testGetter() {
        OvertimeWorkMultipleTimes target = new OvertimeWorkMultipleTimes(
                Collections.emptyList(),
                Collections.emptyList()
        );

        NtsAssert.invokeGetters(target);
    }

    @Test
    public void testCreateSuccess() {
        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(900), new TimeWithDayAttr(1020))),
                        new OvertimeHour(new OvertimeNumber(2), new TimeSpanForCalc(new TimeWithDayAttr(1050), new TimeWithDayAttr(1080)))
                ),
                Arrays.asList(
                        new OvertimeReason(new OvertimeNumber(1), Optional.of(new AppStandardReasonCode(1)), Optional.empty()),
                        new OvertimeReason(new OvertimeNumber(2), Optional.of(new AppStandardReasonCode(1)), Optional.empty())
                )
        );

        assertThat(target.getOvertimeHours().size()).isEqualTo(2);
        assertThat(target.getOvertimeReasons().size()).isEqualTo(2);
    }

    @Test
    public void testCreateFail1() {
        NtsAssert.businessException("Msg_3238", () -> OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(480), new TimeWithDayAttr(1020))),
                        new OvertimeHour(new OvertimeNumber(2), new TimeSpanForCalc(new TimeWithDayAttr(480), new TimeWithDayAttr(1020)))
                ),
                Collections.emptyList()
        ));
    }

    @Test
    public void testCreateFixedReasonEmpty() {
        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Collections.emptyList(),
                Collections.emptyList()
        );

        assertThat(target.createFixedReason()).isEqualTo(Optional.empty());
    }

    @Test
    public void testCreateFixedReasonPresent() {
        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Collections.emptyList(),
                Arrays.asList(
                        new OvertimeReason(new OvertimeNumber(1), Optional.of(new AppStandardReasonCode(1)), Optional.empty()),
                        new OvertimeReason(new OvertimeNumber(2), Optional.of(new AppStandardReasonCode(2)), Optional.empty())
                )
        );

        assertThat(target.createFixedReason()).isEqualTo(Optional.of(new AppStandardReasonCode(1)));
    }

    @Test
    public void testCreateApplyReasonEmpty1() {
        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Collections.emptyList(),
                Collections.emptyList()
        );

        assertThat(target.createApplyReason()).isEqualTo(Optional.empty());
    }

    @Test
    public void testCreateApplyReasonEmpty2() {
        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Collections.emptyList(),
                Arrays.asList(
                        new OvertimeReason(new OvertimeNumber(1), Optional.of(new AppStandardReasonCode(1)), Optional.empty()),
                        new OvertimeReason(new OvertimeNumber(2), Optional.of(new AppStandardReasonCode(2)), Optional.empty())
                )
        );

        assertThat(target.createApplyReason()).isEqualTo(Optional.empty());
    }

    @Test
    public void testCreateApplyReasonPresent() {
        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Collections.emptyList(),
                Arrays.asList(
                        new OvertimeReason(new OvertimeNumber(1), Optional.empty(), Optional.of(new AppReason("test 1"))),
                        new OvertimeReason(new OvertimeNumber(2), Optional.empty(), Optional.of(new AppReason("test 2"))),
                        new OvertimeReason(new OvertimeNumber(3), Optional.of(new AppStandardReasonCode(1)), Optional.empty())
                )
        );

        assertThat(target.createApplyReason()).isEqualTo(Optional.of(new AppReason("test 1/test 2")));
    }

    @Test
    public void testGetWorkingHoursToCalculateOvertime1() {
        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(400), new TimeWithDayAttr(600))),
                        new OvertimeHour(new OvertimeNumber(2), new TimeSpanForCalc(new TimeWithDayAttr(900), new TimeWithDayAttr(1000)))
                ),
                Arrays.asList(
                        new OvertimeReason(new OvertimeNumber(1), Optional.of(new AppStandardReasonCode(1)), Optional.empty()),
                        new OvertimeReason(new OvertimeNumber(2), Optional.of(new AppStandardReasonCode(1)), Optional.empty())
                )
        );

        List<TimeZoneWithWorkNo> timeZones = Arrays.asList(new TimeZoneWithWorkNo(1, 500, 900));

        List<TimeZoneWithWorkNo> result = target.getWorkingHoursToCalculateOvertime(timeZones);

        assertThat(result.get(0).getTimeZone().getStartTime().v()).isEqualTo(400);
        assertThat(result.get(0).getTimeZone().getEndTime().v()).isEqualTo(1000);
    }

    @Test
    public void testGetWorkingHoursToCalculateOvertime1_2() {
        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(600), new TimeWithDayAttr(650))),
                        new OvertimeHour(new OvertimeNumber(2), new TimeSpanForCalc(new TimeWithDayAttr(800), new TimeWithDayAttr(850)))
                ),
                Collections.emptyList()
        );

        List<TimeZoneWithWorkNo> timeZones = Arrays.asList(new TimeZoneWithWorkNo(1, 500, 900));

        List<TimeZoneWithWorkNo> result = target.getWorkingHoursToCalculateOvertime(timeZones);

        assertThat(result.get(0).getTimeZone().getStartTime().v()).isEqualTo(500);
        assertThat(result.get(0).getTimeZone().getEndTime().v()).isEqualTo(900);
    }

    @Test
    public void testGetWorkingHoursToCalculateOvertime2() {
        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(400), new TimeWithDayAttr(600))),
                        new OvertimeHour(new OvertimeNumber(2), new TimeSpanForCalc(new TimeWithDayAttr(900), new TimeWithDayAttr(1000)))
                ),
                Arrays.asList(
                        new OvertimeReason(new OvertimeNumber(1), Optional.of(new AppStandardReasonCode(1)), Optional.empty()),
                        new OvertimeReason(new OvertimeNumber(2), Optional.of(new AppStandardReasonCode(1)), Optional.empty())
                )
        );

        List<TimeZoneWithWorkNo> timeZones = Arrays.asList(
                new TimeZoneWithWorkNo(1, 300, 500),
                new TimeZoneWithWorkNo(2, 700, 930)
        );

        List<TimeZoneWithWorkNo> result = target.getWorkingHoursToCalculateOvertime(timeZones);
        assertThat(result.get(0).getTimeZone().getStartTime().v()).isEqualTo(300);
        assertThat(result.get(0).getTimeZone().getEndTime().v()).isEqualTo(600);
        assertThat(result.get(1).getTimeZone().getStartTime().v()).isEqualTo(700);
        assertThat(result.get(1).getTimeZone().getEndTime().v()).isEqualTo(1000);
    }

    @Test
    public void testGetWorkingHoursToCalculateOvertime3() {
        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(600), new TimeWithDayAttr(650))),
                        new OvertimeHour(new OvertimeNumber(2), new TimeSpanForCalc(new TimeWithDayAttr(800), new TimeWithDayAttr(850)))
                ),
                Collections.emptyList()
        );

        List<TimeZoneWithWorkNo> timeZones = Arrays.asList(
                new TimeZoneWithWorkNo(1, 300, 500),
                new TimeZoneWithWorkNo(2, 700, 930)
        );

        List<TimeZoneWithWorkNo> result = target.getWorkingHoursToCalculateOvertime(timeZones);
        assertThat(result.get(0).getTimeZone().getStartTime().v()).isEqualTo(300);
        assertThat(result.get(0).getTimeZone().getEndTime().v()).isEqualTo(650);
        assertThat(result.get(1).getTimeZone().getStartTime().v()).isEqualTo(700);
        assertThat(result.get(1).getTimeZone().getEndTime().v()).isEqualTo(930);
    }

    @Test
    public void testGetWorkingHoursToCalculateOvertime4() {
        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(200), new TimeWithDayAttr(250)))
                ),
                Collections.emptyList()
        );

        List<TimeZoneWithWorkNo> timeZones = Arrays.asList(
                new TimeZoneWithWorkNo(1, 300, 500),
                new TimeZoneWithWorkNo(2, 700, 930)
        );

        List<TimeZoneWithWorkNo> result = target.getWorkingHoursToCalculateOvertime(timeZones);
        assertThat(result.get(0).getTimeZone().getStartTime().v()).isEqualTo(200);
        assertThat(result.get(0).getTimeZone().getEndTime().v()).isEqualTo(500);
        assertThat(result.get(1).getTimeZone().getStartTime().v()).isEqualTo(700);
        assertThat(result.get(1).getTimeZone().getEndTime().v()).isEqualTo(930);
    }

    @Test
    public void testGetWorkingHoursToCalculateOvertime5() {
        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(800), new TimeWithDayAttr(850)))
                ),
                Collections.emptyList()
        );

        List<TimeZoneWithWorkNo> timeZones = Arrays.asList(
                new TimeZoneWithWorkNo(1, 300, 500),
                new TimeZoneWithWorkNo(2, 700, 930)
        );

        List<TimeZoneWithWorkNo> result = target.getWorkingHoursToCalculateOvertime(timeZones);
        assertThat(result.get(0).getTimeZone().getStartTime().v()).isEqualTo(300);
        assertThat(result.get(0).getTimeZone().getEndTime().v()).isEqualTo(500);
        assertThat(result.get(1).getTimeZone().getStartTime().v()).isEqualTo(700);
        assertThat(result.get(1).getTimeZone().getEndTime().v()).isEqualTo(930);
    }

    @Test
    public void testGetBreakTimeToCalculateOvertime1() {
        WorkInformation workInfo = new WorkInformation("001", null);
        List<TimeZoneWithWorkNo> workingHours = Arrays.asList(new TimeZoneWithWorkNo(1, 510, 1050)); // 勤務時間：　8:30~17:30　（始業終業時刻）
        List<BreakTimeSheet> breakTimes = new ArrayList<>();
        breakTimes.add(new BreakTimeSheet(new BreakFrameNo(1), new TimeWithDayAttr(720), new TimeWithDayAttr(780))); // 休憩時間帯：12:00~13:00
        breakTimes.add(new BreakTimeSheet(new BreakFrameNo(2), new TimeWithDayAttr(1050), new TimeWithDayAttr(1080))); // 休憩時間帯：17:30~18:00

        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(420), new TimeWithDayAttr(480))), // 残業時間：	7:00~8:00
                        new OvertimeHour(new OvertimeNumber(2), new TimeSpanForCalc(new TimeWithDayAttr(1170), new TimeWithDayAttr(1230))), // 残業時間：19:30~20:30
                        new OvertimeHour(new OvertimeNumber(3), new TimeSpanForCalc(new TimeWithDayAttr(1260), new TimeWithDayAttr(1320))) // 残業時間：	21:00~22:00
                ),
                Collections.emptyList()
        );

        List<BreakTimeSheet> result = target.getBreakTimeToCalculateOvertime(require, "", "", GeneralDate.today(), workInfo, workingHours, breakTimes);

        assertThat(result.size()).isEqualTo(4);
        // 休憩枠NO1：8:00~8:30
        assertThat(result.get(0).getStartTime().v()).isEqualTo(480);
        assertThat(result.get(0).getEndTime().v()).isEqualTo(510);

        // 休憩枠NO2：12:00~13:00
        assertThat(result.get(1).getStartTime().v()).isEqualTo(720);
        assertThat(result.get(1).getEndTime().v()).isEqualTo(780);

        // 休憩枠NO3：17:30~19:30
        assertThat(result.get(2).getStartTime().v()).isEqualTo(1050);
        assertThat(result.get(2).getEndTime().v()).isEqualTo(1170);

        // 休憩枠NO4：20:30~21:00
        assertThat(result.get(3).getStartTime().v()).isEqualTo(1230);
        assertThat(result.get(3).getEndTime().v()).isEqualTo(1260);
    }

    @Test
    public void testGetBreakTimeToCalculateOvertime2() {
        WorkInformation workInfo = new WorkInformation("001", null);
        List<TimeZoneWithWorkNo> workingHours = Arrays.asList(new TimeZoneWithWorkNo(1, 510, 1050)); // 勤務時間：　8:30~17:30　（始業終業時刻）
        List<BreakTimeSheet> breakTimes = new ArrayList<>();
        breakTimes.add(new BreakTimeSheet(new BreakFrameNo(1), new TimeWithDayAttr(720), new TimeWithDayAttr(780))); // 休憩時間帯：12:00~13:00
        breakTimes.add(new BreakTimeSheet(new BreakFrameNo(2), new TimeWithDayAttr(1050), new TimeWithDayAttr(1080))); // 休憩時間帯：17:30~18:00

        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(1050), new TimeWithDayAttr(1230))) //残業時間：	17:30~20:30
                ),
                Collections.emptyList()
        );

        List<BreakTimeSheet> result = target.getBreakTimeToCalculateOvertime(require, "", "", GeneralDate.today(), workInfo, workingHours, breakTimes);

        assertThat(result.size()).isEqualTo(2);
        // 休憩枠NO1：12:00~13:00
        assertThat(result.get(0).getStartTime().v()).isEqualTo(720);
        assertThat(result.get(0).getEndTime().v()).isEqualTo(780);

        // 休憩枠NO2：17:30~18:00
        assertThat(result.get(1).getStartTime().v()).isEqualTo(1050);
        assertThat(result.get(1).getEndTime().v()).isEqualTo(1080);
    }

    @Test
    public void testGetBreakTimeToCalculateOvertime3() {
        WorkInformation workInfo = new WorkInformation("001", "002");
        List<TimeZoneWithWorkNo> workingHours = Arrays.asList(new TimeZoneWithWorkNo(1, 510, 1050)); // 勤務時間：　8:30~17:30　（始業終業時刻）
        List<BreakTimeSheet> breakTimes = new ArrayList<>();
        breakTimes.add(new BreakTimeSheet(new BreakFrameNo(1), new TimeWithDayAttr(720), new TimeWithDayAttr(780))); // 休憩時間帯：12:00~13:00
        breakTimes.add(new BreakTimeSheet(new BreakFrameNo(2), new TimeWithDayAttr(1050), new TimeWithDayAttr(1080))); // 休憩時間帯：17:30~18:00

        CalculationParams calculationParams = new CalculationParams(
                "",
                GeneralDate.today(),
                new WorkTypeCode("001"),
                new WorkTimeCode("002"),
                workingHours,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        new Expectations() {
            {
                require.getWorkTimeSetting("", "002");
                result = Optional.of(new WorkTimeSetting(
                        "",
                        new WorkTimeCode("002"),
                        new WorkTimeDivision(WorkTimeDailyAtr.REGULAR_WORK, WorkTimeMethodSet.FLOW_WORK),
                        AbolishAtr.NOT_ABOLISH,
                        new WorkTimeDisplayName(new WorkTimeName("AAA"), new WorkTimeAbName("BBB")),
                        null,
                        null
                ));

                require.tempCalculateOneDayAttendanceTime(calculationParams);
                result = new IntegrationOfDaily(
                        "",
                        GeneralDate.today(),
                        null,
                        null,
                        null,
                        Optional.empty(),
                        new ArrayList<>(),
                        Optional.empty(),
                        new BreakTimeOfDailyAttd(breakTimes),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        new ArrayList<>(),
                        Optional.empty(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        Optional.empty()
                );
            }
        };

        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(420), new TimeWithDayAttr(480))), // 残業時間：	7:00~8:00
                        new OvertimeHour(new OvertimeNumber(2), new TimeSpanForCalc(new TimeWithDayAttr(1170), new TimeWithDayAttr(1230))), // 残業時間：19:30~20:30
                        new OvertimeHour(new OvertimeNumber(3), new TimeSpanForCalc(new TimeWithDayAttr(1260), new TimeWithDayAttr(1320))) // 残業時間：	21:00~22:00
                ),
                Collections.emptyList()
        );

        List<BreakTimeSheet> result = target.getBreakTimeToCalculateOvertime(require, "", "", GeneralDate.today(), workInfo, workingHours, new ArrayList<>());

        assertThat(result.size()).isEqualTo(4);
        // 休憩枠NO1：8:00~8:30
        assertThat(result.get(0).getStartTime().v()).isEqualTo(480);
        assertThat(result.get(0).getEndTime().v()).isEqualTo(510);

        // 休憩枠NO2：12:00~13:00
        assertThat(result.get(1).getStartTime().v()).isEqualTo(720);
        assertThat(result.get(1).getEndTime().v()).isEqualTo(780);

        // 休憩枠NO3：17:30~19:30
        assertThat(result.get(2).getStartTime().v()).isEqualTo(1050);
        assertThat(result.get(2).getEndTime().v()).isEqualTo(1170);

        // 休憩枠NO4：20:30~21:00
        assertThat(result.get(3).getStartTime().v()).isEqualTo(1230);
        assertThat(result.get(3).getEndTime().v()).isEqualTo(1260);
    }

    @Test
    public void testGetBreakTimeToCalculateOvertime3_2() {
        WorkInformation workInfo = new WorkInformation("001", "002");
        List<TimeZoneWithWorkNo> workingHours = Arrays.asList(new TimeZoneWithWorkNo(1, 510, 1050)); // 勤務時間：　8:30~17:30　（始業終業時刻）
        List<BreakTimeSheet> breakTimes = new ArrayList<>();

        CalculationParams calculationParams = new CalculationParams(
                "",
                GeneralDate.today(),
                new WorkTypeCode("001"),
                new WorkTimeCode("002"),
                workingHours,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        new Expectations() {
            {
                require.getWorkTimeSetting("", "002");
                result = Optional.of(new WorkTimeSetting(
                        "",
                        new WorkTimeCode("002"),
                        new WorkTimeDivision(WorkTimeDailyAtr.REGULAR_WORK, WorkTimeMethodSet.FLOW_WORK),
                        AbolishAtr.NOT_ABOLISH,
                        new WorkTimeDisplayName(new WorkTimeName("AAA"), new WorkTimeAbName("BBB")),
                        null,
                        null
                ));

                require.tempCalculateOneDayAttendanceTime(calculationParams);
                result = new IntegrationOfDaily(
                        "",
                        GeneralDate.today(),
                        null,
                        null,
                        null,
                        Optional.empty(),
                        new ArrayList<>(),
                        Optional.empty(),
                        new BreakTimeOfDailyAttd(breakTimes),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        new ArrayList<>(),
                        Optional.empty(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        Optional.empty()
                );
            }
        };

        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(420), new TimeWithDayAttr(480))), // 残業時間：	7:00~8:00
                        new OvertimeHour(new OvertimeNumber(2), new TimeSpanForCalc(new TimeWithDayAttr(1170), new TimeWithDayAttr(1230))), // 残業時間：19:30~20:30
                        new OvertimeHour(new OvertimeNumber(3), new TimeSpanForCalc(new TimeWithDayAttr(1260), new TimeWithDayAttr(1320))) // 残業時間：	21:00~22:00
                ),
                Collections.emptyList()
        );

        List<BreakTimeSheet> result = target.getBreakTimeToCalculateOvertime(require, "", "", GeneralDate.today(), workInfo, workingHours, new ArrayList<>());

        assertThat(result.size()).isEqualTo(3);
        // 休憩枠NO1：8:00~8:30
        assertThat(result.get(0).getStartTime().v()).isEqualTo(480);
        assertThat(result.get(0).getEndTime().v()).isEqualTo(510);

        // 休憩枠NO2：17:30~19:30
        assertThat(result.get(1).getStartTime().v()).isEqualTo(1050);
        assertThat(result.get(1).getEndTime().v()).isEqualTo(1170);

        // 休憩枠NO3：20:30~21:00
        assertThat(result.get(2).getStartTime().v()).isEqualTo(1230);
        assertThat(result.get(2).getEndTime().v()).isEqualTo(1260);
    }

    @Test
    public void testGetBreakTimeToCalculateOvertime4() {
        WorkInformation workInfo = new WorkInformation("001", "002");
        List<TimeZoneWithWorkNo> workingHours = Arrays.asList(new TimeZoneWithWorkNo(1, 510, 1350)); // 勤務時間：　8:30~22:30　（始業終業時刻）
        List<BreakTimeSheet> breakTimes = new ArrayList<>();
        breakTimes.add(new BreakTimeSheet(new BreakFrameNo(1), new TimeWithDayAttr(720), new TimeWithDayAttr(780))); // 休憩時間帯：12:00~13:00
        breakTimes.add(new BreakTimeSheet(new BreakFrameNo(2), new TimeWithDayAttr(1050), new TimeWithDayAttr(1080))); // 休憩時間帯：17:30~18:00

        new Expectations() {
            {
                require.getPredetemineTimeSetting("", "002");
                result = Optional.of(new PredetemineTimeSetting(
                        "",
                        new AttendanceTime(540),
                        null,
                        null,
                        new PrescribedTimezoneSetting(
                                null,
                                null,
                                Arrays.asList(new TimezoneUse(new TimeWithDayAttr(510), new TimeWithDayAttr(1050), UseSetting.USE, 1)) // // 勤務時間：　8:30~17:30
                        ),
                        new TimeWithDayAttr(510),
                        true
                ));
            }
        };

        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(420), new TimeWithDayAttr(480))), // 残業時間：	7:00~8:00
                        new OvertimeHour(new OvertimeNumber(2), new TimeSpanForCalc(new TimeWithDayAttr(1170), new TimeWithDayAttr(1230))), // 残業時間：19:30~20:30
                        new OvertimeHour(new OvertimeNumber(3), new TimeSpanForCalc(new TimeWithDayAttr(1260), new TimeWithDayAttr(1320))) // 残業時間：	21:00~22:00
                ),
                Collections.emptyList()
        );

        List<BreakTimeSheet> result = target.getBreakTimeToCalculateOvertime(require, "", "", GeneralDate.today(), workInfo, workingHours, breakTimes);

        assertThat(result.size()).isEqualTo(5);
        // 休憩枠NO1：8:00~8:30
        assertThat(result.get(0).getStartTime().v()).isEqualTo(480);
        assertThat(result.get(0).getEndTime().v()).isEqualTo(510);

        // 休憩枠NO2：12:00~13:00
        assertThat(result.get(1).getStartTime().v()).isEqualTo(720);
        assertThat(result.get(1).getEndTime().v()).isEqualTo(780);

        // 休憩枠NO3：17:30~19:30
        assertThat(result.get(2).getStartTime().v()).isEqualTo(1050);
        assertThat(result.get(2).getEndTime().v()).isEqualTo(1170);

        // 休憩枠NO4：20:30~21:00
        assertThat(result.get(3).getStartTime().v()).isEqualTo(1230);
        assertThat(result.get(3).getEndTime().v()).isEqualTo(1260);

        // 休憩枠NO5：22:00~22:30
        assertThat(result.get(4).getStartTime().v()).isEqualTo(1320);
        assertThat(result.get(4).getEndTime().v()).isEqualTo(1350);
    }

    @Test
    public void testGetBreakTimeToCalculateOvertime5() {
        WorkInformation workInfo = new WorkInformation("001", "002");
        List<TimeZoneWithWorkNo> workingHours = Arrays.asList(new TimeZoneWithWorkNo(1, 390, 1350)); // 勤務時間：　6:30~22:30　（始業終業時刻）
        List<BreakTimeSheet> breakTimes = new ArrayList<>();
        breakTimes.add(new BreakTimeSheet(new BreakFrameNo(1), new TimeWithDayAttr(720), new TimeWithDayAttr(780))); // 休憩時間帯：12:00~13:00
        breakTimes.add(new BreakTimeSheet(new BreakFrameNo(2), new TimeWithDayAttr(1050), new TimeWithDayAttr(1080))); // 休憩時間帯：17:30~18:00

        new Expectations() {
            {
                require.getPredetemineTimeSetting("", "002");
                result = Optional.of(new PredetemineTimeSetting(
                        "",
                        new AttendanceTime(540),
                        null,
                        null,
                        new PrescribedTimezoneSetting(
                                null,
                                null,
                                Arrays.asList(new TimezoneUse(new TimeWithDayAttr(510), new TimeWithDayAttr(1050), UseSetting.USE, 1)) // // 勤務時間：　8:30~17:30
                        ),
                        new TimeWithDayAttr(510),
                        true
                ));
            }
        };

        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(420), new TimeWithDayAttr(480))), // 残業時間：	7:00~8:00
                        new OvertimeHour(new OvertimeNumber(2), new TimeSpanForCalc(new TimeWithDayAttr(1170), new TimeWithDayAttr(1230))), // 残業時間：19:30~20:30
                        new OvertimeHour(new OvertimeNumber(3), new TimeSpanForCalc(new TimeWithDayAttr(1260), new TimeWithDayAttr(1320))) // 残業時間：	21:00~22:00
                ),
                Collections.emptyList()
        );

        List<BreakTimeSheet> result = target.getBreakTimeToCalculateOvertime(require, "", "", GeneralDate.today(), workInfo, workingHours, breakTimes);

        assertThat(result.size()).isEqualTo(6);

        // 休憩枠NO1：6:30~7:00
        assertThat(result.get(0).getStartTime().v()).isEqualTo(390);
        assertThat(result.get(0).getEndTime().v()).isEqualTo(420);

        // 休憩枠NO2：8:00~8:30
        assertThat(result.get(1).getStartTime().v()).isEqualTo(480);
        assertThat(result.get(1).getEndTime().v()).isEqualTo(510);

        // 休憩枠NO3：12:00~13:00
        assertThat(result.get(2).getStartTime().v()).isEqualTo(720);
        assertThat(result.get(2).getEndTime().v()).isEqualTo(780);

        // 休憩枠NO4：17:30~19:30
        assertThat(result.get(3).getStartTime().v()).isEqualTo(1050);
        assertThat(result.get(3).getEndTime().v()).isEqualTo(1170);

        // 休憩枠NO5：20:30~21:00
        assertThat(result.get(4).getStartTime().v()).isEqualTo(1230);
        assertThat(result.get(4).getEndTime().v()).isEqualTo(1260);

        // 休憩枠NO6：22:00~22:30
        assertThat(result.get(5).getStartTime().v()).isEqualTo(1320);
        assertThat(result.get(5).getEndTime().v()).isEqualTo(1350);
    }

    @Test
    public void testGetBreakTimeToCalculateOvertime6() {
        WorkInformation workInfo = new WorkInformation("001", "002");
        List<TimeZoneWithWorkNo> workingHours = Arrays.asList(new TimeZoneWithWorkNo(1, 510, 1050)); // 勤務時間：　8:30~17:30　（始業終業時刻）
        List<BreakTimeSheet> breakTimes = new ArrayList<>();
        breakTimes.add(new BreakTimeSheet(new BreakFrameNo(1), new TimeWithDayAttr(720), new TimeWithDayAttr(780))); // 休憩時間帯：12:00~13:00
        breakTimes.add(new BreakTimeSheet(new BreakFrameNo(2), new TimeWithDayAttr(1050), new TimeWithDayAttr(1080))); // 休憩時間帯：17:30~18:00

        new Expectations() {
            {
                require.getWorkTimeSetting("", "002");
                result = Optional.of(new WorkTimeSetting(
                        "",
                        new WorkTimeCode("002"),
                        new WorkTimeDivision(WorkTimeDailyAtr.REGULAR_WORK, WorkTimeMethodSet.FIXED_WORK),
                        AbolishAtr.NOT_ABOLISH,
                        new WorkTimeDisplayName(new WorkTimeName("AAA"), new WorkTimeAbName("BBB")),
                        null,
                        null
                ));
            }
        };

        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(420), new TimeWithDayAttr(480))), // 残業時間：	7:00~8:00
                        new OvertimeHour(new OvertimeNumber(2), new TimeSpanForCalc(new TimeWithDayAttr(1170), new TimeWithDayAttr(1230))), // 残業時間：19:30~20:30
                        new OvertimeHour(new OvertimeNumber(3), new TimeSpanForCalc(new TimeWithDayAttr(1260), new TimeWithDayAttr(1320))) // 残業時間：	21:00~22:00
                ),
                Collections.emptyList()
        );

        List<BreakTimeSheet> result = target.getBreakTimeToCalculateOvertime(require, "", "", GeneralDate.today(), workInfo, workingHours, breakTimes);

        assertThat(result.size()).isEqualTo(4);
        // 休憩枠NO1：8:00~8:30
        assertThat(result.get(0).getStartTime().v()).isEqualTo(480);
        assertThat(result.get(0).getEndTime().v()).isEqualTo(510);

        // 休憩枠NO2：12:00~13:00
        assertThat(result.get(1).getStartTime().v()).isEqualTo(720);
        assertThat(result.get(1).getEndTime().v()).isEqualTo(780);

        // 休憩枠NO3：17:30~19:30
        assertThat(result.get(2).getStartTime().v()).isEqualTo(1050);
        assertThat(result.get(2).getEndTime().v()).isEqualTo(1170);

        // 休憩枠NO4：20:30~21:00
        assertThat(result.get(3).getStartTime().v()).isEqualTo(1230);
        assertThat(result.get(3).getEndTime().v()).isEqualTo(1260);
    }

    @Test
    public void testGetBreakTimeToCalculateOvertime7() {
        WorkInformation workInfo = new WorkInformation("001", "002");
        List<TimeZoneWithWorkNo> workingHours = Arrays.asList(new TimeZoneWithWorkNo(1, 420, 1140)); // 勤務時間：　7:00~19:00　（始業終業時刻）
        List<BreakTimeSheet> breakTimes = new ArrayList<>();
        breakTimes.add(new BreakTimeSheet(new BreakFrameNo(1), new TimeWithDayAttr(720), new TimeWithDayAttr(780))); // 休憩時間帯：12:00~13:00

        new Expectations() {
            {
                require.getPredetemineTimeSetting("", "002");
                result = Optional.of(new PredetemineTimeSetting(
                        "",
                        new AttendanceTime(540),
                        null,
                        null,
                        new PrescribedTimezoneSetting(
                                null,
                                null,
                                Arrays.asList(new TimezoneUse(new TimeWithDayAttr(480), new TimeWithDayAttr(1020), UseSetting.USE, 1)) // // 勤務時間：　8:00~17:00
                        ),
                        new TimeWithDayAttr(480),
                        true
                ));
            }
        };

        OvertimeWorkMultipleTimes target = OvertimeWorkMultipleTimes.create(
                Arrays.asList(
                        new OvertimeHour(new OvertimeNumber(1), new TimeSpanForCalc(new TimeWithDayAttr(420), new TimeWithDayAttr(480))) // 残業時間：	7:00~8:00
                ),
                Collections.emptyList()
        );

        List<BreakTimeSheet> result = target.getBreakTimeToCalculateOvertime(require, "", "", GeneralDate.today(), workInfo, workingHours, breakTimes);

        assertThat(result.size()).isEqualTo(2);

        // 休憩枠NO1：12:00~13:00
        assertThat(result.get(0).getStartTime().v()).isEqualTo(720);
        assertThat(result.get(0).getEndTime().v()).isEqualTo(780);

        // 休憩枠NO2：17:00~19:00
        assertThat(result.get(1).getStartTime().v()).isEqualTo(1020);
        assertThat(result.get(1).getEndTime().v()).isEqualTo(1140);
    }
}
