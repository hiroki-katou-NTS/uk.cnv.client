package nts.uk.ctx.at.record.app.command.kdp.kdp003.a;

import lombok.Data;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.app.command.kdp.kdp001.a.RefectActualResultCommand;
import nts.uk.ctx.at.record.app.command.kdp.kdp004.a.StampButtonCommand;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonPositionNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PageNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampButton;

@Data
public class RegisterNameSelectionCommand {
	// note: 社員ID
	private String employeeId;

	// note: 打刻日時
	public GeneralDateTime getDateTime() {
		return GeneralDateTime.now();
	};

	// note: 打刻ボタン
	private StampButtonCommand stampButton;

	// note: 実績への反映内容
	private RefectActualResultCommand refActualResult;

	public StampButton getStampButton() {
		PageNo pageNo = new PageNo(stampButton.getPageNo());
		ButtonPositionNo positionNo = new ButtonPositionNo(stampButton.getButtonPositionNo());
		
		return new StampButton(pageNo, positionNo);
	}
}
