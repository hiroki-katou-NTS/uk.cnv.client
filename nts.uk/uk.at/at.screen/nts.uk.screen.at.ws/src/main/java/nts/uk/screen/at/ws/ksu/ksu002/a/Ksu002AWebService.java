package nts.uk.screen.at.ws.ksu.ksu002.a;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.ResultRegisWorkSchedule;
import nts.uk.ctx.at.shared.app.workrule.workinghours.CheckTimeIsIncorrect;
import nts.uk.ctx.at.shared.app.workrule.workinghours.ContainsResultDto;
import nts.uk.ctx.at.shared.app.workrule.workinghours.TimeOfDayDto;
import nts.uk.ctx.at.shared.app.workrule.workinghours.TimeZoneDto;
import nts.uk.screen.at.app.ksu001.displayinworkinformation.WorkTypeInfomation;
import nts.uk.screen.at.app.ksu001.processcommon.CorrectWorkTimeHalfDayParam;
import nts.uk.screen.at.app.ksu001.processcommon.GetListWorkTypeAvailable;
import nts.uk.screen.at.app.query.ksu.ksu002.a.CorrectWorkTimeHalfDayKSu002;
import nts.uk.screen.at.app.query.ksu.ksu002.a.CorrectWorkTimeHalfDayOutput;
import nts.uk.screen.at.app.query.ksu.ksu002.a.GetDataDaily;
import nts.uk.screen.at.app.query.ksu.ksu002.a.GetScheduleActualOfWorkInfo002;
import nts.uk.screen.at.app.query.ksu.ksu002.a.ListOfPeriodsClose;
import nts.uk.screen.at.app.query.ksu.ksu002.a.RegisterWorkSceduleCommandHandler;
import nts.uk.screen.at.app.query.ksu.ksu002.a.TheInitialDisplayDate;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.RegisterWorkScheduleInputCommand;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.SystemDateDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.WorkScheduleWorkInforDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.input.DisplayInWorkInfoInput;
import nts.uk.screen.at.app.query.ksu.ksu002.a.input.ListOfPeriodsCloseInput;

@Path("screen/ksu/ksu002/")
@Produces("application/json")
public class Ksu002AWebService extends WebService {

	// 初期表示の年月を取得する
	@Inject
	private TheInitialDisplayDate theInitialDisplayDate;

	// 締めに応じる期間リストを取得する
	@Inject
	private ListOfPeriodsClose listOfPeriodsClose;

	@Inject
	private GetScheduleActualOfWorkInfo002 getScheduleActualOfWorkInfo002;

	@Inject
	private GetListWorkTypeAvailable getListWorkTypeAvailable;

	@Inject
	private GetDataDaily getDataDaily;

	@Inject
	private CheckTimeIsIncorrect checkTimeIsIncorrect;

	// @Inject
	// private GetWorkTypeKSU002 getWorkType;

	// Ver2
	@Inject
	private CorrectWorkTimeHalfDayKSu002 correctWorkTimeHalfDay;

	@Inject
	private RegisterWorkSceduleCommandHandler regisWorkSchedule;

	@POST
	@Path("getListOfPeriodsClose")
	public SystemDateDto getListOfPeriodsClose(ListOfPeriodsCloseInput param) {
		if (param == null) {
			int ym = theInitialDisplayDate.getInitialDisplayDate().getYearMonth();
			param = new ListOfPeriodsCloseInput(YearMonth.of(ym));
		}

		return this.listOfPeriodsClose.get(param);
	}

	@POST
	@Path("displayInWorkInformation")
	public List<WorkScheduleWorkInforDto> getScheduleActualOfWorkInfo(DisplayInWorkInfoInput param) {
		return this.getScheduleActualOfWorkInfo002.getDataScheduleAndAactualOfWorkInfo(param);
	}

	@POST
	@Path("getDataDaily")
	public List<WorkScheduleWorkInforDto.Achievement> getDataDaily(DisplayInWorkInfoInput param) {
		return this.getDataDaily.getDataDaily(param);
	}

	@POST
	@Path("getWorkType")
	public List<WorkTypeInfomation> getWorkType() {
		return this.getListWorkTypeAvailable.getData();
	}

	// ScreenQuery : 半日勤務の勤務時間帯を補正する
	@POST
	@Path("correctWorkTimeHalfDay")
	public CorrectWorkTimeHalfDayOutput correctWorkTimeHalfDay(CorrectWorkTimeHalfDayInput param) {
		CorrectWorkTimeHalfDayParam correctWorkTimeHalfDayParam = new CorrectWorkTimeHalfDayParam(
				param.getWorkTypeCode(), param.getWorkTimeCode());
		return this.correctWorkTimeHalfDay.get(correctWorkTimeHalfDayParam);
	}

	// <<Command>> 勤務予定を登録する
	@POST
	@Path("regisWorkSchedule")
	public ResultRegisWorkSchedule regisWorkSchedule(RegisterWorkScheduleInputCommand param) {
		return this.regisWorkSchedule.handle(param);
	}

	// «Query» 時刻が不正かチェックする
	@POST
	@Path("checkTimeIsIncorrect")
	public List<ContainsResultDto> checkTimeIsIncorrect(CheckTimeIsIncorrectInput param) {
		return checkTimeIsIncorrect.check(param.getWorkTypeCode(), param.getWorkTimeCode(),
				new TimeZoneDto(new TimeOfDayDto(param.getStartTime(), 0), new TimeOfDayDto(param.getEndTime(), 0)),
				null);
	}

	// @POST
	// @Path("getDateInfo")
	// public List<DateInformation> getWorkType(GetDateInfoDuringThePeriodInput
	// param) {
	// return this.getDateInfoDuringThePeriod.get(param);
	// }
}
