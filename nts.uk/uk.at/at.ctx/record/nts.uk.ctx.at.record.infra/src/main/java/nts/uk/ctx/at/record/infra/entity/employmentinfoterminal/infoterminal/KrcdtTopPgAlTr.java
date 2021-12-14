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
@Table(name = "KRCDT_TOPPG_AL_TR")
public class KrcdtTopPgAlTr extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcdtTopPgAlTrPK pk;

	/**
	 * エラーの有無
	 */
	@Column(name = "EXIST_ERROR")
	public int existError;

	/**
	 * 中止フラグ
	 */
	@Column(name = "ABORT_FLAG")
	public int abortFlag;

	@Override
	protected Object getKey() {
		return pk;
	}

}
