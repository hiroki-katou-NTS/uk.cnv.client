package nts.uk.file.at.app.export.schedule.personalschedulebydate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.screen.at.app.ksu003.start.dto.ChangeableWorkTimeDto;
import nts.uk.screen.at.app.ksu003.start.dto.TimeShortDto;
import nts.uk.screen.at.app.ksu003.start.dto.TimeVacationAndTypeDto;

import java.util.List;

/**
 * 社員勤務予定・実績dto
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EmployeeWorkScheduleResultDto {
    //年月日
    private GeneralDate date;

    //社員ID
    private String employeeId;

    // List<休憩時間帯>
    private List<BreakTimeSheet> breakTimeList;

    // List<実績休憩時間帯>
    @Setter
    private List<BreakTimeSheet> actualBreakTimeList;

    // List<残業時間帯>
    private List<ChangeableWorkTimeDto> overTimeList;

    // List<育児介護短時間帯> : ★時間帯(実装コードなし/使用不可)
    private List<TimeShortDto> childCareShortTimeList;

    // Map<時間休暇種類, 時間休暇>
    private List<TimeVacationAndTypeDto> listTimeVacationAndType;

    //コア開始時刻
    private Integer coreStartTime;

    //コア終了時刻
    private Integer coreEndTime;

    // 休憩時間合計
    private Integer totalBreakTime;

    // 勤務タイプ : 就業時間帯の勤務形態 (WorkTimeForm)
    private Integer workType;

    // 勤務種類コードf
    private String workTypeCode;

    // 勤務種類名称
    private String workTypeName;

    // 実績開始時刻1
    @Setter
    private Integer actualStartTime1;

    // 実績終了時刻1
    @Setter
    private Integer actualEndTime1;

    // 実績開始時刻2
    @Setter
    private Integer actualStartTime2;

    // 実績終了時刻2
    @Setter
    private Integer actualEndTime2;

    // 就業時間合計
    private Integer totalWorkingHours;

    // 就業時間帯コード
    private String workTimeCode;

    // 就業時間帯名称
    private String workTimeName;

    // 開始時刻1
    private Integer startTime1;

    // 終了時刻1
    private Integer endTime1;

    // 開始時刻2
    private Integer startTime2;

    // 終了時刻2
    private Integer endTime2;
}
