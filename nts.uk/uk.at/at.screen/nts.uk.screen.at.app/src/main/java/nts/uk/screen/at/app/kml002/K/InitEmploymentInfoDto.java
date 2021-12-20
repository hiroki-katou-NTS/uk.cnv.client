package nts.uk.screen.at.app.kml002.K;
/**
 * 
 * @author hoangnd
 *
 */
// 初期情報　dto

import lombok.NoArgsConstructor;
import nts.uk.ctx.bs.employee.app.find.employment.dto.EmploymentDto;
import nts.uk.screen.at.app.kml002.H.HandlingOfCriterionAmountByNoDto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InitEmploymentInfoDto {
	// 雇用の目安金額.雇用コード
	private List<String> employmentCodes;
	// 雇用リスト
	private List<EmploymentDto> employments;
	// 目安金額の扱い.枠別の扱いリスト
	private List<HandlingOfCriterionAmountByNoDto> listHandlingByNo;
}
