package nts.uk.screen.at.ws.kdw.kdw013.bch;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.screen.at.app.kdw013.a.ItemValueCommand;

@NoArgsConstructor
@Getter
@Setter
public class KDW013HSaveCommand {

	//対象社員
	public String empTarget;
	//対象日	
	public GeneralDate targetDate;
	//修正内容	=>	List<itemvalue>	
	public List<ItemValueCommand> items;
}
