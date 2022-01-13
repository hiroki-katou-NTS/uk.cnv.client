package nts.uk.screen.at.app.kdw013.command;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author tutt
 *
 */
@Data
@NoArgsConstructor
public class RegisterFavoriteForOneDayCommand {
	
	private String employeeId;
	
	private String taskName;
	
	private List<TaskBlockDetailContentCommand> contents;

}
