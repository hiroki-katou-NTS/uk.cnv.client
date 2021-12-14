package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng;

//import javax.persistence.EnumType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
//import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.ClosureStatus;

/**
 * 代休月別残数データ
 * 
 * @author do_dt
 *
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyDayoffRemainData extends AggregateRoot {
	/** 社員ID */
	private String sId;
	/** 年月 */
	private YearMonth ym;
	/** 締めID */
	private int closureId;
	/** 締め日 */
	private int closureDay;
	/** 末日とする */
	private boolean lastDayis;
	/** 締め処理状態 */
	private ClosureStatus closureStatus;
	/** 開始年月日 */
	private GeneralDate startDate;
	/** 終了年月日 */
	private GeneralDate endDate;
	/** 代休発生数合計 */
	private DayOffDayAndTimes occurrenceDayTimes;
	/** 代休使用数合計 */
	private DayOffDayTimeUse useDayTimes;
	/** 代休残数 */
	private DayOffRemainDayAndTimes remainingDayTimes;
	/** 代休繰越数 */
	private DayOffRemainCarryForward carryForWardDayTimes;
	/** 代休未消化数 */
	private DayOffDayTimeUnUse unUsedDayTimes;

}
