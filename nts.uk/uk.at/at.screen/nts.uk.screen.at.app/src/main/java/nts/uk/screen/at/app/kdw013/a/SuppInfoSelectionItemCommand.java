package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.ChoiceCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoSelectionItem;

@AllArgsConstructor
@Getter
public class SuppInfoSelectionItemCommand {
	/** 補足情報の選択項目NO: 作業補足情報NO */
	private Integer suppInfoSelectionNo;

	/** 補足選択肢コード: 作業補足情報の選択肢コード */
	private String choiceCode;

	public SuppInfoSelectionItem toDomain() {

		return new SuppInfoSelectionItem(new SuppInfoNo(this.getSuppInfoSelectionNo()),
				this.getChoiceCode()==null?null: new ChoiceCode(this.getChoiceCode()));
	}
}
