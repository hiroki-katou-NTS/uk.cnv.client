package nts.uk.ctx.at.shared.infra.repository.holidaysetting.configuration;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfPublicHoliday;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHoliday;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidayGrantDate;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidayManagementClassification;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidayManagementStartDate;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidayPeriod;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidaySettingGetMemento;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.configuration.KshmtHdpubSet;
import nts.uk.shr.com.context.AppContexts;

public class JpaPublicHolidaySettingGetMemento implements PublicHolidaySettingGetMemento {
	
	private KshmtHdpubSet kshmtPublicHdSet;
	
	public JpaPublicHolidaySettingGetMemento(KshmtHdpubSet entity) {
		this.kshmtPublicHdSet = entity;
	}
	
	@Override
	public String getCompanyID() {
		return AppContexts.user().companyId();
	}

	@Override
	public boolean getIsManageComPublicHd() {
		return this.kshmtPublicHdSet.isManageComPublicHd();
	}

	@Override
	public PublicHolidayManagementClassification getPublicHdManagementClassification() {
		return PublicHolidayManagementClassification.valueOf(this.kshmtPublicHdSet.getPublicHdManageAtr());
	}

	/*@Override
	public PublicHolidayManagementUsageUnit getPublicHdManagementUsageUnit() {
		return new PublicHolidayManagementUsageUnit(this.kshmtPublicHdSet.getIsManageEmpPubHd() == TRUE_VALUE ? true : false,
													this.kshmtPublicHdSet.getIsManageWkpPubHd() == TRUE_VALUE ? true : false,
													this.kshmtPublicHdSet.getIsManageSPubHd() == TRUE_VALUE ? true : false);
		}
	*/
	
	@Override
	public boolean getIsWeeklyHdCheck() {
		return this.kshmtPublicHdSet.isWeeklyHdCheck();
	}

	@Override
	public PublicHolidayManagementStartDate getPublicHolidayManagementStartDate(Integer publicHdManageAtr) {
		if (publicHdManageAtr != null) {
			if (publicHdManageAtr == 0) {
				return new PublicHolidayGrantDate(PublicHolidayPeriod.valueOf(this.kshmtPublicHdSet.getPeriod()));
			} else {
				return new PublicHoliday(this.kshmtPublicHdSet.getFullDate(), 
											this.kshmtPublicHdSet.getDayMonth(), 
											DayOfPublicHoliday.valueOf(this.kshmtPublicHdSet.getDetermineStartD()));
			}
			
		} else {
			if (this.kshmtPublicHdSet.getPublicHdManageAtr() == 0) {
				return new PublicHolidayGrantDate(PublicHolidayPeriod.valueOf(this.kshmtPublicHdSet.getPeriod()));
			} else {
				GeneralDate fullDate = this.kshmtPublicHdSet.getFullDate();
				if (fullDate == null) {
					return new PublicHoliday(null, this.kshmtPublicHdSet.getDayMonth(), 
												DayOfPublicHoliday.valueOf(this.kshmtPublicHdSet.getDetermineStartD()));
				} else {
					return new PublicHoliday(GeneralDate.ymd(fullDate.year(), fullDate.month(), fullDate.day()), 
							this.kshmtPublicHdSet.getDayMonth(), 
							DayOfPublicHoliday.valueOf(this.kshmtPublicHdSet.getDetermineStartD()));
				}
			}
		}
		
	}
}
