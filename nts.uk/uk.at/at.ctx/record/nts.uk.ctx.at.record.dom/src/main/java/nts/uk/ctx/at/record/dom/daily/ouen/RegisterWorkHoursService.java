package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;

/**
 * DS: 		作業工数を登録する	
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.日別時間帯別実績.作業工数を登録する	
 * @author ThanhPV
 */

public class RegisterWorkHoursService {
	
//■Public
	/**
	 * 	[1] 登録する
	 * @input require 
	 * @input cId 会社ID	
	 * @input empId 社員ID	
	 * @input ymd 年月日
	 * @input editStateOfDailyAttd 編集状態	
	 * @input workDetailsParams 作業詳細一覧
	 * @output AtomTask
	 */
	public static ManHourInputResult register(Require require, String cId, String empId, GeneralDate ymd, EditStateSetting editStateSetting, List<WorkDetailsParam> workDetailsParams) {
		List<AtomTask> atomTasks = new ArrayList<>();
		Optional<IntegrationOfDaily> integrationOfDaily = Optional.empty();
		List<OuenWorkTimeSheetOfDailyAttendance> ouenWorkTimeSheetOfDailyAttendance = new ArrayList<>();
		//if 作業詳細一覧.isPresent
		if(!workDetailsParams.isEmpty()) {
			//$作業時間帯 = 応援作業別勤怠時間帯を作成する#作成する(require,社員ID,年月日,作業詳細一覧)
			ouenWorkTimeSheetOfDailyAttendance = CreateAttendanceTimeZoneForEachSupportWork.create(require, empId, ymd, workDetailsParams);
			//$計算結果 = 応援作業別勤怠時間を計算する#計算する(require,社員ID,年月日,$作業時間帯)
			integrationOfDaily = CalculateAttendanceTimeBySupportWorkService.calculate(require, empId, ymd, ouenWorkTimeSheetOfDailyAttendance);
		}
		//List<AtomTask> $登録対象	
		//$登録対象.add(応援作業別勤怠時間帯を登録する#登録する(require,社員ID,年月日,$作業時間帯,編集状態)
		atomTasks.add(RegisterOuenWorkTimeSheetOfDailyService.register(require, empId, ymd, ouenWorkTimeSheetOfDailyAttendance, editStateSetting));
		//	if $計算結果.isPresent
		if(integrationOfDaily.isPresent()) {
			//	$登録対象.add(応援作業別勤怠時間を登録する#登録する(require,社員ID,年月日,$計算結果.応援時間)
			atomTasks.add(RegisterOuenWorkTimeOfDailyService.register(require, empId, ymd, integrationOfDaily.get().getOuenTime()));
			//$エラーコード = 'T001'	
			String errorCode = "T001";
			//$登録対象.add(require.エラーを削除する(社員ID,年月日,$エラーコード))
			atomTasks.add(AtomTask.of(() -> {
				require.delete(empId, ymd, errorCode);
				}
			));
			//$アラーム対象日 = $計算結果.エラー一覧：fillter $.勤務実績のエラーアラームコード = $エラーコード		
			List<EmployeeDailyPerError> employeeError = integrationOfDaily.get().getEmployeeError().stream().filter(c->c.getErrorAlarmWorkRecordCode().v().equals(errorCode)).collect(Collectors.toList());
			//if $アラーム対象日.isPresent		
			if(!employeeError.isEmpty()){
				//$日別実績エラー = 社員の日別実績エラー一覧#社員の日別実績エラー一覧(会社ID,年月日,社員ID,$エラーコード,Optional.empty,Optional.empty)												
				EmployeeDailyPerError employeeDailyPerError = new EmployeeDailyPerError(cId, empId, ymd, new ErrorAlarmWorkRecordCode(errorCode), new ArrayList<>());
				//$登録対象.add(require.エラーを登録する($日別実績エラー))
				atomTasks.add(AtomTask.of(() -> {
					require.insert(employeeDailyPerError);
				}));
			}
		}
		//	return 工数入力結果#工数入力結果(AtomTask.bundle($登録対象),$計算結果)	
		return new ManHourInputResult(AtomTask.bundle(atomTasks), integrationOfDaily);
	}
	
//■Private
//■Require
	public static interface Require extends CreateAttendanceTimeZoneForEachSupportWork.Require, CalculateAttendanceTimeBySupportWorkService.Require, RegisterOuenWorkTimeSheetOfDailyService.Require, RegisterOuenWorkTimeOfDailyService.Require {
		//[R-1] エラーを削除する		ver2
		//社員の日別実績エラー一覧Repository.Delete(エラー発生社員,処理年月日,勤務実績のエラーアラームコード)	
		void delete(String sid, GeneralDate date, String code);
		
		//[R-2] エラーを登録する		ver2
		//社員の日別実績エラー一覧Repository.Insert(社員の日別実績エラー一覧)		
		void insert(EmployeeDailyPerError employeeDailyPerformanceError);
	}
}
