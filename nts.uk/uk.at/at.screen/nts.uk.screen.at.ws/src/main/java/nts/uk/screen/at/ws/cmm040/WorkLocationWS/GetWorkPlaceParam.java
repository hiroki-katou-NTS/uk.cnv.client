package nts.uk.screen.at.ws.cmm040.WorkLocationWS;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.record.app.command.worklocation.WorkplacePossibleCmd;

@Getter
@Setter
@NoArgsConstructor
public class GetWorkPlaceParam {

	public List<WorkplacePossibleCmd> listWorkplace;
	
	public String workLocationCD;
	
}
