package nts.uk.ctx.at.record.infra.repository.divergence.time.history;

import nts.uk.ctx.at.record.dom.divergence.time.history.DivergenceReferenceTimeUsageUnitSetMemento;
import nts.uk.ctx.at.record.infra.entity.divergence.time.history.KrcmtDvgcUnitSet;

/**
 * The Class JpaDivergenceReferenceTimeUsageUnitSetMemento.
 */
public class JpaDivergenceReferenceTimeUsageUnitSetMemento implements DivergenceReferenceTimeUsageUnitSetMemento{

	/** The entity. */
	private KrcmtDvgcUnitSet krcstDrtUseUnit;
		
	/**
	 * Instantiates a new jpa divergence reference time usage unit set memento.
	 *
	 * @param entity
	 *            the entity
	 */
	public JpaDivergenceReferenceTimeUsageUnitSetMemento(KrcmtDvgcUnitSet entity) {
		this.krcstDrtUseUnit = entity;
	}	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.divergence.time.history.
	 * DivergenceReferenceTimeUsageUnitSetMemento#setCompanyId(java.lang.
	 * String)
	 */
	@Override
	public void setCompanyId(String companyId) {
		this.krcstDrtUseUnit.setCid(companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.divergence.time.history.
	 * DivergenceReferenceTimeUsageUnitSetMemento#setWorkTypeUseSet(java.lang.
	 * BigDecimal)
	 */
	@Override
	public void setWorkTypeUseSet(boolean workTypeUseSet) {
		this.krcstDrtUseUnit.setWorktypeUseSet(workTypeUseSet);
	}

}
