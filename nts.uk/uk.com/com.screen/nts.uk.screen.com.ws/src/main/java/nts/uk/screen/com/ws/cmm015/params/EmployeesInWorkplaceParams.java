package nts.uk.screen.com.ws.cmm015.params;

import java.util.List;

import lombok.Getter;
import nts.arc.time.GeneralDate;

@Getter
public class EmployeesInWorkplaceParams {
	private List<String> wkpIds;
	private GeneralDate referDate;
	private Boolean incumbent;
	private Boolean closed;
	private Boolean leave;
	private Boolean retiree;
}
