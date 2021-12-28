/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.monthlyattditem;

import java.util.Optional;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.attendance.AttendanceName;
import nts.uk.ctx.at.shared.dom.attendance.UseSetting;
import nts.uk.ctx.at.shared.dom.monthlyattditem.DisplayMonthResultsMethod;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemAtr;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemGetMemento;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums.PrimitiveValueOfAttendanceItem;
import nts.uk.ctx.at.shared.infra.entity.monthlyattditem.KrcmtMonAttendanceItem;

/**
 * The Class JpaMonthlyAttendanceItemGetMemento.
 */
public class JpaMonthlyAttendanceItemGetMemento implements MonthlyAttendanceItemGetMemento {

	/** The entity. */
	private KrcmtMonAttendanceItem entity;

	/**
	 * Instantiates a new jpa monthly attendance item get memento.
	 *
	 * @param entity the entity
	 */
	public JpaMonthlyAttendanceItemGetMemento(KrcmtMonAttendanceItem entity) {
		this.entity = entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.monthlyattendanceitem.
	 * MonthlyAttendanceItemGetMemento#getCompanyId()
	 */
	@Override
	public String getCompanyId() {
		return this.entity.getKrcmtMonAttendanceItemPK().getCid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.monthlyattendanceitem.
	 * MonthlyAttendanceItemGetMemento#getAttendanceItemId()
	 */
	@Override
	public int getAttendanceItemId() {
		return this.entity.getKrcmtMonAttendanceItemPK().getMAtdItemId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.monthlyattendanceitem.
	 * MonthlyAttendanceItemGetMemento#getAttendanceName()
	 */
	@Override
	public AttendanceName getAttendanceName() {
		return new AttendanceName(this.entity.getMAtdItemName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.monthlyattendanceitem.
	 * MonthlyAttendanceItemGetMemento#getDisplayNumber()
	 */
	@Override
	public int getDisplayNumber() {
		return this.entity.getDispNo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.monthlyattendanceitem.
	 * MonthlyAttendanceItemGetMemento#getUserCanUpdateAtr()
	 */
	@Override
	public UseSetting getUserCanUpdateAtr() {
		return EnumAdaptor.valueOf(this.entity.getIsAllowChange(), UseSetting.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.monthlyattendanceitem.
	 * MonthlyAttendanceItemGetMemento#getMonthlyAttendanceAtr()
	 */
	@Override
	public MonthlyAttendanceItemAtr getMonthlyAttendanceAtr() {
		return MonthlyAttendanceItemAtr.valueOf(this.entity.getMAtdItemAtr());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.monthlyattendanceitem.
	 * MonthlyAttendanceItemGetMemento#getNameLineFeedPosition()
	 */
	@Override
	public int getNameLineFeedPosition() {
		return this.entity.getLineBreakPosName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.monthlyattendanceitem.
	 * MonthlyAttendanceItemGetMemento#getPrimitiveValue()
	 */
	@Override
	public Optional<PrimitiveValueOfAttendanceItem> getPrimitiveValue() {
		return this.entity.getPrimitiveValue() == null ? Optional.empty()
				: Optional.ofNullable(
						EnumAdaptor.valueOf(this.entity.getPrimitiveValue(), PrimitiveValueOfAttendanceItem.class));
	}

	@Override
	public Optional<AttendanceName> getDisplayName() {
		return this.entity.getDisplayName() == null ?  Optional.empty()
				: Optional.of(new AttendanceName(entity.getDisplayName()));
	}

	@Override
	public DisplayMonthResultsMethod getTwoMonthlyDisplay() {
		return EnumAdaptor.valueOf(this.entity.getTwoMonthlyDisplay(), DisplayMonthResultsMethod.class) ;
	}

}
