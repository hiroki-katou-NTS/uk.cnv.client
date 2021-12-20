package nts.uk.file.at.app.export.schedule.personalscheduleindividual;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.file.at.app.export.schedule.personalscheduleindividual.dto.ObjectDatePeriod;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalScheduleByIndividualQuery {

    /**
     * 社員コード
     */
    private String employeeCode;

    /**
     * 社員コード
     */
    private String employeeName;

    /**
     * 対象年月日
     */
    private String date;

    /**
     * 対象期間 start date - end date
     */
    private ObjectDatePeriod period;

    /**
     * 開始曜日
     */
    private int startDate;

    /**
     * 週合計判定
     */
    private boolean isTotalDisplay;

    /**
     * 起算曜日か
     */
    private boolean isStartingDay;
}