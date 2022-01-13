package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.worktype.DailyActualDayCount;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).月の勤怠計算.月別集計処理.縦計方法.休暇取得時の出勤日数カウント
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WorkDaysNumberOnLeaveCount extends AggregateRoot {

	// 会社ID						
	private final String cid;

	// カウントする休暇一覧						
	private final List<LeaveCountedAsWorkDaysType> countedLeaveList;
	
	public WorkDaysNumberOnLeaveCount(String cid, List<LeaveCountedAsWorkDaysType> countedLeaveList) {
		super();
		this.cid = cid;
		this.countedLeaveList = countedLeaveList;
		
		if (checkVacationType(LeaveCountedAsWorkDaysType.SPECIAL_VACATION)
				|| checkVacationType(LeaveCountedAsWorkDaysType.ANNUAL_LEAVE)
				|| checkVacationType(LeaveCountedAsWorkDaysType.ACCUMULATED_ANNUAL_LEAVE)) {
			
			throw new RuntimeException("同じ種類のカウントする休暇が2件以上存在している");
		}
	}

	private boolean checkVacationType(LeaveCountedAsWorkDaysType type) {
		return countedLeaveList.stream().filter(c -> c == type).collect(Collectors.toList()).size() > 1;
	}
	
	/** [1] 休暇時の日数カウントを計算する */
	public DailyActualDayCount countDaysOnHoliday(WorkType workType) {
		
		/** return $日数カウント */
		return new DailyActualDayCount(countedLeaveList.stream().mapToDouble(c -> {

			/** $勤務種類の分類 = $.勤務種類の分類を作成する() */
			val workTypeClass = c.toWorkTypeClassification();
			
			/** $1日午前午後区分 = 勤務種類. 指定の分類の1日午前午後区分を取得($勤務種類の分類) */
			val oneDayAtr = workType.getWorkAtrForWorkTypeClassification(workTypeClass);
			
			return oneDayAtr.map(o -> o.countDays().v()).orElse(0d);
			
		}).sum());
	}
}
