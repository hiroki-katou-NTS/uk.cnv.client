package nts.uk.ctx.at.request.app.find.dialog.annualholiday.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.EmployeeInfoImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualholidaymanagement.AttendRateAtNextHolidayImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualholidaymanagement.NextAnnualLeaveGrantImport;
import nts.uk.ctx.at.shared.app.find.vacation.setting.annualpaidleave.dto.AnnualPaidLeaveSettingFindDto;

/**
 * @author sonnlb
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnualHolidayDto {

	private List<NextAnnualLeaveGrantImport> annualLeaveGrant;

	private AttendRateAtNextHolidayImport attendNextHoliday;
	
	private ReNumAnnLeaReferenceDateDto reNumAnnLeave;
	
	private List<EmployeeInfoImport> employees;
	//	休暇申請設定
	private AnnualPaidLeaveSettingFindDto annualSet;
	//	年休管理区分
	private boolean annualLeaveManagementFg; 
	
	public DatePeriod closingPeriod;

}