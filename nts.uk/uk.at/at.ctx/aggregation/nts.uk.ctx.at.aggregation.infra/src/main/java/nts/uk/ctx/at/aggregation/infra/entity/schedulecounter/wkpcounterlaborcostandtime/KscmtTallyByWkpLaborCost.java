package nts.uk.ctx.at.aggregation.infra.entity.schedulecounter.wkpcounterlaborcostandtime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.laborcostandtime.LaborCostAndTime;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.laborcostandtime.WorkplaceCounterLaborCostAndTime;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.AggregationUnitOfLaborCosts;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KAGMT_TALLY_BYWKP_LABOR_COST")
public class KscmtTallyByWkpLaborCost extends ContractUkJpaEntity implements Serializable {

	@EmbeddedId
	public KscmtTallyByWkpLaborCostPk pk;

	@Column(name = "USE_ATR")
	public boolean useClassification;

	@Column(name = "TIME_FOR_LABOR_COST")
	public boolean time;

	@Column(name = "LABOR_COST")
	public boolean laborCost;

	@Column(name = "BADGET_FOR_LABOR_COST")
	public Boolean budget;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public static List<KscmtTallyByWkpLaborCost> toEntity(String companyId, WorkplaceCounterLaborCostAndTime domain) {
		return domain.getLaborCostAndTimeList().entrySet().stream().map(x -> {
			KscmtTallyByWkpLaborCostPk pk = new KscmtTallyByWkpLaborCostPk(companyId, x.getKey().value);
			KscmtTallyByWkpLaborCost result = new KscmtTallyByWkpLaborCost(
				pk,
				x.getValue().getUseClassification().isUse(),
				x.getValue().getTime().isUse(),
				x.getValue().getLaborCost().isUse(),
				x.getValue().getBudget()
						.map(v -> v.value == 1)
						.orElse(false)
			);

			result.contractCd = AppContexts.user().contractCode();
			return result;
		}).collect(Collectors.toList());
	}

	public static WorkplaceCounterLaborCostAndTime toDomain(List<KscmtTallyByWkpLaborCost> entities) {

		Map<AggregationUnitOfLaborCosts, LaborCostAndTime> laborCostAndTimeList = new HashMap<>();
		entities.forEach(x -> laborCostAndTimeList.put(
			EnumAdaptor.valueOf(x.pk.costType, AggregationUnitOfLaborCosts.class),
			new LaborCostAndTime(
				NotUseAtr.valueOf(x.useClassification ? 1 : 0),
				NotUseAtr.valueOf(x.time ? 1 : 0),
				NotUseAtr.valueOf(x.laborCost ? 1 : 0),
				x.budget == null ? Optional.empty() : Optional.of(NotUseAtr.valueOf(x.budget == true ? 1 : 0)))
		));

		return WorkplaceCounterLaborCostAndTime.create(laborCostAndTimeList);
	}
}
