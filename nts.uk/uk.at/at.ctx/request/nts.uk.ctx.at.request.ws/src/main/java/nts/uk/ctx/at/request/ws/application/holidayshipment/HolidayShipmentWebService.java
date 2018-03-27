package nts.uk.ctx.at.request.ws.application.holidayshipment;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import lombok.Value;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.request.app.command.application.holidayshipment.SaveHolidayShipmentCommand;
import nts.uk.ctx.at.request.app.command.application.holidayshipment.SaveHolidayShipmentCommandHandler;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.HolidayShipmentScreenAFinder;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.HolidayShipmentScreenBFinder;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.dto.ChangeWorkTypeDto;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.dto.HolidayShipmentDto;

@Path("at/request/application/holidayshipment")
@Produces("application/json")
public class HolidayShipmentWebService extends WebService {

	@Inject
	private HolidayShipmentScreenAFinder afinder;
	@Inject
	private HolidayShipmentScreenBFinder bfinder;
	@Inject
	private SaveHolidayShipmentCommandHandler handler;

	@POST
	@Path("start")
	public HolidayShipmentDto startPage(StartAParam param) {
		return this.afinder.startPage(param.getSID(), param.getAppDate(), param.getUiType());
	}

	@POST
	@Path("change_work_type")
	public ChangeWorkTypeDto changeWorkType(ChangeWorkTypeParam param) {
		return this.afinder.changeWorkType(param.getWorkTypeCD(), param.getWkTimeCD());
	}

	@POST
	@Path("update")
	public void update(SaveHolidayShipmentCommand command) {
		handler.handle(command);
	}

	@POST
	@Path("change_day")
	public HolidayShipmentDto changeDay(ChangeDateParam param) {
		return this.afinder.changeDay(param.getTakingOutDate(), param.getHolidayDate(), param.getComType(),
				param.getUiType());
	}

	@POST
	@Path("save")
	public void save(SaveHolidayShipmentCommand command) {
		handler.handle(command);
	}

	@POST
	@Path("find_by_id")
	public HolidayShipmentDto findByID(StartBParam param) {
		return this.bfinder.findByID(param.getAppID());
	}

}

@Value
class StartAParam {
	private String sID;
	private String appDate;
	private int uiType;
}

@Value
class StartBParam {
	private String appID;
}

@Value
class ChangeWorkTypeParam {
	private String workTypeCD;
	private String wkTimeCD;
}

@Value
class ChangeDateParam {

	private String holidayDate;

	private String takingOutDate;

	/**
	 * 申請組み合わせ
	 */
	private int comType;
	private int uiType;

}
