package nts.uk.ctx.at.shared.infra.entity.scherec.monthlyattdcal.aggr.vtotalmethod;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class KrcmtCalcMAttdCountVacationPK implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 会社ID
	 */
	@NotNull
	@Column(name = "CID")
	public String cid;

	/**
	 * 出勤日数としてカウントする休暇の種類
	 */
	@NotNull
	@Column(name = "VACATION_TYPE")
	public int vacationType;
}
