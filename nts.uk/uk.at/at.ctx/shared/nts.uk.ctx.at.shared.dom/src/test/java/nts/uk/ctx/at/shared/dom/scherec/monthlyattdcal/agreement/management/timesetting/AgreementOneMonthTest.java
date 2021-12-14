package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting;


import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeStatusOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.ExcessState;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.AgreementOneMonthTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthErrorAlarmTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthTime;


@RunWith(JMockit.class)
public class AgreementOneMonthTest {
    @Test
    public void getters() {
        AgreementOneMonth agreementsOneMonth = new AgreementOneMonth();
        NtsAssert.invokeGetters(agreementsOneMonth);
    }

    @Test
    public void checkErrorTest() {

        AgreementOneMonth agreementsOneYear = new AgreementOneMonth(
                OneMonthTime.createWithCheck( OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(20), new AgreementOneMonthTime(20))
                        , new AgreementOneMonthTime(20)),
                OneMonthTime.createWithCheck( OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(20), new AgreementOneMonthTime(20))
                        , new AgreementOneMonthTime(20))
        );
        AgreementOneMonthTime agreementOneMonthTime = new AgreementOneMonthTime(30);
        val expect = Pair.of(true, agreementOneMonthTime);
        new Expectations(AgreementOneMonth.class){{
            agreementsOneYear.checkErrorTimeExceeded(agreementOneMonthTime);
            result = expect;
        }};
        Pair<Boolean, AgreementOneMonthTime> rs = agreementsOneYear.checkErrorTimeExceeded(agreementOneMonthTime);

        assertThat(rs.getLeft().booleanValue()).isEqualTo(expect.getLeft().booleanValue());
        assertThat(rs.getRight().v()).isEqualTo(expect.getRight().v());
    }
    @Test
    public void calculateAlarmTimeTest() {
        AgreementOneMonth agreementsOneMonth = new AgreementOneMonth();

        AgreementOneMonthTime agreementOneMonthTime = new AgreementOneMonthTime(20);

        val expect = new AgreementOneMonthTime(20).v();
        new Expectations(AgreementOneMonth.class){{
            agreementsOneMonth.calculateAlarmTime(agreementOneMonthTime);
            result = expect;
        }};

        AgreementOneMonthTime result = agreementsOneMonth.calculateAlarmTime(agreementOneMonthTime);
        assertThat(result.v()).isEqualTo(expect);
    }
    @Test
    public void check_01() {
        AgreementOneMonth agreementsOneMonth = new AgreementOneMonth();
        val specConditionLimit=   agreementsOneMonth.getSpecConditionLimit();
        AttendanceTimeMonth agreementTarget = new AttendanceTimeMonth(10);
        AttendanceTimeMonth legalLimitTarget = new AttendanceTimeMonth(20);
        val rs = ExcessState.ALARM_OVER;
        new Expectations(OneMonthTime.class){
            {
                specConditionLimit.check(legalLimitTarget);
                result = rs;
            }
        };
        assertThat(agreementsOneMonth.check(agreementTarget,legalLimitTarget)).isEqualTo(
                AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ALARM);
    }
    @Test
    public void check_02() {

        AgreementOneMonth agreementsOneMonth = new AgreementOneMonth();
        val specConditionLimit=   agreementsOneMonth.getSpecConditionLimit();
        AttendanceTimeMonth agreementTarget = new AttendanceTimeMonth(10);
        AttendanceTimeMonth legalLimitTarget = new AttendanceTimeMonth(20);
        val rs = ExcessState.ERROR_OVER;
        new Expectations(OneMonthTime.class){
            {
                specConditionLimit.check(legalLimitTarget);
                result = rs;
            }
        };
        assertThat(agreementsOneMonth.check(agreementTarget,legalLimitTarget))
                .isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ERROR);
    }
    @Test
    public void check_03() {

        AgreementOneMonth agreementsOneMonth = new AgreementOneMonth();
        val specConditionLimit=   agreementsOneMonth.getSpecConditionLimit();
        AttendanceTimeMonth agreementTarget = new AttendanceTimeMonth(10);
        AttendanceTimeMonth legalLimitTarget = new AttendanceTimeMonth(20);
        val rs = ExcessState.UPPER_LIMIT_OVER;
        new Expectations(OneMonthTime.class){
            {
                specConditionLimit.check(legalLimitTarget);
                result = rs;
            }
        };
        assertThat(AgreementTimeStatusOfMonthly.EXCESS_BG_GRAY).isEqualTo(
                agreementsOneMonth.check(agreementTarget,legalLimitTarget));
    }
    @Test
    public void check_04() {

        AgreementOneMonth agreementsOneMonth = new AgreementOneMonth();
        val specConditionLimit=   agreementsOneMonth.getSpecConditionLimit();
        val basic = agreementsOneMonth.getBasic();
        AttendanceTimeMonth agreementTarget = new AttendanceTimeMonth(10);
        AttendanceTimeMonth legalLimitTarget = new AttendanceTimeMonth(20);
        new Expectations(OneMonthTime.class){
            {
                specConditionLimit.check(legalLimitTarget);
                result = null;
                basic.check(agreementTarget);
                result = ExcessState.ALARM_OVER;
            }
        };
        assertThat(agreementsOneMonth.check(agreementTarget,legalLimitTarget))
                .isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ALARM);
    }
    @Test
    public void check_05() {

        AgreementOneMonth agreementsOneMonth = new AgreementOneMonth();
        val specConditionLimit=   agreementsOneMonth.getSpecConditionLimit();
        val basic = agreementsOneMonth.getBasic();
        AttendanceTimeMonth agreementTarget = new AttendanceTimeMonth(10);
        AttendanceTimeMonth legalLimitTarget = new AttendanceTimeMonth(20);
        new Expectations(OneMonthTime.class){
            {
                specConditionLimit.check(legalLimitTarget);
                result = null;
                basic.check(agreementTarget);
                result = ExcessState.ERROR_OVER;
            }
        };
        assertThat(agreementsOneMonth.check(agreementTarget,legalLimitTarget))
                .isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR);
    }
    @Test
    public void check_06() {

        AgreementOneMonth agreementsOneMonth = new AgreementOneMonth();
        val specConditionLimit=   agreementsOneMonth.getSpecConditionLimit();
        val basic = agreementsOneMonth.getBasic();
        AttendanceTimeMonth agreementTarget = new AttendanceTimeMonth(10);
        AttendanceTimeMonth legalLimitTarget = new AttendanceTimeMonth(20);
        new Expectations(OneMonthTime.class){
            {
                specConditionLimit.check(legalLimitTarget);
                result = null;
                basic.check(agreementTarget);
                result = ExcessState.UPPER_LIMIT_OVER;
            }
        };
        assertThat(agreementsOneMonth.check(agreementTarget,legalLimitTarget))
                .isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR);
    }
    @Test
    public void check_07() {

        AgreementOneMonth agreementsOneMonth = new AgreementOneMonth();

        val specConditionLimit=   agreementsOneMonth.getSpecConditionLimit();
        val basic = agreementsOneMonth.getBasic();
        AttendanceTimeMonth agreementTarget = new AttendanceTimeMonth(10);
        AttendanceTimeMonth legalLimitTarget = new AttendanceTimeMonth(20);
        new Expectations(OneMonthTime.class){
            {
                specConditionLimit.check(legalLimitTarget);
                result = null;
                basic.check(agreementTarget);
                result = null;
            }
        };
        assertThat(agreementsOneMonth.check(agreementTarget,legalLimitTarget))
                .isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
    }

}
