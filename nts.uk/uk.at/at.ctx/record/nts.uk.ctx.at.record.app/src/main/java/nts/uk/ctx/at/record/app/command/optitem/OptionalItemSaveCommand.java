/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.app.command.optitem;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.record.app.command.optitem.calculation.FormulaDto;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.scherec.optitem.*;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class OptionalItemSaveCommand.
 */
@Getter
@Setter
public class OptionalItemSaveCommand implements OptionalItemGetMemento {

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
	
	private String langId;
	
	private int calAtr;
	
	private String note;
	
	private String description;

	private boolean inputCheck;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.workrecord.OptionalItemGetMemento#getCompanyId()
	 */
	@Override
	public CompanyId getCompanyId() {
		return new CompanyId(AppContexts.user().companyId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.workrecord.OptionalItemGetMemento#
	 * getOptionalItemNo()
	 */
	@Override
	public OptionalItemNo getOptionalItemNo() {
		return new OptionalItemNo(this.optionalItemNo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.workrecord.OptionalItemGetMemento#
	 * getOptionalItemName()
	 */
	@Override
	public OptionalItemName getOptionalItemName() {
		return new OptionalItemName(this.optionalItemName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.workrecord.OptionalItemGetMemento#
	 * getOptionalItemAttribute()
	 */
	@Override
	public OptionalItemAtr getOptionalItemAtr() {
		return EnumAdaptor.valueOf(this.optionalItemAtr, OptionalItemAtr.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.workrecord.OptionalItemGetMemento#
	 * getOptionalItemUsageClassification()
	 */
	@Override
	public OptionalItemUsageAtr getOptionalItemUsageAtr() {
		return EnumAdaptor.valueOf(this.usageAtr, OptionalItemUsageAtr.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.workrecord.OptionalItemGetMemento#
	 * getEmpConditionClassification()
	 */
	@Override
	public EmpConditionAtr getEmpConditionAtr() {
		return EnumAdaptor.valueOf(this.empConditionAtr, EmpConditionAtr.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.workrecord.OptionalItemGetMemento#
	 * getPerformanceClassification()
	 */
	@Override
	public PerformanceAtr getPerformanceAtr() {
		return EnumAdaptor.valueOf(this.performanceAtr, PerformanceAtr.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.workrecord.OptionalItemGetMemento#
	 * getCalculationResultRange()
	 */
	@Override
	public InputControlSetting getInputControlSetting() {
		return new InputControlSetting(
				this.inputCheck,
				new CalcResultRange(this.calcResultRange),
				Optional.of(new DailyResultInputUnit(
						Optional.ofNullable(this.calcResultRange.getTimeInputUnit() == null ? null : EnumAdaptor.valueOf(this.calcResultRange.getTimeInputUnit(), TimeItemInputUnit.class)),
						Optional.ofNullable(this.calcResultRange.getNumberInputUnit() == null ? null : EnumAdaptor.valueOf(this.calcResultRange.getNumberInputUnit(), NumberItemInputUnit.class)),
						Optional.ofNullable(this.calcResultRange.getAmountInputUnit() == null ? null : EnumAdaptor.valueOf(this.calcResultRange.getAmountInputUnit(), AmountItemInputUnit.class))
				))
		);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.record.dom.optitem.OptionalItemGetMemento#getUnit()
	 */
	@Override
	public Optional<UnitOfOptionalItem> getUnit() {
		return Optional.ofNullable(new UnitOfOptionalItem(this.unit));
	}

    @Override
    public CalculationClassification getCalcAtr() {
        return EnumAdaptor.valueOf(this.calAtr, CalculationClassification.class);
    }

    @Override
    public Optional<NoteOptionalItem> getNote() {
        return this.note == null ? Optional.empty() : Optional.ofNullable(new NoteOptionalItem(this.note));
    }

    @Override
    public Optional<DescritionOptionalItem> getDescription() {
        return this.description == null ? Optional.empty() : Optional.ofNullable(new DescritionOptionalItem(this.description));
    }
}
