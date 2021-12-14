package nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.condition.overtimeholiday.subtransfer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.MaximumTimeZone;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.OvertimeHdHourTransfer;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.SubstituteTransferProcess;
import nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.common.ReflectApplicationHelper;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.common.CompensatoryOccurrenceDivision;
import nts.uk.ctx.at.shared.dom.worktime.common.GetSubHolOccurrenceSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.SubHolTransferSetAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

@RunWith(JMockit.class)
public class SubstituteTransferProcessTest {

	@Injectable
	private SubstituteTransferProcess.Require require;
	/*
	 * テストしたい内容
	 * 
	 * →振替をした後の時間（List）をチェック
	 * 
	 * 準備するデータ
	 * 
	 * →未割り当ての時間を算出<=0
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test1(@Mocked GetSubHolOccurrenceSetting setting) {

		MaximumTimeZone maxTimeZone = new MaximumTimeZone();
		List<OvertimeHdHourTransfer> maxTime = Arrays.asList(new OvertimeHdHourTransfer(1, new AttendanceTime(0), new AttendanceTime(0)));// 振替時間=0
		List<OvertimeHdHourTransfer> timeAfterReflectApp = Arrays
				.asList(new OvertimeHdHourTransfer(1, new AttendanceTime(666), new AttendanceTime(999)));

		new Expectations() {
			{
				GetSubHolOccurrenceSetting.process(require, anyString, (Optional<String>) any,
						(CompensatoryOccurrenceDivision) any);
				result = ReflectApplicationHelper.createSubTrans(0, 0, 0,
						SubHolTransferSetAtr.SPECIFIED_TIME_SUB_HOL);
			}
		};

		val result = SubstituteTransferProcess.process(require, "", Optional.empty(), CompensatoryOccurrenceDivision.FromOverTime,
				maxTimeZone, maxTime, timeAfterReflectApp);

		assertThat(result).extracting(x -> x.getNo(), x -> x.getTransferTime().v(), //
				x -> x.getTime().v())//
				.contains(Tuple.tuple(1, 999, 666));

	}

	/*
	 * テストしたい内容
	 * 
	 * →振替をした後の時間（List）をチェック
	 * 
	 * 準備するデータ
	 * 
	 * →未割り当ての時間を算出>=0
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test2(@Mocked GetSubHolOccurrenceSetting setting) {

		MaximumTimeZone maxTimeZone = new MaximumTimeZone();
		maxTimeZone.getTimeSpan().add(Pair.of(new OverTimeFrameNo(1), new TimeSpanForDailyCalc(new TimeWithDayAttr(100), new TimeWithDayAttr(200))));

		List<OvertimeHdHourTransfer> maxTime = Arrays.asList(new OvertimeHdHourTransfer(1, new AttendanceTime(10), new AttendanceTime(10)));
		List<OvertimeHdHourTransfer> timeAfterReflectApp = Arrays
				.asList(new OvertimeHdHourTransfer(1, new AttendanceTime(9), new AttendanceTime(16)));

		new Expectations() {
			{
				GetSubHolOccurrenceSetting.process(require, anyString, (Optional<String>) any,
						(CompensatoryOccurrenceDivision) any);
				result = ReflectApplicationHelper.createSubTrans(0, 0, 0,
						SubHolTransferSetAtr.SPECIFIED_TIME_SUB_HOL);
			}
		};

		val result = SubstituteTransferProcess.process(require, "", Optional.empty(),
				CompensatoryOccurrenceDivision.FromOverTime, maxTimeZone, maxTime, timeAfterReflectApp);
		assertThat(result).extracting(x -> x.getNo(), x -> x.getTransferTime().v(), //
				x -> x.getTime().v())//
				.contains(Tuple.tuple(1, 16, 9));

	}

}
