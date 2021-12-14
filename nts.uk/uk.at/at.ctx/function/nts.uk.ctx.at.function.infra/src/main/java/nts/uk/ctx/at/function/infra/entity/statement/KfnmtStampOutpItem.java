/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.function.infra.entity.statement;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;
import java.math.BigDecimal;


/**
 * The Class KfnmtStampOutpItem.
 */
@Entity
@Table(name="KFNMT_STAMP_OUTP_ITEM")
@Getter
@Setter
public class KfnmtStampOutpItem extends ContractUkJpaEntity implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@EmbeddedId
	private KfnmtStampOutpItemSetPK id;

	/** The output emboss method. */
	@Column(name="OUTPUT_EMBOSS_METHOD")
	private BigDecimal outputEmbossMethod;

	/** The output night time. */
	@Column(name="OUTPUT_NIGHT_TIME")
	private BigDecimal outputNightTime;

	/** The output ot. */
	@Column(name="OUTPUT_OT")
	private BigDecimal outputOt;

	/** The output pos infor. */
	@Column(name="OUTPUT_POS_INFOR")
	private BigDecimal outputPosInfor;

	/** The output set location. */
	@Column(name="OUTPUT_SET_LOCATION")
	private BigDecimal outputSetLocation;

	/** The output support card. */
	@Column(name="OUTPUT_SUPPORT_CARD")
	private BigDecimal outputSupportCard;

	/** The output work hours. */
	@Column(name="OUTPUT_WORK_HOURS")
	private BigDecimal outputWorkHours;

	/** The stamp output set name. */
	@Column(name="STAMP_OUTPUT_SET_NAME")
	private String stampOutputSetName;

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	@Override
	protected Object getKey() {
		return this.id;
	}
}