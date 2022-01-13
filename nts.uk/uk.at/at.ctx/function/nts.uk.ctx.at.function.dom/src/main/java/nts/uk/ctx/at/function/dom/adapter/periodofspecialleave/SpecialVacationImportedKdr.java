package nts.uk.ctx.at.function.dom.adapter.periodofspecialleave;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SpecialVacationImportedKdr {
    /**
     * 付与日数
     */
    private Double grantDays;

    /**
     * 付与日数
     */
    private Integer grantTime;

    /**
     * 使用数日数
     */
    private Double usedDate;

    /**
     * 使用数日数
     */
    private Double usedDateBf;

    /**
     * 使用数日数
     */
    private Double usedDateAf;
    /**
     * 残数日数
     */
    private Double remainDate;

    /**
     * 残数日数
     */
    private Double remainDateBf;

    /**
     * 残数日数
     */
    private Double remainDateAf;


    /**
     * 使用数時間
     */
    private Integer usedHours;
    /**
     * 使用数時間
     */
    private Integer usedHoursBf;
    /**
     * 使用数時間
     */
    private Integer usedHoursAf;
    /**
     * 残数時間
     */
    private Integer remainHours;
    /**
     * 残数時間
     */
    private Integer remainHoursBf;
    /**
     * 残数時間
     */
    private Integer remainHoursAf;

}
