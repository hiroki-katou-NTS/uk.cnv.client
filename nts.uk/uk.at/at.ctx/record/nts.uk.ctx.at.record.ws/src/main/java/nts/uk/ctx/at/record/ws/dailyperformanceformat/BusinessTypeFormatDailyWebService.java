package nts.uk.ctx.at.record.ws.dailyperformanceformat;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.record.app.command.dailyperformanceformat.AddBusinessTypeDailyCommand;
import nts.uk.ctx.at.record.app.command.dailyperformanceformat.AddBusinessTypeDailyCommandHandler;
import nts.uk.ctx.at.record.app.command.dailyperformanceformat.CopybusinessTypeDailyCommand;
import nts.uk.ctx.at.record.app.command.dailyperformanceformat.CopybusinessTypeDailyCommandHandler;
import nts.uk.ctx.at.record.app.command.dailyperformanceformat.UpdateBusinessTypeDailyCommand;
import nts.uk.ctx.at.record.app.command.dailyperformanceformat.UpdateBusinessTypeDailyCommandHandler;
import nts.uk.ctx.at.record.app.find.dailyperformanceformat.BusinessTypeDailyDetailFinder;
import nts.uk.ctx.at.record.app.find.dailyperformanceformat.SheetNoFinder;
import nts.uk.ctx.at.record.app.find.dailyperformanceformat.dto.BusinessTypeFormatDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperformanceformat.dto.SheetNo;

@Path("at/record/businesstype")
@Produces("application/json")
public class BusinessTypeFormatDailyWebService extends WebService {

	@Inject
	private BusinessTypeDailyDetailFinder businessTypeDailyDetailFinder;

	@Inject
	private AddBusinessTypeDailyCommandHandler addBusinessTypeDailyCommandHandler;

	@Inject
	private UpdateBusinessTypeDailyCommandHandler updateBusinessTypeDailyCommandHandler;
	
	@Inject
	private CopybusinessTypeDailyCommandHandler copybusinessTypeDailyCommandHandler;

	@Inject
	private SheetNoFinder sheetNoFinder;
	
	@POST
	@Path("copy")
	public void copy(CopybusinessTypeDailyCommand command) {
		this.copybusinessTypeDailyCommandHandler.handle(command);
	}
	
	@POST
	@Path("findAllBusinessTypeCode")
	public List<String> getAllBusinessTypeCode() {
		return this.sheetNoFinder.getAllBusinessTypeCode();
	}

	@POST
	@Path("findBusinessTypeDailyDetail/{businessTypeCode}/{sheetNo}")
	public BusinessTypeFormatDailyDto getAll(@PathParam("businessTypeCode") String businessTypeCode,
			@PathParam("sheetNo") BigDecimal sheetNo) {
		return this.businessTypeDailyDetailFinder.getDetail(businessTypeCode, sheetNo);
	}
	
	@POST
	@Path("checkDailyMode/{businessTypeCode}")
	public int checkDailyMode(@PathParam("businessTypeCode") String businessTypeCode) {
		return this.businessTypeDailyDetailFinder.checkDailyMode(businessTypeCode);
	}

	@POST
	@Path("addBusinessTypeDailyDetail")
	public void AddBusinessTypeDailyDetail(AddBusinessTypeDailyCommand command) {
		this.addBusinessTypeDailyCommandHandler.handle(command);
	}

	@POST
	@Path("updateBusinessTypeDailyDetail")
	public void UpdateBusinessTypeDailyDetail(UpdateBusinessTypeDailyCommand command) {
		this.updateBusinessTypeDailyCommandHandler.handle(command);
	}

	@POST
	@Path("findBusinessTypeDailyDetail/findSheetNo/{businessTypeCode}")
	public SheetNo getAll(@PathParam("businessTypeCode") String businessTypeCode) {
		return this.sheetNoFinder.getSheetNo(businessTypeCode);
	}

//	@POST
//	@Path("find/businessTypeDetail/{businessTypeCode}/{sheetNo}")
//	public BusinessTypeDetailDto getBusinessTypeDetail(@PathParam("businessTypeCode") String businessTypeCode,
//			@PathParam("sheetNo") BigDecimal sheetNo) {
//		return this.dailyPerformanceFinder.findAll(businessTypeCode, sheetNo);
//	}
}
