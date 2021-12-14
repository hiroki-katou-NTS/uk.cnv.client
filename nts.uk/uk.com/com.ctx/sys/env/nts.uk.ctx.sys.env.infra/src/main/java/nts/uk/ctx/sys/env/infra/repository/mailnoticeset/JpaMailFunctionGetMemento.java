/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.env.infra.repository.mailnoticeset;

import nts.uk.ctx.sys.env.dom.mailnoticeset.FunctionId;
import nts.uk.ctx.sys.env.dom.mailnoticeset.FunctionName;
import nts.uk.ctx.sys.env.dom.mailnoticeset.MailFunctionGetMemento;
import nts.uk.ctx.sys.env.dom.mailnoticeset.SortOrder;
import nts.uk.ctx.sys.env.infra.entity.mailnoticeset.SevmtMailFunction;

/**
 * The Class JpaMailFunctionGetMemento.
 */
public class JpaMailFunctionGetMemento implements MailFunctionGetMemento {

	/** The entity. */
	private SevmtMailFunction entity;

	/**
	 * Instantiates a new jpa mail function get memento.
	 *
	 * @param entity the entity
	 */
	public JpaMailFunctionGetMemento(SevmtMailFunction entity) {
		this.entity = entity;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.env.dom.mailnoticeset.MailFunctionGetMemento#getFunctionId()
	 */
	@Override
	public FunctionId getFunctionId() {
		return new FunctionId(this.entity.getFunctionId().v());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.env.dom.mailnoticeset.MailFunctionGetMemento#getFunctionName()
	 */
	@Override
	public FunctionName getFunctionName() {
		return new FunctionName(this.entity.getFunctionName().v());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.env.dom.mailnoticeset.MailFunctionGetMemento#isProprietySendMailSettingAtr()
	 */
	@Override
	public boolean isProprietySendMailSettingAtr() {
		return this.entity.getSendMailSetAtr() == 0;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.env.dom.mailnoticeset.MailFunctionGetMemento#getSortOrder()
	 */
	@Override
	public SortOrder getSortOrder() {
		return new SortOrder(this.entity.getSortOrder().v());
	}
}
