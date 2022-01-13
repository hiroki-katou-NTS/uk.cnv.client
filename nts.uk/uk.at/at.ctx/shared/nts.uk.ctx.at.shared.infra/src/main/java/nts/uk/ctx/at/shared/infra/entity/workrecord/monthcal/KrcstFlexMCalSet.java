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
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.common.Month;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.AggregateSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.AggregateTimeSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.CarryforwardSetInShortageFlex;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.FlexAggregateMethod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.FlexMonthWorkTimeAggrSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.FlexTimeHandle;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.SettlePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.SettlePeriodMonths;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.ShortageFlexSetting;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The Class KrcstFlexMCalSet.
 */
@Getter
@Setter
@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class KrcstFlexMCalSet extends ContractUkJpaEntity {

	/** 不足設定.繰越設定 */
	@Column(name = "INSUFFIC_SET")
	private int insufficSet;

	/** フレックス時間の扱い.残業時間をフレックス時間に含める */
	@Column(name = "INCLUDE_OT")
	private boolean includeOt;

	/** フレックス時間の扱い.法定外休出時間をフレックス時間に含める */
	@Column(name = "INCLUDE_HDWK")
	private boolean includeHdwk;

	/** 集計方法 */
	@Column(name = "AGGR_METHOD")
	private int aggrMethod;

	/** 法定内集計設定.集計設定 */
	@Column(name = "LEGAL_AGGR_SET")
	private int legalAggrSet;

	/** 不足設定.開始月 */
	@Column(name = "SETTLE_PERIOD")
	private int settlePeriod;
	
	/** 不足設定.期間 */
	@Column(name = "START_MONTH")
	private int startMonth;
	
	/** 不足設定.清算期間 */
	@Column(name = "SETTLE_PERIOD_MON")
	private int settlePeriodMon;
	
	public void transfer(FlexMonthWorkTimeAggrSet domain) {
		
		aggrMethod =  domain.getAggrMethod().value;
		insufficSet = domain.getInsufficSet().getCarryforwardSet().value;
		legalAggrSet = domain.getLegalAggrSet().getAggregateSet().value;
		includeOt = domain.getFlexTimeHandle().isIncludeOverTime();
		includeHdwk = domain.getFlexTimeHandle().isIncludeIllegalHdwk();
		
		settlePeriod = domain.getInsufficSet().getSettlePeriod().value;
		startMonth = domain.getInsufficSet().getStartMonth().v();
		settlePeriodMon = domain.getInsufficSet().getPeriod().value;
	}
	
	public FlexTimeHandle flexTimeHandle() {
		
		return FlexTimeHandle.of(includeOt, includeHdwk);
	}
	
	public FlexAggregateMethod flexAggregateMethod() {
		
		return EnumAdaptor.valueOf(aggrMethod, FlexAggregateMethod.class);
	}
	
	public AggregateTimeSetting aggregateTimeSetting() {
		
		return AggregateTimeSetting.of(EnumAdaptor.valueOf(legalAggrSet, AggregateSetting.class));
	}
	
	public ShortageFlexSetting shortageFlexSetting() {
		
		return ShortageFlexSetting.of(EnumAdaptor.valueOf(insufficSet, CarryforwardSetInShortageFlex.class),
				EnumAdaptor.valueOf(settlePeriod, SettlePeriod.class), 
				new Month(startMonth), 
				EnumAdaptor.valueOf(settlePeriodMon, SettlePeriodMonths.class));
	}
}
