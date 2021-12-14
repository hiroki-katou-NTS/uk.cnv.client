package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.VacationTimeUseInfor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NumberOfDaySuspension;
/**
 * 残数作成元情報(申請)
 * @author do_dt
 *
 */
@AllArgsConstructor
@Setter
@Getter
@Builder
public class AppRemainCreateInfor {
	/**社員ID	 */
	private String sid;
	/**	申請ID */
	private String appId;
	/**	入力日 */
	private GeneralDateTime inputDate;
	/**	申請日 */
	private GeneralDate appDate;
	/**事前事後区分	 */
	private PrePostAtr prePosAtr;
	/**	申請種類 */
	private ApplicationType appType;
	/**	勤務種類コード */
	private Optional<String> workTypeCode;
	/**	就業時間帯コード */
	private Optional<String> workTimeCode;
	/**	時間休暇使用情報 */
	private List<VacationTimeUseInfor> vacationTimes = new ArrayList<>();
	/**	申請休出時間合計 */
	private Optional<Integer> appBreakTimeTotal;
	/**	申請残業時間合計 */
	private Optional<Integer> appOvertimeTimeTotal;
	/**
	 * 
	 */
	private Optional<GeneralDate> startDate;
	private Optional<GeneralDate> endDate;
	/**
	 * 休日申請日
	 */
	private List<GeneralDate> lstAppDate;
	/** 時間消化使用情報 */
	private Optional<TimeDigestionUsageInfor> timeDigestionUsageInfor = Optional.empty();
	
	/**
	 * 振休振出として扱う日数
	 */
	private Optional<NumberOfDaySuspension> numberOfDaySusp;
	
	public static AppRemainCreateInfor createDefault(String sid, String appId, GeneralDateTime inputDate,
			GeneralDate appDate, PrePostAtr prePosAtr, ApplicationType appType, Optional<GeneralDate> startDate, Optional<GeneralDate> endDate) {
		return new AppRemainCreateInfor(sid, appId, inputDate, appDate, prePosAtr, 
				appType, 
				Optional.empty(), 
				Optional.empty(),
				new ArrayList<>(), 
				Optional.empty(), 
				Optional.empty(), 
				startDate, 
				endDate, 
				new ArrayList<>(),
				Optional.empty(), 
				Optional.empty());
	}
}
