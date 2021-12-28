/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.monthlyattditem;

import java.util.Optional;

import nts.uk.ctx.at.shared.dom.attendance.AttendanceName;
import nts.uk.ctx.at.shared.dom.attendance.UseSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums.PrimitiveValueOfAttendanceItem;

/**
 * The Interface MonthlyAttendanceItemSetMemento.
 */
public interface MonthlyAttendanceItemSetMemento {

	/**
	 * Sets the company id.
	 *
	 * @param companyId the new company id
	 */
	void setCompanyId(String companyId);

	/**
	 * Sets the attendance item id.
	 *
	 * @param itemId the new attendance item id
	 */
	void setAttendanceItemId(int itemId);

	/**
	 * Sets the attendance name.
	 *
	 * @param name the new attendance name
	 */
	void setAttendanceName(AttendanceName name);

	/**
	 * Sets the display number.
	 *
	 * @param number the new display number
	 */
	void setDisplayNumber(int number);

	/**
	 * Sets the user can update atr.
	 *
	 * @param canUpdateAtr the new user can update atr
	 */
	void setUserCanUpdateAtr(UseSetting canUpdateAtr);

	/**
	 * Sets the monthly attendance atr.
	 *
	 * @param atr the new monthly attendance atr
	 */
	void setMonthlyAttendanceAtr(MonthlyAttendanceItemAtr atr);

	/**
	 * Sets the name line feed position.
	 *
	 * @param nameLine the new name line feed position
	 */
	void setNameLineFeedPosition(int nameLine);

	/**
	 * Sets the primitiveValue.
	 *
	 * @param nameLine the new primitiveValue
	 */
	void setPrimitiveValue(Optional<PrimitiveValueOfAttendanceItem> primitiveValue);

	/**
	 * 
	 * @param displayName
	 * 
	 */
	void setDisplayName(Optional<AttendanceName> displayName);
	
	/**
	 * Sets the display method.
	 *
	 * @param displayMonthResultsMethod the new display method
	 */
	void setTwoMonthlyDisplay(DisplayMonthResultsMethod twoMonthlyDisplay);
}
