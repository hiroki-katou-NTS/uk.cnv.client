package nts.uk.ctx.at.schedule.dom.workschedule.domainservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ScheManaStatuTempo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * 予定管理状態に応じて日別実績を取得する	
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務予定.勤務予定.勤務予定
 * 
 * @author Hieult
 *
 */
public class DailyResultAccordScheduleStatusService {
	

	/**
	 * 	[1] 取得する	
	 * @param require
	 * @param employeeID
	 * @param datePeriod
	 * @return Map<ScheManaStatuTempo, Optional<IntegrationOfDaily>> --- Map<社員の予定管理状態, Optional<日別勤怠(Work)>>
	 */
	//IntegrationOfDaily
	public static Map<ScheManaStatuTempo , Optional<IntegrationOfDaily>> get(Require require, List<String> lstempID , DatePeriod datePeriod ){
		Map<ScheManaStatuTempo, Optional<IntegrationOfDaily>> map = new HashMap<>();
		lstempID.stream().forEach( x ->{
			/*return 社員IDリスト:																												
			map [prv-1] 社員別に取得する( require, $, 期間 )																		
			flatMap	*/	
			long startTime = System.nanoTime();
			
			Map<ScheManaStatuTempo, Optional<IntegrationOfDaily>> data = DailyResultAccordScheduleStatusService.getByEmp(require, x, datePeriod);
			map.putAll(data);
			
			long endTime = System.nanoTime();
			long duration = (endTime - startTime) / 1000000; // ms;
			System.out.println("employee: " + duration + "ms");
		});
		return map;
	}

	/**
	 * 	[prv-1] 社員別に取得する	
	 * @param employeeID
	 * @param datePeriod
	 * @return Map<ScheManaStatuTempo, Optional<IntegrationOfDaily>> --- Map<社員の予定管理状態, Optional<日別勤怠(Work)>>
	 */
	private static Map<ScheManaStatuTempo , Optional<IntegrationOfDaily>> getByEmp(Require require, String employeeID , DatePeriod datePeriod){
		Map<ScheManaStatuTempo, Optional<IntegrationOfDaily>> map = new HashMap<>();
		datePeriod.stream().forEach(x-> {
			//$社員の予定管理状態 = 社員の予定管理状態#作成する( require, 社員ID, $ )
			ScheManaStatuTempo data = ScheManaStatuTempo.create(require, employeeID, x);
			if(!(data.getScheManaStatus().needCreateWorkSchedule())){
				//return Key: $社員の予定管理状態, Value: Optional.empty			
				 map.put(data,Optional.empty());
			}
			//$日別実績 = require.日別実績を取得する( 社員ID, $ )														
			Optional<IntegrationOfDaily> integrationOfDaily = require.getDailyResults(employeeID, x);
			map.put(data, integrationOfDaily);
		}); 
		return map;
	}

	public static interface Require extends ScheManaStatuTempo.Require {
		// 社員と日付を指定して日別勤怠(Work)を取得するアルゴリズムを利用する（1次の処理に存在するはず） --- http://192.168.50.4:3000/issues/110713
		// Tài liệu chưa chỉ rõ thuật toán 
		Optional<IntegrationOfDaily> getDailyResults(String empId , GeneralDate date);
		
	}
	
}
