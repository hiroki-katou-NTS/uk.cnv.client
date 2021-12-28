package nts.uk.ctx.at.function.ws.monthlycorrection.fixedformatmonthly;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
//import javax.ws.rs.core.MediaType;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.function.app.command.monthlycorrection.fixedformatmonthly.BusinessTypeSortedMonCmd;
import nts.uk.ctx.at.function.app.command.monthlycorrection.fixedformatmonthly.CopyMonthlyCmd;
import nts.uk.ctx.at.function.app.command.monthlycorrection.fixedformatmonthly.CopyMonthlyCmdHandler;
import nts.uk.ctx.at.function.app.command.monthlycorrection.fixedformatmonthly.MonthlyRecordWorkTypeCmd;
import nts.uk.ctx.at.function.app.command.monthlycorrection.fixedformatmonthly.UpdateBusinessTypeSortedMonCmdHandler;
import nts.uk.ctx.at.function.app.command.monthlycorrection.fixedformatmonthly.UpdateMonthlyRecordWorkTypeCmdHandler;
import nts.uk.ctx.at.function.app.find.monthlycorrection.fixedformatmonthly.BusinessTypeSortedMonDto;
import nts.uk.ctx.at.function.app.find.monthlycorrection.fixedformatmonthly.BusinessTypeSortedMonFinder;
import nts.uk.ctx.at.function.app.find.monthlycorrection.fixedformatmonthly.MonthlyRecordWorkTypeDto;
import nts.uk.ctx.at.function.app.find.monthlycorrection.fixedformatmonthly.MonthlyRecordWorkTypeFinder;

/**
 * The Class LinkPlanTimeItemWS.
 */
@Path("at/function/monthlycorrection")
@Produces("application/json")
public class MonthlyRecordWorkTypeWS extends WebService {

	@Inject
	private MonthlyRecordWorkTypeFinder fider;
	
	@Inject
	private UpdateMonthlyRecordWorkTypeCmdHandler updateHandler;
	
	@Inject
	private UpdateBusinessTypeSortedMonCmdHandler updateMonth;
	
	@Inject
	private BusinessTypeSortedMonFinder businessFinder;
	
	@Inject
	private CopyMonthlyCmdHandler copyHandler;
	
	@POST
	@Path("findall")
	public List<MonthlyRecordWorkTypeDto> findAll(){
		return this.fider.getListMonthlyRecordWorkType();
	}
	
	@POST
	@Path("findbycode/{businessTypeCode}")
	public MonthlyRecordWorkTypeDto findByCode(@PathParam("businessTypeCode") String businessTypeCode){
		return this.fider.getMonthlyRecordWorkTypeByCode(businessTypeCode);
	}
	
	@POST
	@Path("updatemonthly")
	public void updateMonthly(MonthlyRecordWorkTypeCmd command){
		this.updateHandler.handle(command);
	}
	
	@POST
	@Path("updatebusinessMonth")
	public void updatebusinessMonth(BusinessTypeSortedMonCmd command){
		this.updateMonth.handle(command);
	}
	
	@POST
	@Path("getallbusiness")
	public BusinessTypeSortedMonDto getAllBusiness(){
		BusinessTypeSortedMonDto data = this.businessFinder.getBusinessTypeSortedMon();
		return data;
	}
	
	@POST
	@Path("copy")
	public void copyMonthly(CopyMonthlyCmd command) {
		this.copyHandler.handle(command);
	}
	
}
