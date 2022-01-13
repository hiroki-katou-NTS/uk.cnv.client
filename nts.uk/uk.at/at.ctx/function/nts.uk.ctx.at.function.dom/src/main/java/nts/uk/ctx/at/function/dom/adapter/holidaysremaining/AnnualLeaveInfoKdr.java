package nts.uk.ctx.at.function.dom.adapter.holidaysremaining;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.AnnualLeaveMaxData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.UsedMinutes;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeaveGrant;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
public class AnnualLeaveInfoKdr {
    /** 年月日 */
    private GeneralDate ymd;
    /** 残数 */
    private AnnualLeaveRemainingKdr remainingNumber;
    /** 付与残数データ */
    private List<AnnualLeaveGrantRemainingData> grantRemainingDataList;
    /** 上限データ */
    private AnnualLeaveMaxData maxData;
    /** 付与情報 */
    private Optional<AnnualLeaveGrant> grantInfo;
    /** 使用日数 */
    private AnnualLeaveUsedDayNumber usedDays;
    /** 使用時間 */
    private UsedMinutes usedTime;
//	/** 付与後フラグ */
//	private boolean afterGrantAtr;

    /** 年休設定 */
    private AnnualPaidLeaveSetting annualPaidLeaveSet;
}
