/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.function.infra.entity.dailyworkschedule;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;


/**
 * The Class KfnmtPrintRemarkCont.
 * @author HoangDD
 */
@Entity
@Table(name="KFNMT_RPT_WK_DAI_OUTNOTE")
@Getter
@Setter
@NoArgsConstructor
public class KfnmtRptWkDaiOutnote extends ContractUkJpaEntity implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@EmbeddedId
	private KfnmtRptWkDaiOutnotePK id;
	
	/** The cid. */
	@Column(name="CID")
	private String cid;

	/** The use cls. */
	@Column(name="USE_CLS")
	private boolean useCls;

	/** The contract cd. */
	@Column(name="CONTRACT_CD")
	private String contractCd;
	
	/** The exclus ver. */
	@Column(name = "EXCLUS_VER")
	public int exclusVer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="LAYOUT_ID", referencedColumnName="LAYOUT_ID", insertable = false, updatable = false)
	public KfnmtRptWkDaiOutItem kfnmtRptWkDaiOutItem;

	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.id;
	}

}