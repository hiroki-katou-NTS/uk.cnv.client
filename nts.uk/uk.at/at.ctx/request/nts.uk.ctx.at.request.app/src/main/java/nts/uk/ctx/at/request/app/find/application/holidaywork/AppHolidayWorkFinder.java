package nts.uk.ctx.at.request.app.find.application.holidaywork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.AppHdWorkDispInfoDto;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.AppHdWorkDispInfoDtoMobile;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.AppHolidayWorkParamMobile;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.AppHolidayWorkParamPC;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.CheckBeforeOutputDto;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.CheckBeforeOutputMultiDto;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.HdWorkDetailOutputDto;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.HolidayWorkCalculationResultDto;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.ParamCalculationHolidayWork;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.ParamCalculationHolidayWorkMobile;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.ParamCheckBeforeRegister;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.ParamCheckBeforeRegisterMobile;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.ParamCheckBeforeRegisterMulti;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.ParamCheckBeforeUpdate;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.ParamHdWorkDetail;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.ParamHolidayWorkChangeDate;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.ParamHolidayWorkChangeDateMobile;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.ParamHolidayWorkChangeWork;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.ParamHolidayWorkChangeWorkMobile;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.PreAppContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementDetail;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.commonalgorithm.ICommonAlgorithmHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.AppHdWorkDispInfoOutputMobile;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.HolidayService;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.AppHdWorkDispInfoOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CalculatedFlag;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CheckBeforeOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CheckBeforeOutputMulti;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.HdSelectWorkDispInfoOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.HdWorkDetailOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.HolidayWorkCalculationResult;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.ICommonAlgorithmOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.service.WorkContent;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.PrePostInitAtr;
import nts.uk.ctx.at.request.dom.workrecord.dailyrecordprocess.dailycreationwork.BreakTimeZoneSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * Refactor5
 * @author huylq
 *
 */
@Stateless
public class AppHolidayWorkFinder {

	public static final String PATTERN_DATE = "yyyy/MM/dd";

	@Inject
	private HolidayService holidayWorkService;
	
	@Inject
	private ICommonAlgorithmHolidayWork commonHolidayWorkAlgorithm;
	
	@Inject
	private ICommonAlgorithmOverTime commonOverTimeAlgorithm;

	public AppHdWorkDispInfoDto getStartNew(AppHolidayWorkParamPC param) {
		String companyId = AppContexts.user().companyId();

		Optional<List<String>> empList = Optional.empty();
		if (param.getAppDispInfoStartupOutput() != null
				&& param.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput() != null) {
			List<String> empListParam = new ArrayList<String>();
			param.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput().getEmployeeInfoLst().stream()
				.forEach(emp -> empListParam.add(emp.getSid()));
			empList = Optional.of(empListParam);
		}

		Optional<List<GeneralDate>> dateListOptional = Optional.empty();
		if (param.getDateList() != null && !param.getDateList().isEmpty()) {
			List<GeneralDate> dateList = new ArrayList<GeneralDate>();
			param.getDateList().stream().filter(date -> StringUtils.isNotBlank(date))
					.forEach(date -> dateList.add(GeneralDate.fromString(date, PATTERN_DATE)));
			if (!dateList.isEmpty()) {
				dateListOptional = Optional.of(dateList);
			}
		}

		// 1.休出申請（新規）起動処理
		AppDispInfoStartupOutput appDispInfoStartupOutput = param.getAppDispInfoStartupOutput().toDomain();
		AppHdWorkDispInfoOutput appHdWorkDispInfoOutput = holidayWorkService.startA(companyId, empList,
				dateListOptional, appDispInfoStartupOutput);

		// 	計算（従業員）
		List<PreAppContentDisplay> opPreAppContentDisplayLst = appHdWorkDispInfoOutput.getAppDispInfoStartupOutput()
				.getAppDispInfoWithDateOutput().getOpPreAppContentDisplayLst().orElse(new ArrayList<PreAppContentDisplay>());
		Optional<AppHolidayWork> appHolidayWork = !opPreAppContentDisplayLst.isEmpty()
				? opPreAppContentDisplayLst.get(0).getAppHolidayWork()
				: Optional.empty();

		WorkContent workContent = commonHolidayWorkAlgorithm.getWorkContent(appHdWorkDispInfoOutput.getHdWorkDispInfoWithDateOutput());

		HolidayWorkCalculationResult calculationResult = holidayWorkService.calculate(
				companyId,
				appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid(),
				dateListOptional.isPresent()
						? Optional.of(dateListOptional.get().get(0))
						: Optional.empty(),
				appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getPrePostAtr(),
				appHdWorkDispInfoOutput.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet(),
				appHolidayWork.isPresent() ? appHolidayWork.get().getApplicationTime() : null,
				appHdWorkDispInfoOutput.getHdWorkDispInfoWithDateOutput().getActualApplicationTime().orElse(null),
				workContent,
				param.getIsAgent());
		appHdWorkDispInfoOutput.setCalculationResult(Optional.ofNullable(calculationResult));

		return AppHdWorkDispInfoDto.fromDomain(appHdWorkDispInfoOutput);
	}

	public HolidayWorkCalculationResultDto calculate(ParamCalculationHolidayWork param) {
		Optional<GeneralDate> dateOp = Optional.empty();
		if (StringUtils.isNotBlank(param.getDate())) {
			dateOp = Optional.of(GeneralDate.fromString(param.getDate(), PATTERN_DATE));	
		}
		
		HolidayWorkCalculationResult calculationResult = holidayWorkService.calculate(param.getCompanyId(),
				param.getEmployeeId(),
				dateOp,
				EnumAdaptor.valueOf(param.getPrePostAtr(), PrePostInitAtr.class),
				param.getOvertimeLeaveAppCommonSet() != null ? param.getOvertimeLeaveAppCommonSet().toDomain() : null,
				param.getPreApplicationTime() != null ? param.getPreApplicationTime().toDomain() : null,
				param.getActualApplicationTime() != null ? param.getActualApplicationTime().toDomain() : null,
				param.getWorkContent() != null ? param.getWorkContent().toDomain() : null,
				param.getIsAgent());

		return HolidayWorkCalculationResultDto.fromDomain(calculationResult);
	}
	
	public AppHdWorkDispInfoDto changeAppDate(ParamHolidayWorkChangeDate param) {
		List<GeneralDate> dateList = new ArrayList<GeneralDate>();
		if(!param.getDateList().isEmpty()) {
			dateList = param.getDateList().stream().map(date -> GeneralDate.fromString(date, PATTERN_DATE)).collect(Collectors.toList());
		}
		
		AppHdWorkDispInfoOutput appHdWorkDispInfoOutput = holidayWorkService.changeAppDate(
				param.getCompanyId(), 
				dateList,
				EnumAdaptor.valueOf(param.getApplicationType(), ApplicationType.class),
				param.getAppHdWorkDispInfoDto().toDomain(),
				param.getIsAgent());
		
		return AppHdWorkDispInfoDto.fromDomain(appHdWorkDispInfoOutput);
	}
	
	public AppHdWorkDispInfoDto changeWorkHours(ParamHolidayWorkChangeWork param) {
		AppHdWorkDispInfoOutput appHdWorkDispInfoOutput = param.getAppHdWorkDispInfoDto().toDomain();
		
		List<ActualContentDisplay> opActualContentDisplayLst = appHdWorkDispInfoOutput.getAppDispInfoStartupOutput()
				.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(Collections.emptyList());
		Optional<AchievementDetail> achievementDetail = !opActualContentDisplayLst.isEmpty() ? 
				opActualContentDisplayLst.get(0).getOpAchievementDetail() : Optional.empty();
		
		BreakTimeZoneSetting breakTimeZoneSettingList = commonOverTimeAlgorithm.selectWorkTypeAndTime(param.getCompanyId(), new WorkTypeCode(param.getWorkTypeCode()), 
				new WorkTimeCode(param.getWorkTimeCode()), Optional.of(new TimeWithDayAttr(param.getStartTime())), 
				Optional.of(new TimeWithDayAttr(param.getEndTime())), achievementDetail);
		appHdWorkDispInfoOutput.getHdWorkDispInfoWithDateOutput().setBreakTimeZoneSettingList(Optional.ofNullable(breakTimeZoneSettingList));
		
		if(appHdWorkDispInfoOutput.getCalculationResult().isPresent()) {
			appHdWorkDispInfoOutput.getCalculationResult().get().setCalculatedFlag(CalculatedFlag.UNCALCULATED);
		}
		
		return AppHdWorkDispInfoDto.fromDomain(appHdWorkDispInfoOutput);
	}
	
	public AppHdWorkDispInfoDto selectWork(ParamHolidayWorkChangeWork param) {
		AppHdWorkDispInfoOutput appHdWorkDispInfoOutput = param.getAppHdWorkDispInfoDto().toDomain();
		
		//	勤務種類・就業時間帯選択時に表示するデータを取得する
		HdSelectWorkDispInfoOutput hdSelectWorkDispInfoOutput = holidayWorkService.selectWork(param.getCompanyId(), 
				!param.getDateList().isEmpty() ? GeneralDate.fromString(param.getDateList().get(0), PATTERN_DATE) : null, 
				new WorkTypeCode(param.getWorkTypeCode()), new WorkTimeCode(param.getWorkTimeCode()), 
				appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(Collections.emptyList()), 
				appHdWorkDispInfoOutput.getAppDispInfoStartupOutput(), appHdWorkDispInfoOutput.getHolidayWorkAppSet());
		
		appHdWorkDispInfoOutput.getHdWorkDispInfoWithDateOutput().setWorkHours(hdSelectWorkDispInfoOutput.getWorkHours());
		appHdWorkDispInfoOutput.getHdWorkDispInfoWithDateOutput().setActualApplicationTime(Optional.ofNullable(hdSelectWorkDispInfoOutput.getActualApplicationTime()));
		appHdWorkDispInfoOutput.getHdWorkDispInfoWithDateOutput().setBreakTimeZoneSettingList(hdSelectWorkDispInfoOutput.getBreakTimeZoneSettingList());
		
		//	計算（従業員）
		WorkContent workContent = commonHolidayWorkAlgorithm.getWorkContent(appHdWorkDispInfoOutput.getHdWorkDispInfoWithDateOutput());
		List<PreAppContentDisplay> preAppContentDisplayList = appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput()
				.getOpPreAppContentDisplayLst().orElse(Collections.emptyList());
		Optional<AppHolidayWork> appHolidayWork = !preAppContentDisplayList.isEmpty() ? preAppContentDisplayList.get(0).getAppHolidayWork() : Optional.empty();
		
		HolidayWorkCalculationResult calculationResult = holidayWorkService.calculate(
				param.getCompanyId(), 
				appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid(), 
				Optional.ofNullable(!param.getDateList().isEmpty() ? GeneralDate.fromString(param.getDateList().get(0), PATTERN_DATE) : null), 
				appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getPrePostAtr(), 
				appHdWorkDispInfoOutput.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet(), 
				appHolidayWork.isPresent() ? appHolidayWork.get().getApplicationTime() : null, 
				appHdWorkDispInfoOutput.getHdWorkDispInfoWithDateOutput().getActualApplicationTime().orElse(null), 
				workContent,
				param.getIsAgent());
		appHdWorkDispInfoOutput.setCalculationResult(Optional.ofNullable(calculationResult));
		
		return AppHdWorkDispInfoDto.fromDomain(appHdWorkDispInfoOutput);
	}

	public CheckBeforeOutputDto checkBeforeRegister(ParamCheckBeforeRegister param) {
		AppHdWorkDispInfoOutput appHdWorkDispInfoOutput = param.getAppHdWorkDispInfo().toDomain();
		Application application = param.getAppHolidayWork().getApplication().toDomain();
		AppHolidayWork appHolidayWork = param.getAppHolidayWork().toDomain();
		appHolidayWork.setApplication(application);
		CheckBeforeOutput checkBeforeOutput = holidayWorkService.checkBeforeRegister(param.isRequire(), param.getCompanyId(), appHdWorkDispInfoOutput, 
				appHolidayWork, param.isProxy());
		
		return CheckBeforeOutputDto.fromDomain(checkBeforeOutput);
	}
	
	public CheckBeforeOutputMultiDto checkBeforeRegisterMulti(ParamCheckBeforeRegisterMulti param) {
		AppHdWorkDispInfoOutput appHdWorkDispInfoOutput = param.getAppHdWorkDispInfo().toDomain();
		Application application = param.getAppHolidayWork().getApplication().toDomain();
		AppHolidayWork appHolidayWork = param.getAppHolidayWork().toDomain();
		appHolidayWork.setApplication(application);
		
		CheckBeforeOutputMulti checkBeforeOutputMulti = holidayWorkService.checkBeforeRegisterMulti(param.isRequire(), param.getCompanyId(), 
				param.getEmpList(), appHdWorkDispInfoOutput, appHolidayWork);
		
		return CheckBeforeOutputMultiDto.fromDomain(checkBeforeOutputMulti);
	}
	
	public HdWorkDetailOutputDto getDetail(ParamHdWorkDetail param) {
		HdWorkDetailOutput hdWorkDetailOutput = holidayWorkService.getDetail(param.getCompanyId(), param.getApplicationId(), 
				param.getAppDispInfoStartup().toDomain());
		return HdWorkDetailOutputDto.fromDomain(hdWorkDetailOutput);
	}
	


	public CheckBeforeOutputDto checkBeforeUpdate(ParamCheckBeforeUpdate param) {
		AppHdWorkDispInfoOutput appHdWorkDispInfoOutput = param.getAppHdWorkDispInfo().toDomain();
		Application application = param.getAppHolidayWork().getApplication().toDomain(param.getAppHdWorkDispInfo().getAppDispInfoStartupOutput().getAppDetailScreenInfo().getApplication());
		AppHolidayWork appHolidayWork = param.getAppHolidayWork().toDomain();
		appHolidayWork.setApplication(application);
		
		CheckBeforeOutput checkBeforeOutput = 
				holidayWorkService.checkBeforeUpdate(param.isRequire(), param.getCompanyId(), appHdWorkDispInfoOutput, appHolidayWork);
		return CheckBeforeOutputDto.fromDomain(checkBeforeOutput);
	}
	
	public AppHdWorkDispInfoDtoMobile getStartMobile(AppHolidayWorkParamMobile param) {
		Optional<GeneralDate> appDate = Optional.empty();
		if (StringUtils.isNotBlank(param.getAppDate())) {
			appDate = Optional.of(GeneralDate.fromString(param.getAppDate(), PATTERN_DATE));	
		}
		AppHdWorkDispInfoOutputMobile appHdWorkDispInfoOutputMobile = holidayWorkService.getStartMobile(
				param.getMode(),
				param.getCompanyId(),
				!StringUtils.isBlank(param.getEmployeeId()) ? Optional.of(param.getEmployeeId()) : Optional.empty(),
				appDate,
				param.getAppHdWorkDispInfo() != null ? Optional.of(param.getAppHdWorkDispInfo().toDomain()) : Optional.empty(),
				param.getAppHolidayWork() != null ? Optional.of(param.getAppHolidayWork().toDomain()) : Optional.empty(),
				param.getAppDispInfoStartupOutput().toDomain());
		
		return AppHdWorkDispInfoDtoMobile.fromDomain(appHdWorkDispInfoOutputMobile);
	}

	public AppHdWorkDispInfoDto changeDateMobile(ParamHolidayWorkChangeDateMobile param) {
		GeneralDate appDate = null;
		if (StringUtils.isNotBlank(param.getAppDate())) {
			appDate = GeneralDate.fromString(param.getAppDate(), PATTERN_DATE);
		}
		AppHdWorkDispInfoOutput appHdWorkDispInfo = holidayWorkService.changeDateMobile(
				param.getCompanyId(),
				appDate,
				param.getAppHdWorkDispInfo().toDomain());
		return AppHdWorkDispInfoDto.fromDomain(appHdWorkDispInfo);
	}

	public AppHdWorkDispInfoDto selectWorkMobile(ParamHolidayWorkChangeWorkMobile param) {
		GeneralDate appDate = null;
		if (StringUtils.isNotBlank(param.getAppDate())) {
			appDate = GeneralDate.fromString(param.getAppDate(), PATTERN_DATE);
		}
		AppHdWorkDispInfoOutput appHdWorkDispInfo = param.getAppHdWorkDispInfo().toDomain();
		//	勤務種類・就業時間帯選択時に表示するデータを取得する
		HdSelectWorkDispInfoOutput hdSelectWorkDispInfoOutput = holidayWorkService.selectWork(param.getCompanyId(), appDate, 
				new WorkTypeCode(param.getWorkTypeCode()), new WorkTimeCode(param.getWorkTimeCode()), 
				appHdWorkDispInfo.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(Collections.emptyList()), 
				appHdWorkDispInfo.getAppDispInfoStartupOutput(), appHdWorkDispInfo.getHolidayWorkAppSet());
		//	取得した「勤務種類・就業時間帯選択時の表示情報」を「休日出勤申請起動時の表示情報」にセット
		appHdWorkDispInfo.getHdWorkDispInfoWithDateOutput().setWorkHours(hdSelectWorkDispInfoOutput.getWorkHours());
		appHdWorkDispInfo.getHdWorkDispInfoWithDateOutput().setActualApplicationTime(Optional.ofNullable(hdSelectWorkDispInfoOutput.getActualApplicationTime()));
		appHdWorkDispInfo.getHdWorkDispInfoWithDateOutput().setBreakTimeZoneSettingList(hdSelectWorkDispInfoOutput.getBreakTimeZoneSettingList());
		return AppHdWorkDispInfoDto.fromDomain(appHdWorkDispInfo);
	}

	public AppHdWorkDispInfoDto changeWorkHoursMobile(ParamHolidayWorkChangeWorkMobile param) {
		AppHdWorkDispInfoOutput appHdWorkDispInfo = param.getAppHdWorkDispInfo().toDomain();
		
		//	休憩時間帯を取得する
		List<ActualContentDisplay> opActualContentDisplayLst = appHdWorkDispInfo.getAppDispInfoStartupOutput()
				.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(Collections.emptyList());
		Optional<AchievementDetail> achievementDetail = !opActualContentDisplayLst.isEmpty() ? 
				opActualContentDisplayLst.get(0).getOpAchievementDetail() : Optional.empty();

		BreakTimeZoneSetting breakTimeZoneSettingList = commonOverTimeAlgorithm.selectWorkTypeAndTime(param.getCompanyId(), 
				new WorkTypeCode(param.getWorkTypeCode()), 
				new WorkTimeCode(param.getWorkTimeCode()),
				Optional.of(new TimeWithDayAttr(param.getStartTime())), 
				Optional.of(new TimeWithDayAttr(param.getEndTime())), 
				achievementDetail);
		//	休日出勤申請起動時の表示情報．休日出勤申請起動時の表示情報(申請対象日関係あり)．休憩時間帯設定リスト＝取得した「勤務種類・就業時間帯選択時の表示情報」．休憩時間帯設定リスト
		appHdWorkDispInfo.getHdWorkDispInfoWithDateOutput().setBreakTimeZoneSettingList(Optional.ofNullable(breakTimeZoneSettingList));
		
		return AppHdWorkDispInfoDto.fromDomain(appHdWorkDispInfo);
	}

	public AppHdWorkDispInfoDto calculateMobile(ParamCalculationHolidayWorkMobile param) {
		String companyId = param.getCompanyId();
		Optional<GeneralDate> appDate = Optional.empty();
		if (StringUtils.isNotBlank(param.getAppDate())) {
			appDate = Optional.of(GeneralDate.fromString(param.getAppDate(), PATTERN_DATE));	
		}
		Application application;
		AppHolidayWork appHolidayWork;
		if (param.getMode()) {
			application = param.getAppHolidayWorkInsert().getApplication().toDomain();
			appHolidayWork = param.getAppHolidayWorkInsert().toDomain();
		} else {
			application = param.getAppHolidayWorkUpdate().getApplication().toDomain(param.getAppHdWorkDispInfo().getAppDispInfoStartupOutput().getAppDetailScreenInfo().getApplication());
			appHolidayWork = param.getAppHolidayWorkUpdate().toDomain();
		}
		
		appHolidayWork.setApplication(application);
		
		AppHdWorkDispInfoOutput appHdWorkDispInfoOutput = holidayWorkService.calculateMobile(
				companyId,
				param.getAppHdWorkDispInfo().toDomain(),
				appHolidayWork,
				param.getMode(),
				param.getEmployeeId(),
				appDate,
				param.getIsAgent());
		
		return AppHdWorkDispInfoDto.fromDomain(appHdWorkDispInfoOutput);
	}

	public CheckBeforeOutputDto checkBeforeRegisterMobile(ParamCheckBeforeRegisterMobile param) {
		CheckBeforeOutput checkBeforeOutput = new CheckBeforeOutput();
		Application application;
		AppHolidayWork appHolidayWork;
		if(param.getMode()) {
			application = param.getAppHolidayWorkInsert().getApplication().toDomain();
			appHolidayWork = param.getAppHolidayWorkInsert().toDomain();
			appHolidayWork.setApplication(application);
		} else {
			application = param.getAppHolidayWorkUpdate().getApplication().toDomain(param.getAppHdWorkDispInfo().getAppDispInfoStartupOutput().getAppDetailScreenInfo().getApplication());
			appHolidayWork = param.getAppHolidayWorkUpdate().toDomain();
			appHolidayWork.setApplication(application);
		}
		
		List<ConfirmMsgOutput> confirmMsgOutputs = commonHolidayWorkAlgorithm.checkAfterMoveToAppTime(param.isRequire(), param.getCompanyId(), 
				param.getAppHdWorkDispInfo().toDomain(), appHolidayWork);
		checkBeforeOutput.setConfirmMsgOutputs(confirmMsgOutputs);
		
		return CheckBeforeOutputDto.fromDomain(checkBeforeOutput);
	}
}
