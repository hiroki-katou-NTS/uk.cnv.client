package nts.uk.ctx.bs.employee.infra.repository.holidaysetting.configuration;

import java.math.BigDecimal;

import nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.FourWeekFourHolidayNumberSettingSetMemento;
import nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.FourWeekPublicHoliday;
import nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.OneWeekPublicHoliday;
import nts.uk.ctx.bs.employee.infra.entity.holidaysetting.configuration.KshmtFourweekfourHdNumbSet;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class JpaFourWeekFourHolidayNumberSettingSetMemento.
 */
public class JpaFourWeekFourHolidayNumberSettingSetMemento implements FourWeekFourHolidayNumberSettingSetMemento{
	/** The Constant TRUE_VALUE. */
	private final static int TRUE_VALUE = 1;
	
	/** The Constant FALSE_VALUE. */
	private final static int FALSE_VALUE = 0;
	
	/** The kshmt fourweekfour hd numb set. */
	private KshmtFourweekfourHdNumbSet kshmtFourweekfourHdNumbSet;
	
	/**
	 * Instantiates a new jpa four week four holiday number setting set memento.
	 *
	 * @param entity the entity
	 */
	public JpaFourWeekFourHolidayNumberSettingSetMemento(KshmtFourweekfourHdNumbSet entity){
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
		if(isOneWeekHoliday){
			this.kshmtFourweekfourHdNumbSet.setIsOneWeekHd(TRUE_VALUE);
		} else {
			this.kshmtFourweekfourHdNumbSet.setIsOneWeekHd(FALSE_VALUE);
		}
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
		if(isFourWeekHoliday){
			this.kshmtFourweekfourHdNumbSet.setIsFourWeekHd(TRUE_VALUE);
		} else {
			this.kshmtFourweekfourHdNumbSet.setIsFourWeekHd(FALSE_VALUE);
		}
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
