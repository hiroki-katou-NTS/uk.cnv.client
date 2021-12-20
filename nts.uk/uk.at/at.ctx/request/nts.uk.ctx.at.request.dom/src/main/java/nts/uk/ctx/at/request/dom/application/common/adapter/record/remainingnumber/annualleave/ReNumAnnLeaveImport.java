package nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * @author anhnm
 * 基準日時点年休残数
 *
 */
@Value
@AllArgsConstructor
public class ReNumAnnLeaveImport {
    
    /**
     * 年休残日数
     */
    private Double remainingDays;
    
    /**
     * 年休残時間
     */
    private Integer remainingTime;

}
