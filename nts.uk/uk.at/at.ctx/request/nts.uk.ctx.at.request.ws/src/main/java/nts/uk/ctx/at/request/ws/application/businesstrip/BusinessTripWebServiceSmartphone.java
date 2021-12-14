package nts.uk.ctx.at.request.ws.application.businesstrip;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.request.app.command.application.businesstrip.AddBusinessTripCommand;
import nts.uk.ctx.at.request.app.command.application.businesstrip.AddBusinessTripCommandHandler;
import nts.uk.ctx.at.request.app.command.application.businesstrip.UpdateBusinessTripCommand;
import nts.uk.ctx.at.request.app.command.application.businesstrip.UpdateBusinessTripCommandHandler;
import nts.uk.ctx.at.request.app.find.application.businesstrip.AppBusinessParam;
import nts.uk.ctx.at.request.app.find.application.businesstrip.BusinessTripFinder;
import nts.uk.ctx.at.request.app.find.application.businesstrip.BusinessTripMobileDto.ApproveTripRequestParam;
import nts.uk.ctx.at.request.app.find.application.businesstrip.BusinessTripMobileDto.CheckPeriodDto;
import nts.uk.ctx.at.request.app.find.application.businesstrip.BusinessTripMobileDto.DetailScreenInfo;
import nts.uk.ctx.at.request.app.find.application.businesstrip.BusinessTripMobileDto.StartScreenBDto;
import nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto.BusinessTripOutputDto;
import nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto.DetailScreenDto;
import nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto.DetailStartScreenInfoDto;
import nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto.ParamStartKDL003;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;


@Path("at/request/application/businesstrip/mobile")
@Produces("application/json")
public class BusinessTripWebServiceSmartphone extends WebService {

	@Inject
	private BusinessTripFinder businessTripFinder;
	
	 @Inject
	    private AddBusinessTripCommandHandler addBusinessTripCommandHandler;
	 
	 @Inject
	    private UpdateBusinessTripCommandHandler updateBusinessTripCommandHandler;

	
	@Path("startMobile")
	@POST
	public BusinessTripOutputDto startMobile(AppBusinessParam appWorkChangeParam) {
		return businessTripFinder.startKAFS08(appWorkChangeParam);
	}

	/**
	 * 次へをクリックして勤務内容を表示する
	 * @param param
	 * @return
	 */
	@Path("changeAppDate")
	@POST
	public DetailStartScreenInfoDto mobilePeriodCheck(CheckPeriodDto param) {
		return this.businessTripFinder.mobilePeriodCheck(param);
	}

	// B:出張の申請確認（スマホ）.起動する
	@Path("startScreenB")
	@POST
	public DetailScreenDto startScreenB(StartScreenBDto param) {
		return this.businessTripFinder.startScreenB(param);
	}

	// B:出張の申請確認（スマホ）.申請を修正する
	@POST
	@Path("approveTripReq")
	public DetailScreenInfo appoveTripReq(ApproveTripRequestParam param){
		return this.businessTripFinder.approveTripRequest(param);
	}

	// A:出張の申請（スマホ）.A2.申請内容を登録する
	@POST
	@Path("checkBeforeRegister")
	public List<ConfirmMsgOutput> checkBeforeRegister(DetailScreenDto param){
		return this.businessTripFinder.checkBeforeRegisterMobile(param);
	}
	
	//Register A2
	@POST
    @Path("register")
    public ProcessResult register(AddBusinessTripCommand param) {
        return this.addBusinessTripCommandHandler.handle(param);
    }
	
	@POST
    @Path("startKDLS02")
    public boolean startKDL003(ParamStartKDL003 param) {
        return this.businessTripFinder.getFlagStartKDL003(param);
    }
	
	/**
	 * Update application
	 * @param param
	 * @return
	 */
	@POST
    @Path("updateBusinessTrip")
    public ProcessResult updateBusinesstrip(UpdateBusinessTripCommand param) {
        return this.updateBusinessTripCommandHandler.handle(param);
    }
}
