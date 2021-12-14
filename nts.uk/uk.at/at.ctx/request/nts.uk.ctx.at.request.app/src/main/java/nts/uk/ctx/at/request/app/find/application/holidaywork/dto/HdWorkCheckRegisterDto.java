package nts.uk.ctx.at.request.app.find.application.holidaywork.dto;

import java.util.List;

import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;

@NoArgsConstructor
public class HdWorkCheckRegisterDto {
	
	/**
	 * 確認メッセージリスト
	 */
	public List<ConfirmMsgOutput> confirmMsgLst;
}
