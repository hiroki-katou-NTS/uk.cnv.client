/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.infra.repository.workrecord.closure;

import java.util.List;

import lombok.Setter;
import nts.uk.ctx.at.record.dom.workrecord.closure.ClosureHistory;
import nts.uk.ctx.at.record.dom.workrecord.closure.ClosureMonth;
import nts.uk.ctx.at.record.dom.workrecord.closure.ClosureSetMemento;
import nts.uk.ctx.at.record.dom.workrecord.closure.CompanyId;
import nts.uk.ctx.at.record.dom.workrecord.closure.UseClassification;
import nts.uk.ctx.at.record.infra.entity.workrecord.closure.KclmtClosure;
import nts.uk.ctx.at.record.infra.entity.workrecord.closure.KclmtClosurePK;

/**
 * The Class JpaClosureSetMemento.
 */
public class JpaClosureSetMemento implements ClosureSetMemento{
	
	/** The kclmt closure. */
	
	@Setter
	private KclmtClosure kclmtClosure;
	
	/**
	 * Instantiates a new jpa closure set memento.
	 *
	 * @param kclmtClosure the kclmt closure
	 */
	public JpaClosureSetMemento(KclmtClosure kclmtClosure) {
		this.kclmtClosure = kclmtClosure;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.workrecord.closure.ClosureSetMemento#
	 * setCompanyId(nts.uk.ctx.at.record.dom.workrecord.closure.CompanyId)
	 */
	@Override
	public void setCompanyId(CompanyId companyId) {
		KclmtClosurePK pk = new KclmtClosurePK();
		pk.setCid(companyId.v());
		this.kclmtClosure.setKclmtClosurePK(pk);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.workrecord.closure.ClosureSetMemento#
	 * setClosureId(java.lang.Integer)
	 */
	@Override
	public void setClosureId(Integer closureId) {
		KclmtClosurePK pk = this.kclmtClosure.getKclmtClosurePK();
		pk.setClosureId(closureId);
		this.kclmtClosure.setKclmtClosurePK(pk);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.workrecord.closure.ClosureSetMemento#
	 * setUseClassification(nts.uk.ctx.at.record.dom.workrecord.closure.
	 * UseClassification)
	 */
	@Override
	public void setUseClassification(UseClassification useClassification) {
		this.kclmtClosure.setUseClass(useClassification.value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.workrecord.closure.ClosureSetMemento#setMonth(
	 * nts.uk.ctx.at.record.dom.workrecord.closure.ClosureMonth)
	 */
	@Override
	public void setMonth(ClosureMonth month) {
		this.kclmtClosure.setMonth(month.getProcessingDate().v());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.workrecord.closure.ClosureSetMemento#
	 * setClosureHistories(java.util.List)
	 */
	@Override
	public void setClosureHistories(List<ClosureHistory> closureHistories) {
		// TODO Auto-generated method stub
	}

}
