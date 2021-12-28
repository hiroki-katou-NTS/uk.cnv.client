package nts.uk.screen.at.app.kdw013.command;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author tutt
 *
 */
@Data
@NoArgsConstructor
public class ChangeDisplayOrderCommand {

	private String reorderedId;

	private int beforeOrder;

	private int afterOrder;

}
