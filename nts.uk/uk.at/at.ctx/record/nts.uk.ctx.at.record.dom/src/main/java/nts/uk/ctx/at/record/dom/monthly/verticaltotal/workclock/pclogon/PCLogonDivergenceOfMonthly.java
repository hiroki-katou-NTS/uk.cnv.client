package nts.uk.ctx.at.record.dom.monthly.verticaltotal.workclock.pclogon;

import lombok.Getter;
import nts.uk.ctx.at.record.dom.actualworkinghours.AttendanceTimeOfDailyPerformance;

/**
 * 月別実績のPCログオン乖離
 * @author shuichu_ishida
 */
@Getter
public class PCLogonDivergenceOfMonthly {

	/** PCログオン乖離 */
	private AggrPCLogonDivergence logonDivergence;
	/** PCログオフ乖離 */
	private AggrPCLogonDivergence logoffDivergence;
	
	/**
	 * コンストラクタ
	 */
	public PCLogonDivergenceOfMonthly(){
		
		this.logonDivergence = new AggrPCLogonDivergence();
		this.logoffDivergence = new AggrPCLogonDivergence();
	}

	/**
	 * ファクトリー
	 * @param logonDivergence PCログオン乖離
	 * @param logoffDivergence PCログオフ乖離
	 * @return 月別実績のログオン乖離
	 */
	public static PCLogonDivergenceOfMonthly of(
			AggrPCLogonDivergence logonDivergence,
			AggrPCLogonDivergence logoffDivergence){
		
		PCLogonDivergenceOfMonthly domain = new PCLogonDivergenceOfMonthly();
		domain.logonDivergence = logonDivergence;
		domain.logoffDivergence = logoffDivergence;
		return domain;
	}
	
	/**
	 * 集計
	 * @param attendanceTimeOfDaily 日別実績の勤怠時間
	 */
	public void aggregate(AttendanceTimeOfDailyPerformance attendanceTimeOfDaily){

		// ログオンの集計
		this.logonDivergence.aggregate(attendanceTimeOfDaily, true);
		
		// ログオフの集計
		this.logoffDivergence.aggregate(attendanceTimeOfDaily, false);
	}
	
	/**
	 * 合算する
	 * @param target 加算対象
	 */
	public void sum(PCLogonDivergenceOfMonthly target){
		
		this.logonDivergence.sum(target.logonDivergence);
		this.logoffDivergence.sum(target.logoffDivergence);
	}
}
