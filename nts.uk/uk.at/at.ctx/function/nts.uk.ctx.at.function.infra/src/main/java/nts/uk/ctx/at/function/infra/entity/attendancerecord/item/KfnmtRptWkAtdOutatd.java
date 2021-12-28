package nts.uk.ctx.at.function.infra.entity.attendancerecord.item;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.UkJpaEntity;


/**
 * The persistent class for the KFNMT_RPT_WK_ATD_OUTATD database table.
 * 
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="KFNMT_RPT_WK_ATD_OUTATD")
public class KfnmtRptWkAtdOutatd extends UkJpaEntity implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The record item id. */
	@Id
	@Column(name = "RECORD_ITEM_ID")
	private String recordItemId;

	/** The exclus ver. */
	@Version
	@Column(name = "EXCLUS_VER")
	private long exclusVer;

	/** The contract cd. */
	@Column(name = "CONTRACT_CD")
	private String contractCd;

	/** The cid. */
	@Column(name = "CID")
	private String cid;

	/** The layout id. */
	@Column(name = "LAYOUT_ID")
	private String layoutId;

	/** The column index. */
	@Column(name = "COLUMN_INDEX")
	private long columnIndex;

	/** The position. */
	@Column(name = "POSITION", table = "KFNMT_RPT_WK_ATD_OUTATD")
	private long position;

	/** The output atr. */
	@Column(name = "OUTPUT_ATR")
	private long outputAtr;

	/** The time item id. */
	@Column(name = "TIME_ITEM_ID")
	private long timeItemId;

	/** The formula type. */
	@Column(name = "FORMULA_TYPE")
	private BigDecimal formulaType;

	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.recordItemId;
	}

	public KfnmtRptWkAtdOutatd(String recordItemId, String contractCd, String cid, String layoutId, long columnIndex,
			long position, long outputAtr, long timeItemId, BigDecimal formulaType) {
		super();
		this.recordItemId = recordItemId;
		this.contractCd = contractCd;
		this.cid = cid;
		this.layoutId = layoutId;
		this.columnIndex = columnIndex;
		this.position = position;
		this.outputAtr = outputAtr;
		this.timeItemId = timeItemId;
		this.formulaType = formulaType;
	}
}