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
 * The Interface MonthlyAttendanceItemGetMemento.
 */
public interface MonthlyAttendanceItemGetMemento {

	/**
	 * Gets the company id.
	 *
	 * @return the company id
	 */
	String getCompanyId();

	/**
	 * Gets the attendance item id.
	 *
	 * @return the attendance item id
	 */
	int getAttendanceItemId();

	/**
	 * Gets the attendance name.
	 *
	 * @return the attendance name
	 */
	AttendanceName getAttendanceName();

	/**
	 * Gets the display number.
	 *
	 * @return the display number
	 */
	int getDisplayNumber();

	/**
	 * Gets the user can update atr.
	 *
	 * @return the user can update atr
	 */
	UseSetting getUserCanUpdateAtr();

	/**
	 * Gets the monthly attendance atr.
	 *
	 * @return the monthly attendance atr
	 */
	MonthlyAttendanceItemAtr getMonthlyAttendanceAtr();

	/**
	 * Gets the name line feed position.
	 *
	 * @return the name line feed position
	 */
	int getNameLineFeedPosition();

	/**
	 * Gets the primitiveValue
	 *
	 * @return the the primitiveValue
	 */
	Optional<PrimitiveValueOfAttendanceItem> getPrimitiveValue();
	
	/**
	 * 表示名称 を取得する
	 * @return 表示名称 
	 */
	Optional<AttendanceName> getDisplayName();

	/**
	 * Gets the display method.
	 *
	 * @return the display method
	 */
	DisplayMonthResultsMethod getTwoMonthlyDisplay();
}
