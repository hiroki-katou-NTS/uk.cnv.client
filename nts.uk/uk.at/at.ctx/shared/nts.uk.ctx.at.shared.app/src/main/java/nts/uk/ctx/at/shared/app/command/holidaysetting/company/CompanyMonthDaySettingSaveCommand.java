package nts.uk.ctx.at.shared.app.command.holidaysetting.company;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.app.find.holidaysetting.common.dto.PublicHolidayMonthSettingDto;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.MonthlyNumberOfDays;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.PublicHolidayMonthSetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.Year;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.company.CompanyMonthDaySettingGetMemento;
import nts.uk.shr.com.context.AppContexts;


/**
 * The Class CompanyMonthDaySettingSaveCommand.
 */
//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMonthDaySettingSaveCommand implements CompanyMonthDaySettingGetMemento {
	
	/** The year. */
	public int year;
	
	/** The public holiday month settings. */
	public List<PublicHolidayMonthSettingDto> publicHolidayMonthSettings;
	
	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.company.CompanyMonthDaySettingGetMemento#getCompanyId()
	 */
	@Override
	public CompanyId getCompanyId() {
		return new CompanyId(AppContexts.user().companyId());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.company.CompanyMonthDaySettingGetMemento#getManagementYear()
	 */
	@Override
	public Year getManagementYear() {
		return new Year(this.year);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.company.CompanyMonthDaySettingGetMemento#getPublicHolidayMonthSettings()
	 */
	@Override
	public List<PublicHolidayMonthSetting> getPublicHolidayMonthSettings() {
		return this.publicHolidayMonthSettings.stream().map(e -> {
			PublicHolidayMonthSetting domain = new PublicHolidayMonthSetting(new Year(e.getPublicHdManagementYear()),
																			new Integer(e.getMonth()),
																			new MonthlyNumberOfDays(e.getInLegalHoliday()));
			return domain;
		}).collect(Collectors.toList());
	}

}
