package nts.uk.screen.at.app.ksu001.achievementorschedule;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.screen.at.app.ksu001.aggreratepersonaltotal.AggregatePersonalDto;
import nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal.AggregateWorkplaceDto;
import nts.uk.screen.at.app.ksu001.getshiftpalette.ShiftMasterDto;
import nts.uk.screen.at.app.ksu001.getworkscheduleshift.ScheduleOfShiftDto;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AchievementOrScheduleDto {

	// List<勤務予定（シフト）dto>
	public List<ScheduleOfShiftDto> listWorkScheduleShift = Collections.emptyList();    
	
	// Map<シフトマスタ, Optional<出勤休日区分>>
	public Map<ShiftMasterDto, Integer> mapShiftMasterWithWorkStyle; 
	
	// 個人計集計結果　←集計内容によって情報が異なる
	public AggregatePersonalDto aggreratePersonal;
	
	// ・職場計集計結果　←集計内容によって情報が異なる
	public AggregateWorkplaceDto aggrerateWorkplace;
	
	// List<勤務予定（勤務情報）dto>
	public List<WorkScheduleWorkInforDto> workScheduleWorkInforDtos = Collections.emptyList();
}
