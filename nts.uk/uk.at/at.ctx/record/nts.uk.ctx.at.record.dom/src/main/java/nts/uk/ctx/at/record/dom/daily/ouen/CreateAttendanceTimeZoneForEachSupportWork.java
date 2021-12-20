package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;

/**
 * DS: 	応援作業別勤怠時間帯を作成する	
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.日別時間帯別実績.応援作業別勤怠時間帯を作成する
 * @author ThanhPV
 */

public class CreateAttendanceTimeZoneForEachSupportWork {
	
//■Public
	/**
	 * 	[prv-1] 編集状態を作成する
	 * @input require
	 * @input empId 社員ID	
	 * @input ymd 年月日
	 * @input workDetailsParams 作業詳細	
	 * @output 	日別勤怠の応援作業時間帯 	OuenWorkTimeSheetOfDailyAttendance
	 */
	public static List<OuenWorkTimeSheetOfDailyAttendance> create(Require require, String empId, GeneralDate ymd,
			List<WorkDetailsParam> workDetailsParams) {

		// $旧の応援作業 = require.応援作業別勤怠時間帯を取得する(社員ID,年月日)
		OuenWorkTimeSheetOfDaily ouenWorkTimeSheetOfDaily = require.find(empId, ymd);

		return workDetailsParams.stream().map(wd -> {
			// $旧の作業時間帯 = $旧の応援作業.応援時間帯：filter 応援勤務枠No == $.応援勤務枠No

			Optional<OuenWorkTimeSheetOfDailyAttendance> oldTime = ouenWorkTimeSheetOfDaily == null ? Optional.empty()
					: ouenWorkTimeSheetOfDaily.getOuenTimeSheet().stream()
							.filter(oen -> oen.getWorkNo() == wd.getSupportFrameNo().v()).findFirst();
			//	[prv-1] 応援作業時間帯を作成する(require,社員ID,年月日,$,$旧の作業時間帯)	
			return createSupportWorkTimeZone(require, empId, ymd, wd, oldTime);

		}).collect(Collectors.toList());

	}
	
//■Private
	
	/**
	 * @name [prv-1] 応援作業時間帯を作成する
	 * @input empId 社員ID
	 * @input ymd 年月日
	 * @input workDetailsParam  作業詳細	
	 * @input ouenWorkTimeOfDaily 旧の作業時間帯
	 * @output 	日別勤怠の応援作業時間帯
	 */
	private static OuenWorkTimeSheetOfDailyAttendance createSupportWorkTimeZone(Require require, String empId, GeneralDate ymd, WorkDetailsParam workDetailsParam, Optional<OuenWorkTimeSheetOfDailyAttendance> ouenWorkTimeSheetOfDailyAttendance) {
		//作業詳細.作業グループ.作業内容の有効期限を確認する(require,年月日) anh tuấn trả lời: khi naof co thi no moi goi, nen chac ko can dau
		workDetailsParam.getWorkGroup().get().checkExpirationDate(require, ymd);
		
		//if $旧の作業時間帯.isPresent
		if(ouenWorkTimeSheetOfDailyAttendance.isPresent()) {
			//		return [prv-2] 既存の応援作業時間帯をセットする($旧の作業時間帯.応援時間帯,作業詳細)
			//đã xác nhận lại với anh tuấn: http://192.168.50.4:3000/issues/115993
			return setExistingSupportWorkTimeZone(ouenWorkTimeSheetOfDailyAttendance.get(), workDetailsParam);
		}
		//	return [prv-3] 新規の応援作業時間帯を作成する(require,社員ID,年月日,作業詳細)
		return createNewOuenWorkTimeSheetOfDailyAttendance(require, empId, ymd, workDetailsParam);
	}
	
	/**
	 * @name 	[prv-2] 既存の応援作業時間帯をセットする
	 * @input oldOuenWorkTimeSheetOfDailyAttendance 	旧の作業時間帯
	 * @input workDetailsParam		作業詳細
	 * @output 	日別勤怠の応援作業時間帯 OuenWorkTimeSheetOfDailyAttendance
	 */
	private static OuenWorkTimeSheetOfDailyAttendance setExistingSupportWorkTimeZone(OuenWorkTimeSheetOfDailyAttendance oldOuenWorkTimeSheetOfDailyAttendance, WorkDetailsParam workDetailsParam) {
		oldOuenWorkTimeSheetOfDailyAttendance.setWorkNo(workDetailsParam.getSupportFrameNo().v());
		oldOuenWorkTimeSheetOfDailyAttendance.getTimeSheet().setStart(workDetailsParam.getTimeZone().getStart());
		oldOuenWorkTimeSheetOfDailyAttendance.getTimeSheet().setEnd(workDetailsParam.getTimeZone().getEnd());
		oldOuenWorkTimeSheetOfDailyAttendance.getWorkContent().getWorkplace().setWorkLocationCD(workDetailsParam.getWorkLocationCD());
		oldOuenWorkTimeSheetOfDailyAttendance.getWorkContent().setWork(workDetailsParam.getWorkGroup());
		oldOuenWorkTimeSheetOfDailyAttendance.getWorkContent().setWorkRemarks(workDetailsParam.getRemarks());
		return oldOuenWorkTimeSheetOfDailyAttendance;
	}
	
	/**
	 * @name [prv-3] 新規の応援作業時間帯を作成する
	 * @input empId 社員ID
	 * @input ymd 年月日
	 * @input workDetailsParam  作業詳細	
	 * @output 	日別勤怠の応援作業時間帯
	 */
	private static OuenWorkTimeSheetOfDailyAttendance createNewOuenWorkTimeSheetOfDailyAttendance(Require require, String empId, GeneralDate ymd, WorkDetailsParam workDetailsParam) {
		//	$職場ID = require.所属職場を取得する(社員ID,年月日)
		String workplateID = require.getAffWkpHistItemByEmpDate(empId, ymd);
		return OuenWorkTimeSheetOfDailyAttendance.create(workDetailsParam.getSupportFrameNo().v(), 
				WorkContent.create(WorkplaceOfWorkEachOuen.create(new WorkplaceId(workplateID), workDetailsParam.getWorkLocationCD().orElse(null)), workDetailsParam.getWorkGroup(), workDetailsParam.getRemarks()), 
				//đã xác nhận QA: http://192.168.50.4:3000/issues/115977
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1), Optional.ofNullable(workDetailsParam.getTimeZone().getStart()), Optional.ofNullable(workDetailsParam.getTimeZone().getEnd())));
	}
	
//■Require
	public static interface Require extends WorkGroup.Require {
		//[R-1] 応援作業別勤怠時間帯を取得する
		//日別実績の応援作業別勤怠時間帯Repository.取得する(社員ID,年月日)	
		OuenWorkTimeSheetOfDaily find(String empId, GeneralDate ymd);
		//[R-2] 所属職場を取得する
		//所属職場履歴Adapter.取得する(社員ID,年月日)	
		String getAffWkpHistItemByEmpDate(String employeeID, GeneralDate date);
	}

}
