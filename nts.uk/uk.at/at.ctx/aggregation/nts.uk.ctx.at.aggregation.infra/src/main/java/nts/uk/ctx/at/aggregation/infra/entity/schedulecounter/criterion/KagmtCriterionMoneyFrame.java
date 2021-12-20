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
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountNo;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.HandlingOfCriterionAmount;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.HandlingOfCriterionAmountByNo;
import nts.uk.shr.com.color.ColorCode;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * @author TU-TK
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KAGMT_CRITERION_MONEY_FRAME")
public class KagmtCriterionMoneyFrame extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KagmtCriterionMoneyFramePk pk;

	/**
	 * 目安金額
	 */
	@Column(name = "COLOR")
	public String color;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public static List<KagmtCriterionMoneyFrame> toEntity(String cid, HandlingOfCriterionAmount domain) {
		return domain.getList().stream()
				.map(c -> new KagmtCriterionMoneyFrame(
						new KagmtCriterionMoneyFramePk(cid, c.getFrameNo().v()), c.getBackgroundColor().v()))
				.collect(Collectors.toList());
	}

	public static HandlingOfCriterionAmount toDomain(List<KagmtCriterionMoneyFrame> entitys) {
		List<HandlingOfCriterionAmountByNo> list = new ArrayList<>();
		for (KagmtCriterionMoneyFrame temp : entitys) {
			list.add(new HandlingOfCriterionAmountByNo(new CriterionAmountNo(temp.pk.frameNo),
					new ColorCode(temp.color)));
		}
		return new HandlingOfCriterionAmount(list);
	}
}
