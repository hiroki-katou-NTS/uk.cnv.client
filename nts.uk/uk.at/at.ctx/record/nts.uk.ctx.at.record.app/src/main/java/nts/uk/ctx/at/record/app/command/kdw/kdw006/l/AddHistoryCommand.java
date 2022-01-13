package nts.uk.ctx.at.record.app.command.kdw.kdw006.l;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddHistoryCommand {

	private String itemId;

	private String historyId;

	private GeneralDate startDate;

	private GeneralDate endDate;

}
