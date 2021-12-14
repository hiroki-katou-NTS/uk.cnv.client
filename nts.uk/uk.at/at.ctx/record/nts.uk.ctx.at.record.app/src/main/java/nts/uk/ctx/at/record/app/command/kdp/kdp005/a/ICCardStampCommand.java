package nts.uk.ctx.at.record.app.command.kdp.kdp005.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.app.command.kdp.kdp001.a.RefectActualResultCommand;
import nts.uk.ctx.at.record.app.command.kdp.kdp004.a.StampButtonCommand;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonPositionNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PageNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampButton;

/**
 * @author thanhpv
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ICCardStampCommand {
	// 社員ID
	private String stampedCardNumber;

	// 打刻日時
	private String datetime;

	// 打刻ボタン
	private StampButtonCommand stampButton;

	// 実績への反映内容
	private RefectActualResultCommand refActualResult;

	public GeneralDateTime getStampDatetime() {
		return GeneralDateTime.now();
	}

	public StampButton getStampButton() {
		return new StampButton(new PageNo(this.stampButton.getPageNo()),
				new ButtonPositionNo(stampButton.getButtonPositionNo()));
	}
	
	public StampNumber getStampNumber() {
		return new StampNumber(this.stampedCardNumber);
	}

}
