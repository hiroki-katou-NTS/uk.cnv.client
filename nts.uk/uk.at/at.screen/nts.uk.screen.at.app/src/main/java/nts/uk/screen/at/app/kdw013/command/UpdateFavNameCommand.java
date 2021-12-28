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
public class UpdateFavNameCommand {
	
	private String favId;
	
	private String favName;
}
