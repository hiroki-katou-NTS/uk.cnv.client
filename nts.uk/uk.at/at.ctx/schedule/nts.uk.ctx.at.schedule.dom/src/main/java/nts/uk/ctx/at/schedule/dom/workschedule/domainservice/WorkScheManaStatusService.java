package nts.uk.ctx.at.schedule.dom.workschedule.domainservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ScheManaStatuTempo;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;


/**
 * «DomainService» 予定管理状態に応じて勤務予定を取得する
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務予定.勤務予定.勤務予定
 * 
 * @author HieuLt
 *
 */
public class WorkScheManaStatusService {

	/**
	 * [1] 取得する
	 * @param require
	 * @param lstEmployeeID
	 * @return Map<社員の予定管理状態, Optional<勤務予定>>
	 */
	public static Map<ScheManaStatuTempo, Optional<WorkSchedule>> getScheduleManagement(Require require,
			List<String> lstEmployeeID, DatePeriod period) {
		/*	return 社員IDリスト:																										
			map [prv-1] 社員別に取得する( require, $, 期間 )																
			flatMap		*/												
		Map<ScheManaStatuTempo, Optional<WorkSchedule>> map = new HashMap<>();
		for (int i = 0; i < lstEmployeeID.size(); i++) {
			long start = System.nanoTime();
			
			map.putAll(WorkScheManaStatusService.getByEmployee(require,lstEmployeeID.get(i), period));
			
			System.out.println("employee: " + ((System.nanoTime() - start )/1000000) + "ms");	

		}
		
		return  map;
	}

	/**
	 * [prv-1] 社員別に取得する
	 * @param employeeID
	 * @param datePeriod
	 * @return Map<社員の予定管理状態, Optional<勤務予定>>
	 */
	private static Map<ScheManaStatuTempo, Optional<WorkSchedule>> getByEmployee(Require require,String employeeID, DatePeriod datePeriod) {
		Map<ScheManaStatuTempo, Optional<WorkSchedule>> map = new HashMap<>();
		
		//期間.stream():
		datePeriod.datesBetween().stream().forEach(x->{
			//	map		$社員の予定管理状態 = 社員の予定管理状態#作成する( require, 社員ID, $ )
			ScheManaStatuTempo zScheManaStatuTempo =  ScheManaStatuTempo.create(require, employeeID, x);
			
			if(!zScheManaStatuTempo.getScheManaStatus().needCreateWorkSchedule()){
				 map.put(zScheManaStatuTempo, Optional.empty());
				 return;
			}
			//$勤務予定 = require.勤務予定を取得する( 社員ID, $ )	
			Optional<WorkSchedule> zWorkSchedule  = require.get(employeeID, x);
			//return Key: $社員の予定管理状態, Value: $勤務予定															
			map.put(zScheManaStatuTempo, zWorkSchedule);
		});
		return map;
	}

	public static interface Require extends ScheManaStatuTempo.Require{
		/**
		 * R-1] 勤務予定を取得する
		 * @param employeeID
		 * @param ymd
		 * @return
		 */
		Optional<WorkSchedule> get(String employeeID, GeneralDate ymd);
	}
}
