package nts.uk.file.at.app.export.schedule.personalschedulebydate;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.primitive.PrimitiveValueBase;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.dailyperform.dto.TimeSpanForCalcDto;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.TimezoneToUseHourlyHoliday;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimeVacation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.GetListWtypeWtimeUseDailyAttendRecordService;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flexset.GetTimezoneOfCoreTimeService;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.service.WorkTimeForm;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.AttendanceDayAttr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.file.at.app.export.schedule.personalschedulebydate.dto.EmployeeWorkScheduleResultDto;
import nts.uk.screen.at.app.ksu003.start.dto.*;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 日付別予定情報を取得する
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU003_個人スケジュール修正(日付別).D：出力の設定.メニュー別OCD.日付別予定情報を取得する.日付別予定情報を取得する
 */
@Stateless
public class ScheduleInformationByDateFileQuery {

    @Inject
    private WorkTypeRepository workTypeRepo;

    @Inject
    private WorkTimeSettingRepository workTimeSettingRepo;

    @Inject
    private FlexWorkSettingRepository flexWorkSet;

    @Inject
    private PredetemineTimeSettingRepository predetemineTimeSet;

    @Inject
    private FixedWorkSettingRepository fixedWorkSettingRepo;

    public List<EmployeeWorkScheduleResultDto> get(List<IntegrationOfDaily> lstIntegrationOfDaily, boolean graphVacationDisplay,
                                                   boolean doubleWorkDisplay) {
        if (lstIntegrationOfDaily.isEmpty()) return Collections.emptyList();

        String companyId = AppContexts.user().companyId();
        List<EmployeeWorkScheduleResultDto> empWorkScheduleResults = new ArrayList<>();

        // 日別勤怠の勤務情報リスト = List<日別勤怠(Work)>．values: List $.勤務情報
        val lstWorkInfoOfDailyAttendance = lstIntegrationOfDaily.stream().map(IntegrationOfDaily::getWorkInformation)
                .collect(Collectors.toList());
        // 1. 取得(List<日別勤怠の勤務情報>): input.日別勤怠の勤務情報 , output 日別勤怠の実績で利用する勤務種類と就業時間帯
        val workTypeWorkTimeList = GetListWtypeWtimeUseDailyAttendRecordService.getdata(lstWorkInfoOfDailyAttendance);

        // 2. get(会社ID, 日別勤怠の実績で利用する勤務種類と就業時間帯.勤務種類リスト) : output WorkType
        List<WorkType> workTypeList = workTypeRepo.findByCidAndWorkTypeCodes(
                companyId,
                workTypeWorkTimeList.getLstWorkTypeCode().stream().map(PrimitiveValueBase::v).collect(Collectors.toList()));

        // 3. get(会社ID,List<就業時間帯コード>): output WorkTimeSetting
        List<WorkTimeSetting> workTimeSettingList = workTimeSettingRepo.getListWorkTimeSetByListCode(
                companyId,
                workTypeWorkTimeList.getLstWorkTimeCode().stream().map(PrimitiveValueBase::v).collect(Collectors.toList())
        );

        // 4. loop：日別勤怠(Work) in input.List<日別勤怠(Work)>
        for (IntegrationOfDaily dailyInfo : lstIntegrationOfDaily) {
            // 勤務種類コード = 日別勤怠(Work)．勤務情報．勤務情報．勤務種類コード
            val workTypeCode = dailyInfo.getWorkInformation().getRecordInfo().getWorkTypeCode().v();
            // 就業時間帯コード = 日別勤怠(Work)．勤務情報．勤務情報．就業時間帯コード
            val workTimeCode = dailyInfo.getWorkInformation().getRecordInfo().getWorkTimeCode() != null ? dailyInfo.getWorkInformation().getRecordInfo().getWorkTimeCode().v() : null;

            val workTypeOpt = workTypeList.stream().filter(x -> x.getWorkTypeCode().equals(workTypeCode)).findFirst();
            Optional<WorkTimeSetting> workTimeSetOpt = workTimeCode != null ? workTimeSettingList.stream().filter(wt -> wt.getWorktimeCode().equals(workTimeCode)).findFirst() : Optional.empty();

            // 4.1. 勤務形態を取得する() : return 就業時間帯の勤務形態
            WorkTimeForm workTimeForm = workTimeSetOpt.isPresent() ? workTimeSetOpt.get().getWorkTimeDivision().getWorkTimeForm() : null;

            // 4.2. 就業時間帯の勤務形態 == フレックス勤務用: コアタイム時間帯を取得する
            Optional<TimeSpanForCalc> coreTimeOpt = Optional.empty();
            if (workTimeForm != null && workTimeForm == WorkTimeForm.FLEX) {
                RequireTimezoneOfCoreImpl requireImpl = new RequireTimezoneOfCoreImpl(workTypeRepo, flexWorkSet, predetemineTimeSet);
                // コアタイム時間帯を取得する.取得する(Require, 勤務種類コード, 就業時間帯コード): INPUT: require, 日別勤怠(Work)．勤務情報．勤務情報．勤務種類コード, 日別勤怠(Work)．勤務情報．勤務情報．就業時間帯コード : OUTPUT Optional<計算時間帯>
                coreTimeOpt = GetTimezoneOfCoreTimeService.get(requireImpl, new WorkTypeCode(workTypeCode), new WorkTimeCode(workTimeCode));
            }

            // 4.3. 就業時間帯の勤務形態 == 固定勤務
            List<TimeSpanForCalc> lstOver = new ArrayList<>();
            if (workTimeForm != null && workTimeForm == WorkTimeForm.FIXED) {
                // 4.3.1. get(会社ID, 就業時間帯コード): return Optional<固定勤務設定>
                val fixedWorkSettingOpt = fixedWorkSettingRepo.findByKey(companyId, workTimeCode);

                // 4.3.2. 1日半日出勤・1日休日系の判定（休出判定あり）() : output 出勤日区分
                AttendanceDayAttr dayAttr = workTypeOpt.get().chechAttendanceDay();

                // 4.3.3. 固定勤務設定.isPresent: 指定した午前午後区分の残業時間帯を取得する(午前午後区分): param 出勤日区分.午前午後区分に変換() : return List<計算用時間帯>
                if (fixedWorkSettingOpt.isPresent()) {
                    lstOver = fixedWorkSettingOpt.get().getTimeZoneOfOvertimeWorkByAmPmAtr(dayAttr.toAmPmAtr().get());
                }
            }

            // 4.4. グラフ休暇表示==true: 時間休暇を取得する() : return Map<時間休暇種類, 時間休暇>
            Map<TimezoneToUseHourlyHoliday, TimeVacation> timeVacationMap = graphVacationDisplay ? dailyInfo.getTimeVacation() : new HashMap<>();
            List<TimeVacationAndTypeDto> timeVacationList = new ArrayList<>();
            if (!timeVacationMap.isEmpty()) {
                // ※存在しない場合はempty
                timeVacationList = timeVacationMap.entrySet().stream().map(x ->
                        new TimeVacationAndTypeDto(x.getKey().value, new TimeVacationDto(
                                x.getValue().getTimeList().stream().map(y -> new TimeSpanForCalcDto(y.getStart().v(), y.getEnd().v())).collect(Collectors.toList()),
                                new DailyAttdTimeVacationDto(x.getValue().getUseTime().getTimeAnnualLeaveUseTime().v(),
                                        x.getValue().getUseTime().getTimeCompensatoryLeaveUseTime().v(),
                                        x.getValue().getUseTime().getSixtyHourExcessHolidayUseTime().v(),
                                        x.getValue().getUseTime().getTimeSpecialHolidayUseTime().v(),
                                        x.getValue().getUseTime().getSpecialHolidayFrameNo().isPresent() ? x.getValue().getUseTime().getSpecialHolidayFrameNo().get().v() : null,
                                        x.getValue().getUseTime().getTimeChildCareHolidayUseTime().v(),
                                        x.getValue().getUseTime().getTimeCareHolidayUseTime().v())))).collect(Collectors.toList());
            }

            // 4.5. create()
            String employeeId = dailyInfo.getEmployeeId();
            GeneralDate date = dailyInfo.getYmd();
            Integer startTime1 = null, startTime2 = null, endTime1 = null, endTime2 = null;

            // List<育児介護短時間帯>= 日別勤怠(Work)．短時間勤務時間帯．時間帯 : ※項目の値は存在しない場合はempty
            List<TimeShortDto> shortWorkingTimeSheets = !dailyInfo.getShortTime().isPresent()
                    ? Collections.emptyList()
                    : dailyInfo.getShortTime().get().getShortWorkingTimeSheets().stream().map(x -> new TimeShortDto(
                    x.getStartTime().v(),
                    x.getEndTime().v(),
                    x.getChildCareAttr().value,
                    x.getShortWorkTimeFrameNo().v())).collect(Collectors.toList());

            // List＜休憩時間帯＞＝日別勤怠(Work)．日別勤怠の休憩時間帯．時間帯
            List<BreakTimeSheet> breakTimeSheets = dailyInfo.getBreakTime() != null ? dailyInfo.getBreakTime().getBreakTimeSheets() : Collections.emptyList();

            // 開始時刻 1= 日別勤怠(Work)．出退勤．出退勤．出勤  : ※勤務NO = 1のもの。
            // 終了時刻 1= 日別勤怠(Work)．出退勤．出退勤．退勤  : ※勤務NO = 1のもの。
            val timeLeavingWork1 = dailyInfo.getAttendanceLeave().get().getTimeLeavingWorks()
                    .stream().filter(x -> x.getWorkNo().v() == 1).findFirst();
            if (timeLeavingWork1.isPresent()) {
                startTime1 = timeLeavingWork1.get().getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v();
                endTime1 = timeLeavingWork1.get().getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v();
            }

            // 開始時刻 2= 日別勤怠(Work)．出退勤．出退勤．出勤  : ※勤務NO = 2のもの。, ２回勤務表示==false場合はempty
            // 終了時刻 2= 日別勤怠(Work)．出退勤．出退勤．退勤  : ※勤務NO = 2のもの。
            val timeLeavingWork2 = dailyInfo.getAttendanceLeave().get().getTimeLeavingWorks()
                    .stream().filter(x -> x.getWorkNo().v() == 2).findFirst();
            if (timeLeavingWork2.isPresent() && doubleWorkDisplay) {
                startTime2 = timeLeavingWork2.get().getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v();
                endTime2 = timeLeavingWork2.get().getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v();
            }

            // 勤務種類名称= 勤務種類．勤務種類略名
            String workTypeName = workTypeOpt.get().getAbbreviationName().v();

            // 就業時間帯名称 = 就業時間帯の設定．表示名．略名
            String workTimeName = workTimeSetOpt.isPresent() ? workTimeSetOpt.get().getWorkTimeDisplayName().getWorkTimeAbName().v() : null;

            // 勤務タイプ = 就業時間帯の設定.勤務区分.勤務形態を取得する()
            Integer workType = workTimeForm != null ? workTimeForm.value : null;

            // コア開始時刻 = Optional<計算時間帯>．開始時刻 : ※4.2で取得したもの。
            Integer coreStartTime = coreTimeOpt.map(TimeSpanForCalc::start).orElse(null);

            // コア終了時刻 = Optional<計算時間帯>．終了時刻 : ※4.2で取得したもの。
            Integer coreEndTime = coreTimeOpt.map(TimeSpanForCalc::end).orElse(null);

            // 就業時間合計=日別勤怠(Work).勤怠時間.勤務時間.総労働時間.所定内時間.就業時間
            Integer totalWorkingHours = dailyInfo.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
                    .getTotalWorkingTime().getWithinStatutoryTimeOfDaily().getWorkTime().v();

            // 休憩時間合計=日別勤怠(Work).勤怠時間.勤務時間.総労働時間.休憩時間.計上用合計時間.合計時間.時間
            Integer totalBreakTime = dailyInfo.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
                    .getTotalWorkingTime().getBreakTimeOfDaily().getToRecordTotalTime() != null ? dailyInfo.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
                    .getTotalWorkingTime().getBreakTimeOfDaily().getToRecordTotalTime().getTotalTime().getTime().v() : null;

            // List<残業時間帯>
            List<ChangeableWorkTimeDto> overTimeSheets = workTimeForm != null ? this.getOverTimeSheets(workTimeForm, lstOver) : Collections.emptyList();

            empWorkScheduleResults.add(new EmployeeWorkScheduleResultDto(
                    date,
                    employeeId,
                    breakTimeSheets,          // breakTimeList
                    Collections.emptyList(),  // actualBreakTimeList
                    overTimeSheets,           // overTimeList
                    shortWorkingTimeSheets,
                    timeVacationList,         // timeVacationMap
                    coreStartTime,
                    coreEndTime,
                    totalBreakTime,
                    workType,
                    workTypeCode,
                    workTypeName,
                    null,
                    null,
                    null,
                    null,
                    totalWorkingHours,
                    workTimeCode,
                    workTimeName,
                    startTime1,
                    endTime1,
                    startTime2,
                    endTime2
            ));
        }
        return empWorkScheduleResults.stream().sorted(Comparator.comparing(EmployeeWorkScheduleResultDto::getEmployeeId)).collect(Collectors.toList());
    }

    private List<ChangeableWorkTimeDto> getOverTimeSheets(WorkTimeForm form, List<TimeSpanForCalc> lstOver) {
        List<ChangeableWorkTimeDto> lstOverTime = new ArrayList<>();
        switch (form) {
            case FLEX: //フレックス勤務
            case FLOW: //流動勤務
                return lstOverTime;
            case FIXED:
                for (int i = 1; i <= lstOver.size(); i++) {
                    lstOverTime.add(new ChangeableWorkTimeDto(i, lstOver.get(i - 1).getStart().v(), lstOver.get(i - 1).getEnd().v()));
                }
                return lstOverTime;
            default:
                break;
        }

        return lstOverTime;
    }

    @AllArgsConstructor
    public static class RequireTimezoneOfCoreImpl implements GetTimezoneOfCoreTimeService.Require {

        private final String companyId = AppContexts.user().companyId();

        private WorkTypeRepository workTypeRepo;

        private FlexWorkSettingRepository flexWorkSet;

        private PredetemineTimeSettingRepository predetemineTimeSet;

        @Override
        public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
            Optional<PredetemineTimeSetting> workSetting = predetemineTimeSet.findByWorkTimeCode(companyId, wktmCd.v());
            return workSetting.orElse(null);
        }

        @Override
        public Optional<WorkType> getWorkType(WorkTypeCode workTypeCode) {
            return workTypeRepo.findByPK(companyId, workTypeCode.v());
        }

        @Override
        public Optional<FlexWorkSetting> getFlexWorkSetting(WorkTimeCode workTimeCode) {
            return flexWorkSet.find(companyId, workTimeCode.v());
        }
    }
}
