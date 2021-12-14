package nts.uk.ctx.at.shared.infra.repository.era.name;

import java.math.BigDecimal;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.era.name.EraName;
import nts.uk.ctx.at.shared.dom.era.name.EraNameDomSetMemento;
import nts.uk.ctx.at.shared.dom.era.name.SymbolName;
import nts.uk.ctx.at.shared.dom.era.name.SystemType;
import nts.uk.ctx.at.shared.infra.entity.era.name.CismtEraName;

/**
 * The Class JpaEraNameSetMemento.
 */
public class JpaEraNameSetMemento implements EraNameDomSetMemento {
	
	/** The entity. */
	private CismtEraName entity;
	
	/**
	 * Instantiates a new jpa era name set memento.
	 */
	public JpaEraNameSetMemento() {
		super();
	}

	/**
	 * Instantiates a new jpa era name set memento.
	 *
	 * @param entity the entity
	 */
	public JpaEraNameSetMemento(CismtEraName entity) {
		this.entity = entity;
	}

	@Override
	public void setEraNameId(String eraNameId) {
		this.entity.setEraNameId(eraNameId);
	}

	@Override
	public void setEndDate(GeneralDate endDate) {
		this.entity.setEndDate(endDate);
	}

	@Override
	public void setEraName(EraName eraName) {
		this.entity.setEraName(eraName.toString());
	}

	@Override
	public void setStartDate(GeneralDate startDate) {
		this.entity.setStartDate(startDate);
	}

	@Override
	public void setSymbol(SymbolName symbol) {
		this.entity.setSymbol(symbol.toString());
	}

	@Override
	public void setSystemType(SystemType systemType) {
		this.entity.setSystemType(BigDecimal.valueOf(systemType.value));
	}
}
