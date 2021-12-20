package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.List;
import java.util.Optional;

import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;

/**
 * DS: 	応援作業別勤怠時間を登録する	
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.日別時間帯別実績.応援作業別勤怠時間を登録する
 * @author ThanhPV
 */

public class RegisterOuenWorkTimeOfDailyService {
	
//■Public
	/**
	 * [1] 登録する
	 * @input require
	 * @input empId 社員ID	
	 * @input ymd 年月日
	 * @input ouenTimeSheet 作業時間	 List<日別勤怠の応援作業時間>		
	 * @output Atomtask
	 */
	public static AtomTask register(Require require,String empId, GeneralDate ymd, List<OuenWorkTimeOfDailyAttendance> ouenTimes) {
		//$実績の作業時間 = require.作業時間を取得するを取得する(社員ID,年月日)	
		Optional<OuenWorkTimeOfDaily> domain = require.getOuenWorkTimeOfDaily(empId, ymd);
		//	if $実績の作業時間.isPresent
		if(domain.isPresent()) {
			//$更新時間 = $実績の作業時間.変更する(作業時間)
			OuenWorkTimeOfDaily newDomain = OuenWorkTimeOfDaily.create(domain.get().getEmpId(), domain.get().getYmd(),
					ouenTimes);
			//	if $更新時間.応援時間.isEmpty	
			if(newDomain.getOuenTimes().isEmpty()) {
				//return require.作業時間を削除する($実績の作業時間)
				return AtomTask.of(() -> {
					require.delete(domain.get());
				});
			}else {
				//return require.作業時間を更新する($更新時間)
				return AtomTask.of(() -> {
					require.update(newDomain);
				});
			}
		}
		//$追加時間 = 日別実績の応援作業別勤怠時間#日別実績の応援作業別勤怠時間(社員ID,年月日,作業時間)
		OuenWorkTimeOfDaily ouenWorkTimeOfDailyNew = OuenWorkTimeOfDaily.create(empId, ymd, ouenTimes);
		//return require.作業時間を追加する($追加時間)
		return AtomTask.of(() -> {
			require.insert(ouenWorkTimeOfDailyNew);
		});
	}
	
//■Require
	public static interface Require {
		//[R-1] 作業時間を取得する
		//日別実績の応援作業別勤怠時間Repository.Get(社員ID,年月日)	
		Optional<OuenWorkTimeOfDaily> getOuenWorkTimeOfDaily(String empId, GeneralDate ymd);
		
		//[R-2] 作業時間を追加する
		//日別実績の応援作業別勤怠時間Repository.Insert(日別実績の応援作業別勤怠時間)	
		void insert(OuenWorkTimeOfDaily domain);
		
		//[R-3] 作業時間を更新する
		//日別実績の応援作業別勤怠時間Repository.Update(日別実績の応援作業別勤怠時間)	
		void update(OuenWorkTimeOfDaily domain);
		
		//[R-4] 作業時間を削除する
		//日別実績の応援作業別勤怠時間Repository.Delete(日別実績の応援作業別勤怠時間)							
		void delete(OuenWorkTimeOfDaily domain);
	}

}
