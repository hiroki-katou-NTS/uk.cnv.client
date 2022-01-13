/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.infra.repository.optitem;

import java.util.Optional;

import nts.uk.ctx.at.record.infra.entity.optitem.KrcmtAnyv;
import nts.uk.ctx.at.record.infra.entity.optitem.KrcmtAnyfResultRange;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.scherec.optitem.*;

/**
 * The Class JpaOptionalItemSetMemento.
 */
public class JpaOptionalItemSetMemento implements OptionalItemSetMemento {

	/** The type value. */
	private KrcmtAnyv entity;

	/**
	 * Instantiates a new jpa optional item set memento.
	 *
	 * @param entity the entity
	 */
	public JpaOptionalItemSetMemento(KrcmtAnyv entity) {
		this.entity = entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.OptionalItemSetMemento#setCompanyId(nts.
	 * uk.ctx.at.shared.dom.common.CompanyId)
	 */
	@Override
	public void setCompanyId(CompanyId comId) {
		this.entity.getKrcmtAnyvPK().setCid(comId.v());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.OptionalItemSetMemento#setOptionalItemNo
	 * (nts.uk.ctx.at.record.dom.optitem.OptionalItemNo)
	 */
	@Override
	public void setOptionalItemNo(OptionalItemNo optionalItemNo) {
		this.entity.getKrcmtAnyvPK().setOptionalItemNo(optionalItemNo.v());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.optitem.OptionalItemSetMemento#
	 * setOptionalItemName(nts.uk.ctx.at.record.dom.optitem.OptionalItemName)
	 */
	@Override
	public void setOptionalItemName(OptionalItemName optionalItemName) {
		this.entity.setOptionalItemName(optionalItemName.v());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.optitem.OptionalItemSetMemento#
	 * setOptionalItemAtr(nts.uk.ctx.at.record.dom.optitem.OptionalItemAtr)
	 */
	@Override
	public void setOptionalItemAtr(OptionalItemAtr optionalItemAtr) {
		this.entity.setOptionalItemAtr(optionalItemAtr.value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.optitem.OptionalItemSetMemento#
	 * setOptionalItemUsageAtr(nts.uk.ctx.at.record.dom.optitem.
	 * OptionalItemUsageAtr)
	 */
	@Override
	public void setOptionalItemUsageAtr(OptionalItemUsageAtr optionalItemUsageAtr) {
		this.entity.setUsageAtr(optionalItemUsageAtr.value);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.optitem.OptionalItemSetMemento#
	 * setEmpConditionAtr(nts.uk.ctx.at.record.dom.optitem.EmpConditionAtr)
	 */
	@Override
	public void setEmpConditionAtr(EmpConditionAtr empConditionAtr) {
		this.entity.setEmpConditionAtr(empConditionAtr.value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.OptionalItemSetMemento#setPerformanceAtr
	 * (nts.uk.ctx.at.record.dom.optitem.PerformanceAtr)
	 */
	@Override
	public void setPerformanceAtr(PerformanceAtr performanceAtr) {
		this.entity.setPerformanceAtr(performanceAtr.value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.optitem.OptionalItemSetMemento#
	 * setCalculationResultRange(nts.uk.ctx.at.record.dom.optitem.
	 * CalcResultRange)
	 */
	@Override
	public void setInputControlSetting(InputControlSetting inputControlSetting) {
		this.entity.setInputCheck(inputControlSetting.isInputWithCheckbox());
		KrcmtAnyfResultRange entityRange = this.entity.getKrcstCalcResultRange();
		inputControlSetting.getCalcResultRange().saveToMemento(new JpaCalcResultRangeSetMemento(entityRange));
		if (inputControlSetting.getDailyInputUnit().isPresent()) {
			entityRange.setTimeItemInputUnit(inputControlSetting.getDailyInputUnit().get().getTimeItemInputUnit().map(i -> i.value).orElse(null));
			entityRange.setNumberItemInputUnit(inputControlSetting.getDailyInputUnit().get().getNumberItemInputUnit().map(i -> i.value).orElse(null));
			entityRange.setAmountItemInputUnit(inputControlSetting.getDailyInputUnit().get().getAmountItemInputUnit().map(i -> i.value).orElse(null));
		}
		this.entity.setKrcstCalcResultRange(entityRange);
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
		this.entity.setUnitOfOptionalItem(unit.isPresent() ? unit.get().v() : null);
	}

    @Override
    public void setCalAtr(CalculationClassification calcResultRange) {
        this.entity.setCalcAtr(calcResultRange.value);
    }

    @Override
    public void setNote(Optional<NoteOptionalItem> note) {
        this.entity.setNote(note.isPresent() ? note.get().v() : null);
    }

    @Override
    public void setDescription(Optional<DescritionOptionalItem> description) {
        this.entity.setDescription(description.isPresent() ? description.get().v() : null);
    }

}
