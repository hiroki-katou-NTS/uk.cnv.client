package nts.uk.screen.at.app.ktgwidget.find.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.screen.at.app.ktgwidget.ktg004.VacationSetting;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OptionalWidgetInfoMobileDto {
	
	/** 1 OverTime Work Number 残業指示件数 */
	private int overTime;
	
	/** 2 Instructions Holiday Number 休出指示件数 */
	private int holidayInstruction;
	
	/** 3 Approved Number 承認された件数 */
	private int approved;
	
	/** 4 Approved Number 未承認件数 */
	private int unApproved;
	
	/** 5 Denied Number 否認された件数 */
	private int deniedNo;

	/** 6 Remand Number 差し戻し件数 */
	private int remand;
	
	/** 7 App Deadline Month 今月の申請締め切り日 */
	private DeadlineOfRequest appDeadlineMonth = new DeadlineOfRequest(false,GeneralDate.today());
	
	/** 8 Presence Daily Per 日別実績のエラー有無 */
	private boolean presenceDailyPer = false;
	
	/** 10 Overtime Hours 残業時間 */
	private TimeOT overtimeHours = new TimeOT(0, 0);
	
	/** 11 Flex Time フレックス時間 */
	private TimeOT flexTime = new TimeOT(0, 0);
	
	/** 12 Rest Time 休出時間 */
	private TimeOT restTime = new TimeOT(0, 0);
	
	/** 13 Night Work Hours 就業時間外深夜時間 */
	private TimeOT nightWorktime = new TimeOT(0, 0);
	
	/** 14 Late Or Early Retreat 遅刻/早退回数 */
	private int lateRetreat = 0;
	
	private int earlyRetreat = 0;
	
	/** 15 Yearly Holiday 年休残数 */
	private YearlyHoliday yearlyHoliday;
	
	/** 16 Reserved Years Remain Number 積立年休残数 */
	private RemainingNumber reservedYearsRemainNo;
	
	/** 18 Remain Alternation Number 代休の残時間 */
	private int remainAlternationNoDay;
	
	/** 18 Remain Alternation Number 代休の残日残数 */
	private Double remainAlternationDay;
	
	/** 19 RemainsLeft 振休残数 */
	private Double remainsLeft;
	
	/** Public Holiday Number 公休残数 */
	private int publicHDNo = 0;
	
	/** 21 Child nursing vacation remaining number 子の看護休暇残数 */
	private RemainingNumber childRemainNo = new RemainingNumber();
	
	/** 22 Care Leave Number 介護休暇残数 */
	private RemainingNumber careLeaveNo = new RemainingNumber();
	
	/** 23 Special Holiday Remain Number 特休残数 */
	private List<RemainingNumber> sPHDRamainNo = new ArrayList<>();
	
	/** ６０Ｈ超休残数 */
	private TimeOT extraRest = new TimeOT(0, 0);
	
	/** 休暇設定 */
	private VacationSetting vacationSetting;

}
