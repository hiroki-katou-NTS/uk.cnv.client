package nts.uk.ctx.at.record.infra.entity.employmentinfoterminal.infoterminal;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_TOPPG_AL_MANAGER_TR")
public class KrcdtTopPgAlManagerTr extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcdtTopPgAlManagerTrPK pk;

	/**
	 * 了解フラグ
	 */
	@Column(name = "ROGER_FLAG")
	public int rogerFlag;

	@Override
	protected Object getKey() {
		return pk;
	}

}
