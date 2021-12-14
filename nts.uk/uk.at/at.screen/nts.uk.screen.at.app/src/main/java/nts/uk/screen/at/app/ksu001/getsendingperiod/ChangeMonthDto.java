/**
 * 
 */
package nts.uk.screen.at.app.ksu001.getsendingperiod;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.screen.at.app.ksu001.displayinshift.ShiftMasterMapWithWorkStyle;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DateInformationDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DisplayControlPersonalCondDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.PersonalConditionsDto;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.EmployeeInformationDto;
import nts.uk.screen.at.app.ksu001.getworkscheduleshift.ScheduleOfShiftDto;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto;
import nts.uk.screen.at.app.ksu001.start.DataBasicDto;

/**
 * @author laitv
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChangeMonthDto {
	
	public DataBasicDto dataBasicDto;
	
	public List<EmployeeInformationDto> listEmpInfo;
	
	public List<DateInformationDto> listDateInfo;
	public List<PersonalConditionsDto> listPersonalConditions; 
	public DisplayControlPersonalCondDto displayControlPersonalCond;
	
	public List<WorkScheduleWorkInforDto> listWorkScheduleWorkInfor; 

	public List<ShiftMasterMapWithWorkStyle> shiftMasterWithWorkStyleLst;
	public List<ScheduleOfShiftDto> listWorkScheduleShift; // ・List<勤務予定（シフト）dto>
	
}
