package nts.uk.ctx.at.function.infra.entity.attendancerecord;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The persistent class for the KFNMT_RPT_WK_ATD_OUTFRAME database table.
 * 
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "KFNMT_RPT_WK_ATD_OUTFRAME")
public class KfnmtRptWkAtdOutframe extends ContractUkJpaEntity implements Serializable  {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@EmbeddedId
	private KfnmtRptWkAtdOutframePK id;
	
	/** The exclus ver. */
	@Column(name="EXCLUS_VER")
	private long exclusVer;
	
	/** The contract cd. */
	@Column(name="CONTRACT_CD")
	private String contractCd;
	
	/** The cid. */
	@Column(name="CID")
	private String cid;

	/** The use atr. */
	@Column(name = "USE_ATR")
	private boolean useAtr;

	/** The item name. */
	@Column(name = "ITEM_NAME")
	private String itemName;

	/** The attribute. */
	@Column(name = "ATTRIBUTE")
	private BigDecimal attribute;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.id;
	}

}