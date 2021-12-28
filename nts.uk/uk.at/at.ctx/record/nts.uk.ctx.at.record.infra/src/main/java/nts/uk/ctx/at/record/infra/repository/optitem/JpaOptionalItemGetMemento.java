/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.infra.repository.optitem;

import java.util.Optional;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.record.infra.entity.optitem.KrcmtAnyv;
import nts.uk.ctx.at.record.infra.entity.optitem.KrcmtAnyfResultRange;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.scherec.optitem.*;

/**
 * The Class JpaOptionalItemGetMemento.
 */
public class JpaOptionalItemGetMemento implements OptionalItemGetMemento {

	/** The type value. */
	private KrcmtAnyv typeValue;
	
	/** The krcst calc result range. */
	private KrcmtAnyfResultRange krcstCalcResultRange;

	/**
	 * Instantiates a new jpa optional item get memento.
	 *
	 * @param typeValue
	 *            the type value
	 */
	public JpaOptionalItemGetMemento(KrcmtAnyv typeValue, KrcmtAnyfResultRange... krcstCalcResultRangeView) {
		
		if(krcstCalcResultRangeView.length > 0) {
			this.krcstCalcResultRange = krcstCalcResultRangeView[0];
		}
		
		this.typeValue = typeValue;
	}
	
	public JpaOptionalItemGetMemento(KrcmtAnyv typeValue, KrcmtAnyfResultRange krcstCalcResultRange) {
		this.typeValue = typeValue;
		this.krcstCalcResultRange = krcstCalcResultRange;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.OptionalItemGetMemento#getCompanyId()
	 */
	@Override
	public CompanyId getCompanyId() {
		return new CompanyId(this.typeValue.getKrcmtAnyvPK().getCid());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.OptionalItemGetMemento#getOptionalItemNo
	 * ()
	 */
	@Override
	public OptionalItemNo getOptionalItemNo() {
		return new OptionalItemNo(this.typeValue.getKrcmtAnyvPK().getOptionalItemNo());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.optitem.OptionalItemGetMemento#
	 * getOptionalItemName()
	 */
	@Override
	public OptionalItemName getOptionalItemName() {
		return new OptionalItemName(this.typeValue.getOptionalItemName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.optitem.OptionalItemGetMemento#
	 * getOptionalItemAtr()
	 */
	@Override
	public OptionalItemAtr getOptionalItemAtr() {
		return EnumAdaptor.valueOf(this.typeValue.getOptionalItemAtr(), OptionalItemAtr.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.optitem.OptionalItemGetMemento#
	 * getOptionalItemUsageAtr()
	 */
	@Override
	public OptionalItemUsageAtr getOptionalItemUsageAtr() {
		return EnumAdaptor.valueOf(this.typeValue.getUsageAtr(), OptionalItemUsageAtr.class);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.optitem.OptionalItemGetMemento#
	 * getEmpConditionAtr()
	 */
	@Override
	public EmpConditionAtr getEmpConditionAtr() {
		return EnumAdaptor.valueOf(this.typeValue.getEmpConditionAtr(), EmpConditionAtr.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.OptionalItemGetMemento#getPerformanceAtr
	 * ()
	 */
	@Override
	public PerformanceAtr getPerformanceAtr() {
		return EnumAdaptor.valueOf(this.typeValue.getPerformanceAtr(), PerformanceAtr.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.optitem.OptionalItemGetMemento#
	 * getCalculationResultRange()
	 */
	@Override
	public InputControlSetting getInputControlSetting() {
		if (this.krcstCalcResultRange != null)
			return new InputControlSetting(
					this.typeValue.isInputCheck(),
					new CalcResultRange(new JpaCalcResultRangeGetMemento(this.krcstCalcResultRange)),
					this.krcstCalcResultRange.getDailyInputUnit()
			);
		else
			return new InputControlSetting(
					this.typeValue.isInputCheck(),
					new CalcResultRange(new JpaCalcResultRangeGetMemento(this.typeValue.getKrcstCalcResultRange())),
					this.typeValue.getKrcstCalcResultRange().getDailyInputUnit()
			);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.record.dom.optitem.OptionalItemGetMemento#getUnit()
	 */
	@Override
	public Optional<UnitOfOptionalItem> getUnit() {
		return Optional.ofNullable(new UnitOfOptionalItem(this.typeValue.getUnitOfOptionalItem()));
	}

    @Override
    public CalculationClassification getCalcAtr() {
        return EnumAdaptor.valueOf(this.typeValue.getCalcAtr(), CalculationClassification.class);
    }

    @Override
    public Optional<NoteOptionalItem> getNote() {
        return this.typeValue.getNote() == null ? Optional.empty() : Optional.of(new NoteOptionalItem(this.typeValue.getNote()));
    }

    @Override
    public Optional<DescritionOptionalItem> getDescription() {
        return this.typeValue.getDescription() == null ? Optional.empty() : Optional.of(new DescritionOptionalItem(this.typeValue.getDescription()));
    }

}
