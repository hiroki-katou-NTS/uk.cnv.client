package nts.uk.ctx.at.request.app.find.application.overtime;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.CheckBeforeOutputMultiDto;
import nts.uk.ctx.at.request.app.find.application.overtime.dto.CheckBeforeOutputDto;
import nts.uk.ctx.at.request.app.find.application.overtime.dto.DetailOutputDto;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.CheckBeforeOutput;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.ICommonAlgorithmOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.service.DisplayInfoOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.service.DisplayInfoOverTimeMobile;
import nts.uk.ctx.at.request.dom.application.overtime.service.OvertimeService;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.request.dom.application.overtime.service.SelectWorkOutput;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.PrePostInitAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

@Stateless
public class AppOvertimeFinder {
	
	public static final String PATTERN_DATE = "yyyy/MM/dd";
	
	@Inject
	private OvertimeService overtimeService;
	
	@Inject
	private ICommonAlgorithmOverTime commonAlgorithmOverTime;
	
	/**
	 * Refactor5
	 * start at A
	 * @param paramOverTimeStart
	 * @return
	 */
	public DisplayInfoOverTimeDto start(ParamOverTimeStart param) {
		DisplayInfoOverTime output;
		String companyId = param.companyId;
		Optional<GeneralDate> dateOp = Optional.empty();
		if (StringUtils.isNotBlank(param.dateOp)) {
			dateOp = Optional.of(GeneralDate.fromString(param.dateOp, PATTERN_DATE));	
		}
		
		OvertimeAppAtr overtimeAppAtr = EnumAdaptor.valueOf(param.overtimeAppAtr, OvertimeAppAtr.class);
		AppDispInfoStartupOutput appDispInfoStartupOutput = param.appDispInfoStartupDto.toDomain();
		Optional<Integer> startTimeSPR = Optional.ofNullable(param.startTimeSPR);
		
		Optional<Integer> endTimeSPR = Optional.ofNullable(param.endTimeSPR);
		Boolean agent = param.agent;

		output = overtimeService.startA(
				companyId,
				param.sids.get(0),
				dateOp,
				overtimeAppAtr,
				appDispInfoStartupOutput,
				startTimeSPR,
				endTimeSPR,
				agent
				);
		return DisplayInfoOverTimeDto.fromDomain(output);
	}
	public DisplayInfoOverTimeDto changeDate(ParamOverTimeChangeDate param) {
		DisplayInfoOverTime output = param.displayInfoOverTime.toDomain();
		String companyId = param.companyId;
		Optional<GeneralDate> dateOp = Optional.empty();
		if (StringUtils.isNotBlank(param.dateOp)) {
			dateOp = Optional.of(GeneralDate.fromString(param.dateOp, PATTERN_DATE));	
		}
		
		AppDispInfoStartupOutput appDispInfoStartupOutput = param.appDispInfoStartupDto.toDomain();
		Optional<Integer> startTimeSPR = Optional.ofNullable(param.startTimeSPR);
		
		Optional<Integer> endTimeSPR = Optional.ofNullable(param.endTimeSPR);

		output = overtimeService.changeDate(
				companyId,
				param.employeeId,
				dateOp,
				EnumAdaptor.valueOf(param.overtimeAppAtr, OvertimeAppAtr.class),
				appDispInfoStartupOutput,
				startTimeSPR,
				endTimeSPR,
				param.overTimeAppSet.toDomain(companyId),
				CollectionUtil.isEmpty(param.worktypes) ? Collections.emptyList() : param.worktypes
						.stream()
						.map(x -> x.toDomain(param.companyId))
						.collect(Collectors.toList()),
				EnumAdaptor.valueOf(param.prePost, PrePostInitAtr.class),
				output,
				param.agent
				);
		return DisplayInfoOverTimeDto.fromDomain(output);
	}
	
	public DisplayInfoOverTimeDto selectWorkInfo(ParamSelectWork param) {
		DisplayInfoOverTime output;
		Optional<GeneralDate> dateOp = Optional.empty();
		
		if (StringUtils.isNotBlank(param.date)) {
			dateOp = Optional.of(GeneralDate.fromString(param.date, PATTERN_DATE));	
		}
		
		Optional<Integer> startTimeSPR = Optional.ofNullable(param.startSPR);
		
		Optional<Integer> endTimeSPR = Optional.ofNullable(param.endSPR);
		
		output = overtimeService.selectWorkInfo(
				param.companyId,
				param.employeeId,
				dateOp,
				Optional.ofNullable(param.workType).map(x -> new WorkTypeCode(x)).orElse(null),
				Optional.ofNullable(param.workTime).map(x -> new WorkTimeCode(x)).orElse(null),
				startTimeSPR,
				endTimeSPR,
				param.appDispInfoStartupDto.toDomain(),
				param.overtimeAppSet.toDomain(param.companyId),
				EnumAdaptor.valueOf(param.prePost, PrePostInitAtr.class),
				param.agent
				);
		
		return DisplayInfoOverTimeDto.fromDomainChangeDate(output);
	}
	
	public DisplayInfoOverTimeDto calculate(ParamCalculation param) {
		String companyId = param.companyId;
		Optional<GeneralDate> dateOp = Optional.empty();
		if (StringUtils.isNotBlank(param.dateOp)) {
			dateOp = Optional.of(GeneralDate.fromString(param.dateOp, PATTERN_DATE));	
		}
		
		DisplayInfoOverTime output = overtimeService.calculate(
				companyId,
				param.employeeId,
				dateOp,
				EnumAdaptor.valueOf(param.prePostInitAtr, PrePostInitAtr.class),
				param.overtimeLeaveAppCommonSet.toDomain(),
				param.advanceApplicationTime == null ? null : param.advanceApplicationTime.toDomain(),
				param.achieveApplicationTime == null ? null : param.achieveApplicationTime.toDomain(),
				param.workContent.toDomain(),
				param.overtimeAppSetCommand.toDomain(companyId),
				param.agent
				);
		
		
		return DisplayInfoOverTimeDto.fromDomainCalculation(output);
	}
	
	public CheckBeforeOutputDto checkBeforeRegister(ParamCheckBeforeRegister param) {
		CheckBeforeOutput output = null;
		DisplayInfoOverTime displayInfoOverTime = param.displayInfoOverTime.toDomain();
		Application application = param.appOverTime.application.toDomain();
		AppOverTime appOverTime = param.appOverTime.toDomain();
		appOverTime.setApplication(application);
		output = overtimeService.checkErrorRegister(
				param.require,
				param.companyId,
				displayInfoOverTime,
				appOverTime);
		return CheckBeforeOutputDto.fromDomain(output);
	}
	
	public CheckBeforeOutputMultiDto checkErrorRegisterMultiple(ParamCheckBeforeRegister param) {
		CheckBeforeOutputMultiDto output = null;
		DisplayInfoOverTime displayInfoOverTime = param.displayInfoOverTime.toDomain();
		Application application = param.appOverTime.application.toDomain();
		AppOverTime appOverTime = param.appOverTime.toDomain();
		appOverTime.setApplication(application);
		output = CheckBeforeOutputMultiDto.fromDomain(overtimeService.checkErrorRegisterMultiple(
				param.require,
				param.companyId,
				displayInfoOverTime,
				appOverTime));
		return output;
		
	}
	
	public CheckBeforeOutputDto checkBeforeUpdate(ParamCheckBeforeUpdate param) {
		CheckBeforeOutput output = null;
		DisplayInfoOverTime displayInfoOverTime = param.displayInfoOverTime.toDomain();
		Application application = param.appOverTime.application.toDomain(param.displayInfoOverTime.appDispInfoStartup.getAppDetailScreenInfo().getApplication());
		AppOverTime appOverTime = param.appOverTime.toDomain();
		appOverTime.setApplication(application);
		output = overtimeService.checkBeforeUpdate(
				param.require,
				param.companyId,
				appOverTime,
				displayInfoOverTime);
		return CheckBeforeOutputDto.fromDomain(output);
	}
	
	public DetailOutputDto getDetail(ParamDetail param) {
		
		return DetailOutputDto.fromDomain(overtimeService.getDetailData(
				param.companyId,
				param.appId,
				param.appDispInfoStartup.toDomain()));
	}
	
	public BreakTimeZoneSettingDto getBreakTime(ParamBreakTime param) {
		if (!StringUtils.isBlank(param.workTypeCode) && !StringUtils.isBlank(param.workTimeCode)) {
			return BreakTimeZoneSettingDto.fromDomain(commonAlgorithmOverTime.selectWorkTypeAndTime(
					param.companyId,
					Optional.ofNullable(param.workTypeCode).map(x -> new WorkTypeCode(x)).orElse(null),
					Optional.ofNullable(param.workTimeCode).map(x -> new WorkTimeCode(x)).orElse(null),
					param.startTime == null ? Optional.empty() : Optional.of(new TimeWithDayAttr(param.startTime)),
							param.endTime == null ? Optional.empty() : Optional.of(new TimeWithDayAttr(param.endTime)),
									CollectionUtil.isEmpty(param.actualContentDisplayDtos) ? Optional.empty() : 
										(param.actualContentDisplayDtos.get(0).getOpAchievementDetail() == null ?
												Optional.empty() : Optional.ofNullable(param.actualContentDisplayDtos.get(0).getOpAchievementDetail().toDomain()))
					));
			
		} else {
			return null;
		}
	}	
	
	
	

	/**
	 * Mobile
	 */
	public DisplayInfoOverTimeMobileDto startMobile(ParamStartMobile param) {
		Optional<GeneralDate> dateOp = Optional.empty();
		if (StringUtils.isNotBlank(param.dateOptional)) {
			dateOp = Optional.of(GeneralDate.fromString(param.dateOptional, PATTERN_DATE));	
		}
		DisplayInfoOverTimeMobile output = overtimeService.startMobile(
				param.mode,
				param.companyId,
				StringUtils.isBlank(param.employeeIdOptional) ? Optional.empty() : Optional.of(param.employeeIdOptional),
				dateOp,
				param.disOptional == null ? Optional.empty() : Optional.of(param.disOptional.toDomain()),
				param.appOptional == null ? Optional.empty() : Optional.of(param.appOptional.toDomain()),
				param.appDispInfoStartupOutput.toDomain(),
				EnumAdaptor.valueOf(param.overtimeAppAtr, OvertimeAppAtr.class),
				param.agent
				);
		return DisplayInfoOverTimeMobileDto.fromDomain(output);
	}
	
	public DisplayInfoOverTimeDto changeDateMobile(ParamChangeDateMobile param) {
		GeneralDate date = null;
		if (StringUtils.isNotBlank(param.date)) {
			date = GeneralDate.fromString(param.date, PATTERN_DATE);
		}
		DisplayInfoOverTime output = overtimeService.changeDateMobile(
				param.companyId,
				date,
				param.displayInfoOverTime.toDomain());
		return DisplayInfoOverTimeDto.fromDomain(output);
	}
	
	public SelectWorkOutputDto selectWorkInfoMobile(ParamSelectWorkMobile param) {
		Optional<GeneralDate> dateOp = Optional.empty();
		if (StringUtils.isNotBlank(param.dateOp)) {
			dateOp = Optional.of(GeneralDate.fromString(param.dateOp, PATTERN_DATE));	
		}
		SelectWorkOutput output = overtimeService.selectWork(
				param.companyId,
				param.employeeId,
				dateOp,
				new WorkTypeCode(param.workTypeCode),
				new WorkTimeCode(param.workTimeCode),
				Optional.ofNullable(param.startTimeSPR),
				Optional.ofNullable(param.endTimeSPR),
				param.actualContentDisplay == null ? Optional.empty() :	Optional.of(param.actualContentDisplay.toDomain()),
				param.overtimeAppSet.toDomain(param.companyId));
		
		return SelectWorkOutputDto.fromDomain(output);
	}
	
	public List<ConfirmMsgOutput> checkBeforeInsert(ParamCheckBeforeRegister param) {
		Application application = param.appOverTime.application.toDomain();
		AppOverTime appOverTime = param.appOverTime.toDomain();
		appOverTime.setApplication(application);
		return overtimeService.checkBeforeInsert(
				param.require,
				param.companyId,
				appOverTime,
				param.displayInfoOverTime.toDomain());
	}
	
	public DisplayInfoOverTimeDto calculateMobile(ParamCalculateMobile param) {
		String companyId = param.companyId;
		Optional<GeneralDate> dateOp = Optional.empty();
		if (StringUtils.isNotBlank(param.dateOp)) {
			dateOp = Optional.of(GeneralDate.fromString(param.dateOp, PATTERN_DATE));	
		}
		Application application;
		AppOverTime appOverTime;
		if (param.mode) {
			application = param.appOverTimeInsert.application.toDomain();
			appOverTime = param.appOverTimeInsert.toDomain();
		} else {
			application = param.appOverTimeUpdate.application.toDomain(param.displayInfoOverTime.appDispInfoStartup.getAppDetailScreenInfo().getApplication());
			appOverTime = param.appOverTimeUpdate.toDomain();
		}
		
		appOverTime.setApplication(application);
		
		DisplayInfoOverTime output = overtimeService.calculateMobile(
				companyId,
				param.displayInfoOverTime.toDomain(),
				appOverTime,
				param.mode,
				param.employeeId,
				dateOp,
				false);
		
		
		return DisplayInfoOverTimeDto.fromDomainCalculation(output);
	}
}
