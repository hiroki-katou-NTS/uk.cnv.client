/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.entity.workrecord.monthcal;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.BooleanUtils;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.ExcessOutsideTimeSetReg;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.RegularWorkTimeAggrSet;
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
	private boolean includeLegalOt;

	/** The include holiday ot. */
	@Column(name = "INCLUDE_HOLIDAY_OT")
	private boolean includeHolidayOt;

	/** The include extra ot. */
	@Column(name = "INCLUDE_EXTRA_OT")
	private boolean includeExtraOt;

	/** The except legal holidaywork ot. */
	@Column(name = "EXC_LEGAL_HDWK_OT")
	private boolean exceptLegalHdwkOt;
	
	/** The include legal aggr. */
	@Column(name = "INCLUDE_LEGAL_AGGR")
	private boolean includeLegalAggr;

	/** The include holiday aggr. */
	@Column(name = "INCLUDE_HOLIDAY_AGGR")
	private boolean includeHolidayAggr;

	/** The include extra aggr. */
	@Column(name = "INCLUDE_EXTRA_AGGR")
	private boolean includeExtraAggr;

	public void transfer(RegularWorkTimeAggrSet domain) {
		
		setAggregateTimeSet(domain.getAggregateTimeSet());
		
		setExcessOutsideTimeSet(domain.getExcessOutsideTimeSet());
	}
	
	private void setAggregateTimeSet(ExcessOutsideTimeSetReg aggregateTimeSet) {
		this.includeLegalAggr = 
				BooleanUtils.toBoolean(aggregateTimeSet.isLegalOverTimeWork());
		this.includeHolidayAggr =
				BooleanUtils.toBoolean(aggregateTimeSet.isLegalHoliday());
		this.includeExtraAggr = 
				BooleanUtils.toBoolean(aggregateTimeSet.isSurchargeWeekMonth());
	}
	
	public void setExcessOutsideTimeSet(ExcessOutsideTimeSetReg excessOutsideTimeSet) {
		this.includeLegalOt = 
				BooleanUtils.toBoolean(excessOutsideTimeSet.isLegalOverTimeWork());
		this.includeHolidayOt =
				BooleanUtils.toBoolean(excessOutsideTimeSet.isLegalHoliday());
		this.includeExtraOt =
				BooleanUtils.toBoolean(excessOutsideTimeSet.isSurchargeWeekMonth());
		this.exceptLegalHdwkOt =
				BooleanUtils.toBoolean(excessOutsideTimeSet.isExceptLegalHdwk());
	}
	
	public ExcessOutsideTimeSetReg getAggregateTimeSet() {
		
		return new ExcessOutsideTimeSetReg(
				BooleanUtils.toBoolean(includeLegalAggr),
				BooleanUtils.toBoolean(includeHolidayAggr), 
				BooleanUtils.toBoolean(includeExtraAggr),
				false);
	}
	
	public ExcessOutsideTimeSetReg getExcessOutsideTimeSet() {
		
		return new ExcessOutsideTimeSetReg(
				BooleanUtils.toBoolean(includeLegalOt),
				BooleanUtils.toBoolean(includeHolidayOt), 
				BooleanUtils.toBoolean(includeExtraOt),
				BooleanUtils.toBoolean(exceptLegalHdwkOt));
	}
}
