package nts.uk.ctx.at.aggregation.infra.entity.schedulecounter.criterion;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountUsageSetting;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * @author TU-TK
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KAGMT_CRITERION_MONEY_USAGE")
public class KagmtCriterionMoneyUseage extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CID")
	public String companyId;

	/**
	 * 目安利用区分
	 */
	@Column(name = "EMPLOYEMENT_USE")
	public int employmentUse;

	@Override
	protected Object getKey() {
		return this.companyId;
	}

	public static KagmtCriterionMoneyUseage toEntity(CriterionAmountUsageSetting domain) {
		return new KagmtCriterionMoneyUseage(domain.getCid(), domain.getEmploymentUse().value);
	}

	public CriterionAmountUsageSetting toDomain() {
		return new CriterionAmountUsageSetting(this.companyId, NotUseAtr.valueOf(this.employmentUse));
	}
}
