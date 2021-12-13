package nts.uk.screen.at.app.ksu001.getsendingperiod;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;
import nts.uk.screen.at.app.ksu001.aggreratepersonaltotal.AggregatePersonalDto;
import nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal.AggregateWorkplaceDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DataSpecDateAndHolidayDto;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChangePeriodInWorkInfoResult {
	public List<EmployeeInformationImport> listEmpInfo;
	public DataSpecDateAndHolidayDto dataSpecDateAndHolidayDto;
	public List<WorkScheduleWorkInforDto> listWorkScheduleWorkInfor;
	
	// 個人計集計結果　←集計内容によって情報が異なる
	public AggregatePersonalDto aggreratePersonal;
	
	// ・職場計集計結果　←集計内容によって情報が異なる
	public AggregateWorkplaceDto aggrerateWorkplace;
}
