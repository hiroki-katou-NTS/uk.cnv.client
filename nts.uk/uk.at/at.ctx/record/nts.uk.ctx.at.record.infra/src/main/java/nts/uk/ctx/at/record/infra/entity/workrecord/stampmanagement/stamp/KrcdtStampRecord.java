package nts.uk.ctx.at.record.infra.entity.workrecord.stampmanagement.stamp;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecord;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * @author ThanhNX
 *
 *         打刻記録
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_STAMP_RECORD")
public class KrcdtStampRecord extends UkJpaEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "STAMP_RECORD_ID")
	public String stampRecordId;
	
 	/**
	 * CID
	 */
	@Basic(optional = false)
	@Column(name = "CID")
	public String Cid;
	
	/**
	 * 表示する打刻区分
	 */
	@Basic(optional = false)
	@Column(name = "DISPLAY_STAMP_ART")
	public String stampTypeDisplay;
	
	/**
	 * 契約コード
	 */
	@Basic(optional = false)
	@Column(name = "CONTRACT_CD")
	public String contractCd;
	
	/**
	 * 打刻カード番号
	 */
	@Basic(optional = false)
	@Column(name = "CARD_NUMBER")
	public String cardNumber;

	/**
	 * 打刻日時
	 */
	@Basic(optional = false)
	@Column(name = "STAMP_DATE_TIME")
	public GeneralDateTime stampDateTime;

	@Override
	protected Object getKey() {
		return this.stampRecordId;
	}

	public KrcdtStampRecord toUpdateEntity(StampRecord domain) {
		this.stampTypeDisplay = domain.getStampTypeDisplay().v();
		return this;
	}

}
