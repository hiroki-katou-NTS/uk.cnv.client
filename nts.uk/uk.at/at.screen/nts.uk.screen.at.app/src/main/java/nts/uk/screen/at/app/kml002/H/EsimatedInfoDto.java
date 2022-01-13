package nts.uk.screen.at.app.kml002.H;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.HandlingOfCriterionAmountByNo;

/**
 * 
 * @author hoangnd
 *
 */
// 会社の目安金額　dto
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EsimatedInfoDto {
	// 会社の目安金額.目安金額詳細.月度目安金額.目安金額目安金額リスト
	private List<CriterionAmountByNoDto> months;
	
	// 会社の目安金額.目安金額詳細.年間目安金額.目安金額目安金額リスト
	private List<CriterionAmountByNoDto> annuals;
	
	// 目安金額の扱い.枠別の扱いリスト
	private List<HandlingOfCriterionAmountByNoDto> listHandlingByNo;
	
}
