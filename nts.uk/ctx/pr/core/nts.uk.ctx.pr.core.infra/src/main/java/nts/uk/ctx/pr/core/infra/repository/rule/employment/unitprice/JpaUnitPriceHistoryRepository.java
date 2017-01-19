/******************************************************************
 * Copyright (c) 2016 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.infra.repository.rule.employment.unitprice;

import java.util.List;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.pr.core.dom.rule.employment.unitprice.UnitPriceHistory;
import nts.uk.ctx.pr.core.dom.rule.employment.unitprice.UnitPriceHistoryRepository;

/**
 * The Class JpaUnitPriceHistoryRepository.
 */
@Stateless
public class JpaUnitPriceHistoryRepository extends JpaRepository implements UnitPriceHistoryRepository {

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.dom.rule.employment.unitprice.
	 * UnitPriceHistoryRepository#add(nts.uk.ctx.pr.core.dom.rule.employment.
	 * unitprice.UnitPriceHistory)
	 */
	@Override
	public void add(UnitPriceHistory unitPriceHistory) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.dom.rule.employment.unitprice.
	 * UnitPriceHistoryRepository#update(nts.uk.ctx.pr.core.dom.rule.employment.
	 * unitprice.UnitPriceHistory)
	 */
	@Override
	public void update(UnitPriceHistory unitPriceHistory) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.dom.rule.employment.unitprice.
	 * UnitPriceHistoryRepository#remove(java.lang.String, java.lang.Long)
	 */
	@Override
	public void remove(String id, Long version) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.dom.rule.employment.unitprice.
	 * UnitPriceHistoryRepository#findAll(int)
	 */
	@Override
	public List<UnitPriceHistory> findAll(int companyCode) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.dom.rule.employment.unitprice.
	 * UnitPriceHistoryRepository#findById(java.lang.String)
	 */
	@Override
	public UnitPriceHistory findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
