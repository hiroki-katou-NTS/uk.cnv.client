/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.function.infra.repository.statement;

import java.math.BigDecimal;

import nts.uk.ctx.at.function.dom.statement.StampOutputSettingCode;
import nts.uk.ctx.at.function.dom.statement.StampOutputSettingName;
import nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetSetMemento;
import nts.uk.ctx.at.function.infra.entity.statement.KfnmtStampOutpItemSet;
import nts.uk.ctx.at.function.infra.entity.statement.KfnmtStampOutpItemSetPK;
import nts.uk.ctx.at.shared.dom.common.CompanyId;

/**
 * The Class JpaKfnmtStampOutpItemSetSetMemento.
 */
public class JpaStampOutpItemSetSetMemento implements StampingOutputItemSetSetMemento {

	/** The entity. */
	private KfnmtStampOutpItemSet entity;
	
	/** The Constant VALUE_TRUE. */
	private static final Integer VALUE_TRUE = 1;
	
	/** The Constant VALUE_FALSE. */
	private static final Integer VALUE_FALSE = 0;
	
	/**
	 * Instantiates a new jpa kfnmt stamp outp item set set memento.
	 *
	 * @param entity the entity
	 */
	public JpaStampOutpItemSetSetMemento(KfnmtStampOutpItemSet entity) {
		super();
		this.entity = entity;
		if (this.entity.getId() == null) {
			this.entity.setId(new KfnmtStampOutpItemSetPK());
		}
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetSetMemento#setCompanyID(nts.uk.ctx.at.shared.dom.common.CompanyId)
	 */
	@Override
	public void setCompanyID(CompanyId companyID) {
		this.entity.getId().setCid(companyID.v());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetSetMemento#setStampOutputSetCode(nts.uk.ctx.at.function.dom.statement.StampOutputSettingCode)
	 */
	@Override
	public void setStampOutputSetCode(StampOutputSettingCode stampOutputSettingCode) {
		this.entity.getId().setStampOutputSetCode(stampOutputSettingCode.v());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetSetMemento#setStampOutputSetName(nts.uk.ctx.at.function.dom.statement.StampOutputSettingName)
	 */
	@Override
	public void setStampOutputSetName(StampOutputSettingName stampOutputSettingName) {
		this.entity.setStampOutputSetName(stampOutputSettingName.v());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetSetMemento#setOutputEmbossMethod(boolean)
	 */
	@Override
	public void setOutputEmbossMethod(boolean outputEmbossMethod) {
		this.entity.setOutputEmbossMethod(BigDecimal.valueOf(outputEmbossMethod == true ? VALUE_TRUE : VALUE_FALSE));
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetSetMemento#setOutputWorkHours(boolean)
	 */
	@Override
	public void setOutputWorkHours(boolean outputWorkHours) {
		this.entity.setOutputWorkHours(BigDecimal.valueOf(outputWorkHours == true ? VALUE_TRUE : VALUE_FALSE));
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetSetMemento#setOutputSetLocation(boolean)
	 */
	@Override
	public void setOutputSetLocation(boolean outputSetLocation) {
		this.entity.setOutputSetLocation(BigDecimal.valueOf(outputSetLocation == true ? VALUE_TRUE : VALUE_FALSE));
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetSetMemento#setOutputPosInfor(boolean)
	 */
	@Override
	public void setOutputPosInfor(boolean outputPosInfor) {
		this.entity.setOutputPosInfor(BigDecimal.valueOf(outputPosInfor == true ? VALUE_TRUE : VALUE_FALSE));
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetSetMemento#setOutputOT(boolean)
	 */
	@Override
	public void setOutputOT(boolean outputOT) {
		this.entity.setOutputOt(BigDecimal.valueOf(outputOT == true ? VALUE_TRUE : VALUE_FALSE));
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetSetMemento#setOutputNightTime(boolean)
	 */
	@Override
	public void setOutputNightTime(boolean outputNightTime) {
		this.entity.setOutputNightTime(BigDecimal.valueOf(outputNightTime == true ? VALUE_TRUE : VALUE_FALSE));
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.function.dom.statement.StampingOutputItemSetSetMemento#setOutputSupportCard(boolean)
	 */
	@Override
	public void setOutputSupportCard(boolean outputSupportCard) {
		this.entity.setOutputSupportCard(BigDecimal.valueOf(outputSupportCard == true ? VALUE_TRUE : VALUE_FALSE));
	}
}
