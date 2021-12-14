package nts.uk.ctx.at.shared.dom.monthlyattdcal.agreement.management.oneYear;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.ExcessState;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.AgreementOneYearTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.OneYearErrorAlarmTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.OneYearTime;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OneYearTimeTest {


	@Test
	public void getters() {
		OneYearTime oneMonthTime = OneYearTime.createWithCheck(OneYearErrorAlarmTime.of(new AgreementOneYearTime(30),new AgreementOneYearTime(20)),
				new AgreementOneYearTime(40));
		NtsAssert.invokeGetters(oneMonthTime);
	}

	@Test
	public void createTest_1() {
		NtsAssert.businessException("Msg_59", ()->{
			OneYearTime.createWithCheck(OneYearErrorAlarmTime.of(new AgreementOneYearTime(30),
					new AgreementOneYearTime(20)),new AgreementOneYearTime(10));
		});
	}

	@Test
	public void createTest_2() {
        OneYearErrorAlarmTime errorTimeInYear = OneYearErrorAlarmTime.of(new AgreementOneYearTime(30),new AgreementOneYearTime(20));

		OneYearTime target = OneYearTime.createWithCheck(errorTimeInYear,new AgreementOneYearTime(40));

		assertThat(target.getErAlTime().getError()).isEqualTo(errorTimeInYear.getError());
		assertThat(target.getErAlTime().getAlarm()).isEqualTo(errorTimeInYear.getAlarm());
		assertThat(target.getUpperLimit()).isEqualTo(new AgreementOneYearTime(40));
	}

	@Test
	public void checkErrorTimeExceededTest_1() {
		OneYearTime target = OneYearTime.createWithCheck(OneYearErrorAlarmTime.of(new AgreementOneYearTime(30),new AgreementOneYearTime(20)),
				new AgreementOneYearTime(40));
		Pair<Boolean, AgreementOneYearTime> result =  target.isErrorTimeOver(new AgreementOneYearTime(50));

		assertThat(result.getLeft()).isEqualTo(true);
		assertThat(result.getRight()).isEqualTo(new AgreementOneYearTime(30));
	}

	@Test
	public void checkErrorTimeExceededTest_2() {
		OneYearTime target = OneYearTime.createWithCheck(OneYearErrorAlarmTime.of(new AgreementOneYearTime(30),new AgreementOneYearTime(20)),
				new AgreementOneYearTime(40));
		Pair<Boolean, AgreementOneYearTime> result =  target.isErrorTimeOver(new AgreementOneYearTime(10));

		assertThat(result.getLeft()).isEqualTo(false);
		assertThat(result.getRight()).isEqualTo(new AgreementOneYearTime(30));
	}

	@Test
	public void calculateAlarmTimeTest_1() {
		OneYearTime target = OneYearTime.createWithCheck(OneYearErrorAlarmTime.of(new AgreementOneYearTime(30),new AgreementOneYearTime(20)),
				new AgreementOneYearTime(40));
		AgreementOneYearTime result =  target.calcAlarmTime(new AgreementOneYearTime(50));

		assertThat(result).isEqualTo(new AgreementOneYearTime(40));
	}

	@Test
	public void calculateAlarmTimeTest_2() {
		OneYearTime target = OneYearTime.createWithCheck(OneYearErrorAlarmTime.of(new AgreementOneYearTime(30),new AgreementOneYearTime(20)),
				new AgreementOneYearTime(40));
		AgreementOneYearTime result =  target.calcAlarmTime(new AgreementOneYearTime(10));

		assertThat(result).isEqualTo(new AgreementOneYearTime(0));
	}

	@Test
	public void check_01() {
		OneYearTime target = OneYearTime.createWithCheck(OneYearErrorAlarmTime.of(new AgreementOneYearTime(30),new AgreementOneYearTime(20)),
				new AgreementOneYearTime(40));
		ExcessState excessState = target.check(new AgreementOneYearTime(50));
		assertThat(excessState).isEqualTo(ExcessState.UPPER_LIMIT_OVER);
	}
	@Test
	public void check_02() {

		OneYearTime target = OneYearTime.createWithCheck(OneYearErrorAlarmTime.of(new AgreementOneYearTime(30),new AgreementOneYearTime(20)),
				new AgreementOneYearTime(40));
		ExcessState excessState = target.check(new AgreementOneYearTime(35));
		assertThat(excessState).isEqualTo(ExcessState.ERROR_OVER);
	}

	@Test
	public void check_03() {

		OneYearTime target = OneYearTime.createWithCheck(OneYearErrorAlarmTime.of(new AgreementOneYearTime(30),new AgreementOneYearTime(20)),
				new AgreementOneYearTime(40));
		ExcessState excessState = target.check(new AgreementOneYearTime(25));
		assertThat(excessState).isEqualTo(ExcessState.ALARM_OVER);
	}

	@Test
	public void check_04() {

		OneYearTime target = OneYearTime.createWithCheck(OneYearErrorAlarmTime.of(new AgreementOneYearTime(30),new AgreementOneYearTime(20)),
				new AgreementOneYearTime(40));
		ExcessState excessState = target.check(new AgreementOneYearTime(15));
		assertThat(excessState).isEqualTo(ExcessState.NORMAL);
	}

}
