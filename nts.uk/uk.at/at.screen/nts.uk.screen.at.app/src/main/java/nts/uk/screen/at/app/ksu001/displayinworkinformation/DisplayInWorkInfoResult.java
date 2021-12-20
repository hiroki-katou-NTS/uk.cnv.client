package nts.uk.screen.at.app.ksu001.displayinworkinformation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.screen.at.app.ksu001.aggreratepersonaltotal.AggregatePersonalDto;
import nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal.AggregateWorkplaceDto;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DisplayInWorkInfoResult {
	
	public List<WorkTypeInfomation> listWorkTypeInfo;         // List<勤務種類, 必須任意不要区分, 出勤休日区分>
		
	// List<勤務予定（勤務情報）dto>
	public List<WorkScheduleWorkInforDto> workScheduleWorkInforDtos;

	// 個人計集計結果　←集計内容によって情報が異なる
	public AggregatePersonalDto aggreratePersonal;
	
	// ・職場計集計結果　←集計内容によって情報が異なる
	public AggregateWorkplaceDto aggrerateWorkplace;
	
}
