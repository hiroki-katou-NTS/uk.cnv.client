package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;

/**
 * DS: 	最近よく使う作業を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.日別時間帯別実績.最近よく使う作業を取得する
 * @author ThanhPV
 */

public class GetTheWorkYouUseMostRecentlyService {
	
//■Public
	/**
	 * 	[1] 取得する
	 * @input require
	 * @input empId 社員ID	
	 * @output List<WorkGroup>	よく利用作業一覧	List<作業グループ>
	 */
	public static List<WorkGroup> get(Require require, String empId) {
		//	$対象期間 = 期間#期間(年月日#今日().月を足す(-1), 年月日#今日())  	
		DatePeriod targetPeriod = new DatePeriod(GeneralDate.today().addMonths(-1), GeneralDate.today());
		//	$実績作業一覧 = require.日別実績を取得する(社員ID, $対象期間)	
		List<OuenWorkTimeSheetOfDaily> ouenWorkTimeSheetOfDaily = require.findOuenWorkTimeSheetOfDaily(empId, targetPeriod);
		//$よく使う作業一覧 = $実績作業一覧：																		
				//filter 応援時間帯.作業内容.作業.作業内容を確認する(require) == true							
				//map groupingBy $.応援時間帯.作業内容.作業
		List<WorkGroup> workGroups = new ArrayList<WorkGroup>();
		for (OuenWorkTimeSheetOfDaily ouenWork : ouenWorkTimeSheetOfDaily) {
			workGroups.addAll(ouenWork.getOuenTimeSheet().stream()
				.filter(c-> {
					return c.getWorkContent().getWork().isPresent() 
							&& c.getWorkContent().getWork().get().checkWorkContents(require); 
				})
				.map(c->c.getWorkContent().getWork().get())
				.collect(Collectors.toList()));
		}
		
//		return $よく使う作業一覧：																				
//				map 作業利用頻度#作業利用頻度($.key,$.value.size)													
//				sort $.回数 DESC 																				
//				limit(10)																						
//				map $.作業グループ
		List<WorkUsageFrequency> workUsageFrequency = new ArrayList<WorkUsageFrequency>();
		for (WorkGroup w : workGroups) {
			Optional<WorkUsageFrequency> e = workUsageFrequency.stream().filter(c->c.getWorkGroup().compare(w)).findAny();
			if(e.isPresent()) {
				e.get().addOne();
			}else {
				workUsageFrequency.add(new WorkUsageFrequency(w, 1));
			}
		}
		
		workUsageFrequency.sort(Comparator.comparingInt(WorkUsageFrequency::getNumber).reversed());
		
		return workUsageFrequency.stream().limit(10).map(c->c.getWorkGroup()).collect(Collectors.toList());
		
	}	
	
//■Private
	
//■Require
	public static interface Require extends WorkGroup.Require {
		//[R-1] 日別実績を取得する				
		//日別実績の応援作業別勤怠時間帯Repository.get(社員,期間)	
		List<OuenWorkTimeSheetOfDaily> findOuenWorkTimeSheetOfDaily(String empId, DatePeriod targetPeriod);
	}

}
