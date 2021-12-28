package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.premiumtime;

import java.io.Serializable;

import lombok.Getter;
import lombok.val;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceAmountMonth;

/**
 * 集計割増時間
 * @author shuichi_ishida
 */
@Getter
public class AggregatePremiumTime implements Serializable{

	/** Serializable */
	private static final long serialVersionUID = 1L;

	/** 割増時間項目No */
	private ExtraTimeItemNo premiumTimeItemNo;
	/** 時間 */
	private AttendanceTimeMonth time;
	/** 金額 */
	private AttendanceAmountMonth amount;
	
	/**
	 * コンストラクタ
	 */
	public AggregatePremiumTime(ExtraTimeItemNo premiumTimeItemNo){
		
		this.premiumTimeItemNo = premiumTimeItemNo;
		this.time = new AttendanceTimeMonth(0);
		this.amount = new AttendanceAmountMonth(0L);
	}
	
	/**
	 * ファクトリー
	 * @param premiumTimeItemNo 割増時間項目No
	 * @param time 時間
	 * @return 集計割増時間
	 */
	public static AggregatePremiumTime of(
			ExtraTimeItemNo premiumTimeItemNo,
			AttendanceTimeMonth time,
			AttendanceAmountMonth amount){

		val domain = new AggregatePremiumTime(premiumTimeItemNo);
		domain.time = time;
		domain.amount = amount;
		return domain;
	}
			
	/**
	 * 時間に分を加算する
	 * @param minutes 分
	 */
	public void addMinutesToTime(int minutes){
		this.time = this.time.addMinutes(minutes);
	}
	
	/**
	 * 金額に金額を加算する
	 * @param minutes 分
	 */
	public void addAmount(long amount){
		this.amount = this.amount.addAmount(amount);
	}
}
