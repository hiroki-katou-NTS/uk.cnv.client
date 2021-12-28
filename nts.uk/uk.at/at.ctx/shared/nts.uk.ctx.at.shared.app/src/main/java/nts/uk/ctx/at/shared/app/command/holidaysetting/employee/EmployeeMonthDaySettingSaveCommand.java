package nts.uk.ctx.at.shared.app.command.holidaysetting.employee;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import nts.uk.ctx.at.shared.app.find.holidaysetting.common.dto.PublicHolidayMonthSettingDto;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.MonthlyNumberOfDays;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.PublicHolidayMonthSetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.Year;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.EmployeeMonthDaySettingGetMemento;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class EmployeeMonthDaySettingSaveCommand.
 */
@Data
public class EmployeeMonthDaySettingSaveCommand implements EmployeeMonthDaySettingGetMemento {
	
	/** The year. */
	private int year;
	
	/** The s id. */
	private String employeeID;
	
	/** The public holiday month settings. */
	private List<PublicHolidayMonthSettingDto> publicHolidayMonthSettings;

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.employee.EmployeeMonthDaySettingGetMemento#getCompanyId()
	 */
	@Override
	public CompanyId getCompanyId() {
		return new CompanyId(AppContexts.user().companyId());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.employee.EmployeeMonthDaySettingGetMemento#getEmployeeId()
	 */
	@Override
	public String getEmployeeId() {
		return this.employeeID;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.employee.EmployeeMonthDaySettingGetMemento#getManagementYear()
	 */
	@Override
	public Year getManagementYear() {
		return new Year(this.year);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.employee.EmployeeMonthDaySettingGetMemento#getPublicHolidayMonthSettings()
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
