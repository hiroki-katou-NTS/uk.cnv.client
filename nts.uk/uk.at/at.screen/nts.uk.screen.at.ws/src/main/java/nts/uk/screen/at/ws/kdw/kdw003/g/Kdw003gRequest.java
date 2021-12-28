package nts.uk.screen.at.ws.kdw.kdw003.g;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
/**
 * 
 * @author quytb
 *
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Kdw003gRequest {
	private List<String> employeeIds;
	private String baseDate;
	
	public GeneralDate toDate() {
		return GeneralDate.fromString(this.baseDate, "yyyy/MM/dd");
	}
}
