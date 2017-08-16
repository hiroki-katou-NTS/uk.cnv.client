package nts.uk.ctx.at.request.app.command.common.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteApplicationCommand {
	/**
	 * 申請ID
	 */
	private String applicationID;
}
