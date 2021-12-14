package nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.condition.overtimeholiday.subtransfer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.CreateWorkMaxTimeZone;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.MaximumTimeZone;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.OvertimeHdHourTransfer;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.SubstituteTransferProcess;
import nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.common.ReflectApplicationHelper;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.algorithm.subtransfer.TranferOvertimeCompensatoryApp;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTimeSheet;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.common.CompensatoryOccurrenceDivision;
import nts.uk.ctx.at.shared.dom.worktime.common.GetSubHolOccurrenceSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.SubHolTransferSetAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

@RunWith(JMockit.class)
public class TranferOvertimeCompensatoryAppTest {

	@Injectable
	private TranferOvertimeCompensatoryApp.Require require;

	private String cid = "1";

	/*
	 * テストしたい内容
	 * 
	 * 
	 * →残業時間の代休振替
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * → 代休振替処理から最大の時間.振替時間, 時間 = (999, 666)
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test01(@Mocked CreateWorkMaxTimeZone createMaxTime, @Mocked SubstituteTransferProcess sub,
			@Mocked GetSubHolOccurrenceSetting getSubHol) {

		DailyRecordOfApplication dailyApp = ReflectApplicationHelper
				.createRCWithTimeLeavFull(ScheduleRecordClassifi.SCHEDULE, 1);

		new Expectations() {
			{
				CreateWorkMaxTimeZone.process(require, cid, (IntegrationOfDaily) any);
				result = ReflectApplicationHelper.createRCWithTimeLeavOverTime();

				SubstituteTransferProcess.process(require, anyString, (Optional<String>) any,
						CompensatoryOccurrenceDivision.FromOverTime, (MaximumTimeZone) any,
						(List<OvertimeHdHourTransfer>) any, (List<OvertimeHdHourTransfer>) any);
				result = Arrays.asList(new OvertimeHdHourTransfer(1, new AttendanceTime(666), new AttendanceTime(999)));
			}
		};

		TranferOvertimeCompensatoryApp.process(require, cid, dailyApp.getDomain());
		assertThat(
				dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
						.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTime())
								.extracting(x -> x.getOverWorkFrameNo().v(), x -> x.getOverTimeWork().getTime().v(),
										x -> x.getTransferTime().getTime().v())
								.contains(Tuple.tuple(1, 666, 999));

	}

	/*
	 * テストしたい内容
	 * 
	 * 
	 * →残業時間の代休振がない
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * → 日別勤怠の残業時間=empty
	 * 
	 */
	@Test
	public void test02(@Mocked CreateWorkMaxTimeZone createMaxTime) {
		DailyRecordOfApplication dailyApp = ReflectApplicationHelper
				.createRCWithTimeLeavFull(ScheduleRecordClassifi.SCHEDULE, 1);
		dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
				.getExcessOfStatutoryTimeOfDaily().updateOverTime(null);

//		new Expectations() {
//			{
//				CreateWorkMaxTimeZone.process(require, cid, (IntegrationOfDaily) any);
//				result = dailyApp;
//			}
//		};

		TranferOvertimeCompensatoryApp.process(require, cid, dailyApp.getDomain());
		assertThat(dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
				.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork()).isEmpty();
	}

	/**
	 * 1 代休に必要な時間->1日8ｈ 申請時間① 1 普通残業 8:00 4 深夜残業 6:00
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test11(@Mocked CreateWorkMaxTimeZone createMaxTime, @Mocked GetSubHolOccurrenceSetting getSub) {

		DailyRecordOfApplication dailyApp = createRCOverCase();

		new Expectations() {
			{
				CreateWorkMaxTimeZone.process(require, cid, (IntegrationOfDaily) any);
				result = createRCOverCase();

				GetSubHolOccurrenceSetting.process(require, anyString, (Optional<String>) any,
						(CompensatoryOccurrenceDivision) any);
				result = ReflectApplicationHelper.createSubTrans(0, 480, 210,
						SubHolTransferSetAtr.SPECIFIED_TIME_SUB_HOL);

			}
		};

		TranferOvertimeCompensatoryApp.process(require, cid, dailyApp.getDomain());
		assertThat(
				dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
						.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTime())
								.extracting(x -> x.getOverWorkFrameNo().v(), x -> x.getOverTimeWork().getTime().v(),
										x -> x.getTransferTime().getTime().v())
								.contains(Tuple.tuple(1, 240, 240), Tuple.tuple(4, 120, 240));

	}

	// 申請時間①
	@SuppressWarnings("unchecked")
	@Test
	public void test21(@Mocked CreateWorkMaxTimeZone createMaxTime, @Mocked GetSubHolOccurrenceSetting getSub) {

		List<OverTimeFrameTime> overTimeWorkFrameTime = new ArrayList<OverTimeFrameTime>();
		overTimeWorkFrameTime.addAll(Arrays.asList(create(1, 60, 0), create(2, 240, 0), create(4, 180, 0)));
		DailyRecordOfApplication dailyApp = createRCOverCase2(overTimeWorkFrameTime);
		new Expectations() {
			{
				CreateWorkMaxTimeZone.process(require, cid, (IntegrationOfDaily) any);
				result = createRCOverCase2(overTimeWorkFrameTime);

				GetSubHolOccurrenceSetting.process(require, anyString, (Optional<String>) any,
						(CompensatoryOccurrenceDivision) any);
				result = ReflectApplicationHelper.createSubTrans(0, 480, 210,
						SubHolTransferSetAtr.SPECIFIED_TIME_SUB_HOL);

			}
		};

		TranferOvertimeCompensatoryApp.process(require, cid, dailyApp.getDomain());
		assertThat(
				dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
						.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTime())
								.extracting(x -> x.getOverWorkFrameNo().v(), x -> x.getOverTimeWork().getTime().v(),
										x -> x.getTransferTime().getTime().v())
								.contains(Tuple.tuple(1, 0, 60), Tuple.tuple(2, 0, 240), Tuple.tuple(4, 0, 180));

	}

	// 申請時間②
	@SuppressWarnings("unchecked")
	@Test
	public void test22(@Mocked CreateWorkMaxTimeZone createMaxTime, @Mocked GetSubHolOccurrenceSetting getSub) {

		List<OverTimeFrameTime> overTimeWorkFrameTime = new ArrayList<OverTimeFrameTime>();
		overTimeWorkFrameTime.addAll(Arrays.asList(create(1, 60, 0), create(2, 240, 0), create(4, 150, 0)));
		DailyRecordOfApplication dailyApp = createRCOverCase2(overTimeWorkFrameTime);
		new Expectations() {
			{
				CreateWorkMaxTimeZone.process(require, cid, (IntegrationOfDaily) any);
				result = createRCOverCase2(overTimeWorkFrameTime);

				GetSubHolOccurrenceSetting.process(require, anyString, (Optional<String>) any,
						(CompensatoryOccurrenceDivision) any);
				result = ReflectApplicationHelper.createSubTrans(480, 0, 0,
						SubHolTransferSetAtr.CERTAIN_TIME_EXC_SUB_HOL);

			}
		};

		TranferOvertimeCompensatoryApp.process(require, cid, dailyApp.getDomain());
		assertThat(
				dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
						.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTime())
								.extracting(x -> x.getOverWorkFrameNo().v(), x -> x.getOverTimeWork().getTime().v(),
										x -> x.getTransferTime().getTime().v())
								.contains(Tuple.tuple(1, 60, 0), Tuple.tuple(2, 240, 0), Tuple.tuple(4, 150, 0));

	}

	// 申請時間③
	@SuppressWarnings("unchecked")
	@Test
	public void test23(@Mocked CreateWorkMaxTimeZone createMaxTime, @Mocked GetSubHolOccurrenceSetting getSub) {

		List<OverTimeFrameTime> overTimeWorkFrameTime = new ArrayList<OverTimeFrameTime>();
		overTimeWorkFrameTime.addAll(Arrays.asList(create(3, 360, 0), create(10, 180, 0)));
		DailyRecordOfApplication dailyApp = createRCOverCase2(overTimeWorkFrameTime);
		new Expectations() {
			{
				CreateWorkMaxTimeZone.process(require, cid, (IntegrationOfDaily) any);
				result = createRCOverCase2(overTimeWorkFrameTime);
				
				GetSubHolOccurrenceSetting.process(require, anyString, (Optional<String>) any,
						(CompensatoryOccurrenceDivision) any);
				result = ReflectApplicationHelper.createSubTrans(0, 0, 0,
						SubHolTransferSetAtr.CERTAIN_TIME_EXC_SUB_HOL);

			}
		};

		TranferOvertimeCompensatoryApp.process(require, cid, dailyApp.getDomain());
		assertThat(
				dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
						.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTime())
								.extracting(x -> x.getOverWorkFrameNo().v(), x -> x.getOverTimeWork().getTime().v(),
										x -> x.getTransferTime().getTime().v())
								.contains(Tuple.tuple(3, 0, 360), Tuple.tuple(10, 0, 180));

	}

	// 申請時間④
	@SuppressWarnings("unchecked")
	@Test
	public void test24(@Mocked CreateWorkMaxTimeZone createMaxTime, @Mocked GetSubHolOccurrenceSetting getSub) {

		List<OverTimeFrameTime> overTimeWorkFrameTime = new ArrayList<OverTimeFrameTime>();
		overTimeWorkFrameTime.addAll(Arrays.asList(create(3, 300, 0), create(10, 150, 0)));
		DailyRecordOfApplication dailyApp = createRCOverCase2(overTimeWorkFrameTime);
		new Expectations() {
			{
				CreateWorkMaxTimeZone.process(require, cid, (IntegrationOfDaily) any);
				result = createRCOverCase2(overTimeWorkFrameTime);
				
				GetSubHolOccurrenceSetting.process(require, anyString, (Optional<String>) any,
						(CompensatoryOccurrenceDivision) any);
				result = ReflectApplicationHelper.createSubTrans(480, 0, 0,
						SubHolTransferSetAtr.CERTAIN_TIME_EXC_SUB_HOL);

			}
		};

		TranferOvertimeCompensatoryApp.process(require, cid, dailyApp.getDomain());
		assertThat(
				dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
						.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTime())
								.extracting(x -> x.getOverWorkFrameNo().v(), x -> x.getOverTimeWork().getTime().v(),
										x -> x.getTransferTime().getTime().v())
								.contains(Tuple.tuple(3, 300, 0), Tuple.tuple(10, 150, 0));

	}

	// 申請時間⑤
	@SuppressWarnings("unchecked")
	@Test
	public void test25(@Mocked CreateWorkMaxTimeZone createMaxTime, @Mocked GetSubHolOccurrenceSetting getSub) {

		List<OverTimeFrameTime> overTimeWorkFrameTime = new ArrayList<OverTimeFrameTime>();
		overTimeWorkFrameTime.addAll(Arrays.asList(create(3, 240, 0), create(4, 420, 0)));
		DailyRecordOfApplication dailyApp = createRCOverCase2(overTimeWorkFrameTime);
		new Expectations() {
			{
				CreateWorkMaxTimeZone.process(require, cid, (IntegrationOfDaily) any);
				result = createRCOverCase2(overTimeWorkFrameTime);
				
				GetSubHolOccurrenceSetting.process(require, anyString, (Optional<String>) any,
						(CompensatoryOccurrenceDivision) any);
				result = ReflectApplicationHelper.createSubTrans(0, 0, 0,
						SubHolTransferSetAtr.CERTAIN_TIME_EXC_SUB_HOL);

			}
		};

		TranferOvertimeCompensatoryApp.process(require, cid, dailyApp.getDomain());
		assertThat(
				dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
						.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTime())
								.extracting(x -> x.getOverWorkFrameNo().v(), x -> x.getOverTimeWork().getTime().v(),
										x -> x.getTransferTime().getTime().v())
								.containsExactly(Tuple.tuple(3, 0, 240), Tuple.tuple(4, 0, 420));

	}

	// 申請時間⑥
	@SuppressWarnings("unchecked")
	@Test
	public void test26(@Mocked CreateWorkMaxTimeZone createMaxTime, @Mocked GetSubHolOccurrenceSetting getSub) {

		List<OverTimeFrameTime> overTimeWorkFrameTime = new ArrayList<OverTimeFrameTime>();
		overTimeWorkFrameTime.addAll(Arrays.asList(create(1, 240, 0), create(10, 420, 0)));
		DailyRecordOfApplication dailyApp = createRCOverCase2(overTimeWorkFrameTime);
		new Expectations() {
			{
				CreateWorkMaxTimeZone.process(require, cid, (IntegrationOfDaily) any);
				result = createRCOverCase2(overTimeWorkFrameTime);
				
				GetSubHolOccurrenceSetting.process(require, anyString, (Optional<String>) any,
						(CompensatoryOccurrenceDivision) any);
				result = ReflectApplicationHelper.createSubTrans(0, 0, 0,
						SubHolTransferSetAtr.CERTAIN_TIME_EXC_SUB_HOL);

			}
		};

		TranferOvertimeCompensatoryApp.process(require, cid, dailyApp.getDomain());
		assertThat(
				dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
						.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTime())
								.extracting(x -> x.getOverWorkFrameNo().v(), x -> x.getOverTimeWork().getTime().v(),
										x -> x.getTransferTime().getTime().v())
								.contains(Tuple.tuple(1, 0, 240), Tuple.tuple(10, 0, 420));

	}

	// 代休に必要な時間->1日0ｈ
	// 申請時間⑦
	@SuppressWarnings("unchecked")
	@Test
	public void test27(@Mocked CreateWorkMaxTimeZone createMaxTime, @Mocked GetSubHolOccurrenceSetting getSub) {

		List<OverTimeFrameTime> overTimeWorkFrameTime = new ArrayList<OverTimeFrameTime>();
		overTimeWorkFrameTime.addAll(Arrays.asList(create(1, 60, 0), create(2, 240, 0), create(4, 180, 0)));
		DailyRecordOfApplication dailyApp = createRCOverCase2(overTimeWorkFrameTime);
		new Expectations() {
			{
				CreateWorkMaxTimeZone.process(require, cid, (IntegrationOfDaily) any);
				result = createRCOverCase2(overTimeWorkFrameTime);
				
				GetSubHolOccurrenceSetting.process(require, anyString, (Optional<String>) any,
						(CompensatoryOccurrenceDivision) any);
				result = ReflectApplicationHelper.createSubTrans(0, 0, 0,
						SubHolTransferSetAtr.SPECIFIED_TIME_SUB_HOL);

			}
		};

		TranferOvertimeCompensatoryApp.process(require, cid, dailyApp.getDomain());
		assertThat(
				dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
						.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTime())
								.extracting(x -> x.getOverWorkFrameNo().v(), x -> x.getOverTimeWork().getTime().v(),
										x -> x.getTransferTime().getTime().v())
								.contains(Tuple.tuple(1, 60, 0), Tuple.tuple(2, 240, 0), Tuple.tuple(4, 180, 0));

	}

	// 申請時間⑧
	@SuppressWarnings("unchecked")
	@Test
	public void test28(@Mocked CreateWorkMaxTimeZone createMaxTime, @Mocked GetSubHolOccurrenceSetting getSub) {

		List<OverTimeFrameTime> overTimeWorkFrameTime = new ArrayList<OverTimeFrameTime>();
		overTimeWorkFrameTime.addAll(Arrays.asList(create(3, 300, 0), create(10, 180, 0)));
		DailyRecordOfApplication dailyApp = createRCOverCase2(overTimeWorkFrameTime);
		new Expectations() {
			{
				CreateWorkMaxTimeZone.process(require, cid, (IntegrationOfDaily) any);
				result = createRCOverCase2(overTimeWorkFrameTime);
				
				GetSubHolOccurrenceSetting.process(require, anyString, (Optional<String>) any,
						(CompensatoryOccurrenceDivision) any);
				result = ReflectApplicationHelper.createSubTrans(0, 0, 0,
						SubHolTransferSetAtr.SPECIFIED_TIME_SUB_HOL);

			}
		};

		TranferOvertimeCompensatoryApp.process(require, cid, dailyApp.getDomain());
		assertThat(
				dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
						.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTime())
								.extracting(x -> x.getOverWorkFrameNo().v(), x -> x.getOverTimeWork().getTime().v(),
										x -> x.getTransferTime().getTime().v())
								.contains(Tuple.tuple(3, 300, 0), Tuple.tuple(10, 180, 0));

	}

	public DailyRecordOfApplication createRCOverCase2(List<OverTimeFrameTime> lstFrameTime) {

		List<OverTimeFrameTimeSheet> frameTimeSheetList = new ArrayList<OverTimeFrameTimeSheet>();
		frameTimeSheetList.addAll(Arrays.asList(createTimeSpan(1, 300, 540, 0, 240),
				createTimeSpan(2, 1050, 1320, 0, 240), createTimeSpan(4, 1320, 1740, 420, 0)));
		return ReflectApplicationHelper.createRCWithTimeLeavOverTime(ScheduleRecordClassifi.RECORD, 1, lstFrameTime,
				frameTimeSheetList);
	}

	public DailyRecordOfApplication createRCOverCase() {
		List<OverTimeFrameTime> overTimeWorkFrameTime = new ArrayList<OverTimeFrameTime>();
		overTimeWorkFrameTime.addAll(Arrays.asList(create(1, 480, 0), create(4, 360, 0)));

		List<OverTimeFrameTimeSheet> frameTimeSheetList = new ArrayList<OverTimeFrameTimeSheet>();
		frameTimeSheetList.addAll(Arrays.asList(createTimeSpan(4, 300, 540, 0, 240),
				createTimeSpan(1, 930, 1320, 120, 240), createTimeSpan(4, 1320, 1740, 420, 0)));
		return ReflectApplicationHelper.createRCWithTimeLeavOverTime(ScheduleRecordClassifi.RECORD, 1,
				overTimeWorkFrameTime, frameTimeSheetList);
	}

	private OverTimeFrameTime create(int no, int over, int calc) {
		return new OverTimeFrameTime(new OverTimeFrameNo(no),
				TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(over),
						new AttendanceTime(0)),
				TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(0), new AttendanceTime(0)),
				new AttendanceTime(0), new AttendanceTime(0));
	}

	private OverTimeFrameTimeSheet createTimeSpan(int no, int start, int end, int overtimeCalc, int overTimeTransfer) {
		return new OverTimeFrameTimeSheet(
				new TimeSpanForDailyCalc(new TimeWithDayAttr(start), new TimeWithDayAttr(end)), new OverTimeFrameNo(no),
				new AttendanceTime(overtimeCalc), new AttendanceTime(overTimeTransfer));
	}
}
