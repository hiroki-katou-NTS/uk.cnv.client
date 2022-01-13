package nts.uk.file.at.app.export.schedule.personalscheduleindividual;

import lombok.val;
import nts.arc.primitive.PrimitiveValueBase;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.GetListWtypeWtimeUseDailyAttendRecordService;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.file.at.app.export.schedule.personalscheduleindividual.dto.WorkScheduleWorkInforDto;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 勤務予定で勤務予定（勤務情報）dtoを作成する
 */
@Stateless
public class CreateWorkScheduleDtoQuery {
    @Inject
    private WorkTypeRepository workTypeRepository;
    @Inject
    private WorkTimeSettingRepository workTimeSettingRepository;
    @Inject
    private BasicScheduleService basicScheduleService;
    @Inject
    private WorkTypeRepository workTypeRepo;
    @Inject
    private FixedWorkSettingRepository fixedWorkSet;
    @Inject
    private FlowWorkSettingRepository flowWorkSet;
    @Inject
    private FlexWorkSettingRepository flexWorkSet;
    @Inject
    private PredetemineTimeSettingRepository predetemineTimeSet;

    /**
     * 取得する     *
     *
     * @param dailyListAttendanceWork
     * @return DatePeriodListDto
     */
    public List<WorkScheduleWorkInforDto> get(List<IntegrationOfDaily> dailyListAttendanceWork) {
        if (dailyListAttendanceWork.isEmpty()) return Collections.emptyList();
        String companyId = AppContexts.user().companyId();
        val workInfoOfDailyAttendanceList = dailyListAttendanceWork
                .stream().map(IntegrationOfDaily::getWorkInformation).collect(Collectors.toList());

       // 1.日別勤怠の実績で利用する勤務種類と就業時間帯
        val lstWrkTypeWrkTimeDailyAtt = GetListWtypeWtimeUseDailyAttendRecordService.getdata(workInfoOfDailyAttendanceList);

        // 2.廃止された勤務種類
        val workTypeList = workTypeRepository.findByCidAndWorkTypeCodes(
                companyId,
                lstWrkTypeWrkTimeDailyAtt.getLstWorkTypeCode().stream().map(PrimitiveValueBase::v).collect(Collectors.toList()));

        // 3.就業時間帯も取得する
        val workTimeSettingList = workTimeSettingRepository.getListWorkTime(
                companyId,
                lstWrkTypeWrkTimeDailyAtt.getLstWorkTimeCode().stream().map(PrimitiveValueBase::v).collect(Collectors.toList())
        );

        List<WorkScheduleWorkInforDto> workInforDtoList = new ArrayList<>();
        //loop：日別勤怠(Work) in input.List<日別勤怠(Work)>
        for (val ntegrationOfDaily : dailyListAttendanceWork) {
            // 4.1出勤・休日系の判定(@Require)
            val workStyle = ntegrationOfDaily.getWorkInformation().getRecordInfo().getWorkStyle(new WorkInformation.Require() {
                @Override
                public Optional<WorkType> getWorkType(String workTypeCd) {
                    return workTypeRepo.findByPK(companyId, workTypeCd);
                }

                @Override
                public Optional<WorkTimeSetting> getWorkTime(String workTimeCode) {
                    return workTimeSettingRepository.findByCode(companyId, workTimeCode);
                }

                @Override
                public SetupType checkNeededOfWorkTimeSetting(String workTypeCode) {
                    return basicScheduleService.checkNeededOfWorkTimeSetting(workTypeCode);
                }

                @Override
                public FixedWorkSetting getWorkSettingForFixedWork(WorkTimeCode code) {
                    Optional<FixedWorkSetting> workSetting = fixedWorkSet.findByKey(companyId, code.v());
                    return workSetting.isPresent() ? workSetting.get() : null;
                }

                @Override
                public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
                    Optional<FlowWorkSetting> workSetting = flowWorkSet.find(companyId, code.v());
                    return workSetting.isPresent() ? workSetting.get() : null;
                }

                @Override
                public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
                    Optional<FlexWorkSetting> workSetting = flexWorkSet.find(companyId, code.v());
                    return workSetting.isPresent() ? workSetting.get() : null;
                }

                @Override
                public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
                    Optional<PredetemineTimeSetting> workSetting = predetemineTimeSet.findByWorkTimeCode(companyId, wktmCd.v());
                    return workSetting.isPresent() ? workSetting.get() : null;
                }
            });


            // 勤務種類コード = 日別勤怠(Work)．勤務情報．勤務情報．勤務種類コード
            val workTypeCode = ntegrationOfDaily.getWorkInformation().getRecordInfo().getWorkTypeCode().v();
            val workTypeOpt = workTypeList.stream().filter(x -> x.getWorkTypeCode().v().equals(workTypeCode)).findFirst();
            // 勤務種類名= 勤務種類．勤務種類略名
            String workTypeName = workTypeOpt.isPresent() ? workTypeOpt.get().getAbbreviationName().v() : "";

            // 就業時間帯コード = 日別勤怠(Work)．勤務情報．勤務情報．就業時間帯コード
            val workTimeCode = ntegrationOfDaily.getWorkInformation().getRecordInfo().getWorkTimeCode() != null
                    ? ntegrationOfDaily.getWorkInformation().getRecordInfo().getWorkTimeCode().v()
                    : "";
//            if (StringUtils.isEmpty(workTimeCode)) continue;
            val workTimeOpt = workTimeSettingList.stream().filter(x -> x.getWorktimeCode().equals(workTimeCode)).findFirst();
            // 就業時間帯名= 就業時間帯の設定．表示名．略名
            val worTimeName = workTimeOpt.isPresent() ? workTimeOpt.get().getWorkTimeDisplayName().getWorkTimeAbName().v() : "";

            Integer startTime = null;
            if (ntegrationOfDaily.getAttendanceLeave().isPresent()) {
                Optional<TimeLeavingWork> timeLeavingWork = ntegrationOfDaily.getAttendanceLeave().get().getTimeLeavingWorks().stream().filter(i -> i.getWorkNo().v() == 1).findFirst();
                if (timeLeavingWork.isPresent()) {
                    if (timeLeavingWork.get().getAttendanceStamp().isPresent()) {
                        if (timeLeavingWork.get().getAttendanceStamp().get().getStamp().isPresent()) {
                            if (timeLeavingWork.get().getAttendanceStamp().get().getStamp().get().getTimeDay() != null) {
                                if (timeLeavingWork.get().getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()) {
                                    startTime = timeLeavingWork.get().getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v();
                                }
                            }
                        }
                    }
                }
            }

            Integer endTime = null;
            if (ntegrationOfDaily.getAttendanceLeave().isPresent()) {
                val timeLeavingWork = ntegrationOfDaily.getAttendanceLeave().get().getTimeLeavingWorks().stream().filter(i -> i.getWorkNo().v() == 1).findFirst();
                if (timeLeavingWork.isPresent()) {
                    if (timeLeavingWork.get().getLeaveStamp().isPresent()) {
                        if (timeLeavingWork.get().getLeaveStamp().get().getStamp().isPresent()) {
                            if (timeLeavingWork.get().getLeaveStamp().get().getStamp().get().getTimeDay() != null) {
                                if (timeLeavingWork.get().getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()) {
                                    endTime = timeLeavingWork.get().getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v();
                                }
                            }
                        }
                    }
                }
            }
            workInforDtoList.add(
                    new WorkScheduleWorkInforDto(
                            ntegrationOfDaily.getYmd(),
                            workStyle,
                            Optional.of(workTypeCode),
                            Optional.of(workTypeName),
                            Optional.of(workTimeCode),
                            Optional.of(worTimeName),
                            Optional.ofNullable(startTime),
                            Optional.ofNullable(endTime)
                    )
            );

        }
        return workInforDtoList;
    }
}
