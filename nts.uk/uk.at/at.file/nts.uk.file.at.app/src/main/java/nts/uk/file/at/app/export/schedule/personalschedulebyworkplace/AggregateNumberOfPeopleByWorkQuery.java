package nts.uk.file.at.app.export.schedule.personalschedulebyworkplace;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter.CountNumberOfPeopleByEachWorkMethodService;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter.NumberOfPeopleByEachWorkMethod;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterRepository;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.dto.ShiftMasterDto;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

/**
 * 勤務方法ごとに人数を集計する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AggregateNumberOfPeopleByWorkQuery {
    @Inject
    private ShiftMasterRepository shiftMasterRepo;

    @Inject
    private WorkTimeSettingRepository workTimeSettingRepo;
    
	@Inject
	private WorkTypeRepository workTypeRepository;

    /**
     * 集計する
     */
    public Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<CodeNameValue>>> get(TargetOrgIdenInfor targetOrg, DatePeriod period, List<IntegrationOfDaily> schedules, List<IntegrationOfDaily> records, boolean shiftDisplay) {
        String companyId = AppContexts.user().companyId();
        CountNumberOfPeopleByEachWorkMethodService.Require require = new CountNumberOfPeopleByEachWorkMethodService.Require() {
            @Override
            public Optional<ShiftMaster> getShiftMaster(WorkInformation workInformation) {
                return shiftMasterRepo.getByWorkTypeAndWorkTime(companyId, workInformation.getWorkTypeCode().v(), workInformation.getWorkTimeCodeNotNull().map(i -> i.v()).orElse(null));
            }

			@Override
			public List<WorkType> getWorkTypes(List<WorkTypeCode> workTypeCodes) {
				return workTypeRepository.getPossibleWorkType(
						AppContexts.user().companyId()
					,	workTypeCodes.stream()
									.map(c -> c.v())
									.collect(Collectors.toList())
						);
			}
        };

        if (shiftDisplay) {
            Map<String, ShiftMasterDto> shiftMasterMap = shiftMasterRepo.getAllByCid(companyId).stream().collect(Collectors.toMap(ShiftMasterDto::getShiftMasterCode, Function.identity()));
            Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<CodeNameValue>>> shiftResult = CountNumberOfPeopleByEachWorkMethodService.getByShift(
                    require,
                    targetOrg,
                    period,
                    schedules,
                    records
            ).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(i -> new NumberOfPeopleByEachWorkMethod<>(
                    new CodeNameValue(
                            i.getWorkMethod().v(),
                            shiftMasterMap.get(i.getWorkMethod().v()) != null
                                    ? shiftMasterMap.get(i.getWorkMethod().v()).getShiftMasterName()
                                    : i.getWorkMethod().v() + TextResource.localize("KSU001_22")
                    ),
                    i.getPlanNumber(),
                    i.getScheduleNumber(),
                    i.getActualNumber()
            )).collect(Collectors.toList())));
            return shiftResult;
        } else {
            Map<String, WorkTimeSetting> workTimeSettingMap = workTimeSettingRepo.findActiveItems(companyId).stream().collect(Collectors.toMap(i -> i.getWorktimeCode().v(), Function.identity()));
            Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<CodeNameValue>>> workResult = CountNumberOfPeopleByEachWorkMethodService.getByWorkTime(
                    require,
                    targetOrg,
                    period,
                    schedules,
                    records
            ).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(i -> new NumberOfPeopleByEachWorkMethod<>(
                    new CodeNameValue(
                            i.getWorkMethod().v(),
                            workTimeSettingMap.get(i.getWorkMethod().v()) != null
                                    ? workTimeSettingMap.get(i.getWorkMethod().v()).getWorkTimeDisplayName().getWorkTimeAbName().v()
                                    : i.getWorkMethod().v() + TextResource.localize("KSU001_22")
                    ),
                    i.getPlanNumber(),
                    i.getScheduleNumber(),
                    i.getActualNumber()
            )).collect(Collectors.toList())));
            return workResult;
        }
    }
}
