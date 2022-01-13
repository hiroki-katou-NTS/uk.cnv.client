package nts.uk.screen.at.app.kdw013.a;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PremiumTimeOfDailyPerformanceDto {

	/** 割増時間 **/
	private List<PremiumTimeDto> premiumTimes;

	/** 割増金額合計 **/
	private Integer totalAmount;

	/** 割増労働時間合計 **/
	private Integer totalWorkingTime;

	public static PremiumTimeOfDailyPerformanceDto fromDomain(PremiumTimeOfDailyPerformance domain) {

		return new PremiumTimeOfDailyPerformanceDto(
				CollectionUtil.isEmpty(domain.getPremiumTimes()) ? Collections.emptyList()
						: domain.getPremiumTimes().stream().map(pt -> PremiumTimeDto.fromDomain(pt))
								.collect(Collectors.toList()),
				domain.getTotalAmount().v(), domain.getTotalWorkingTime().v());
	}

}
