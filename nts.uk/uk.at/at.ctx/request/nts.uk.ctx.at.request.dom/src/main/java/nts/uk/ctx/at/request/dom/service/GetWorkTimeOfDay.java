package nts.uk.ctx.at.request.dom.service;

import java.util.Optional;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ScBasicScheduleImport;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

/**
 * @author thanh_nx
 *
 *         該当日の就業時間帯を取得する
 */
public class GetWorkTimeOfDay {

	// 就業時間帯を取得する
	public static Optional<String> getWorkInfoFromSetting(Require require, String companyId, String employeeId,
			GeneralDate date, Optional<String> workTypeCode, Optional<String> workTimeCode) {

		Optional<String> workTypeCodeProcess = workTypeCode;
		// 申請から就業時間帯コードを反映するかどうか
		if (workTimeCode.isPresent()) {
			return workTimeCode;
		}

		Optional<WorkInfoOfDailyAttendance> workInfoOfDailyRecord = require.getWorkInfoOfDailyAttendance(employeeId,
				date);

		if (workInfoOfDailyRecord.isPresent()
				&& workInfoOfDailyRecord.get().getRecordInfo().getWorkTimeCodeNotNull().isPresent()) {
			return workInfoOfDailyRecord.get().getRecordInfo().getWorkTimeCodeNotNull().map(x -> x.v());
		}

		workTypeCodeProcess = updateWorkTypeCode(workTypeCodeProcess,
				workInfoOfDailyRecord.map(x -> x.getRecordInfo().getWorkTypeCode().v()));

		val schedule = require.findByIDRefactor(employeeId, date);
		if (schedule.isPresent() &&  schedule.get().getWorkTimeCode().isPresent()) {
			return schedule.get().getWorkTimeCode();
		}

		workTypeCodeProcess = updateWorkTypeCode(workTypeCodeProcess, schedule.map(x -> x.getWorkTypeCode()));

		return getWorkInfoFromSetting(require, companyId, employeeId, date, workTypeCodeProcess)
				.map(x -> x.getWorkTypeCode().v());
	}

	private static Optional<String> updateWorkTypeCode(Optional<String> workTypeCodeBefore,
			Optional<String> workTypeCodeAfter) {
		if (workTypeCodeBefore.isPresent()) {
			return workTypeCodeBefore;
		}

		return workTypeCodeAfter;
	}

	// 勤務情報を取得する
	private static Optional<WorkInformation> getWorkInfoFromSetting(Require require, String companyId, String employeeId,
			GeneralDate date, Optional<String> workTypeCode) {
		if (!workTypeCode.isPresent()) {
			return Optional.empty();
		}
		val workType = require.findByPK(companyId, workTypeCode.get());
		if (!workType.isPresent()) {
			return Optional.empty();
		}

		val workCondition = require.getWorkingConditionItemByEmpIDAndDate(companyId, date, employeeId);
		if (!workCondition.isPresent()) {
			return Optional.empty();
		}

		// 就業時間帯コードを取得する
		return workCondition.get().getWorkCategory().getWorkInfoFromSetting(companyId, employeeId, date,
				workType.get());

	}

	public static interface Require {

		// 勤務予定を取得する
		public Optional<ScBasicScheduleImport> findByIDRefactor(String employeeID, GeneralDate date);

		// WorkScheWorkInforSharedAdapter
		// 日別勤怠(Work)を取得する
		public Optional<WorkInfoOfDailyAttendance> getWorkInfoOfDailyAttendance(String employeeId, GeneralDate ymd);

		//労働条件項目を取得する
		public Optional<WorkingConditionItem> getWorkingConditionItemByEmpIDAndDate(String companyID, GeneralDate ymd,
				String empID);

		//勤務種類を取得する
		public Optional<WorkType> findByPK(String companyId, String workTypeCd);
	}
}
