/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.app.find.optitem;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.record.app.find.optitem.calculation.FormulaDto;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.scherec.optitem.*;

/**
 * The Class OptionalItemDto.
 */
@Getter
@Setter
public class OptionalItemDto implements OptionalItemSetMemento {

	/** The optional item no. */
	private int optionalItemNo;

	/** The optional item name. */
	private String optionalItemName;

	/** The optional item atr. */
	private int optionalItemAtr;

	/** The usage classification. */
	private int usageAtr;

	/** The emp condition classification. */
	private int empConditionAtr;

	/** The performance classification. */
	private int performanceAtr;

	/** The calculation result range. */
	private CalcResultRangeDto calcResultRange;

	/** The formulas. */
	private List<FormulaDto> formulas;

	/** The unit. */
	private String unit;
	
	/** The calAtr */
	private int calAtr;
	
	/** The note */
	private String note;
	
	/** The description */
	private String description;

	private boolean inputCheck;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.timeitemmanagement.OptionalItemSetMemento#
	 * setCompanyId(nts.uk.ctx.at.shared.dom.common.CompanyId)
	 */
	@Override
	public void setCompanyId(CompanyId comId) {
		// Not used.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.timeitemmanagement.OptionalItemSetMemento#
	 * setOptionalItemNo(nts.uk.ctx.at.shared.dom.timeitemmanagement.
	 * OptionalItemNo)
	 */
	@Override
	public void setOptionalItemNo(OptionalItemNo optionalItemNo) {
		this.optionalItemNo = optionalItemNo.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.timeitemmanagement.OptionalItemSetMemento#
	 * setOptionalItemName(nts.uk.ctx.at.shared.dom.timeitemmanagement.
	 * OptionalItemName)
	 */
	@Override
	public void setOptionalItemName(OptionalItemName optionalItemName) {
		this.optionalItemName = optionalItemName.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.timeitemmanagement.OptionalItemSetMemento#
	 * setOptionalItemAttribute(nts.uk.ctx.at.shared.dom.timeitemmanagement.
	 * OptionalItemAttribute)
	 */
	@Override
	public void setOptionalItemAtr(OptionalItemAtr optionalItemAttribute) {
		this.optionalItemAtr = optionalItemAttribute.value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.timeitemmanagement.OptionalItemSetMemento#
	 * setOptionalItemUsageClassification(nts.uk.ctx.at.shared.dom.
	 * timeitemmanagement.OptionalItemUsageClassification)
	 */
	@Override
	public void setOptionalItemUsageAtr(OptionalItemUsageAtr optionalItemUsageClassification) {
		this.usageAtr = optionalItemUsageClassification.value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.timeitemmanagement.OptionalItemSetMemento#
	 * setEmpConditionClassification(nts.uk.ctx.at.shared.dom.timeitemmanagement
	 * .EmpConditionClassification)
	 */
	@Override
	public void setEmpConditionAtr(EmpConditionAtr empConditionClassification) {
		this.empConditionAtr = empConditionClassification.value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.timeitemmanagement.OptionalItemSetMemento#
	 * setPerformanceClassification(nts.uk.ctx.at.shared.dom.timeitemmanagement.
	 * PerformanceClassification)
	 */
	@Override
	public void setPerformanceAtr(PerformanceAtr performanceClassification) {
		this.performanceAtr = performanceClassification.value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.timeitemmanagement.OptionalItemSetMemento#
	 * setCalculationResultRange(nts.uk.ctx.at.shared.dom.timeitemmanagement.
	 * CalculationResultRange)
	 */
	@Override
	public void setInputControlSetting(InputControlSetting inputControlSetting) {
		this.calcResultRange = new CalcResultRangeDto();
		inputControlSetting.getCalcResultRange().saveToMemento(this.calcResultRange);
		if (inputControlSetting.getDailyInputUnit().isPresent()) {
			this.calcResultRange.setTimeInputUnit(inputControlSetting.getDailyInputUnit().get().getTimeItemInputUnit().map(i -> i.value).orElse(null));
			this.calcResultRange.setNumberInputUnit(inputControlSetting.getDailyInputUnit().get().getNumberItemInputUnit().map(i -> i.value).orElse(null));
			this.calcResultRange.setAmountInputUnit(inputControlSetting.getDailyInputUnit().get().getAmountItemInputUnit().map(i -> i.value).orElse(null));
		}
		this.inputCheck = inputControlSetting.isInputWithCheckbox();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.OptionalItemSetMemento#setUnit(nts.uk.
	 * ctx.at.record.dom.optitem.UnitOfOptionalItem)
	 */
	@Override
	public void setUnit(Optional<UnitOfOptionalItem> unit) {
		this.unit = unit.isPresent() ? unit.get().v() : null;
	}

    @Override
    public void setCalAtr(CalculationClassification calAtr) {
        this.calAtr = calAtr.value;
    }

    @Override
    public void setNote(Optional<NoteOptionalItem> note) {
        this.note = note.isPresent() ? note.get().v() : null;
    }

    @Override
    public void setDescription(Optional<DescritionOptionalItem> description) {
        this.description = description.isPresent() ? description.get().v() : null;
    }
}
