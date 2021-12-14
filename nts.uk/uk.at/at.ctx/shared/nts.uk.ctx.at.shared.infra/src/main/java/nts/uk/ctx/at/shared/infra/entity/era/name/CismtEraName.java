package nts.uk.ctx.at.shared.infra.entity.era.name;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.infra.data.entity.type.GeneralDateToDBConverter;
import nts.arc.time.GeneralDate;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;


/**
 * The persistent class for the CISMT_ERA_NAME database table.
 * 
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="CISMT_ERA_NAME")
public class CismtEraName extends ContractUkJpaEntity implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The era name id. */
	@Id
	@Column(name="ERA_NAME_ID")
	private String eraNameId;

	/** The end date. */
	@Column(name="END_DATE")
	@Convert(converter = GeneralDateToDBConverter.class)
	private GeneralDate endDate;

	/** The era name. */
	@Column(name="ERA_NAME")
	private String eraName;

	/** The start date. */
	@Column(name="START_DATE")
	@Convert(converter = GeneralDateToDBConverter.class)
	private GeneralDate startDate;

	/** The symbol. */
	@Column(name="SYMBOL")
	private String symbol;

	/** The system type. */
	@Column(name="SYSTEM_TYPE")
	private BigDecimal systemType;

	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.eraNameId;
	}

}