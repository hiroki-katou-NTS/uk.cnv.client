/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.scherec.optitem;

import java.util.Optional;

import nts.uk.ctx.at.shared.dom.common.CompanyId;

/**
 * The Interface OptionalItemGetMemento.
 */
public interface OptionalItemGetMemento {

	/**
	 * Gets the company id.
	 *
	 * @return the company id
	 */
	CompanyId getCompanyId();

	/**
	 * Gets the optional item no.
	 *
	 * @return the optional item no
	 */
	OptionalItemNo getOptionalItemNo();

	/**
	 * Gets the optional item name.
	 *
	 * @return the optional item name
	 */
	OptionalItemName getOptionalItemName();

	/**
	 * Gets the optional item atr.
	 *
	 * @return the optional item atr
	 */
	OptionalItemAtr getOptionalItemAtr();

	/**
	 * Gets the optional item usage atr.
	 *
	 * @return the optional item usage atr
	 */
	OptionalItemUsageAtr getOptionalItemUsageAtr();

	/**
	 * Gets the emp condition atr.
	 *
	 * @return the emp condition atr
	 */
	EmpConditionAtr getEmpConditionAtr();

	/**
	 * Gets the performance atr.
	 *
	 * @return the performance atr
	 */
	PerformanceAtr getPerformanceAtr();

	/**
	 * Gets the inputControlSetting.
	 *
	 * @return the inputControlSetting
	 */
	InputControlSetting getInputControlSetting();

	/**
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	Optional<UnitOfOptionalItem> getUnit();
	
	/**
	 * Gets the calculation atr
	 * @return the calAtr
	 */
	CalculationClassification getCalcAtr();
	
	/**
	 * Gets the not
	 * @return the note
	 */
	Optional<NoteOptionalItem> getNote();
	
	/**
	 * Gets the description
	 * @return the descroption
	 */
	Optional<DescritionOptionalItem> getDescription();
}
