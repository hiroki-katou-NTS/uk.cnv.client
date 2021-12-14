package nts.uk.ctx.at.record.app.command.kdp.kdp004.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.app.command.kdp.kdp001.a.RefectActualResultCommand;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonPositionNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PageNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampButton;

/**
 * 打刻データを登録する(指静脈)
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterFingerStampCommand {
	// 社員ID
	private String employeeId;

	// 打刻日時
	private String datetime;

	// 打刻ボタン
	private StampButtonCommand stampButton;

	// 実績への反映内容
	private RefectActualResultCommand refActualResult;

	// 認証方法
	private Integer authcMethod;

	public GeneralDateTime getStampDatetime() {
		return GeneralDateTime.now();
	}

	public StampButton getStampButton() {
		return new StampButton(new PageNo(this.stampButton.getPageNo()),
				new ButtonPositionNo(stampButton.getButtonPositionNo()));
	}

}
