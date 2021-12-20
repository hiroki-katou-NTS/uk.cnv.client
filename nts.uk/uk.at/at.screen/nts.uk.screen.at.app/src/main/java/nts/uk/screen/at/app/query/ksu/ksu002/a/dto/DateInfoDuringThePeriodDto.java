package nts.uk.screen.at.app.query.ksu.ksu002.a.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.schedule.dom.shift.management.DateInformation;

/**
 * 
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DateInfoDuringThePeriodDto {
	
	/** 祝日であるか **/
	private boolean isHoliday;

	/** 特定日であるか **/
	private boolean isSpecificDay;

	/** 職場行事名称 **/
	private String optWorkplaceEventName;

	/** 会社行事名称 **/
	private String optCompanyEventName;

	/** 職場の特定日名称リスト **/
	private List<String> listSpecDayNameWorkplace;

	/** 会社の特定日名称リスト **/
	private List<String> listSpecDayNameCompany;
	
	private String holidayName;

	public DateInfoDuringThePeriodDto(DateInformation information) {
		super();
		this.setHoliday(information.isHoliday());
		this.setListSpecDayNameCompany(
				information.getListSpecDayNameCompany().stream().map(c -> c.v()).collect(Collectors.toList()));
		this.setListSpecDayNameWorkplace(
				information.getListSpecDayNameWorkplace().stream().map(c -> c.v()).collect(Collectors.toList()));
		this.setOptCompanyEventName(information.getOptCompanyEventName().map(c -> c.v()).orElse(null));
		this.setOptWorkplaceEventName(information.getOptWorkplaceEventName().map(c -> c.v()).orElse(null));
		this.setSpecificDay(information.isSpecificDay());
		this.setHolidayName(information.getHolidayName().map(c -> c.v()).orElse(""));
	}
	
	

}
