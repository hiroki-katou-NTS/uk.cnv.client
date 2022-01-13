package nts.uk.ctx.at.record.infra.entity.dailyperformanceprocessing.creationprocess;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

;

@Entity
@Table(name = "KRCMT_CREATE_DAILYDATA_CONDITION")
@AllArgsConstructor
@NoArgsConstructor
public class KrcmtCreateDailyDataCondition extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 会社ID
	 */
	@Id
	@NotNull
	@Column(name="CID")
	public String cid;
	
	/**
	 * 未来日を作成する
	 */
	@NotNull
	@Column(name = "IS_FUTURE_DAY")
	public boolean isFutureDay;

	@Override
	protected Object getKey() {
		return this.cid;
	}
}
