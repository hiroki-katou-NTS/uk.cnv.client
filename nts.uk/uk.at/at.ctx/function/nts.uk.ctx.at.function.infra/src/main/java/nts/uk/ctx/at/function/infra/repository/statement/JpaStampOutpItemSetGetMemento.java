/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.function.infra.repository.statement;

import nts.uk.ctx.at.function.dom.statement.StampOutputSettingCode;
import nts.uk.ctx.at.function.dom.statement.StampOutputSettingName;
import nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetGetMemento;
import nts.uk.ctx.at.function.infra.entity.statement.KfnmtStampOutpItem;
import nts.uk.ctx.at.shared.dom.common.CompanyId;

/**
 * The Class JpaKfnmtStampOutpItemSetGetMemento.
 */
public class JpaStampOutpItemSetGetMemento implements StampingOutputItemSetGetMemento{

	/** The entity. */
	private KfnmtStampOutpItem entity;
	
	/** The Constant VALUE_TRUE. */
	private static final Integer VALUE_TRUE = 1;
	
	/**
	 * Instantiates a new jpa kfnmt stamp outp item set get memento.
	 *
	 * @param entity the entity
	 */
	public JpaStampOutpItemSetGetMemento(KfnmtStampOutpItem entity) {
		super();
		this.entity = entity;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetGetMemento#getCompanyID()
	 */
	@Override
	public CompanyId getCompanyID() {
		return new CompanyId(this.entity.getId().getCid());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetGetMemento#getStampOutputSetCode()
	 */
	@Override
	public StampOutputSettingCode getStampOutputSetCode() {
		return new StampOutputSettingCode(this.entity.getId().getStampOutputSetCode());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetGetMemento#getStampOutputSetName()
	 */
	@Override
	public StampOutputSettingName getStampOutputSetName() {
		return new StampOutputSettingName(this.entity.getStampOutputSetName());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetGetMemento#getOutputEmbossMethod()
	 */
	@Override
	public boolean getOutputEmbossMethod() {
		return this.entity.getOutputEmbossMethod().intValue() == VALUE_TRUE ? true : false; 
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetGetMemento#getOutputWorkHours()
	 */
	@Override
	public boolean getOutputWorkHours() {
		return this.entity.getOutputWorkHours().intValue() == VALUE_TRUE ? true : false;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetGetMemento#getOutputSetLocation()
	 */
	@Override
	public boolean getOutputSetLocation() {
		return this.entity.getOutputSetLocation().intValue() == VALUE_TRUE ? true : false;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetGetMemento#getOutputPosInfor()
	 */
	@Override
	public boolean getOutputPosInfor() {
		return this.entity.getOutputPosInfor().intValue() == VALUE_TRUE	? true : false;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetGetMemento#getOutputOT()
	 */
	@Override
	public boolean getOutputOT() {
		return this.entity.getOutputOt().intValue() == VALUE_TRUE ? true : false;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetGetMemento#getOutputNightTime()
	 */
	@Override
	public boolean getOutputNightTime() {
		return this.entity.getOutputNightTime().intValue() == VALUE_TRUE ? true : false;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetGetMemento#getOutputSupportCard()
	 */
	@Override
	public boolean getOutputSupportCard() {
		return this.entity.getOutputSupportCard().intValue() == VALUE_TRUE ? true : false;
	}
}
