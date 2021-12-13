package nts.uk.ctx.at.shared.infra.repository.holidaysetting.configuration;

import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.ForwardSettingOfPublicHolidayGetMemento;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidayCarryOverDeadline;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.configuration.KshmtHdpubForwardSet;

/**
 * The Class JpaForwardSettingOfPublicHolidayGetMemento.
 */
public class JpaForwardSettingOfPublicHolidayGetMemento implements ForwardSettingOfPublicHolidayGetMemento {
	
	/** The kshmt forward set of public hd. */
	private KshmtHdpubForwardSet kshmtForwardSetOfPublicHd;
	
	/**
	 * Instantiates a new jpa forward setting of public holiday get memento.
	 *
	 * @param entity the entity
	 */
	public JpaForwardSettingOfPublicHolidayGetMemento(KshmtHdpubForwardSet entity){
		this.kshmtForwardSetOfPublicHd = entity;
	}
	
	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.ForwardSettingOfPublicHolidayGetMemento#getCompanyID()
	 */
	@Override
	public String getCompanyID() {
		return this.kshmtForwardSetOfPublicHd.getCid();
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.ForwardSettingOfPublicHolidayGetMemento#getIsTransferWhenPublicHdIsMinus()
	 */
	@Override
	public boolean getIsTransferWhenPublicHdIsMinus() {
		return this.kshmtForwardSetOfPublicHd.isPublicHdMinus();
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.ForwardSettingOfPublicHolidayGetMemento#getCarryOverDeadline()
	 */
	@Override
	public PublicHolidayCarryOverDeadline getCarryOverDeadline() {
		return PublicHolidayCarryOverDeadline.valueOf(this.kshmtForwardSetOfPublicHd.getCarryOverDeadline());
	}

}
