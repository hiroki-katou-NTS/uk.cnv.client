package nts.uk.ctx.at.record.app.command.kdw.kdw006.l;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeleteHistoryCommand {

	private String historyId;
	
	private String itemId;

}
