package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DivergenceTimeDto {
	// 乖離時間
	private Integer divTime;

	// 乖離時間NO - primitive value
	private int divTimeId;

	// 乖離理由

	private String divReason;

	// 乖離理由コード

	private String divResonCode;

	public static DivergenceTimeDto fromDomain(DivergenceTime domain) {
		return new DivergenceTimeDto(domain.getDivTime().v(), domain.getDivTimeId(),
				domain.getDivReason().map(x -> x.v()).orElse(null), 
				domain.getDivResonCode().map(x-> x.v()).orElse(null));
	}
}
