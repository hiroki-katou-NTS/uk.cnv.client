package nts.uk.ctx.at.aggregation.infra.entity.schedulecounter.criterion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmount;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountByNo;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForEmployment;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountList;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountNo;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountValue;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * @author TU-TK
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KAGMT_CRITERION_MONEY_EMP")
public class KagmtCriterionMoneyEmp extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KagmtCriterionMoneyEmpPk pk;

	/**
	 * 目安金額
	 */
	@Column(name = "AMOUNT_OF_MONEY")
	public int amount;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public static List<KagmtCriterionMoneyEmp> toEntity(String companyId,
			CriterionAmountForEmployment domain) {
		List<KagmtCriterionMoneyEmp> result = new ArrayList<>();
		// 0：年間目安金額
		for (CriterionAmountByNo temp : domain.getCriterionAmount().getYearly().getList()) {
			result.add(new KagmtCriterionMoneyEmp(new KagmtCriterionMoneyEmpPk(companyId,
					domain.getEmploymentCode().v(), 0, temp.getFrameNo().v()), temp.getAmount().v()));
		}
		// 1：月間目安金額
		for (CriterionAmountByNo temp : domain.getCriterionAmount().getMonthly().getList()) {
			result.add(new KagmtCriterionMoneyEmp(new KagmtCriterionMoneyEmpPk(companyId,
					domain.getEmploymentCode().v(), 1, temp.getFrameNo().v()), temp.getAmount().v()));
		}
		return result;
	}

	public static CriterionAmountForEmployment toDomain(String employmentCode,
			List<KagmtCriterionMoneyEmp> entitys) {
		List<KagmtCriterionMoneyEmp> listYearly = new ArrayList<>();
		List<KagmtCriterionMoneyEmp> listMonthly = new ArrayList<>();
		for (KagmtCriterionMoneyEmp ent : entitys) {
			// 0：年間目安金額
			if (ent.pk.yearMonthAtr == 0) {
				listYearly.add(ent);
			} else if (ent.pk.yearMonthAtr == 1) {// 1：月間目安金額
				listMonthly.add(ent);
			}
		}

		CriterionAmountList yearly = new CriterionAmountList(listYearly.stream().map(
				c -> new CriterionAmountByNo(new CriterionAmountNo(c.pk.frameNo), new CriterionAmountValue(c.amount)))
				.collect(Collectors.toList()));

		CriterionAmountList monthly = new CriterionAmountList(listMonthly.stream().map(
				c -> new CriterionAmountByNo(new CriterionAmountNo(c.pk.frameNo), new CriterionAmountValue(c.amount)))
				.collect(Collectors.toList()));
		return new CriterionAmountForEmployment(new EmploymentCode(employmentCode),
				new CriterionAmount(yearly, monthly));
	}
}
