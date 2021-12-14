package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.RecordRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.TimeDigestionUsageInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.AppTimeType;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.VacationTimeUseInfor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.OverTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.HolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;

/**
 * @author thanh_nx
 *
 *         残数作成元情報を作成する
 */
public class CreateRemainInformation {

	public static RecordRemainCreateInfor create(Require require, String cid, IntegrationOfDaily domainDaily,
			SubsTransferProcessMode processMode) {

		// 残業振替時間の合計を算出する
		int transferOvertimesTotal = getTotalOvertimeTransferTime(require, cid, domainDaily, processMode);

		// 休出振替時間の合計を算出する
		int transferTotal = getTransferTotal(require, cid, domainDaily, processMode);

		// 時間休暇使用情報を作成する
		List<VacationTimeUseInfor> lstVacUseInfo = getLstVacationTimeInfor(
				domainDaily.getAttendanceTimeOfDailyPerformance());

		// 時間消化使用情報を作成する
		TimeDigestionUsageInfor timeDigestUsage = getTimeDigestionUsageInfor(
				domainDaily.getAttendanceTimeOfDailyPerformance());
		return new RecordRemainCreateInfor(domainDaily.getEmployeeId(), domainDaily.getYmd(),
				domainDaily.getWorkInformation().getRecordInfo().getWorkTypeCode().v(), lstVacUseInfo, timeDigestUsage,
				transferOvertimesTotal, transferTotal,
				domainDaily.getWorkInformation().getRecordInfo().getWorkTimeCodeNotNull().map(x -> x.v()),
				domainDaily.getWorkInformation().getNumberDaySuspension());
	}

	// 残業振替時間の合計を算出する
	private static int getTotalOvertimeTransferTime(Require require, String cid, IntegrationOfDaily domainDaily,
			SubsTransferProcessMode processMode) {
		Optional<OverTimeOfDaily> overTimeWork = domainDaily.getAttendanceTimeOfDailyPerformance()
				.flatMap(x -> x.getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily()
						.getOverTimeWork());
		if (!overTimeWork.isPresent()) {
			// 振替残業時間合計をクリアする
			return 0;
		}
		// 残業時間の代休振替を呼ぶかどうか
		if (overTimeWork.get().tranferOvertimeCompenCall(processMode)) {
			// input.日別勤怠(work）から残業枠時間（List）の内容を移送し、[申請を反映させた後の時間]へセットする
			List<OvertimeHdHourTransfer> timeAfterReflectApp = overTimeWork.get().getOverTimeWorkFrameTime().stream()
					.map(x -> {
						return new OvertimeHdHourTransfer(x.getOverWorkFrameNo().v(), x.getBeforeApplicationTime(),
								new AttendanceTime(0));
					}).collect(Collectors.toList());
			domainDaily = TranferOvertimeCompensatory.process(require, cid, domainDaily, timeAfterReflectApp);
		}

		Optional<OverTimeOfDaily> overTimeWorkOutput = domainDaily.getAttendanceTimeOfDailyPerformance()
				.flatMap(x -> x.getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily()
						.getOverTimeWork());
		return overTimeWorkOutput.map(x -> x.calcTransTotalFrameTime().v()).orElse(0);
	}

	// 休出振替時間の合計を算出する
	private static int getTransferTotal(Require require, String cid, IntegrationOfDaily domainDaily,
			SubsTransferProcessMode processMode) {

		Optional<HolidayWorkTimeOfDaily> workHolidayTime = domainDaily.getAttendanceTimeOfDailyPerformance()
				.flatMap(x -> x.getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily()
						.getWorkHolidayTime());
		if (!workHolidayTime.isPresent()) {
			// 休出振替時間合計をクリアする
			return 0;
		}

		if (workHolidayTime.get().tranferHdWorkCompenCall(processMode)) {
			// input.日別勤怠(work）から休出枠時間（List）の内容を移送し、[申請を反映させた後の時間]へセットする
			List<OvertimeHdHourTransfer> timeAfterReflectApp = workHolidayTime.get().getHolidayWorkFrameTime().stream()
					.map(x -> {
						return new OvertimeHdHourTransfer(x.getHolidayFrameNo().v(),
								x.getBeforeApplicationTime().isPresent() ? x.getBeforeApplicationTime().get() : new AttendanceTime(0),
								new AttendanceTime(0));
					}).collect(Collectors.toList());
			domainDaily = TranferHdWorkCompensatory.process(require, cid, domainDaily, timeAfterReflectApp);
		}

		Optional<HolidayWorkTimeOfDaily> workHolidayTimeOutput = domainDaily.getAttendanceTimeOfDailyPerformance()
				.flatMap(x -> x.getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily()
						.getWorkHolidayTime());
		// 休出振替時間合計を設定する
		return workHolidayTimeOutput.map(x -> x.calcTransTotalFrameTime().v()).orElse(0);
	}

	/**
	 * 時間休暇使用情報を作成する
	 * 
	 * @param optional
	 * @return List<時間休暇使用情報>
	 */
	private static List<VacationTimeUseInfor> getLstVacationTimeInfor(
			Optional<AttendanceTimeOfDailyAttendance> attenTime) {

		List<VacationTimeUseInfor> result = new ArrayList<VacationTimeUseInfor>();

		if (!attenTime.isPresent()) {
			return result;
		}

		List<LateTimeOfDaily> lateTimes = attenTime.get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
				.getLateTimeOfDaily();

		if (!lateTimes.isEmpty()) {
			// 日別勤怠の遅刻時間.勤怠NO = 1
			lateTimes.stream().filter(x -> x.getWorkNo().v() == 1).findFirst().ifPresent(x -> {
				result.add(VacationTimeUseInfor.fromLateDomain(x, AppTimeType.ATWORK));
			});

			// 日別勤怠の遅刻時間.勤怠NO = 2
			lateTimes.stream().filter(x -> x.getWorkNo().v() == 2).findFirst().ifPresent(x -> {
				result.add(VacationTimeUseInfor.fromLateDomain(x, AppTimeType.ATWORK2));
			});
		}

		List<LeaveEarlyTimeOfDaily> earlyTimes = attenTime.get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
				.getLeaveEarlyTimeOfDaily();

		if (!earlyTimes.isEmpty()) {
			// 日別勤怠の早退時間.勤怠NO = 1
			earlyTimes.stream().filter(x -> x.getWorkNo().v() == 1).findFirst().ifPresent(x -> {
				result.add(VacationTimeUseInfor.fromEarlyDomain(x, AppTimeType.OFFWORK));
			});

			// 日別勤怠の早退時間.勤怠NO = 2
			earlyTimes.stream().filter(x -> x.getWorkNo().v() == 2).findFirst().ifPresent(x -> {
				result.add(VacationTimeUseInfor.fromEarlyDomain(x, AppTimeType.OFFWORK2));
			});
		}

		List<OutingTimeOfDaily> outingTimes = attenTime.get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
				.getOutingTimeOfDailyPerformance();

		if (!outingTimes.isEmpty()) {
			// 日別勤怠の外出時間.外出時間 = 私用
			outingTimes.stream().filter(x -> x.getReason().equals(GoingOutReason.PRIVATE)).findFirst().ifPresent(x -> {
				result.add(VacationTimeUseInfor.fromOutDomain(x, AppTimeType.PRIVATE));
			});

			// 日別勤怠の外出時間.外出時間 = 組合
			outingTimes.stream().filter(x -> x.getReason().equals(GoingOutReason.UNION)).findFirst().ifPresent(x -> {
				result.add(VacationTimeUseInfor.fromOutDomain(x, AppTimeType.UNION));
			});
		}

		return result;
	}

	/**
	 * 時間消化使用情報を作成する
	 * 
	 * @param attenTime
	 * @return 時間消化使用情報
	 */
	private static TimeDigestionUsageInfor getTimeDigestionUsageInfor(
			Optional<AttendanceTimeOfDailyAttendance> attenTime) {

		TimeDigestionUsageInfor result = new TimeDigestionUsageInfor();
		if (!attenTime.isPresent()) {
			return result;
		}

		HolidayOfDaily holiday = attenTime.get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
				.getHolidayOfDaily();
		// 時間年休使用時間 = 年休.時間消化休暇使用時間
		result.setNenkyuTime(holiday.getAnnual().getDigestionUseTime().v());
		// 時間代休使用時間 = 代休.時間消化休暇使用時間
		result.setKyukaTime(holiday.getSubstitute().getDigestionUseTime().v());
		// 60H超休使用時間 = 超過有休.超過有休使用時間
		result.setHChoukyuTime(holiday.getOverSalary().getDigestionUseTime().v());
		// 子の看護使用時間 = 子の看護介護．有償休暇．子の看護＋子の看護介護．無償休暇．子の看護
		result.setChildCareTime(0);
		// 介護使用時間 = 子の看護介護．有償休暇．介護＋子の看護介護．無償休暇．介護
		result.setLongCareTime(0);

		return result;
	}

	public static interface Require extends TranferOvertimeCompensatory.Require, TranferHdWorkCompensatory.Require {

	}

}
