package nts.uk.ctx.at.function.ws.holidaysremaining;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.enums.EnumConstant;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.function.app.command.holidaysremaining.*;
import nts.uk.ctx.at.function.app.find.holidaysremaining.*;
import nts.uk.ctx.at.function.app.query.outputworkstatustable.CheckDailyPerformAuthorQuery;
import nts.uk.ctx.at.function.dom.holidaysremaining.BreakSelection;
import nts.uk.shr.com.context.AppContexts;
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

	@Inject
	private CheckDailyPerformAuthorQuery checkDailyPerformAuthor;


	@POST
	@Path("findAll")
	public HDDto getHdRemainManageList() {
		return this.hdRemainManageFinder.findAllNew();
	}
	@POST
	@Path("findFreeSetting")
	public   List<HdRemainManageDto> getHdRemainManageListFreeSetting() {
		return this.hdRemainManageFinder.findFreeSetting();
	}

	@POST
	@Path("findStandard")
	public   List<HdRemainManageDto> getHdRemainManageListStandard() {
		return this.hdRemainManageFinder.findFreeStandard();
	}

	@POST
	@Path("getInfor/{setting}")
	public   List<HdRemainManageDto> getHdRemainManage(@PathParam("setting") Integer setting) {
		return this.hdRemainManageFinder.finBySetting(setting);
	}

	@POST
	@Path("findByCode/{code}")
	public HdRemainManageDto getHdRemainManageByCode(@PathParam("code") String code) {
		return this.hdRemainManageFinder.findDtoByCode(code);
	}

	@POST
	@Path("findByLayOutId/{layOutId}")
	public HdRemainManageDto getHdRemainManage(@PathParam("layOutId") String layOutId) {
		return this.hdRemainManageFinder.findDtoByLayOutId(layOutId);
	}

	@POST
	@Path("add")
	public void addHdRemainManage(HdRemainManageCommand command) {
		this.addHdRemainManageCommandHandler.handle(command);
	}

	@POST
	@Path("update")
	public void updateHdRemainManage(HdRemainManageCommand command) {
		this.updateHdRemainManageCommandHandler.handle(command);
	}

	@POST
	@Path("remove")
	public void removerHdRemainManage(HdDeleteRemainManageCommand command) {
		this.removeHdRemainManageCommandHandler.handle(command);
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

	@POST
	@Path("getCheckAuthor")
	public boolean getCheckAuthor() {
		val roleID = AppContexts.user().roles().forAttendance();
		return checkDailyPerformAuthor.checkDailyPerformAuthor(roleID);
	}
}
