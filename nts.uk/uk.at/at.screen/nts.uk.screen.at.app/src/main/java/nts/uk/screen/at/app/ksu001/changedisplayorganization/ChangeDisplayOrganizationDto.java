package nts.uk.screen.at.app.ksu001.changedisplayorganization;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.screen.at.app.ksu001.aggreratepersonaltotal.AggregatePersonalDto;
import nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal.AggregateWorkplaceDto;
import nts.uk.screen.at.app.ksu001.displayinshift.ShiftMasterMapWithWorkStyle;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DataSpecDateAndHolidayDto;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.EmployeeInformationDto;
import nts.uk.screen.at.app.ksu001.getshiftpalette.PageInfo;
import nts.uk.screen.at.app.ksu001.getshiftpalette.ShiftMasterDto;
import nts.uk.screen.at.app.ksu001.getshiftpalette.TargetShiftPalette;
import nts.uk.screen.at.app.ksu001.getworkscheduleshift.ScheduleOfShiftDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangeDisplayOrganizationDto {
	// List<社員ID, 社員コード, ビジネスネーム>
	public List<EmployeeInformationDto> employeeInformationDtos = Collections.emptyList();
	// 
	public DataSpecDateAndHolidayDto dataSpecDateAndHolidayDto;
	
	// ・List<ページ, 名称>　　※シフトパレット
	public List<PageInfo> listPageInfo; 
	// ・対象ページのシフトパレット
	public TargetShiftPalette targetShiftPalette;
	
	public List<ScheduleOfShiftDto> listWorkScheduleShift;  // List<勤務予定（シフト）dto> ==> data hiển thị trên grid
	
	
	// Map<シフトマスタ, Optional<出勤休日区分>>
	public Map<ShiftMasterDto, Integer> mapShiftMasterWithWorkStyle; 
	
	// 個人計集計結果　←集計内容によって情報が異なる
	public AggregatePersonalDto aggreratePersonal;
	
	// ・職場計集計結果　←集計内容によって情報が異なる
	public AggregateWorkplaceDto aggrerateWorkplace;
	

}
