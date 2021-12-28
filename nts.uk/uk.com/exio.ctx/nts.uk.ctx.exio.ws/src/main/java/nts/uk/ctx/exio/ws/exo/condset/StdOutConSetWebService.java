package nts.uk.ctx.exio.ws.exo.condset;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.exio.app.command.exo.authset.*;
import nts.uk.ctx.exio.app.command.exo.condset.CopyOutCondSet;
import nts.uk.ctx.exio.app.command.exo.condset.CopyOutputCondSetCommandHandler;
import nts.uk.ctx.exio.app.command.exo.condset.ExcuteCopyOutCondSetCommandHandler;
import nts.uk.ctx.exio.app.command.exo.condset.RegisterStdOutputCondSetCommandHandler;
import nts.uk.ctx.exio.app.command.exo.condset.RemoveStdOutputCondSetCommandHandler;
import nts.uk.ctx.exio.app.command.exo.condset.SaveOutputPeriodSetCommand;
import nts.uk.ctx.exio.app.command.exo.condset.SaveOutputPeriodSetCommandHandler;
import nts.uk.ctx.exio.app.command.exo.condset.StdOutputCondSetCommand;
import nts.uk.ctx.exio.app.find.exo.authset.ExOutCtgAuthSetDTO;
import nts.uk.ctx.exio.app.find.exo.authset.ExOutCtgAuthSetFinder;
import nts.uk.ctx.exio.app.find.exo.category.Cmf002Dto;
import nts.uk.ctx.exio.app.find.exo.category.ExOutCtgDto;
import nts.uk.ctx.exio.app.find.exo.category.ExOutCtgFinder;
import nts.uk.ctx.exio.app.find.exo.categoryitemdata.CtgItemDataDto;
import nts.uk.ctx.exio.app.find.exo.categoryitemdata.CtgItemDataFinder;
import nts.uk.ctx.exio.app.find.exo.condset.CondSetDto;
import nts.uk.ctx.exio.app.find.exo.condset.OutputPeriodSettingDto;
import nts.uk.ctx.exio.app.find.exo.condset.OutputPeriodSettingFinder;
import nts.uk.ctx.exio.app.find.exo.condset.StdOutputCondSetDto;
import nts.uk.ctx.exio.app.find.exo.condset.StdOutputCondSetFinder;
import nts.uk.ctx.exio.app.find.exo.item.StdOutItemDto;
import nts.uk.ctx.exio.app.find.exo.menu.RoleAuthorityDto;
import nts.uk.ctx.exio.dom.exo.condset.StdOutputCondParam;

@Path("exio/exo/condset")
@Produces("application/json")
public class StdOutConSetWebService extends WebService {

	@Inject
	private StdOutputCondSetFinder stdOutputCondSetFinder;

	@Inject
	private ExcuteCopyOutCondSetCommandHandler excuteCopyOutCondSetCommandHandler;

	@Inject
	private RegisterStdOutputCondSetCommandHandler registerStdOutputCondSetCommandHandler;
	
	@Inject
	private RemoveStdOutputCondSetCommandHandler removeStdOutputCondSetCommandHandler;

	@Inject
	private CtgItemDataFinder ctgItemDataFinder;
	
	@Inject
	private CopyOutputCondSetCommandHandler copyOutputCondSetCommandHandler;
	
	@Inject
	private OutputPeriodSettingFinder outputPeriodSettingFinder;
	
	@Inject
	private SaveOutputPeriodSetCommandHandler saveOutputPeriodSettingCommandHandler;

	@Inject
	private ExOutCtgFinder exOutCtgFinder;

	@Inject
	private ExOutCtgAuthSetFinder exOutCtgAuthSetFinder;

	@Inject
	private RegisterOrUpdateExOutCtgAuthCommandHandler registerExOutputCtgAuthCommand;

	@Inject
	private DuplicateExOutCtgAuthCommandHandler duplicateExOutputCtgAuthCommand;

	@POST
	@Path("excuteCopy")
	public CopyOutCondSet ExecuteCopy(StdOutputCondSetCommand command) {
		return excuteCopyOutCondSetCommandHandler.handle(command);
	}	
	
	@POST
	@Path("getCndSet")
	public List<CondSetDto> getCndSet(RoleAuthorityDto param) {
		return stdOutputCondSetFinder.getCndSet(param);
	}

	@POST
	@Path("findByCode/{cndSetCd}/{outItemCode}")
	public StdOutItemDto findByCode(@PathParam("cndSetCd") String cndSetCd,
			@PathParam("outItemCode") String outItemCode) {
		return stdOutputCondSetFinder.getByKey(cndSetCd, outItemCode);
	}

	@POST
	@Path("register")
	public void register(StdOutputCondSetCommand command) {
		registerStdOutputCondSetCommandHandler.handle(command);
	}

	@POST
	@Path("delete")
	public void delete(StdOutputCondSetCommand command) {
		removeStdOutputCondSetCommandHandler.handle(command);
	}

	@POST
	@Path("getAllCategoryItem/{categoryId}/{dataType}")
	public List<CtgItemDataDto> getAllCategoryItem(@PathParam("categoryId") Integer categoryId,
			@PathParam("dataType") Integer dataType) {
		return ctgItemDataFinder.getAllCategoryItem(categoryId, dataType);
	}

	@POST
	@Path("getCondSet")
	public List<StdOutputCondSetDto> getCondSet(StdOutputCondParam param) {
		return stdOutputCondSetFinder.getConditionSetting(param.getModeScreen(),param.getCndSetCd(),param.getRoleId());
		
	}
	@POST
	@Path("getExOutCtgDto/{categoryId}")
	public Cmf002Dto getExOutCtgDto(@PathParam("categoryId") Integer categoryId) {
		return stdOutputCondSetFinder.getExOutCtgDto(categoryId);

	}
	
	@POST
	@Path("copy")
	public void copy(CopyOutCondSet copy) {
		copyOutputCondSetCommandHandler.handle(copy);
	}
	
	@POST
	@Path("outSetContent/{cndSetCd}/{standType}")
	public List<StdOutItemDto> outSetContent(@PathParam("cndSetCd") String cndSetCd,
	        @PathParam("standType") int standType) {
		return stdOutputCondSetFinder.getOutItem(cndSetCd, standType);
	}

	@POST
	@Path("findOutputPeriodSetting/{conditionSetCode}")
	public OutputPeriodSettingDto findOutputPeriodSetting(@PathParam("conditionSetCode") String conditionSetCode) {
		return this.outputPeriodSettingFinder.findByConditionSetCode(conditionSetCode);
	}
	
	@POST
	@Path("saveOutputPeriodSetting")
	public void saveOutputPeriodSetting(SaveOutputPeriodSetCommand command) {
		this.saveOutputPeriodSettingCommandHandler.handle(command);
	}

	@POST
	@Path("getExOutCategory/{roleType}")
	public List<ExOutCtgDto> getInitExOutCategory(@PathParam("roleType") int roleType) {
		return this.exOutCtgFinder.get(roleType);
	}

	@POST
	@Path("exOutCtgAuthSet/{roleId}")
	public List<ExOutCtgAuthSetDTO> getExOutCtgPermissionSetting(@PathParam("roleId") String roleId) {
		return this.exOutCtgAuthSetFinder.get(roleId);
	}

	@POST
	@Path("exOutCtgAuthSet/register")
	public void RegisterExOutCtgAuth(RegisterExOutCtgAuthCommand commands) {
		this.registerExOutputCtgAuthCommand.handle(commands);
	}

	@POST
	@Path("exOutCtgAuthSet/copy")
	public DuplicateExOutputCtgAuthResult DuplicateExOutCtgAuth(DuplicateExOutCtgAuthCommand command) {
		return this.duplicateExOutputCtgAuthCommand.handle(command);
	}
}
