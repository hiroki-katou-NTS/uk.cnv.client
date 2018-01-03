package nts.uk.ctx.bs.employee.app.command.holidaysetting.workplace;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import nts.uk.ctx.bs.employee.app.find.holidaysetting.common.dto.PublicHolidayMonthSettingDto;
import nts.uk.ctx.bs.employee.dom.common.CompanyId;
import nts.uk.ctx.bs.employee.dom.holidaysetting.common.MonthlyNumberOfDays;
import nts.uk.ctx.bs.employee.dom.holidaysetting.common.PublicHolidayMonthSetting;
import nts.uk.ctx.bs.employee.dom.holidaysetting.common.Year;
import nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySettingGetMemento;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class WorkplaceMonthDaySettingSaveCommand.
 */
@Data
public class WorkplaceMonthDaySettingSaveCommand implements WorkplaceMonthDaySettingGetMemento{
	
	/** The year. */
	private int year;
	
	/** The workplace id. */
	private String workplaceId;
	
	/** The public holiday month settings. */
	private List<PublicHolidayMonthSettingDto> publicHolidayMonthSettings;

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySettingGetMemento#getCompanyId()
	 */
	@Override
	public CompanyId getCompanyId() {
		return new CompanyId(AppContexts.user().companyId());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySettingGetMemento#getWorkplaceID()
	 */
	@Override
	public String getWorkplaceID() {
		return this.workplaceId;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySettingGetMemento#getManagementYear()
	 */
	@Override
	public Year getManagementYear() {
		return new Year(this.year);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.workplace.WorkplaceMonthDaySettingGetMemento#getPublicHolidayMonthSettings()
	 */
	@Override
	public List<PublicHolidayMonthSetting> getPublicHolidayMonthSettings() {
		return this.publicHolidayMonthSettings.stream().map(e -> {
			PublicHolidayMonthSetting domain = new PublicHolidayMonthSetting(new Year(this.year),
																			new Integer(e.getMonth()),
																			new MonthlyNumberOfDays(BigDecimal.valueOf(e.getInLegalHoliday())));
			return domain;
		}).collect(Collectors.toList());
	}

}
