package nts.uk.ctx.at.shared.dom.monthlyattdcal.agreement.management.oneMonth;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.ExcessState;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.AgreementOneMonthTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthErrorAlarmTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthTime;
import org.apache.commons.lang3.tuple.Pair;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class OneMonthTimeTest {

    @Test
    public void getters() {
        OneMonthTime oneMonthTime = new OneMonthTime();
        NtsAssert.invokeGetters(oneMonthTime);
    }

    @Test
    public void createTest_1() {
        NtsAssert.businessException("Msg_59", () -> {
            OneMonthErrorAlarmTime oneMonthErrorAlarmTime = OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(30),
                    new AgreementOneMonthTime(20));
            OneMonthTime.createWithCheck(oneMonthErrorAlarmTime, new AgreementOneMonthTime(10));
        });
    }

    @Test
    public void createTest_2() {
        OneMonthErrorAlarmTime errorTimeInMonth = OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(40), new AgreementOneMonthTime(30));

        OneMonthTime target = OneMonthTime.createWithCheck(errorTimeInMonth,new AgreementOneMonthTime(50));

        assertThat(target.getErAlTime().getError()).isEqualTo(errorTimeInMonth.getError());
        assertThat( target.getErAlTime().getAlarm()).isEqualTo(errorTimeInMonth.getAlarm());
        assertThat( target.getUpperLimit()).isEqualTo(new AgreementOneMonthTime(50));
    }

    @Test
    public void errorCheckTest_Normal() {
        OneMonthTime target = OneMonthTime.createWithCheck(OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(40), new AgreementOneMonthTime(30)),
                new AgreementOneMonthTime(50));
        ExcessState result = target.check(new AttendanceTimeMonth(30));

        assertThat(result.value).isEqualTo(ExcessState.NORMAL.value);
    }

    @Test
    public void errorCheckTest_AlarmOver() {
        OneMonthTime target = OneMonthTime.createWithCheck(OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(40), new AgreementOneMonthTime(30)),
                new AgreementOneMonthTime(50));
        ExcessState result = target.check(new AttendanceTimeMonth(40));

        assertThat(result.value).isEqualTo(ExcessState.ALARM_OVER.value);
    }

    @Test
    public void errorCheckTest_ErrorOver() {
        OneMonthTime target = OneMonthTime.createWithCheck(OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(40), new AgreementOneMonthTime(30)),
                new AgreementOneMonthTime(50));
        ExcessState result = target.check(new AttendanceTimeMonth(45));

        assertThat(result.value).isEqualTo(ExcessState.ERROR_OVER.value);
    }

    @Test
    public void errorCheckTest_MaxTime() {
        OneMonthTime target = OneMonthTime.createWithCheck(OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(40), new AgreementOneMonthTime(30)),
                new AgreementOneMonthTime(50));
        ExcessState result = target.check(new AttendanceTimeMonth(60));

        assertThat(result.value).isEqualTo(ExcessState.UPPER_LIMIT_OVER.value);
    }

    @Test
    public void checkErrorTimeExceededTest_1() {
        OneMonthTime target = OneMonthTime.createWithCheck(OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(40), new AgreementOneMonthTime(30)),
                new AgreementOneMonthTime(50));
        Pair<Boolean, AgreementOneMonthTime> result = target.isErrorTimeOver(new AgreementOneMonthTime(60));

        assertThat(result.getLeft()).isEqualTo(true);
        assertThat(result.getRight()).isEqualTo(new AgreementOneMonthTime(40));
    }

    @Test
    public void checkErrorTimeExceededTest_2() {
        OneMonthTime target = OneMonthTime.createWithCheck(OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(40), new AgreementOneMonthTime(30)),
                new AgreementOneMonthTime(50));
        Pair<Boolean, AgreementOneMonthTime> result = target.isErrorTimeOver(new AgreementOneMonthTime(35));

        assertThat(result.getLeft()).isEqualTo(false);
        assertThat(result.getRight()).isEqualTo(new AgreementOneMonthTime(40));
    }

    @Test
    public void calculateAlarmTimeTest_1() {
        OneMonthTime target = OneMonthTime.createWithCheck(OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(40), new AgreementOneMonthTime(30)),
                new AgreementOneMonthTime(50));
        AgreementOneMonthTime result = target.calcAlarmTime(new AgreementOneMonthTime(50));

        assertThat(result).isEqualTo(new AgreementOneMonthTime(40));
    }

    @Test
    public void calculateAlarmTimeTest_2() {
        OneMonthTime target = OneMonthTime.createWithCheck(OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(40), new AgreementOneMonthTime(30)),
                new AgreementOneMonthTime(50));
        AgreementOneMonthTime result = target.calcAlarmTime(new AgreementOneMonthTime(10));

        assertThat(result).isEqualTo(new AgreementOneMonthTime(0));
    }

}
