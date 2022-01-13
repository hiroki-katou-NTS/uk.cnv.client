package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.childcare;


import lombok.*;
import nts.arc.time.YearMonth;

/**
 * 介護休暇過去状況
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NursingCareLeaveMonthlyRemaining {
    /** 社員ID */
    private  String employeeId;
    /** 年月 */
    private YearMonth yearMonth;
    /**
     * 使用日数 : 使用日数←子の看護休暇月別残数データ．本年使用数．使用数．日数
     */
    private Double daysOfUse;
    /**
     * 使用時間: 使用時間←子の看護休暇月別残数データ．本年使用数．使用数．時間
     */
    private Integer usageTime;

    /**
     * 残日数:残日数　←子の看護休暇月別残数データ．本年使用数．使用数．日数
     */
    private Double remainingDays;

    /**
     * 残時間:残時間　←子の看護休暇月別残数データ．本年使用数．使用数．時間
     */
    private Integer timeRemaining;
}
