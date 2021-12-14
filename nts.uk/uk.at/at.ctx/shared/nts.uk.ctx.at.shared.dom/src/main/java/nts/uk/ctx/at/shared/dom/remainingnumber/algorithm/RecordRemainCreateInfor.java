package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.VacationTimeUseInfor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NumberOfDaySuspension;

/**
 * 残数作成元情報(実績)
 * @author do_dt
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RecordRemainCreateInfor {
	/**	社員ID */
	private String sid;
	/**	年月日 */
	private GeneralDate ymd;
	/**	勤務種類コード */
	private String workTypeCode;
	/**	時間休暇使用情報 */
	private List<VacationTimeUseInfor> lstVacationTimeInfor;
	/** 時間消化使用情報 */
	private TimeDigestionUsageInfor timeDigestionUsageInfor;
	/**	振替残業時間合計 */
	private int transferOvertimesTotal;
	/**	振替時間合計     休出 */
	private int transferTotal;
	/**	就業時間帯コード */
	private Optional<String> workTimeCode;
	/** 振休振出として扱う日数 */
	private Optional<NumberOfDaySuspension> numberDaySuspension = Optional.empty();
	
	
}
