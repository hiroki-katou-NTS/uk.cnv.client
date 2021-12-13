package nts.uk.ctx.at.shared.infra.entity.scherec.appreflectprocess.appreflectcondition.cancellation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "KSRDT_REFLECT_APP_HIST")
public class KsrdtReflectAppHist extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KsrdtReflectAppHistPK pk;

	// 会社ID
	@Column(name = "CID")
	public String cid;
		
	// 取消区分
	@Column(name = "DELETE_ATR")
	public int deleteAtr;

	// 再反映
	@Column(name = "RE_REFLECT_ATR")
	public int reReflect;

	// 実行ID
	@Column(name = "EXECUTION_ID")
	public String execId;
	
	@Override
	protected Object getKey() {
		return pk;
	}

}
