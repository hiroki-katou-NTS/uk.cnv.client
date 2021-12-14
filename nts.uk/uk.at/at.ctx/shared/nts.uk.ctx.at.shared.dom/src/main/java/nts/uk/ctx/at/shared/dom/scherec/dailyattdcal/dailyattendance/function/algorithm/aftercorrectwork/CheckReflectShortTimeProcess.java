package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.aftercorrectwork;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.aftercorrectatt.ChangeDailyAttendanceProcess;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;

/**
 * @author thanh_nx
 *
 *         短時間勤務時間帯を反映するか確認する(new)
 */
@Stateless
public class CheckReflectShortTimeProcess {

	@Inject
	private WorkTypeRepository workTypeRepo;

	@Inject
	private ChangeDailyAttendanceProcess changeDailyAtt;

	// issues/111398
	public CheckReflectShortTimerResult check(String companyId, GeneralDate date, String employeeId,
			WorkInfoOfDailyAttendance workInfo, TimeLeavingOfDailyAttd timeLeavingOfDailyPerformance) {

		// ドメイン「勤務種類」を取得する(lấy domain 「勤務種類」)
		Optional<WorkType> workTypeOpt = workTypeRepo.findByPK(companyId,
				workInfo.getRecordInfo().getWorkTypeCode().v());

		// 取得できない
		if (!workTypeOpt.isPresent()) {
			/// 【output】確認結果 ← 反映しない
			return CheckReflectShortTimerResult.NOT_REFLECT;
		}

		WorkTypeClassification workTypeClass = workTypeOpt.get().getDailyWork().getClassification();

		boolean numberOne = false;
		boolean numberTwo = false;
		boolean numberThree = false;

		// 勤務種類から、判断方法を分岐
		if (workTypeClass == WorkTypeClassification.Attendance || workTypeClass == WorkTypeClassification.Shooting) {
			numberOne = true;
		} else if (workTypeClass == WorkTypeClassification.AnnualHoliday
				|| workTypeClass == WorkTypeClassification.YearlyReserved
				|| workTypeClass == WorkTypeClassification.SpecialHoliday
				|| workTypeClass == WorkTypeClassification.Absence
				|| workTypeClass == WorkTypeClassification.TimeDigestVacation) {
			numberTwo = true;
		} else {
			numberThree = true;
		}

		if (numberOne) {
			/// 出退勤のどちらかでも存在しているかどうかチェック
			boolean check = checkAttendanceOrGoOut(timeLeavingOfDailyPerformance);
			/// 【output】確認結果を返す
			return check ? CheckReflectShortTimerResult.REFLECT : CheckReflectShortTimerResult.NOT_REFLECT;
		} else if (!numberOne && numberTwo) {
			/// 【output】確認結果 ← 反映する
			return CheckReflectShortTimerResult.REFLECT;
		} else if (!numberOne && !numberTwo && numberThree) {
			/// 【output】確認結果 ← 反映しない
			return CheckReflectShortTimerResult.NOT_REFLECT;
		}

		/// 【output】確認結果 ← 反映しない
		return CheckReflectShortTimerResult.NOT_REFLECT;

	}

	// 出退勤のどちらかでも存在しているかどうかチェック
	public boolean checkAttendanceOrGoOut(TimeLeavingOfDailyAttd timeLeaving) {

		if (timeLeaving == null)
			return false;

		// input.日別勤怠の出退勤を受け取る
		return timeLeaving.getTimeLeavingWorks().stream().filter(att -> {
			// 出退勤の条件を確認する。
			return checkHasAtt(att) || checkHasLeav(att);
		}).findFirst().isPresent();
	}

	private boolean checkHasAtt(TimeLeavingWork timeLeav) {
		return timeLeav.getAttendanceStamp().isPresent() && timeLeav.getAttendanceStamp().get().getStamp().isPresent()
				&& timeLeav.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent();
	}

	private boolean checkHasLeav(TimeLeavingWork timeLeav) {
		return timeLeav.getLeaveStamp().isPresent() && timeLeav.getLeaveStamp().get().getStamp().isPresent()
				&& timeLeav.getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent();
	}
	
	public static enum CheckReflectShortTimerResult {
		/**
		 * 反映する
		 */
		REFLECT(1),
		/**
		 * 反映しない
		 */
		NOT_REFLECT(0);

		public final int value;

		private CheckReflectShortTimerResult(int value) {
			this.value = value;
		}

	}
}
