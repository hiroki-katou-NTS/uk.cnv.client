package nts.uk.screen.at.app.query.kdw.kdw003.g;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskInitialSelSettingDto {
	private List<Integer> ids;
	private String employeeId;
	private List<String> lstStartDate;
	private List<String> lstEndDate;
	private List<String> lstTaskItem;
}
