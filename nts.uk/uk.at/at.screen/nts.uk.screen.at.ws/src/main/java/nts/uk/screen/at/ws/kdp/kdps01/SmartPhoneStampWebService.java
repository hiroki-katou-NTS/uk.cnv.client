package nts.uk.screen.at.ws.kdp.kdps01;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.command.kdp.kdp004.a.StampButtonCommand;
import nts.uk.ctx.at.record.app.command.kdp.kdps01.a.CheckCanUseSmartPhoneStampCommand;
import nts.uk.ctx.at.record.app.command.kdp.kdps01.a.CheckCanUseSmartPhoneStampCommandHandler;
import nts.uk.ctx.at.record.app.command.kdp.kdps01.a.CheckCanUseSmartPhoneStampResult;
import nts.uk.ctx.at.record.app.command.kdp.kdps01.a.RegisterSmartPhoneStampCommand;
import nts.uk.ctx.at.record.app.command.kdp.kdps01.a.RegisterSmartPhoneStampCommandHandler;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.StampToSuppress;
import nts.uk.screen.at.app.command.kdp.kdps01.c.RegisterVerifiDailyResultCommand;
import nts.uk.screen.at.app.command.kdp.kdps01.c.RegisterVerifiDailyResultCommandHandler;
import nts.uk.screen.at.app.query.kdp.kdp001.a.EmployeeStampInfoDto;
import nts.uk.screen.at.app.query.kdp.kdps01.a.GetOmissionContent;
import nts.uk.screen.at.app.query.kdp.kdps01.a.GetOmissionContentDto;
import nts.uk.screen.at.app.query.kdp.kdps01.a.GetSettingSmartPhone;
import nts.uk.screen.at.app.query.kdp.kdps01.a.SettingSmartPhoneDto;
import nts.uk.screen.at.app.query.kdp.kdps01.a.SuppressingStampButton;
import nts.uk.screen.at.app.query.kdp.kdps01.b.DisplayConfirmStampResultScreenB;
import nts.uk.screen.at.app.query.kdp.kdps01.b.DisplayConfirmStampResultScreenBDto;
import nts.uk.screen.at.app.query.kdp.kdps01.b.DisplayConfirmStampResultScreenBQuery;
import nts.uk.screen.at.app.query.kdp.kdps01.c.DisplayConfirmStampResultScreenC;
import nts.uk.screen.at.app.query.kdp.kdps01.c.DisplayConfirmStampResultScreenCDto;
import nts.uk.screen.at.app.query.kdp.kdps01.c.DisplayConfirmStampResultScreenCQuery;
import nts.uk.screen.at.app.query.kdp.kdps01.s.DisplayHistorySmartPhoneStamp;
import nts.uk.screen.at.app.query.kdp.kdps01.s.DisplayHistorySmartPhoneStampQuery;

@Path("at/record/stamp/smart-phone")
@Produces("application/json")
public class SmartPhoneStampWebService extends WebService {

	@Inject
	private CheckCanUseSmartPhoneStampCommandHandler checkCanUseHandler;

	@Inject
	private GetSettingSmartPhone getSettingSmartPhone;

	@Inject
	private RegisterSmartPhoneStampCommandHandler registerCommandHandler;

	@Inject
	private SuppressingStampButton suppressButton;

	@Inject
	private GetOmissionContent getOmission;

	@Inject
	private DisplayConfirmStampResultScreenB displayConfirmStampResultScreenB;

	@Inject
	private DisplayConfirmStampResultScreenC displayConfirmStampResultScreenC;

	@Inject
	private RegisterVerifiDailyResultCommandHandler regDailyResultHandler;

	@Inject
	private DisplayHistorySmartPhoneStamp displayHistorySmartPhoneStamp;

	// Screen A

	/**
	 * ????????????(?????????)??????????????????????????????
	 */

	@POST
	@Path("check-can-use-stamp")
	public CheckCanUseSmartPhoneStampResult checkCanUseStamp(CheckCanUseSmartPhoneStampCommand command) {
		return this.checkCanUseHandler.handle(command);
	}

	/**
	 * ????????????(?????????)???????????????
	 */
	@POST
	@Path("get-setting")
	public SettingSmartPhoneDto GetSetting() {

		return this.getSettingSmartPhone.GetSetting();
	}

	/**
	 * ????????????(?????????)???????????????
	 */
	@POST
	@Path("register-stamp")
	public GeneralDate registerStamp(RegisterSmartPhoneStampCommand command) {

		return this.registerCommandHandler.handle(command);
	}

	/**
	 * ????????????(?????????)?????????????????????????????????????????????
	 */
	@POST
	@Path("get-suppress")
	public StampToSuppress getSuppressing() {

		return this.suppressButton.getSuppressingStampButton();
	}

	/**
	 * ????????????(?????????)???????????????????????????????????????
	 */

	@POST
	@Path("get-omission")
	public GetOmissionContentDto getOmission(StampButtonCommand query) {

		return this.getOmission.getOmission(query);
	}

	/**
	 * ????????????(?????????)???????????????????????????????????????
	 */

	// Screen B

	@POST
	@Path("get-stamp-result-screen-b")
	public DisplayConfirmStampResultScreenBDto getStampInfoResultScreenB(DisplayConfirmStampResultScreenBQuery query) {
		return this.displayConfirmStampResultScreenB.getStampInfoResult(query.getPeriod());
	}

	// Screen C
	/**
	 * ????????????(?????????)???????????????????????????????????????????????????
	 */
	@POST
	@Path("get-stamp-result-screen-c")
	public DisplayConfirmStampResultScreenCDto getStampInfoResultScreenC(DisplayConfirmStampResultScreenCQuery query) {
		return this.displayConfirmStampResultScreenC.getStampInfoResult(query.getPeriod(), query.getBaseDate(),
				query.getAttendanceItemIds());
	}

	/**
	 * ????????????(?????????)???????????????????????????????????????????????????
	 */
	@POST
	@Path("reg-daily-result")
	public void regDailyResult(RegisterVerifiDailyResultCommand command) {
		this.regDailyResultHandler.handle(command);
	}
	
	// Screen S

	/**
	 * ????????????(?????????)????????????????????????????????????
	 */
	@POST
	@Path("display-history")
	public List<EmployeeStampInfoDto> displayHistory(DisplayHistorySmartPhoneStampQuery query) {
		return this.displayHistorySmartPhoneStamp.displayHistorySmartPhoneStampList(query.getPeriod());
	}

}
