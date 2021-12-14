/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.workrecord.monthcal.company;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.com.ComFlexMonthActCalSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.com.ComFlexMonthActCalSetRepo;
import nts.uk.ctx.at.shared.infra.entity.workrecord.monthcal.company.KrcmtCalcMSetFleCom;

/**
 * The Class JpaComFlexMonthActCalSetRepository.
 */
@Stateless
public class JpaComFlexMonthActCalSetRepository extends JpaRepository implements ComFlexMonthActCalSetRepo {

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.workrecord.monthcal.company.
	 * ComFlexMonthActCalSetRepository#find(java.lang.String)
	 */
	@Override
	public Optional<ComFlexMonthActCalSet> find(String companyId) {
		// Get info
		return this.queryProxy().find(companyId, KrcmtCalcMSetFleCom.class).map(c -> toDomain(c));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.workrecord.monthcal.company.
	 * ComFlexMonthActCalSetRepository#add(nts.uk.ctx.at.record.dom.workrecord.
	 * monthcal.company.ComFlexMonthActCalSet)
	 */
	@Override
	public void add(ComFlexMonthActCalSet domain) {
		// Create new entity
		KrcmtCalcMSetFleCom entity = new KrcmtCalcMSetFleCom();

		// Transfer data
		entity.transfer(domain);
		entity.setCid(domain.getComId());
		entity.setWithinTimeUse(domain.isWithinTimeUsageAttr() ? 1 : 0);

		// Insert into DB
		this.commandProxy().insert(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.workrecord.monthcal.company.
	 * ComFlexMonthActCalSetRepository#update(nts.uk.ctx.at.record.dom.
	 * workrecord.monthcal.company.ComFlexMonthActCalSet)
	 */
	@Override
	public void update(ComFlexMonthActCalSet domain) {
		// Get info
		this.queryProxy().find(domain.getComId(), KrcmtCalcMSetFleCom.class).ifPresent(e -> {
			
			e.transfer(domain);
			e.setWithinTimeUse(domain.isWithinTimeUsageAttr() ? 1 : 0);
			
			this.commandProxy().update(e);
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.workrecord.monthcal.company.
	 * ComFlexMonthActCalSetRepository#remove(java.lang.String)
	 */
	@Override
	public void remove(String companyId) {
		this.commandProxy().remove(KrcmtCalcMSetFleCom.class, companyId);
	}

	private ComFlexMonthActCalSet toDomain (KrcmtCalcMSetFleCom e) {
		
		return ComFlexMonthActCalSet.of(e.getCid(),
										e.flexAggregateMethod(),
										e.shortageFlexSetting(), 
										e.aggregateTimeSetting(), 
										e.flexTimeHandle(),
										e.getWithinTimeUse() == 1);
	}

}
