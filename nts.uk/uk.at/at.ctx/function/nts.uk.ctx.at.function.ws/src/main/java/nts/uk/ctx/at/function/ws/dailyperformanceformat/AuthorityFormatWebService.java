package nts.uk.ctx.at.function.ws.dailyperformanceformat;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.function.app.command.dailyperformanceformat.AddAutDaiFormatCommandHandler;
import nts.uk.ctx.at.function.app.command.dailyperformanceformat.AddAuthorityDailyFormatCommand;
import nts.uk.ctx.at.function.app.command.dailyperformanceformat.DeleteAuthFormatBySheetcmd;
import nts.uk.ctx.at.function.app.command.dailyperformanceformat.DeleteAuthFormatBySheetcmdHandler;
import nts.uk.ctx.at.function.app.command.dailyperformanceformat.DuplicateAuthorityDailyFormatCommand;
import nts.uk.ctx.at.function.app.command.dailyperformanceformat.DuplicateAuthorityDailyFormatCommandHandler;
import nts.uk.ctx.at.function.app.command.dailyperformanceformat.RemoveAuthorityCommand;
import nts.uk.ctx.at.function.app.command.dailyperformanceformat.RemoveAuthorityCommandHandler;
import nts.uk.ctx.at.function.app.command.dailyperformanceformat.UpdateAutDaiFormatCommandHandler;
import nts.uk.ctx.at.function.app.command.dailyperformanceformat.UpdateAuthorityDailyFormatCommand;
import nts.uk.ctx.at.function.app.find.dailyperformanceformat.DailyPerformanceAuthorityFinder;
import nts.uk.ctx.at.function.app.find.dailyperformanceformat.DailyPerformanceCodeFinder;
import nts.uk.ctx.at.function.app.find.dailyperformanceformat.dto.AuthorityDailyFormatDto;
import nts.uk.ctx.at.function.app.find.dailyperformanceformat.dto.DailyAttendanceAuthorityDailyDto;
import nts.uk.ctx.at.function.app.find.dailyperformanceformat.dto.DailyAttendanceAuthorityDetailDto;
import nts.uk.ctx.at.function.app.find.dailyperformanceformat.dto.DailyPerformanceCodeDto;

@Path("at/function/dailyperformanceformat")
@Produces("application/json")
public class AuthorityFormatWebService extends WebService {
	
	@Inject
	private RemoveAuthorityCommandHandler removeAuthorityCommandHandler;
	
	@Inject
	private AddAutDaiFormatCommandHandler addAuthorityDailyCommandHandler;
	
	@Inject
	private DuplicateAuthorityDailyFormatCommandHandler duplicateAutDaiFormatCommandHandler;
	
	@Inject
	private UpdateAutDaiFormatCommandHandler updateAuthorityDailyCommandHandler;
	
	@Inject
	private DailyPerformanceAuthorityFinder dailyPerformanceAuthorityFinder;
	
	@Inject
	private DailyPerformanceCodeFinder dailyPerformanceCodeFinder;
	
	//tu add
	@Inject
	private DeleteAuthFormatBySheetcmdHandler deleteBySheetNo;
	
	@POST
	@Path("removeAuthorityFormat")
	public void removeAuthorityFormat(RemoveAuthorityCommand command) {
		this.removeAuthorityCommandHandler.handle(command);
	}
	
	@POST
	@Path("addAuthorityDailyFormat")
	public void addAuthorityDailyFormat(AddAuthorityDailyFormatCommand command) {
		this.addAuthorityDailyCommandHandler.handle(command);
	}
	
	@POST
	@Path("duplicateAuthorityDailyFormat")
	public void duplicateAuthorityDailyFormat(DuplicateAuthorityDailyFormatCommand command) {
		this.duplicateAutDaiFormatCommandHandler.handle(command);
	}
	
	@POST
	@Path("findAllByCompanyIdAndCode/{formatCode}")
	public AuthorityDailyFormatDto findAllByCompanyId(@PathParam("formatCode") String formatCode) {
		return dailyPerformanceAuthorityFinder.findAllByCompanyId(formatCode);
	}
	
//	@POST
//	@Path("addAuthorityMonthlyFormat")
//	public void addAuthorityMonthlyFormat(AddAuthorityMonthlyCommand command) {
//		this.addAuthorityMonthlyCommandHandler.handle(command);
//	}
	
//	@POST
//	@Path("updateAuthorityMonthlyFormat")
//	public void updateAuthorityMonthlyFormat(UpdateAuthorityMonthlyCommand command) {
//		this.updateAuthorityMonthlyCommandHandler.handle(command);
//	}
	
	@POST
	@Path("updateAuthorityDailyFormat")
	public void updateAuthorityDailyFormat(UpdateAuthorityDailyFormatCommand command) {
		this.updateAuthorityDailyCommandHandler.handle(command);
	}

	@POST
	@Path("getAuthorityDailyFormat/{formatCode}/{sheetNo}")
	public DailyAttendanceAuthorityDailyDto getAuthorityDailyFormat(@PathParam("formatCode") String formatCode,
			@PathParam("sheetNo") BigDecimal sheetNo) {
		return this.dailyPerformanceAuthorityFinder.findAllDaily(formatCode, sheetNo);
	}

	@POST
	@Path("getAuthorityMonthlyFormat/{formatCode}")
	public List<DailyAttendanceAuthorityDetailDto> getAuthorityMonthlyFormat(@PathParam("formatCode") String formatCode) {
		return this.dailyPerformanceAuthorityFinder.findAllMonthly(formatCode);
	}

	@POST
	@Path("getAuthorityDailyFormatCode")
	public List<DailyPerformanceCodeDto> getListCode() {
		return this.dailyPerformanceCodeFinder.findAll();
	}
	
	@POST
	@Path("deletebysheet")
	public void deleteAuthBySheet(DeleteAuthFormatBySheetcmd command) {
		this.deleteBySheetNo.handle(command);
	}
}
