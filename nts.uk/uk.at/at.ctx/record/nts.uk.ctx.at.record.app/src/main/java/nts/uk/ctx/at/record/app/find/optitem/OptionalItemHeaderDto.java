/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.app.find.optitem;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.scherec.optitem.*;

/**
 * The Class OptionalItemHeaderDto.
 */
@Getter
@Setter
public class OptionalItemHeaderDto implements OptionalItemSetMemento {
	
	/** The optional item no. */
	private int itemNo;

	/** The optional item name. */
	private String itemName;

	/** The performance atr. */
	private int performanceAtr;

	/** The usage classification. */
	private int usageAtr;

	/** The calculation atr. */
	private int calcAtr;
	
	private String nameNotJP;

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
		this.itemNo = optionalItemNo.v();
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
		this.itemName = optionalItemName.v();
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
		// Not used.
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
		// Not used.
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
	public void setInputControlSetting(InputControlSetting calculationResultRange) {
		// Not used.
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
		// Not used.
	}

    @Override
    public void setCalAtr(CalculationClassification calAtr) {
        this.calcAtr = calAtr.value;
        
    }

    @Override
    public void setNote(Optional<NoteOptionalItem> note) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setDescription(Optional<DescritionOptionalItem> description) {
        // TODO Auto-generated method stub
        
    }
}
