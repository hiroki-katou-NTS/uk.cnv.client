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
import nts.uk.ctx.at.shared.dom.common.Month;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.DeforLaborCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.DeforLaborSettlementPeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.DeforWorkTimeAggrSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.ExcessOutsideTimeSetReg;
import nts.uk.ctx.at.shared.infra.repository.workrecord.monthcal.BooleanGetAtr;
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

	/** The is ot irg. */
	@Column(name = "IS_OT_IRG")
	private int isOtIrg;

	/** The period. */
	@Column(name = "PERIOD")
	private int period;

	/** The repeat atr. */
	@Column(name = "REPEAT_ATR")
	private int repeatAtr;

	/** The str month. */
	@Column(name = "STR_MONTH")
	private int strMonth;
	
	public void transfer(DeforWorkTimeAggrSet domain) {
		
		includeLegalAggr = (
				BooleanGetAtr.getAtrByBoolean(domain.getAggregateTimeSet().isLegalOverTimeWork()));
		includeHolidayAggr = (
				BooleanGetAtr.getAtrByBoolean(domain.getAggregateTimeSet().isLegalHoliday()));
		includeExtraAggr = (
				BooleanGetAtr.getAtrByBoolean(domain.getAggregateTimeSet().isSurchargeWeekMonth()));
		
		includeLegalOt = (
				BooleanGetAtr.getAtrByBoolean(domain.getExcessOutsideTimeSet().isLegalOverTimeWork()));
		includeHolidayOt = (
				BooleanGetAtr.getAtrByBoolean(domain.getExcessOutsideTimeSet().isLegalHoliday()));
		includeExtraOt = (
				BooleanGetAtr.getAtrByBoolean(domain.getExcessOutsideTimeSet().isSurchargeWeekMonth()));
		
		exceptLegalHdwkOt = (
				BooleanGetAtr.getAtrByBoolean(domain.getExcessOutsideTimeSet().isExceptLegalHdwk()));
		
		isOtIrg = (BooleanGetAtr.getAtrByBoolean(domain.getDeforLaborCalSetting().isOtTransCriteria()));
		
		period = (domain.getSettlementPeriod().getPeriod().v());
		strMonth = (domain.getSettlementPeriod().getStartMonth().v());
		repeatAtr = (BooleanGetAtr.getAtrByBoolean(domain.getSettlementPeriod().isRepeat()));
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
	
	public DeforLaborCalSetting deforLaborCalSetting() {
		
		return new DeforLaborCalSetting(isOtIrg == 1);
	}
	
	public DeforLaborSettlementPeriod deforLaborSettlementPeriod() {
		
		return new DeforLaborSettlementPeriod(new Month(strMonth),
												new Month(period), 
												repeatAtr == 1);
	}

}
