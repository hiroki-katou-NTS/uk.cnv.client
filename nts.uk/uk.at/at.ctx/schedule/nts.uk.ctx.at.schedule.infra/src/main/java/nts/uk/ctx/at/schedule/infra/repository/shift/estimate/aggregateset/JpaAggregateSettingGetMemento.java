package nts.uk.ctx.at.schedule.infra.repository.shift.estimate.aggregateset;

import java.util.ArrayList;
import java.util.List;

import nts.uk.ctx.at.schedule.dom.shift.estimate.aggregateset.AggregateSettingGetMemento;
import nts.uk.ctx.at.schedule.dom.shift.estimate.aggregateset.ExtraTimeItemNo;
import nts.uk.ctx.at.schedule.dom.shift.estimate.aggregateset.HalfDayWorkCountCat;
import nts.uk.ctx.at.schedule.dom.shift.estimate.aggregateset.MonthlyWorkingDaySetting;
import nts.uk.ctx.at.schedule.dom.shift.estimate.aggregateset.NotUseAtr;
import nts.uk.ctx.at.schedule.infra.entity.shift.estimate.aggregateset.KscmtEstAggregate;
import nts.uk.ctx.at.shared.dom.common.CompanyId;

/**
 * The Class JpaAggregateSettingGetMemento.
 */
public class JpaAggregateSettingGetMemento implements AggregateSettingGetMemento{
	
	/** The kscst est aggregate set. */
	private KscmtEstAggregate kscstEstAggregateSet;
	
	/**
	 * Instantiates a new jpa aggregate setting get memento.
	 *
	 * @param entity the entity
	 */
	public JpaAggregateSettingGetMemento(KscmtEstAggregate entity){
		this.kscstEstAggregateSet = entity;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.schedule.dom.shift.estimate.aggregateset.AggregateSettingGetMemento#getCompanyId()
	 */
	@Override
	public CompanyId getCompanyId() {
		return new CompanyId(this.kscstEstAggregateSet.getCid());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.schedule.dom.shift.estimate.aggregateset.AggregateSettingGetMemento#getPremiumNo()
	 */
	@Override
	public List<ExtraTimeItemNo> getPremiumNo() {
		List<ExtraTimeItemNo> list = new ArrayList<>();
		return list;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.schedule.dom.shift.estimate.aggregateset.AggregateSettingGetMemento#getMonthlyWorkingDaySetting()
	 */
	@Override
	public MonthlyWorkingDaySetting getMonthlyWorkingDaySetting() {
		return new MonthlyWorkingDaySetting(HalfDayWorkCountCat.valueOf(this.kscstEstAggregateSet.getHalfDayAtr()),
											NotUseAtr.valueOf(this.kscstEstAggregateSet.getYearHdAtr()),
											NotUseAtr.valueOf(this.kscstEstAggregateSet.getSphdAtr()),
											NotUseAtr.valueOf(this.kscstEstAggregateSet.getHavyHdAtr()));
	}

}
