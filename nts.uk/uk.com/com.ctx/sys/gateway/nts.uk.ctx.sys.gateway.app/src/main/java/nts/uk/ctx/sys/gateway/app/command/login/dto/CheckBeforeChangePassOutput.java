package nts.uk.ctx.sys.gateway.app.command.login.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class CheckBeforeChangePassOutput {
	private boolean error;
	private List<String> message;
}
