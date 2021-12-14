package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeStatusOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.ExcessState;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.AgreementOneYearTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.OneYearErrorAlarmTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.OneYearTime;

@RunWith(JMockit.class)
public class AgreementOneYearTest {
    @Test
    public void getters() {
        AgreementOneYear agreementOneYear = new AgreementOneYear(
                 OneYearErrorAlarmTime.of(new AgreementOneYearTime(20),new AgreementOneYearTime(20)),
                 OneYearTime.createWithCheck( OneYearErrorAlarmTime.of(new AgreementOneYearTime(20),new AgreementOneYearTime(20))
                        ,new AgreementOneYearTime(20))
        );
        NtsAssert.invokeGetters(agreementOneYear);
    }
    @Test
    public void checkErrorTimeExceededTest() {

        AgreementOneYear agreementOneYear = new AgreementOneYear(
                 OneYearErrorAlarmTime.of(new AgreementOneYearTime(20),new AgreementOneYearTime(20)),
                 OneYearTime.createWithCheck( OneYearErrorAlarmTime.of(new AgreementOneYearTime(20),new AgreementOneYearTime(20))
                        ,new AgreementOneYearTime(20))
        );
        val agreementOneYearTime = new AgreementOneYearTime(60);
        val expect = Pair.of(true, agreementOneYearTime);
        new Expectations(AgreementOneYear.class){{
            agreementOneYear.checkErrorTimeExceeded(agreementOneYearTime);
            result = expect;
        }};
        Pair<Boolean, AgreementOneYearTime> rs =  agreementOneYear.checkErrorTimeExceeded(new AgreementOneYearTime(60));

        assertThat(rs.getLeft().booleanValue()).isEqualTo(expect.getLeft().booleanValue());
        assertThat(rs.getRight().v()).isEqualTo(expect.getRight().v());
    }

    @Test
    public void calculateAlarmTimeTest() {
        AgreementOneYear agreementOneYear = new AgreementOneYear(
                 OneYearErrorAlarmTime.of(new AgreementOneYearTime(20),new AgreementOneYearTime(20)),
                 OneYearTime.createWithCheck( OneYearErrorAlarmTime.of(new AgreementOneYearTime(20),new AgreementOneYearTime(20))
                        ,new AgreementOneYearTime(20))
        );
        val agreementOneYearTime = new AgreementOneYearTime(20);
        val expect = new AgreementOneYearTime(20);
        new Expectations(AgreementOneYear.class){{
            agreementOneYear.calculateAlarmTime(agreementOneYearTime);
            result = expect;
        }};
        AgreementOneYearTime result =  agreementOneYear.calculateAlarmTime(agreementOneYearTime);
        assertThat(result.v()).isEqualTo(expect.v());
    }
    @Test
    public void check_01() {
        AgreementOneYear agreementOneYear = new AgreementOneYear();
        val specConditionLimit=   agreementOneYear.getSpecConditionLimit();
        AgreementOneYearTime agreementTarget = new AgreementOneYearTime(10);
        AgreementOneYearTime legalLimitTarget = new AgreementOneYearTime(20);
        val rs = ExcessState.ALARM_OVER;
        new Expectations(OneYearTime.class){
            {
                specConditionLimit.check(legalLimitTarget);
                result = rs;
            }
        };
        assertThat(agreementOneYear.check(agreementTarget,legalLimitTarget))
                .isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ALARM );
    }
    @Test
    public void check_02() {

        AgreementOneYear agreementOneYear = new AgreementOneYear();
        val specConditionLimit=   agreementOneYear.getSpecConditionLimit();
        AgreementOneYearTime agreementTarget = new AgreementOneYearTime(10);
        AgreementOneYearTime legalLimitTarget = new AgreementOneYearTime(20);
        val rs = ExcessState.ERROR_OVER;
        new Expectations(OneYearTime.class){
            {
                specConditionLimit.check(legalLimitTarget);
                result = rs;
            }
        };
        assertThat(agreementOneYear.check(agreementTarget,legalLimitTarget))
                .isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ERROR);
    }
    @Test
    public void check_03() {

        AgreementOneYear agreementOneYear = new AgreementOneYear();
        val specConditionLimit=   agreementOneYear.getSpecConditionLimit();
        AgreementOneYearTime agreementTarget = new AgreementOneYearTime(10);
        AgreementOneYearTime legalLimitTarget = new AgreementOneYearTime(20);
        val rs = ExcessState.UPPER_LIMIT_OVER;
        new Expectations(OneYearTime.class){
            {
                specConditionLimit.check(legalLimitTarget);
                result = rs;
            }
        };
        assertThat(agreementOneYear.check(agreementTarget,legalLimitTarget))
                .isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_BG_GRAY);
    }
    @Test
    public void check_04() {

        AgreementOneYear agreementOneYear = new AgreementOneYear(
                OneYearErrorAlarmTime.of(new AgreementOneYearTime(20),new AgreementOneYearTime(20)),
                OneYearTime.createWithCheck( OneYearErrorAlarmTime.of(new AgreementOneYearTime(20),new AgreementOneYearTime(20))
                        ,new AgreementOneYearTime(20))
        );
        val specConditionLimit=   agreementOneYear.getSpecConditionLimit();
        AgreementOneYearTime agreementTarget = new AgreementOneYearTime(50);
        AgreementOneYearTime legalLimitTarget = new AgreementOneYearTime(20);
        new Expectations(OneYearTime.class){
            {
                specConditionLimit.check(legalLimitTarget);
                result = null;
            }
        };
        assertThat(agreementOneYear.check(agreementTarget,legalLimitTarget))
                .isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR );
    }
    @Test
    public void check_05() {

        AgreementOneYear agreementOneYear = new AgreementOneYear(
                OneYearErrorAlarmTime.of(new AgreementOneYearTime(21),new AgreementOneYearTime(20)),
                OneYearTime.createWithCheck( OneYearErrorAlarmTime.of(new AgreementOneYearTime(20),new AgreementOneYearTime(20))
                        ,new AgreementOneYearTime(20))
        );
        val specConditionLimit=   agreementOneYear.getSpecConditionLimit();
        AgreementOneYearTime agreementTarget = new AgreementOneYearTime(16);
        AgreementOneYearTime legalLimitTarget = new AgreementOneYearTime(20);
        new Expectations(OneYearTime.class){
            {
                specConditionLimit.check(legalLimitTarget);
                result = null;
            }
        };
        assertThat(agreementOneYear.check(agreementTarget,legalLimitTarget))
                .isEqualTo( AgreementTimeStatusOfMonthly.NORMAL);
    }
    @Test
    public void check_06() {

        AgreementOneYear agreementOneYear = new AgreementOneYear(
                OneYearErrorAlarmTime.of(new AgreementOneYearTime(18),new AgreementOneYearTime(15)),
                OneYearTime.createWithCheck( OneYearErrorAlarmTime.of(new AgreementOneYearTime(20),new AgreementOneYearTime(20))
                        ,new AgreementOneYearTime(20))
        );
        val specConditionLimit=   agreementOneYear.getSpecConditionLimit();
        AgreementOneYearTime agreementTarget = new AgreementOneYearTime(16);
        AgreementOneYearTime legalLimitTarget = new AgreementOneYearTime(20);
        new Expectations(OneYearTime.class){
            {
                specConditionLimit.check(legalLimitTarget);
                result = null;
            }
        };
        assertThat(agreementOneYear.check(agreementTarget,legalLimitTarget))
                .isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ALARM);
    }
}
