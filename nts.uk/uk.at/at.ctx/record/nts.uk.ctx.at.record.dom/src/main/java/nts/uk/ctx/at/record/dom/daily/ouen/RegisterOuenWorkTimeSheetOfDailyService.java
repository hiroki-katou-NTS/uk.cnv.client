package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.editstate.EditStateOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;

/**
 * DS: 応援作業別勤怠時間帯を登録する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.日別時間帯別実績.応援作業別勤怠時間帯を登録する
 * @author ThanhPV
 */

public class RegisterOuenWorkTimeSheetOfDailyService {
	
//■Public
	/**
	 * 	[prv-1] 編集状態を作成する
	 * @input require
	 * @input empId 社員ID	
	 * @input ymd 年月日
	 * @input ouenWorkTimeSheetOfDaily 作業時間帯
	 * @input editStateOfDailyAttd 編集状態
	 * @output Atomtask
	 */
	public static AtomTask register(Require require, String empId, GeneralDate ymd, List<OuenWorkTimeSheetOfDailyAttendance> ouenWorkTimeSheetOfDailys, EditStateSetting editStateSetting) {
		//List<AtomTask> $登録対象	
		List<AtomTask> atomTasks = new ArrayList<>();
		//	$実績の作業時間帯 = require.作業時間帯を取得するを取得する(社員ID,年月日)
		Optional<OuenWorkTimeSheetOfDaily> ouenWorkTimeSheetOfDaily = require.findOuenWorkTimeSheetOfDaily(empId, ymd);
		//	$実績の編集状態 = require.編集状態を取得するを取得する(社員ID,年月日)																
		List<EditStateOfDailyPerformance> editStateOfDailyPerformance = require.getEditStateOfDailyPerformance(empId, ymd);
		//	if $実績の作業時間帯.isPresent
		if(ouenWorkTimeSheetOfDaily.isPresent()) {
			OuenWorkTimeSheetOfDaily OldtimeSheet = new OuenWorkTimeSheetOfDaily(ouenWorkTimeSheetOfDaily.get().getEmpId(),
					ouenWorkTimeSheetOfDaily.get().getYmd(), ouenWorkTimeSheetOfDaily.get().getOuenTimeSheet());
			//	$変更結果 = $実績の作業時間帯.変更する(作業時間帯)
			AttendanceItemToChange attendanceItemToChange = ouenWorkTimeSheetOfDaily.get().change(ouenWorkTimeSheetOfDailys);
			//	$更新時間帯 = $変更結果.応援作業別時間帯.応援時間帯	
			List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheet = attendanceItemToChange.getOuenWorkTimeSheetOfDaily().getOuenTimeSheet();
			//		if $更新時間帯.isEmpty	
			if(ouenTimeSheet.isEmpty()) {
				//	$登録対象.add(require.作業時間帯を削除する($実績の作業時間帯))
				atomTasks.add(AtomTask.of(() -> require.delete(OldtimeSheet)));
			}else {
				//$登録対象.add(require.作業時間帯を更新する($更新時間帯))		
				atomTasks.add(AtomTask.of(() -> require.update(ouenWorkTimeSheetOfDaily.get())));
			}
			//	$変更結果.勤怠項目リスト:	
			for (Integer id : attendanceItemToChange.getAttendanceId()) {
				//$編集状態 = [prv-1] 編集状態を作成する($実績の編集状態,社員ID,年月日,編集状態,$)
				//	$登録対象.add($編集状態)	
				atomTasks.add(createEditState(require, editStateOfDailyPerformance, empId, ymd, editStateSetting, id));
			}
		}else {
			//	$追加時間帯 = 日別実績の応援作業別勤怠時間帯#日別実績の応援作業別勤怠時間帯(社員ID,年月日,作業時間帯)
			OuenWorkTimeSheetOfDaily domain = new OuenWorkTimeSheetOfDaily(empId, ymd, ouenWorkTimeSheetOfDailys);
			//	$登録対象.add(require.作業時間帯を追加する($追加時間帯))
			atomTasks.add(AtomTask.of(() -> require.insert(domain)));
			//	$勤怠項目一覧 = $追加時間帯.応援時間帯に対応する勤怠項目ID一覧()	
			List<Integer> attendanceId = domain.getAttendanceId();
			//	$勤怠項目一覧:	
			for (Integer id : attendanceId) {
				//$編集状態 = [prv-1] 編集状態を作成する($実績の編集状態,社員ID,年月日,編集状態,$)				
				//$登録対象.add($編集状態)
				atomTasks.add(createEditState(require, editStateOfDailyPerformance, empId, ymd, editStateSetting, id));
			}
		}
		//return AtomTask.bundle($登録対象)
		return AtomTask.bundle(atomTasks);
	}
	
//■Private
	/**
	 * 	[prv-1] 編集状態を作成する
	 * @input require
	 * @input editStateOfDailyPerformance 	編集状態リスト
	 * @input empId 社員ID	
	 * @input ymd 年月日
	 * @input editStateOfDailyAttd 編集状態
	 * @input attendanceId 勤怠項目ID
	 * @output Atomtask
	 */
	private static AtomTask createEditState(Require require, List<EditStateOfDailyPerformance> editStateOfDailyPerformance, String empId, GeneralDate ymd, EditStateSetting editStateSetting, int attendanceId) {
		//	[mapping]
		EditStateOfDailyPerformance domain = new EditStateOfDailyPerformance(empId, attendanceId, ymd, editStateSetting);
		
		//if 編集状態リスト.編集状態.勤怠項目ID.含む(勤怠項目ID)
		if(editStateOfDailyPerformance.stream().map(c->c.getEditState().getAttendanceItemId()).collect(Collectors.toList()).contains(attendanceId)) {
			//	return require.編集状態を更新する($日別実績の編集状態)	
			return AtomTask.of(() -> {
				require.update(domain);
			});
		}
		//return require.編集状態を追加する($日別実績の編集状態)
		return AtomTask.of(() -> {
			require.insert(domain);
		});
	}
	
	
//■Require
	public static interface Require {
		//[R-1] 作業時間帯を取得する
		//日別実績の応援作業別勤怠時間帯Repository.Get(社員ID,年月日)			
		Optional<OuenWorkTimeSheetOfDaily> findOuenWorkTimeSheetOfDaily(String empId, GeneralDate ymd);
		//[R-2] 編集状態を取得する
		//日別実績の編集状態Repository.Get(社員ID,年月日)		
		List<EditStateOfDailyPerformance> getEditStateOfDailyPerformance(String empId, GeneralDate ymd);
		//[R-3] 作業時間帯を追加する
		//日別実績の応援作業別勤怠時間帯Repository.Insert(日別実績の応援作業別勤怠時間帯)		
		void insert(OuenWorkTimeSheetOfDaily domain);
		//[R-4] 作業時間帯を更新する
		//日別実績の応援作業別勤怠時間帯Repository.Update(日別実績の応援作業別勤怠時間帯)		
		void update(OuenWorkTimeSheetOfDaily domain);
		//[R-5] 作業時間帯を削除する
		//日別実績の応援作業別勤怠時間帯Repository.Delete(日別実績の応援作業別勤怠時間帯)		
		void delete(OuenWorkTimeSheetOfDaily domain);
		//[R-6] 編集状態を追加する
		//日別実績の編集状態Repository.Insert(日別実績の編集状態)					
		void insert(EditStateOfDailyPerformance domain);
		//[R-7] 編集状態を更新する
		//日別実績の編集状態Repository.Update(日別実績の編集状態)			
		void update(EditStateOfDailyPerformance domain);
	}

}
