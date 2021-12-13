/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.function.infra.entity.statement;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;


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
	private boolean outputEmbossMethod;

	/** The output night time. */
	@Column(name="OUTPUT_NIGHT_TIME")
	private boolean outputNightTime;

	/** The output ot. */
	@Column(name="OUTPUT_OT")
	private boolean outputOt;

	/** The output pos infor. */
	@Column(name="OUTPUT_POS_INFOR")
	private boolean outputPosInfor;

	/** The output set location. */
	@Column(name="OUTPUT_SET_LOCATION")
	private boolean outputSetLocation;

	/** The output support card. */
	@Column(name="OUTPUT_SUPPORT_CARD")
	private boolean outputSupportCard;

	/** The output work hours. */
	@Column(name="OUTPUT_WORK_HOURS")
	private boolean outputWorkHours;

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