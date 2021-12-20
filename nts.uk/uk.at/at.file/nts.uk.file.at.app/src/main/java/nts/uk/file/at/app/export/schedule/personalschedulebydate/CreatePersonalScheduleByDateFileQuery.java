package nts.uk.file.at.app.export.schedule.personalschedulebydate;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.aggregation.dom.common.ScheRecGettingAtr;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.file.at.app.export.schedule.personalschedulebydate.dto.EmployeeWorkScheduleResultDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 個人スケジュール表(日付別)を作成する
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU003_個人スケジュール修正(日付別).D：出力の設定.メニュー別OCD.個人スケジュール表(日付別)を作成する.個人スケジュール表(日付別)を作成する
 */
@Stateless
public class CreatePersonalScheduleByDateFileQuery {

    @Inject
    private BasicInfoPersonalScheduleFileQuery basicInfoQuery;

    @Inject
    private DailyAttendanceGettingFileQuery dailyAttendanceQuery;

    @Inject
    private ScheduleInformationByDateFileQuery scheduleInfoByDateQuery;

    @Inject
    private GetPerformanceInfoByDateExportQuery performanceInfoByDateQuery;

    public PersonalScheduleByDateDataSource get(int orgUnit, String orgId, GeneralDate baseDate, List<String> sortedEmployeeIds,
                                                boolean isDisplayActual, boolean isGraphVacationDisplay, boolean isDoubleWorkDisplay) {

        long startTime = System.nanoTime();
        // 1. 出力する(対象組織識別情報, 対象期間, List(社員ID)): input (対象組織, 年月日, 並び順社員リスト)
        val basicInformation = this.basicInfoQuery.getInfo(
                orgUnit,
                orgId,
                baseDate,
                sortedEmployeeIds);

        // 2. 取得する(社員リスト, 期間, 実績も取得するか)
        Map<ScheRecGettingAtr, List<IntegrationOfDaily>> dailyAttendanceMap = this.dailyAttendanceQuery.get(
                sortedEmployeeIds,
                new DatePeriod(baseDate, baseDate),
                isDisplayActual);

        // 3. 作成する(List<日別勤怠(Work)>, boolean, boolean) : input List<日別勤怠(Work)>>=$Map<予実取得区分, List<日別勤怠(Work)>>.get(予定のみ)
        val lstScheduleDaily = this.scheduleInfoByDateQuery.get(
                dailyAttendanceMap.get(ScheRecGettingAtr.ONLY_SCHEDULE),
                isGraphVacationDisplay,
                isDoubleWorkDisplay);

        // 4.実績も取得するか == true
        List<EmployeeWorkScheduleResultDto> lstScheduleAchievement = new ArrayList<>();
        if (isDisplayActual) {
            // 4.1. 作成する(List<日別勤怠(Work)>) : input List<日別勤怠(Work)>=$Map<予実取得区分, List<日別勤怠(Work)>>.get(実績のみ)
            lstScheduleAchievement = this.performanceInfoByDateQuery.get(dailyAttendanceMap.get(ScheRecGettingAtr.ONLY_RECORD));
        }

        // 4.2. 3予定と4.1実績のList<社員勤務予定・実績　dto>をMergeする。
        // 実績が存在する日（社員ID、年月日が一致するデータが存在する場合）は、 取得した予定を実績で上書きしてListを作る。
        List<EmployeeWorkScheduleResultDto> employeeWorkScheduleResultList = CollectionUtil.isEmpty(lstScheduleAchievement)
                ? lstScheduleDaily
                : this.mergeDataSchedule(lstScheduleDaily, lstScheduleAchievement);

        System.out.println("Thoi gian get data: " + (System.nanoTime() - startTime) / 1000000000 + " seconds");
        return new PersonalScheduleByDateDataSource(
                basicInformation.getCompanyInfo(),
                basicInformation.getDisplayInfoOrganization(),
                basicInformation.getDateInformation(),
                basicInformation.getEmployeeInfoList(),
                employeeWorkScheduleResultList,
                null);
    }

    /**
     * 3予定と4.1実績のList<社員勤務予定・実績　dto>をMergeする。
     * @param scheduleDailies
     * @param scheduleAchievements
     * @return List<EmployeeWorkScheduleResultDto>
     */
    private List<EmployeeWorkScheduleResultDto> mergeDataSchedule(List<EmployeeWorkScheduleResultDto> scheduleDailies, List<EmployeeWorkScheduleResultDto> scheduleAchievements) {
        scheduleDailies.forEach(daily -> {
            val scheduleAchievementOpt = scheduleAchievements.stream().filter(achievement -> achievement.getEmployeeId().equals(daily.getEmployeeId())).findFirst();
            if (scheduleAchievementOpt.isPresent()) {
                daily.setActualBreakTimeList(scheduleAchievementOpt.get().getActualBreakTimeList());
                daily.setActualStartTime1(scheduleAchievementOpt.get().getActualStartTime1());
                daily.setActualEndTime1(scheduleAchievementOpt.get().getActualEndTime1());
                daily.setActualStartTime2(scheduleAchievementOpt.get().getActualStartTime2());
                daily.setActualEndTime2(scheduleAchievementOpt.get().getActualEndTime2());
            }
        });

        return scheduleDailies.stream().sorted(Comparator.comparing(EmployeeWorkScheduleResultDto::getEmployeeId)).collect(Collectors.toList());
    }
}
