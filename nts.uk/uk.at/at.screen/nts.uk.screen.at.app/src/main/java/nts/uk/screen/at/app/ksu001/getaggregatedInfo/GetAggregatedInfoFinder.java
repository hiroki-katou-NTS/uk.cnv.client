/**
 * 
 */
package nts.uk.screen.at.app.ksu001.getaggregatedInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DateInMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;
import nts.uk.ctx.at.schedule.app.find.budget.external.ExternalBudgetDto;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.screen.at.app.ksu001.aggreratedinformation.AggregatedInformationDto;
import nts.uk.screen.at.app.ksu001.aggreratedinformation.ScreenQueryAggregatedInformation;
import nts.uk.screen.at.app.ksu001.displayinshift.ShiftMasterMapWithWorkStyle;
import nts.uk.screen.at.app.ksu001.getworkscheduleshift.ScheduleOfShiftDto;
import nts.uk.screen.at.app.ksu001.processcommon.ScreenQueryCreateWorkSchedule;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleShiftBaseResult;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto;
import nts.uk.screen.at.app.ksu001.processcommon.nextorderdschedule.PlanAndActual;
import nts.uk.screen.at.app.ksu001.processcommon.nextorderdschedule.ScreenQueryPlanAndActual;
import nts.uk.screen.at.app.ksu001.processcommon.nextorderdschedule.ScreenQueryWorkScheduleShift;
import nts.uk.screen.at.app.ksu001.start.AggregatePersonalMapDto;
import nts.uk.screen.at.app.ksu001.start.AggregateWorkplaceMapDto;
import nts.uk.screen.at.app.ksu001.start.ExternalBudgetMapDto;
import nts.uk.screen.at.app.ksu001.start.ExternalBudgetMapDtoList;

/**
 * @author laitv
 *
 */
@Stateless
public class GetAggregatedInfoFinder {

	@Inject
	private ScreenQueryAggregatedInformation screenQAggreratedInfo;
	
	@Inject
	private ScreenQueryPlanAndActual screenQueryPlanAndActual;
	
	@Inject
	private ScreenQueryCreateWorkSchedule screenQueryCreateWorkSchedule;
	
	@Inject
	private ScreenQueryWorkScheduleShift screenQueryWorkScheduleShift;
	
	private static final String DATE_FORMAT = "yyyy/MM/dd";

	public AggregatedInformationRs getData(AggregatedInfoParam param) {
		DatePeriod datePeriod = new DatePeriod(GeneralDate.fromString(param.startDate, DATE_FORMAT), GeneralDate.fromString(param.endDate, DATE_FORMAT));
		DateInMonth closeDate = new DateInMonth(param.day, param.isLastDay);
		Boolean isAchievement = param.getActualData;
		Optional<PersonalCounterCategory> personalCounterOp = StringUtil.isNullOrEmpty(param.personTotalSelected, true) ? Optional.empty() : Optional.of(PersonalCounterCategory.of(Integer.valueOf(param.personTotalSelected)));
		Optional<WorkplaceCounterCategory> workplaceCounterOp = StringUtil.isNullOrEmpty(param.workplaceSelected, true) ? Optional.empty() : Optional.of(WorkplaceCounterCategory.of(Integer.valueOf(param.workplaceSelected)));
		TargetOrgIdenInfor targetOrgIdenInfor = null;
		if (param.unit == TargetOrganizationUnit.WORKPLACE.value) {
			targetOrgIdenInfor = new TargetOrgIdenInfor(TargetOrganizationUnit.WORKPLACE,Optional.of(param.workplaceId == null ? param.workplaceId : param.workplaceId), Optional.empty());
		} else {
			targetOrgIdenInfor = new TargetOrgIdenInfor(TargetOrganizationUnit.WORKPLACE_GROUP, Optional.empty(),Optional.of(param.workplaceGroupId == null ? param.workplaceGroupId : param.workplaceGroupId));
		}
		
		AggregatedInformationDto dto = screenQAggreratedInfo.get(param.listSid, datePeriod, closeDate, isAchievement, targetOrgIdenInfor, personalCounterOp, workplaceCounterOp, param.isShiftMode);
		
		List<WorkScheduleWorkInforDto> workScheduleWorkInfors = new ArrayList<>();
		List<ShiftMasterMapWithWorkStyle> shiftMasterWithWorkStyleLst = new ArrayList<>();
		WorkScheduleShiftBaseResult workScheduleShiftBaseResult = new WorkScheduleShiftBaseResult(new ArrayList<>(), new HashMap<>());
		PlanAndActual planAndActual = screenQueryPlanAndActual.getPlanAndActual(param.listSid, datePeriod, param.getActualData);
		if (param.getWorkschedule) {
			if (!param.isShiftMode) {

				workScheduleWorkInfors = screenQueryCreateWorkSchedule.get(planAndActual.getSchedule(),
						planAndActual.getDailySchedule(), param.getActualData);
			} else {

				workScheduleShiftBaseResult = screenQueryWorkScheduleShift.create(param.getListShiftMasterNotNeedGetNew(),
						planAndActual.getSchedule(), planAndActual.getDailySchedule(), param.getActualData);
				
				if(!workScheduleShiftBaseResult.mapShiftMasterWithWorkStyle.isEmpty()){
					workScheduleShiftBaseResult.mapShiftMasterWithWorkStyle.forEach((key, value) -> {
						shiftMasterWithWorkStyleLst.add(new ShiftMasterMapWithWorkStyle(key, value == null || !value.isPresent() ? null : String.valueOf(value.get().value)));
					});
				}
			}
		}
		return convertData(dto, workScheduleWorkInfors, workScheduleShiftBaseResult.listWorkScheduleShift, shiftMasterWithWorkStyleLst);
	}
	
	private AggregatedInformationRs convertData(AggregatedInformationDto dto, 
			List<WorkScheduleWorkInforDto> workScheduleWorkInfors, 
			List<ScheduleOfShiftDto> listWorkScheduleShift,
			List<ShiftMasterMapWithWorkStyle> shiftMasterWithWorkStyleLst) {
		return new AggregatedInformationRs(
				convertExternalBudget(dto.externalBudget), 
				dto.aggrerateSchedule.aggreratePersonal == null ? null : AggregatePersonalMapDto.convertMap(dto.aggrerateSchedule.aggreratePersonal), 
				dto.aggrerateSchedule.aggrerateWorkplace == null ? null : AggregateWorkplaceMapDto.convertMap(dto.aggrerateSchedule.aggrerateWorkplace),
				workScheduleWorkInfors,
				listWorkScheduleShift,
				shiftMasterWithWorkStyleLst);
		
	}

	private List<ExternalBudgetMapDtoList> convertExternalBudget(
			Map<GeneralDate, Map<ExternalBudgetDto, String>> externalBudget) {
		
		if (externalBudget.isEmpty()) {
			return new ArrayList<ExternalBudgetMapDtoList>();
		}

		return externalBudget.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().entrySet().stream()
						.map(x -> new ExternalBudgetMapDto(
								x.getKey().getExternalBudgetCode(),
								x.getKey().getExternalBudgetName(), 
								x.getValue(),
								x.getKey().getBudgetAtr(),
								x.getKey().getUnitAtr()))
						.collect(Collectors.toList())))
				.entrySet().stream().map(x -> new ExternalBudgetMapDtoList(x.getKey(), x.getValue()))
				.collect(Collectors.toList());
	}
}
