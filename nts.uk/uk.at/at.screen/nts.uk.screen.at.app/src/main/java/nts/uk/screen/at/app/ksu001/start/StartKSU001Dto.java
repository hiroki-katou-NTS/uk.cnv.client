/**
 * 
 */
package nts.uk.screen.at.app.ksu001.start;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.screen.at.app.ksu001.displayinshift.ShiftMasterMapWithWorkStyle;
import nts.uk.screen.at.app.ksu001.displayinworkinformation.WorkTypeInfomation;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DateInformationDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DisplayControlPersonalCondDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.PersonalConditionsDto;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.EmployeeInformationDto;
import nts.uk.screen.at.app.ksu001.getshiftpalette.PageInfo;
import nts.uk.screen.at.app.ksu001.getshiftpalette.TargetShiftPalette;
import nts.uk.screen.at.app.ksu001.getworkscheduleshift.ScheduleOfShiftDto;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto;

/**
 * @author laitv
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StartKSU001Dto {
	
	// data tra ve cua step1
	public DataBasicDto dataBasicDto;
	
	// data tra ve cua step2
	public List<EmployeeInformationDto> listEmpInfo;
	
	// data tra ve cua step3
	public List<DateInformationDto> listDateInfo;
	public List<PersonalConditionsDto> listPersonalConditions; 
	public DisplayControlPersonalCondDto displayControlPersonalCond;
	
	// data trả về của step 4 || 5.2
	public List<WorkTypeInfomation> listWorkTypeInfo;
	public List<WorkScheduleWorkInforDto> listWorkScheduleWorkInfor; 
	
	// data trả về của step 5.1
	public List<PageInfo> listPageInfo; // List<ページ, 名称>
	public TargetShiftPalette targetShiftPalette; // 対象のシフトパレッ
	public List<ShiftMasterMapWithWorkStyle> shiftMasterWithWorkStyleLst;
	public List<ScheduleOfShiftDto> listWorkScheduleShift; // ・List<勤務予定（シフト）dto>
	
}
