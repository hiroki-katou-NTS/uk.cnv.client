package nts.uk.ctx.at.request.app.find.application.holidaywork.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CheckBeforeOutput;

/**
 * Refactor5
 * @author huylq
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CheckBeforeOutputDto {
	
	//	確認メッセージリスト
	private List<ConfirmMsgOutput> confirmMsgOutputs;
	
	public static CheckBeforeOutputDto fromDomain(CheckBeforeOutput domain) {
		return new CheckBeforeOutputDto(domain.getConfirmMsgOutputs());
	}
}
