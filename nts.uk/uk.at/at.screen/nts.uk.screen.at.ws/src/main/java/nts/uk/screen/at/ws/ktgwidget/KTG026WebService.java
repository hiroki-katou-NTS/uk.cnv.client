package nts.uk.screen.at.ws.ktgwidget;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import lombok.Data;
import nts.arc.layer.ws.WebService;
import nts.uk.screen.at.app.ktgwidget.KTG026QueryProcessor;
import nts.uk.screen.at.app.ktgwidget.find.dto.EmployeesOvertimeDisplayDto;

@Path("screen/at/ktg26")
@Produces("application/json")
public class KTG026WebService extends WebService{
	
	@Inject
	KTG026QueryProcessor processor;
	
	@POST
	@Path("startScreen")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EmployeesOvertimeDisplayDto startScreenKtg026(Ktg026Parmas ktg026Params) {
		return processor.startScreenKtg026(
				ktg026Params.closingId,
				ktg026Params.employeeId,
				ktg026Params.targetDate,
				ktg026Params.targetYear,
				ktg026Params.currentOrNextMonth,
				ktg026Params.processingYm);
	}
	
	@POST
	@Path("extractOvertime")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EmployeesOvertimeDisplayDto extractOvertime(Ktg026Parmas ktg026Params) {
		return processor.extractOvertime(ktg026Params.employeeId, ktg026Params.closingId, ktg026Params.targetYear, ktg026Params.processingYm);
	}

}

@Data
class Ktg026Parmas {
	// 社員ID
	String employeeId;
	// 締めID
	int closingId;
	// 対象年月
	Integer targetDate;
	// 対象年
	Integer targetYear;
    // 表示年月
    int currentOrNextMonth;
    // 処理年月
    int processingYm;
}

