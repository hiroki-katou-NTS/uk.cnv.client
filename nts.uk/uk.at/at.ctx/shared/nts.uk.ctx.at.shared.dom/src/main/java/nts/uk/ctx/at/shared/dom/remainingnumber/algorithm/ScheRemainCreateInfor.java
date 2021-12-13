package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm;

/**
 * 残数作成元情報(予定)
 * @author do_dt
 *
 */

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.VacationTimeUseInfor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NumberOfDaySuspension;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ScheRemainCreateInfor {
	/** 社員ID */
	private String sid;
	/** 年月日 */
	private GeneralDate ymd;
	/** 勤務種類コード */
	private String workTypeCode;
	/** 就業時間帯コード */
	private Optional<String> workTimeCode;
	/** 振替時間合計 */
	private Integer transferTotal = 0;
	/** 振替残業時間合計 */
	private Integer transferOvertimesTotal = 0;
	/** 時間休暇使用情報 */
	private List<VacationTimeUseInfor> lstVacationTimeInfor;
	/** 振休振出として扱う日数 */
	private Optional<NumberOfDaySuspension> numberDaySuspension;
	/** 時間消化使用情報 */
	private TimeDigestionUsageInfor timeDigestionUsageInfor;

	public static ScheRemainCreateInfor toScheRemain(RecordRemainCreateInfor record) {
		return new ScheRemainCreateInfor(record.getSid(), record.getYmd(), record.getWorkTypeCode(),
				record.getWorkTimeCode(), record.getTransferTotal(), record.getTransferOvertimesTotal(),
				record.getLstVacationTimeInfor(), record.getNumberDaySuspension(), record.getTimeDigestionUsageInfor());
	}
	
	
	
}
