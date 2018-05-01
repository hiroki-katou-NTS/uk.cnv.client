package nts.uk.ctx.at.record.dom.monthly.vacation.annualleave;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveRemainingDayNumber;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveRemainingTime;

/**
 * 年休残明細
 * @author shuichu_ishida
 */
@Getter
@Setter
public class AnnualLeaveRemainingDetail {

	/** 日数 */
	private AnnualLeaveRemainingDayNumber days;
	/** 時間 */
	private AnnualLeaveRemainingTime time;
	/** 付与日 */
	private GeneralDate grantDate;
	
	/**
	 * コンストラクタ
	 */
	public AnnualLeaveRemainingDetail(){
		
		this.days = new AnnualLeaveRemainingDayNumber(0.0);
		this.time = new AnnualLeaveRemainingTime(0);
		this.grantDate = GeneralDate.max();
	}
	
	/**
	 * ファクトリー
	 * @param days 日数
	 * @param time 時間
	 * @param grantDate 付与日
	 * @return 年休残明細
	 */
	public static AnnualLeaveRemainingDetail of(
			AnnualLeaveRemainingDayNumber days,
			AnnualLeaveRemainingTime time,
			GeneralDate grantDate){
		
		AnnualLeaveRemainingDetail domain = new AnnualLeaveRemainingDetail();
		domain.days = days;
		domain.time = time;
		domain.grantDate = grantDate;
		return domain;
	}
}
