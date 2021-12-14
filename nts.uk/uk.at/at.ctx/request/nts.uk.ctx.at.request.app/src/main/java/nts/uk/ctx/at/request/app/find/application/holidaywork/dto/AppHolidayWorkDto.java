package nts.uk.ctx.at.request.app.find.application.holidaywork.dto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.app.find.application.gobackdirectly.WorkInformationDto;
import nts.uk.ctx.at.request.app.find.application.overtime.ApplicationTimeDto;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.shared.app.find.common.TimeZoneWithWorkNoDto;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.NotUseAtr;

/**
 * Refactor5
 * @author huylq
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppHolidayWorkDto {

	/**
	 * 勤務情報
	 */
	private WorkInformationDto workInformation;
	
	/**
	 * 申請時間
	 */
	private ApplicationTimeDto applicationTime;
	
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
	private List<TimeZoneWithWorkNoDto> breakTimeList;
	
	/**
	 * 勤務時間帯
	 */
	private List<TimeZoneWithWorkNoDto> workingTimeList;
	
	public static AppHolidayWorkDto fromDomain(AppHolidayWork domain) {
		if(domain == null) return null;
		return new AppHolidayWorkDto(WorkInformationDto.fromDomain(domain.getWorkInformation()), 
				ApplicationTimeDto.fromDomain(domain.getApplicationTime()), 
				domain.getBackHomeAtr().equals(NotUseAtr.USE), domain.getGoWorkAtr().equals(NotUseAtr.USE), 
				domain.getBreakTimeList().orElse(Collections.emptyList()).stream().map(breakTime -> TimeZoneWithWorkNoDto.fromDomain(breakTime)).collect(Collectors.toList()), 
				domain.getWorkingTimeList().orElse(Collections.emptyList()).stream().map(workingTime -> TimeZoneWithWorkNoDto.fromDomain(workingTime)).collect(Collectors.toList()));
	}
}
