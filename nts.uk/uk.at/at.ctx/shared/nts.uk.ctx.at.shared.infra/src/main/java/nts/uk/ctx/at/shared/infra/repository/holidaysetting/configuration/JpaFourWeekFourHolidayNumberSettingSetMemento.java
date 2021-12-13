package nts.uk.ctx.at.shared.infra.repository.holidaysetting.configuration;

import java.math.BigDecimal;

import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.FourWeekFourHolidayNumberSettingSetMemento;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.FourWeekPublicHoliday;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.OneWeekPublicHoliday;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.configuration.Kshmt4w4dNumSet;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class JpaFourWeekFourHolidayNumberSettingSetMemento.
 */
public class JpaFourWeekFourHolidayNumberSettingSetMemento implements FourWeekFourHolidayNumberSettingSetMemento{
	
	/** The kshmt fourweekfour hd numb set. */
	private Kshmt4w4dNumSet kshmtFourweekfourHdNumbSet;
	
	/**
	 * Instantiates a new jpa four week four holiday number setting set memento.
	 *
	 * @param entity the entity
	 */
	public JpaFourWeekFourHolidayNumberSettingSetMemento(Kshmt4w4dNumSet entity){
		if(entity.getCid() == null){
			entity.setCid(AppContexts.user().companyId());
		}
		this.kshmtFourweekfourHdNumbSet = entity;
	}
	
	/**
	 * Sets the checks if is one week holiday.
	 *
	 * @param isOneWeekHoliday the new checks if is one week holiday
	 */
	@Override
	public void setIsOneWeekHoliday(boolean isOneWeekHoliday) {
		this.kshmtFourweekfourHdNumbSet.setOneWeekHd(isOneWeekHoliday);
	}

	/**
	 * Sets the one week.
	 *
	 * @param oneWeek the new one week
	 */
	@Override
	public void setOneWeek(OneWeekPublicHoliday oneWeek) {
		this.kshmtFourweekfourHdNumbSet.setInLegalHdOwph(BigDecimal.valueOf(oneWeek.getInLegalHoliday().v()));
		this.kshmtFourweekfourHdNumbSet.setOutLegalHdOwph(BigDecimal.valueOf(oneWeek.getOutLegalHoliday().v()));
		this.kshmtFourweekfourHdNumbSet.setInLegalHdLwhnoow(BigDecimal.valueOf(oneWeek.getLastWeekAddedDays().getInLegalHoliday().v()));
		this.kshmtFourweekfourHdNumbSet.setOutLegalHdLwhnoow(BigDecimal.valueOf(oneWeek.getLastWeekAddedDays().getOutLegalHoliday().v()));
	}

	/**
	 * Sets the checks if is four week holiday.
	 *
	 * @param isFourWeekHoliday the new checks if is four week holiday
	 */
	@Override
	public void setIsFourWeekHoliday(boolean isFourWeekHoliday) {
		this.kshmtFourweekfourHdNumbSet.setFourWeekHd(isFourWeekHoliday);
	}

	/**
	 * Sets the four week.
	 *
	 * @param fourWeek the new four week
	 */
	@Override
	public void setFourWeek(FourWeekPublicHoliday fourWeek) {
		this.kshmtFourweekfourHdNumbSet.setInLegelHdFwph(BigDecimal.valueOf(fourWeek.getInLegalHoliday().v()));
		this.kshmtFourweekfourHdNumbSet.setOutLegalHdFwph(BigDecimal.valueOf(fourWeek.getOutLegalHoliday().v()));
		this.kshmtFourweekfourHdNumbSet.setInLegalHdLwhnofw(BigDecimal.valueOf(fourWeek.getLastWeekAddedDays().getInLegalHoliday().v()));
		this.kshmtFourweekfourHdNumbSet.setOutLegalHdLwhnofw(BigDecimal.valueOf(fourWeek.getLastWeekAddedDays().getOutLegalHoliday().v()));
	}

	/**
	 * Sets the cid.
	 *
	 * @param CID the new cid
	 */
	@Override
	public void setCID(String CID) {
		// do not code here
	}

}
