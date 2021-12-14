package nts.uk.screen.at.app.query.ksu.ksu002.a.input;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
public class DisplayInWorkInfoInput {
	public List<String> listSid;
	public GeneralDate startDate;
	public GeneralDate endDate;
	public boolean actualData;
	
}
