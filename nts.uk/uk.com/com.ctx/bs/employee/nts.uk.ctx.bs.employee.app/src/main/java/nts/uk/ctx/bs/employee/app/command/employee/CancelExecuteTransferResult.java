package nts.uk.ctx.bs.employee.app.command.employee;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.shr.pereg.app.command.MyCustomizeException;

@AllArgsConstructor
@Getter
public class CancelExecuteTransferResult {
	private List<MyCustomizeException> affWorkplaceErr;
	private List<MyCustomizeException> affJobTitleErr;
}
