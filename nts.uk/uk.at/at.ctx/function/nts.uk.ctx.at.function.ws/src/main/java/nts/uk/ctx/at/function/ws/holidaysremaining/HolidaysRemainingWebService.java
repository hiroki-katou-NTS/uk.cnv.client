package nts.uk.ctx.at.function.ws.holidaysremaining;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.enums.EnumAdaptor;
import nts.arc.enums.EnumConstant;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.function.app.command.holidaysremaining.AddHdRemainManageCommandHandler;
import nts.uk.ctx.at.function.app.command.holidaysremaining.HdRemainManageCommand;
import nts.uk.ctx.at.function.app.command.holidaysremaining.RemoveHdRemainManageCommandHandler;
import nts.uk.ctx.at.function.app.command.holidaysremaining.UpdateHdRemainManageCommandHandler;
import nts.uk.ctx.at.function.app.find.holidaysremaining.DateHolidayRemainingDto;
import nts.uk.ctx.at.function.app.find.holidaysremaining.HdRemainManageDto;
import nts.uk.ctx.at.function.app.find.holidaysremaining.HdRemainManageFinder;
import nts.uk.ctx.at.function.app.find.holidaysremaining.RoleWhetherLoginDto;
import nts.uk.ctx.at.function.app.find.holidaysremaining.VariousVacationControlDto;
import nts.uk.ctx.at.function.dom.holidaysremaining.BreakSelection;
import nts.uk.shr.infra.i18n.resource.I18NResourcesForUK;

@Path("at/function/holidaysremaining")
@Produces("application/json")
public class HolidaysRemainingWebService extends WebService {

	/* Finder */
	@Inject
	private HdRemainManageFinder hdRemainManageFinder;

	@Inject
	private AddHdRemainManageCommandHandler addHdRemainManageCommandHandler;

	@Inject
	private UpdateHdRemainManageCommandHandler updateHdRemainManageCommandHandler;

	@Inject
	private RemoveHdRemainManageCommandHandler removeHdRemainManageCommandHandler;

	@Inject
	private I18NResourcesForUK i18n;

	@POST
	@Path("findAll")
	public List<HdRemainManageDto> getHdRemainManageList() {
		return this.hdRemainManageFinder.findAll();
	}

	@POST
	@Path("findByCode/{code}")
	public HdRemainManageDto getHdRemainManageByCode(@PathParam("code") String code) {
		return this.hdRemainManageFinder.findDtoByCode(code);
	}

	@POST
	@Path("add")
	public void addHdRemainManage(HdRemainManageCommand comand) {
		this.addHdRemainManageCommandHandler.handle(comand);
	}

	@POST
	@Path("update")
	public void updateHdRemainManage(HdRemainManageCommand comand) {
		this.updateHdRemainManageCommandHandler.handle(comand);
	}

	@POST
	@Path("remove")
	public void removerHdRemainManage(HdRemainManageCommand comand) {
		this.removeHdRemainManageCommandHandler.handle(comand);
	}

	@POST
	@Path("getDate")
	public DateHolidayRemainingDto getDate() {
		return this.hdRemainManageFinder.getDate();
	}

	@POST
	@Path("getCurrentLoginerRole")
	public RoleWhetherLoginDto getCurrentLoginerRole() {
		return this.hdRemainManageFinder.getCurrentLoginerRole();
	}

	@POST
	@Path("getBreakSelection")
	public List<EnumConstant> getEnumValueOutputFormat() {
		return EnumAdaptor.convertToValueNameList(BreakSelection.class, i18n);
	}

	@POST
	@Path("getVariousVacationControl")
	public VariousVacationControlDto getVariousVacationControl() {
		return this.hdRemainManageFinder.getVariousVacationControl();
	}
}
