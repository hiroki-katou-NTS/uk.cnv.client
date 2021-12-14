package nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;

@NoArgsConstructor
@Getter
@Setter
public class HdWorkCheckRegisterOutput {
	
	/**
	 * 確認メッセージリスト
	 */
	private List<ConfirmMsgOutput> confirmMsgLst;
	
}
