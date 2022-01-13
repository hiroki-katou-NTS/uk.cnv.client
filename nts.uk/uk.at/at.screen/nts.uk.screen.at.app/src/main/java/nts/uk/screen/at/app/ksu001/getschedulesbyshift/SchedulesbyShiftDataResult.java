package nts.uk.screen.at.app.ksu001.getschedulesbyshift;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.screen.at.app.ksu001.aggreratepersonaltotal.AggregatePersonalDto;
import nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal.AggregateWorkplaceDto;
import nts.uk.screen.at.app.ksu001.getshiftpalette.ShiftMasterDto;
import nts.uk.screen.at.app.ksu001.getworkscheduleshift.ScheduleOfShiftDto;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SchedulesbyShiftDataResult {

	// List<勤務予定（シフト）dto>
	public List<ScheduleOfShiftDto> listWorkScheduleShift;    
	
	// Map<シフトマスタ, Optional<出勤休日区分>>
	public Map<ShiftMasterDto, Integer> mapShiftMasterWithWorkStyle; 
	
	// 個人計集計結果　←集計内容によって情報が異なる
	public AggregatePersonalDto aggreratePersonal;
	
	// ・職場計集計結果　←集計内容によって情報が異なる
	public AggregateWorkplaceDto aggrerateWorkplace;
	
}
