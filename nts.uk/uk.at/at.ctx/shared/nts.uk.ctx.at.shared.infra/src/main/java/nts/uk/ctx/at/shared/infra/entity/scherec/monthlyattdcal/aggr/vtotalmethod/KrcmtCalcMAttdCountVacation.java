package nts.uk.ctx.at.shared.infra.entity.scherec.monthlyattdcal.aggr.vtotalmethod;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * 休暇取得時の出勤日数カウント
 */
@Entity
@Table(name = "KRCMT_CALC_M_ATTD_COUNT_VACATION")
@AllArgsConstructor
@NoArgsConstructor
public class KrcmtCalcMAttdCountVacation extends UkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcmtCalcMAttdCountVacationPK pk;
	
	/**
	 * 契約コード
	 */
	@NotNull
	@Column(name = "CONTRACT_CD")
	public String contractCd;

	@Override
	protected Object getKey() {
		return this.pk;
	}
}
