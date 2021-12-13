package nts.uk.ctx.at.shared.infra.repository.holidaysetting.configuration;

import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.FourWeekDay;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.FourWeekFourHolidayNumberSettingGetMemento;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.FourWeekPublicHoliday;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.LastWeekHolidayNumberOfFourWeek;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.LastWeekHolidayNumberOfOneWeek;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.OneWeekPublicHoliday;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.WeekNumberOfDay;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.configuration.Kshmt4w4dNumSet;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class JpaFourWeekFourHolidayNumberSettingGetMemento.
 */
public class JpaFourWeekFourHolidayNumberSettingGetMemento implements FourWeekFourHolidayNumberSettingGetMemento{
	
	/** The kshmt fourweekfour hd numb set. */
	private Kshmt4w4dNumSet kshmtFourweekfourHdNumbSet;
	
	/**
	 * Instantiates a new jpa four week four holiday number setting get memento.
	 *
	 * @param entity the entity
	 */
	public JpaFourWeekFourHolidayNumberSettingGetMemento(Kshmt4w4dNumSet entity){
		this.kshmtFourweekfourHdNumbSet = entity;
	}
	
	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.FourWeekFourHolidayNumberSettingGetMemento#getIsOneWeekHoliday()
	 */
	@Override
	public boolean getIsOneWeekHoliday() {
		return this.kshmtFourweekfourHdNumbSet.isOneWeekHd();
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.FourWeekFourHolidayNumberSettingGetMemento#getOneWeek()
	 */
	@Override
	public OneWeekPublicHoliday getOneWeek() {
		LastWeekHolidayNumberOfOneWeek obj = new LastWeekHolidayNumberOfOneWeek(new WeekNumberOfDay(this.kshmtFourweekfourHdNumbSet.getInLegalHdLwhnoow().doubleValue()),
													new WeekNumberOfDay(this.kshmtFourweekfourHdNumbSet.getOutLegalHdLwhnoow().doubleValue()));
		return new OneWeekPublicHoliday(obj, 
										new WeekNumberOfDay(this.kshmtFourweekfourHdNumbSet.getInLegalHdOwph().doubleValue()),
										new WeekNumberOfDay(this.kshmtFourweekfourHdNumbSet.getOutLegalHdOwph().doubleValue()));
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.FourWeekFourHolidayNumberSettingGetMemento#getIsFourWeekHoliday()
	 */
	@Override
	public boolean getIsFourWeekHoliday() {
		return this.kshmtFourweekfourHdNumbSet.isFourWeekHd();
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.FourWeekFourHolidayNumberSettingGetMemento#getFourWeek()
	 */
	@Override
	public FourWeekPublicHoliday getFourWeek() {
		LastWeekHolidayNumberOfFourWeek obj = new LastWeekHolidayNumberOfFourWeek(
														new FourWeekDay(this.kshmtFourweekfourHdNumbSet.getInLegalHdLwhnofw().doubleValue()),
														new FourWeekDay(this.kshmtFourweekfourHdNumbSet.getOutLegalHdLwhnofw().doubleValue()));
		return new FourWeekPublicHoliday(obj,
										 new FourWeekDay(this.kshmtFourweekfourHdNumbSet.getInLegelHdFwph().doubleValue()),
										 new FourWeekDay(this.kshmtFourweekfourHdNumbSet.getOutLegalHdFwph().doubleValue()));
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.FourWeekFourHolidayNumberSettingGetMemento#getCID()
	 */
	@Override
	public String getCID() {
		return AppContexts.user().companyId();
	}

}
