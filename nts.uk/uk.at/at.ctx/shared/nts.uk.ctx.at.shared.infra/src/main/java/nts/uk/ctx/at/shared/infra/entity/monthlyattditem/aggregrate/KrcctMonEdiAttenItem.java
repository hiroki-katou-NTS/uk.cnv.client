package nts.uk.ctx.at.shared.infra.entity.monthlyattditem.aggregrate;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.monthlyattditem.aggregate.MonthlyAttItemCanAggregate;

/**
 * The monthly attendance item can be aggregated
 *
 * @author LienPTK
 */
@Getter
@Setter
@Entity
@Table(name = "KRCCT_MON_EDI_ATTEN_ITEM")
public class KrcctMonEdiAttenItem implements Serializable
										   , MonthlyAttItemCanAggregate.MementoSetter
										   , MonthlyAttItemCanAggregate.MementoGetter {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@EmbeddedId
	private KrcctMonEdiAttenItemPK id;
	
	/** 排他バージョン */
	@Version
	@Column(name = "EXCLUS_VER")
	private int exclusVer;
	
	/** 契約コード */
	@Column(name = "CONTRACT_CD")
	private String contractCd;
	
	/** 修正可能区分  */
	@Column(name = "EDITABLE")
	private BigDecimal editable;

	@Override
	public String getCid() {
		return this.id.getCid();
	}

	@Override
	public BigDecimal getAttItemId() {
		return this.id.getMAtItemId();
	}

	@Override
	public void setCid(String cid) {
		if (this.id == null) {
			this.id = new KrcctMonEdiAttenItemPK();
		}
		this.id.setCid(cid);
	}

	@Override
	public void setAttItemId(BigDecimal attItemId) {
		if (this.id == null) {
			this.id = new KrcctMonEdiAttenItemPK();
		}
		this.id.setMAtItemId(attItemId);
	}

	@Override
	public boolean isEditable() {
		return this.editable.equals(BigDecimal.ONE);
	}

	@Override
	public void setEditable(boolean editable) {
		this.editable = editable ? BigDecimal.ONE : BigDecimal.ZERO;
	}

}
