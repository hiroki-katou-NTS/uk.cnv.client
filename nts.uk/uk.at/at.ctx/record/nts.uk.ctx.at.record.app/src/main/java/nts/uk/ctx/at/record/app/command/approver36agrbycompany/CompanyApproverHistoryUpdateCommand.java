package nts.uk.ctx.at.record.app.command.approver36agrbycompany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyApproverHistoryUpdateCommand {
    /**
     * 会社ID
     */
    private String companyId;

    /**
     * 期間
     */
    @Setter
    private DatePeriod period;

    /**
     * 承認者リスト
     */
    private List<String> approvedList;
    /**
     * 確認者リスト
     */
    private List<String> confirmedList;

    private GeneralDate startDateBeforeChange;
}
