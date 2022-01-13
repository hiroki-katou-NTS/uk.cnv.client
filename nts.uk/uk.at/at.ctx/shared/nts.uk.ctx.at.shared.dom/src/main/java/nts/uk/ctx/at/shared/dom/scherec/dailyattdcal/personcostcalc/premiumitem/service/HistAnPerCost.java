package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.HistPersonCostCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostCalculation;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Setter
@NoArgsConstructor
@Getter
public class HistAnPerCost {
    HistPersonCostCalculation histPersonCostCalculation;
    List<PersonCostCalculation> personCostCalculation;

	/**
	 * 指定日時点の人件費計算設定を取得する
	 * @param baseDate
	 * @return 人件費計算設定
	 */
	public Optional<PersonCostCalculation> get(GeneralDate baseDate) {
		Optional<String> historyId = this.histPersonCostCalculation.items().stream()
				.filter(h -> h.contains(baseDate))
				.map(h -> h.identifier()).findFirst();
		
		if(!historyId.isPresent()) {
			return Optional.empty();
		}
		return this.personCostCalculation.stream()
				.filter(p -> p.getHistoryID().equals(historyId.get()))
				.findFirst();
	}
}
