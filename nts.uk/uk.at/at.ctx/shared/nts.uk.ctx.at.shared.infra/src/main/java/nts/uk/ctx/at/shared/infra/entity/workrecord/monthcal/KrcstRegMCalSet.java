/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.entity.workrecord.monthcal;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.ExcessOutsideTimeSetReg;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.RegularWorkTimeAggrSet;
import nts.uk.ctx.at.shared.infra.repository.workrecord.monthcal.BooleanGetAtr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The Class KrcstRegMCalSet.
 */
@Getter
@Setter
@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class KrcstRegMCalSet extends ContractUkJpaEntity {

	/** The include legal ot. */
	@Column(name = "INCLUDE_LEGAL_OT")
	private int includeLegalOt;

	/** The include holiday ot. */
	@Column(name = "INCLUDE_HOLIDAY_OT")
	private int includeHolidayOt;

	/** The include extra ot. */
	@Column(name = "INCLUDE_EXTRA_OT")
	private int includeExtraOt;

	/** The except legal holidaywork ot. */
	@Column(name = "EXC_LEGAL_HDWK_OT")
	private int exceptLegalHdwkOt;
	
	/** The include legal aggr. */
	@Column(name = "INCLUDE_LEGAL_AGGR")
	private int includeLegalAggr;

	/** The include holiday aggr. */
	@Column(name = "INCLUDE_HOLIDAY_AGGR")
	private int includeHolidayAggr;

	/** The include extra aggr. */
	@Column(name = "INCLUDE_EXTRA_AGGR")
	private int includeExtraAggr;

	public void transfer(RegularWorkTimeAggrSet domain) {
		
		setAggregateTimeSet(domain.getAggregateTimeSet());
		
		setExcessOutsideTimeSet(domain.getExcessOutsideTimeSet());
	}
	
	private void setAggregateTimeSet(ExcessOutsideTimeSetReg aggregateTimeSet) {
		this.includeLegalAggr = 
				BooleanGetAtr.getAtrByBoolean(aggregateTimeSet.isLegalOverTimeWork());
		this.includeHolidayAggr =
				BooleanGetAtr.getAtrByBoolean(aggregateTimeSet.isLegalHoliday());
		this.includeExtraAggr = 
				BooleanGetAtr.getAtrByBoolean(aggregateTimeSet.isSurchargeWeekMonth());
	}
	
	public void setExcessOutsideTimeSet(ExcessOutsideTimeSetReg excessOutsideTimeSet) {
		this.includeLegalOt = 
				BooleanGetAtr.getAtrByBoolean(excessOutsideTimeSet.isLegalOverTimeWork());
		this.includeHolidayOt =
				BooleanGetAtr.getAtrByBoolean(excessOutsideTimeSet.isLegalHoliday());
		this.includeExtraOt =
				BooleanGetAtr.getAtrByBoolean(excessOutsideTimeSet.isSurchargeWeekMonth());
		this.exceptLegalHdwkOt =
				BooleanGetAtr.getAtrByBoolean(excessOutsideTimeSet.isExceptLegalHdwk());
	}
	
	public ExcessOutsideTimeSetReg getAggregateTimeSet() {
		
		return new ExcessOutsideTimeSetReg(
				BooleanGetAtr.getAtrByInteger(includeLegalAggr),
				BooleanGetAtr.getAtrByInteger(includeHolidayAggr), 
				BooleanGetAtr.getAtrByInteger(includeExtraAggr),
				false);
	}
	
	public ExcessOutsideTimeSetReg getExcessOutsideTimeSet() {
		
		return new ExcessOutsideTimeSetReg(
				BooleanGetAtr.getAtrByInteger(includeLegalOt),
				BooleanGetAtr.getAtrByInteger(includeHolidayOt), 
				BooleanGetAtr.getAtrByInteger(includeExtraOt),
				BooleanGetAtr.getAtrByInteger(exceptLegalHdwkOt));
	}
}
