package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoSelectionItem;

@AllArgsConstructor
@Getter
public class SuppInfoSelectionItemDto {
	/** 補足情報の選択項目NO: 作業補足情報NO */
	private Integer suppInfoSelectionNo;

	/** 補足選択肢コード: 作業補足情報の選択肢コード */
	private String choiceCode;

	public static SuppInfoSelectionItemDto fromDomain(SuppInfoSelectionItem domain) {

		return new SuppInfoSelectionItemDto(domain.getSuppInfoSelectionNo().v(), domain.getChoiceCode()== null ? null :   domain.getChoiceCode().v());
	}
}
