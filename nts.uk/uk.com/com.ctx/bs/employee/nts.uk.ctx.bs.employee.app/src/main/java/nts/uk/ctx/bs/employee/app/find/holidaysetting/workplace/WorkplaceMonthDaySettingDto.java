package nts.uk.ctx.bs.employee.app.find.holidaysetting.workplace;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import nts.uk.ctx.bs.employee.app.find.holidaysetting.common.dto.PublicHolidayMonthSettingDto;
import nts.uk.ctx.bs.employee.dom.common.CompanyId;
import nts.uk.ctx.bs.employee.dom.holidaysetting.common.PublicHolidayMonthSetting;
import nts.uk.ctx.bs.employee.dom.holidaysetting.common.Year;
import nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySettingSetMemento;

/**
 * The Class WorkplaceMonthDaySettingDto.
 */
@Data
public class WorkplaceMonthDaySettingDto implements WorkplaceMonthDaySettingSetMemento {
	
	/** The year. */
	private int year;
	
	/** The workplace id. */
	private String workplaceId; 
	
	/** The public holiday month settings. */
	private List<PublicHolidayMonthSettingDto> publicHolidayMonthSettings;

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySettingSetMemento#setCompanyId(nts.uk.ctx.bs.employee.dom.common.CompanyId)
	 */
	@Override
	public void setCompanyId(CompanyId companyId) {
		// Nothing code
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySettingSetMemento#setWorkplaceID(java.lang.String)
	 */
	@Override
	public void setWorkplaceID(String workplaceID) {
		this.workplaceId = workplaceID;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySettingSetMemento#setManagementYear(nts.uk.ctx.bs.employee.dom.holidaysetting.common.Year)
	 */
	@Override
	public void setManagementYear(Year managementYear) {
		this.year = managementYear.v();
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySettingSetMemento#setPublicHolidayMonthSettings(java.util.List)
	 */
	@Override
	public void setPublicHolidayMonthSettings(List<PublicHolidayMonthSetting> publicHolidayMonthSettings) {
		this.publicHolidayMonthSettings = publicHolidayMonthSettings.stream().map(e -> {
			PublicHolidayMonthSettingDto dto = new PublicHolidayMonthSettingDto(e.getPublicHdManagementYear().v(), e.getMonth().intValue(),
																				e.getInLegalHoliday().v());
			return dto;
		}).collect(Collectors.toList());
	}
}
