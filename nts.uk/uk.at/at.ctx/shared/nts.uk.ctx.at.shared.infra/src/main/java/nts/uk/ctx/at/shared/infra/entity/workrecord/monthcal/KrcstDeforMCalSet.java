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
import nts.uk.ctx.at.shared.dom.common.Month;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.DeforLaborCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.DeforLaborSettlementPeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.DeforWorkTimeAggrSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.ExcessOutsideTimeSetReg;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The Class KrcstDeforMCalSet.
 */
@Getter
@Setter
@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class KrcstDeforMCalSet extends ContractUkJpaEntity {

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

	/** The is ot irg. */
	@Column(name = "IS_OT_IRG")
	private boolean isOtIrg;

	/** The period. */
	@Column(name = "PERIOD")
	private int period;

	/** The repeat atr. */
	@Column(name = "REPEAT_ATR")
	private boolean repeatAtr;

	/** The str month. */
	@Column(name = "STR_MONTH")
	private int strMonth;
	
	public void transfer(DeforWorkTimeAggrSet domain) {
		
		includeLegalAggr = (
				BooleanUtils.toBoolean(domain.getAggregateTimeSet().isLegalOverTimeWork()));
		includeHolidayAggr = (
				BooleanUtils.toBoolean(domain.getAggregateTimeSet().isLegalHoliday()));
		includeExtraAggr = (
				BooleanUtils.toBoolean(domain.getAggregateTimeSet().isSurchargeWeekMonth()));
		
		includeLegalOt = (
				BooleanUtils.toBoolean(domain.getExcessOutsideTimeSet().isLegalOverTimeWork()));
		includeHolidayOt = (
				BooleanUtils.toBoolean(domain.getExcessOutsideTimeSet().isLegalHoliday()));
		includeExtraOt = (
				BooleanUtils.toBoolean(domain.getExcessOutsideTimeSet().isSurchargeWeekMonth()));
		
		exceptLegalHdwkOt = (
				BooleanUtils.toBoolean(domain.getExcessOutsideTimeSet().isExceptLegalHdwk()));
		
		isOtIrg = (BooleanUtils.toBoolean(domain.getDeforLaborCalSetting().isOtTransCriteria()));
		
		period = (domain.getSettlementPeriod().getPeriod().v());
		strMonth = (domain.getSettlementPeriod().getStartMonth().v());
		repeatAtr = (BooleanUtils.toBoolean(domain.getSettlementPeriod().isRepeat()));
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
	
	public DeforLaborCalSetting deforLaborCalSetting() {
		
		return new DeforLaborCalSetting(isOtIrg);
	}
	
	public DeforLaborSettlementPeriod deforLaborSettlementPeriod() {
		
		return new DeforLaborSettlementPeriod(new Month(strMonth),
												new Month(period), 
												repeatAtr);
	}

}
