package nts.uk.screen.at.app.ksu001.aggreratepersonaltotal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.TotalTimes;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TotalTimesDto {
	/** 回数集計NO */
	public Integer totalCountNo;
	
	/** 回数集計名称 */
	public String totalTimesName;
	
	public static TotalTimesDto fromDomain(TotalTimes domain) {
		
		return new TotalTimesDto(domain.getTotalCountNo(), domain.getTotalTimesName().v());
	}
}
