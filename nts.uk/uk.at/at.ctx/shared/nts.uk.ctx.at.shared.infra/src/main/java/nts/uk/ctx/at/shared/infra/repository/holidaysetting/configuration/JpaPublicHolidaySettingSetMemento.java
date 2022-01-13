package nts.uk.ctx.at.shared.infra.repository.holidaysetting.configuration;

import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfPublicHoliday;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHoliday;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidayGrantDate;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidayManagementClassification;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidayManagementStartDate;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidaySettingSetMemento;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.configuration.KshmtHdpubSet;

/**
 * The Class JpaPublicHolidaySettingSetMemento.
 */
public class JpaPublicHolidaySettingSetMemento implements PublicHolidaySettingSetMemento{
	
	/** The kshmt public hd set. */
	private KshmtHdpubSet kshmtPublicHdSet;
	
	/**
	 * Instantiates a new jpa public holiday setting set memento.
	 *
	 * @param entity the entity
	 */
	public JpaPublicHolidaySettingSetMemento(KshmtHdpubSet entity) {
		this.kshmtPublicHdSet = entity;
	}
	
	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.PublicHolidaySettingSetMemento#setCompanyID(java.lang.String)
	 */
	@Override
	public void setCompanyID(String CompanyID) {
		this.kshmtPublicHdSet.setCid(CompanyID);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.PublicHolidaySettingSetMemento#setIsManageComPublicHd(boolean)
	 */
	@Override
	public void setIsManageComPublicHd(boolean isManageComPublicHd) {
		this.kshmtPublicHdSet.setManageComPublicHd(isManageComPublicHd);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.PublicHolidaySettingSetMemento#setPublicHdManagementClassification(nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.PublicHolidayManagementClassification)
	 */
	@Override
	public void setPublicHdManagementClassification(
			PublicHolidayManagementClassification publicHdManagementClassification) {
		this.kshmtPublicHdSet.setPublicHdManageAtr(publicHdManagementClassification.value);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.PublicHolidaySettingSetMemento#setIsWeeklyHdCheck(boolean)
	 */
	@Override
	public void setIsWeeklyHdCheck(boolean isWeeklyHdCheck) {
		this.kshmtPublicHdSet.setWeeklyHdCheck(isWeeklyHdCheck);
	}

	@Override
	public void setPublicHolidayManagementStartDate(PublicHolidayManagementStartDate publicHolidayManagementStartDate) {
		if (this.kshmtPublicHdSet.getPublicHdManageAtr() == 0) {
			PublicHolidayGrantDate publicHolidayGrantDate = (PublicHolidayGrantDate) publicHolidayManagementStartDate;
			this.kshmtPublicHdSet.setPeriod(publicHolidayGrantDate.getPeriod().value);
		} else {
			PublicHoliday publicHoliday = (PublicHoliday) publicHolidayManagementStartDate;
			this.kshmtPublicHdSet.setDetermineStartD(publicHoliday.getDetermineStartDate().value);
			if (publicHoliday.getDetermineStartDate().value == DayOfPublicHoliday.DESIGNATE_BY_YEAR_MONTH_DAY.value) {
				this.kshmtPublicHdSet.setFullDate(publicHoliday.getDate());
			} else {
				this.kshmtPublicHdSet.setDayMonth(publicHoliday.getDayMonth());
			}
		}
	}

	@Override
	public void setPublicHolidayManagementStartDate(PublicHolidayManagementStartDate publicHolidayManagementStartDate,
			Integer type) {
		
	}

}
