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
public class UpdateOneDayFavNameCommand {
	
	private String favId;
	
	private String favName;
}
