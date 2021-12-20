package nts.uk.screen.at.app.ksu003.getworkselectioninfor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetTaskParam {
	public String baseDate;
	public int targetUnit; 
	public int page;
	public String organizationID;
}
