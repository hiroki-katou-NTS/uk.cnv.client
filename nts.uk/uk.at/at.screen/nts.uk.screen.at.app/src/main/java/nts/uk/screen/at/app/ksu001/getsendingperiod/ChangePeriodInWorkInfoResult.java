/**
 * 
 */
package nts.uk.screen.at.app.ksu001.getsendingperiod;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DataSpecDateAndHolidayDto;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto;

/**
 * @author laitv
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChangePeriodInWorkInfoResult {
	
	public List<EmployeeInformationImport> listEmpInfo;
	public DataSpecDateAndHolidayDto dataSpecDateAndHolidayDto;
	public List<WorkScheduleWorkInforDto> listWorkScheduleWorkInfor;
}
