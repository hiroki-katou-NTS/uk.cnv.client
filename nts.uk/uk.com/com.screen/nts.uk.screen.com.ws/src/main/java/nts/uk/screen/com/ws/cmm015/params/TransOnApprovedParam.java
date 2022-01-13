package nts.uk.screen.com.ws.cmm015.params;

import java.util.List;

import lombok.Getter;
import nts.arc.time.GeneralDate;

@Getter
public class TransOnApprovedParam {
	private List<String> sids;
	private GeneralDate baseDate;
}
