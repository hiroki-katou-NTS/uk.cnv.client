package nts.uk.screen.at.app.query.ksu.ksu002.a.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
