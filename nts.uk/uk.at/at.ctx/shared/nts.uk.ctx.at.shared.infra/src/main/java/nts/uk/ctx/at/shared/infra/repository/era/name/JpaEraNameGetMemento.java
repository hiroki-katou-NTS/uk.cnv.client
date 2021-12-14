package nts.uk.ctx.at.shared.infra.repository.era.name;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.era.name.EraName;
import nts.uk.ctx.at.shared.dom.era.name.EraNameDomGetMemento;
import nts.uk.ctx.at.shared.dom.era.name.SymbolName;
import nts.uk.ctx.at.shared.dom.era.name.SystemType;
import nts.uk.ctx.at.shared.infra.entity.era.name.CismtEraName;

/**
 * The Class JpaEraNameGetMemento.
 */
public class JpaEraNameGetMemento implements EraNameDomGetMemento {
	
	/** The entity. */
	private CismtEraName entity;
	
	/**
	 * Instantiates a new jpa era name get memento.
	 *
	 * @param entity the entity
	 */
	public JpaEraNameGetMemento(CismtEraName entity) {
		super();
		this.entity = entity;
	}

	/**
	 * Instantiates a new jpa era name get memento.
	 */
	public JpaEraNameGetMemento() {
		super();
	}

	@Override
	public String getEraNameId(){
		return this.entity.getEraNameId();
	};

	@Override
	public GeneralDate getEndDate(){
		return this.entity.getEndDate();
	};

	@Override
	public EraName getEraName(){
		return new EraName(this.entity.getEraName());
	};
	
	@Override
	public GeneralDate getStartDate(){
		return this.entity.getStartDate();
	};
	
	@Override
	public SymbolName getSymbol(){
		return new SymbolName(this.entity.getSymbol());
	};
	
	@Override
	public SystemType getSystemType(){
		return SystemType.valueOf(this.entity.getSystemType().intValue());
	};

}
