package nts.uk.ctx.at.request.app.command.application.holidaywork;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.app.command.application.overtime.ApplicationTimeCommand;
import nts.uk.ctx.at.request.app.command.application.overtime.TimeZoneWithWorkNoCommand;
import nts.uk.ctx.at.request.app.command.application.overtime.WorkInformationCommand;
import nts.uk.ctx.at.request.app.find.application.ApplicationDto;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.NotUseAtr;

/**
 * Refactor5
 * @author huylq
 *
 */
@NoArgsConstructor
@Data
public class AppHolidayWorkCmd {

	/**
	 * 勤務情報
	 */
	private WorkInformationCommand workInformation;
	
	/**
	 * 申請時間
	 */
	private ApplicationTimeCommand applicationTime;
	
	/**
	 * 直帰区分
	 */
	private boolean backHomeAtr;
	
	/**
	 * 直行区分
	 */
	private boolean goWorkAtr;
	
	/**
	 * 休憩時間帯
	 */
	private List<TimeZoneWithWorkNoCommand> breakTimeList;
	
	/**
	 * 勤務時間帯
	 */
	private List<TimeZoneWithWorkNoCommand> workingTimeList;
	
	public ApplicationDto application;
	
	public AppHolidayWorkCmd(WorkInformationCommand workInformation, ApplicationTimeCommand applicationTime, boolean backHomeAtr, boolean goWorkAtr, 
			List<TimeZoneWithWorkNoCommand> breakTime, List<TimeZoneWithWorkNoCommand> workingTime) {
		this.workInformation = workInformation;
		this.applicationTime = applicationTime;
		this.backHomeAtr = backHomeAtr;
		this.goWorkAtr = goWorkAtr;
		this.breakTimeList = breakTime;
		this.workingTimeList = workingTime;
	}
	
	public Application toDomainApplication() {
		return application.toDomain();
	}
	
	public AppHolidayWork toDomain() {
		return new AppHolidayWork(this.workInformation.toDomain(), 
				this.applicationTime != null ? this.applicationTime.toDomain() : null, 
				this.backHomeAtr ? NotUseAtr.USE : NotUseAtr.NOT_USE, this.goWorkAtr ? NotUseAtr.USE : NotUseAtr.NOT_USE, 
				this.breakTimeList != null ?
						Optional.ofNullable(this.breakTimeList.stream().map(breakTime -> breakTime.toDomain()).collect(Collectors.toList())) : Optional.empty(), 
				this.workingTimeList != null ? 
						Optional.ofNullable(this.workingTimeList.stream().map(workingTime -> workingTime.toDomain()).collect(Collectors.toList())) : Optional.empty());
	}
}
