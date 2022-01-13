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
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForCompany;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountList;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountNo;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountValue;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * @author TU-TK
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KAGMT_CRITERION_MONEY_CMP")
public class KagmtCriterionMoneyCmp extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KagmtCriterionMoneyCmpPk pk;

	/**
	 * 目安金額
	 */
	@Column(name = "AMOUNT_OF_MONEY")
	public int amount;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public static List<KagmtCriterionMoneyCmp> toEntity(String companyId, CriterionAmountForCompany domain) {
		List<KagmtCriterionMoneyCmp> result = new ArrayList<>();
		// 0：年間目安金額
		for (CriterionAmountByNo temp : domain.getCriterionAmount().getYearly().getList()) {
			result.add(new KagmtCriterionMoneyCmp(
					new KagmtCriterionMoneyCmpPk(companyId, 0, temp.getFrameNo().v()), temp.getAmount().v()));
		}
		// 1：月間目安金額
		for (CriterionAmountByNo temp : domain.getCriterionAmount().getMonthly().getList()) {
			result.add(new KagmtCriterionMoneyCmp(
					new KagmtCriterionMoneyCmpPk(companyId, 1, temp.getFrameNo().v()), temp.getAmount().v()));
		}
		return result;
	}

	public static CriterionAmountForCompany toDomain(List<KagmtCriterionMoneyCmp> entitys) {
		List<KagmtCriterionMoneyCmp> listYearly = new ArrayList<>();
		List<KagmtCriterionMoneyCmp> listMonthly = new ArrayList<>();
		for (KagmtCriterionMoneyCmp ent : entitys) {
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
		return new CriterionAmountForCompany(new CriterionAmount(yearly, monthly));
	}

}
