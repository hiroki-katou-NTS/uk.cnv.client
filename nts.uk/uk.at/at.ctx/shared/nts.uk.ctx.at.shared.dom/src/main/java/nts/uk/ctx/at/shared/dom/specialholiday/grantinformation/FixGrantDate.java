package nts.uk.ctx.at.shared.dom.specialholiday.grantinformation;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.specialholiday.NextSpecialHolidayGrantParameter;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday.Require;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.GrantDeadline;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.LimitCarryoverDays;
import nts.uk.shr.com.time.calendar.MonthDay;

/**
 * 指定日付与
 * @author masaaki_jinno
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FixGrantDate {

	/** 付与日数 */
	private RegularGrantDays grantDays;

	/** 期限 */
	private GrantDeadline grantPeriodic;

	/** 付与月日 */
	private Optional<MonthDay> grantMonthDay;



	/**
	 * Create from Java Type
	 * @return
	 */
	public static FixGrantDate createFromJavaType(
			String companyId,
			int specialHolidayCode,
			int grantDays,
			GrantDeadline deadline,
			Integer grantMD) {

		Optional<MonthDay> grant_md = Optional.empty();
		if ( grantMD != null ) {
			int grant_month = (int) Math.floor(grantMD / 100);
			int grant_day = grantMD % 100;
			grant_md = Optional.of(new MonthDay(grant_month, grant_day));
		}

		return new FixGrantDate(
				RegularGrantDays.createFromJavaType(grantDays),
				deadline,
				grant_md
			);

	}
	
	/**
	 * 付与基準日を取得
	 * @param require
	 * @param cacheCarrier
	 * @param parameter
	 * @param grantDate
	 * @return
	 */
	public Optional<GeneralDate> getGrantDate(			
			Require require,
			CacheCarrier cacheCarrier,
			NextSpecialHolidayGrantParameter parameter,
			Optional<GrantDate> grantDate){
		
		// 付与月日に値が入っているか
		if ( getGrantMonthDay().isPresent() ){ // 付与月日に値が入っているとき

			// 期間開始日の年と指定日付与.付与日の月日をパラメータ「付与基準日」にセットする
			return Optional.of(GeneralDate.ymd(
					parameter.getPeriod().start().year(),
					getGrantMonthDay().get().getMonth(),
					getGrantMonthDay().get().getDay()));
			

		} else { // 付与月日に値が入っていないとき
			
			if(!grantDate.isPresent()){
				return Optional.empty();
			}
			
			Optional<GeneralDate> grantReferenceDate = grantDate.get().getSpecialLeaveGrantDate(require, cacheCarrier,
					parameter);
			
				
			if(grantReferenceDate.isPresent()){
				return Optional.of(GeneralDate.ymd(
						parameter.getPeriod().start().year(),
						grantReferenceDate.get().month(),
						grantReferenceDate.get().day()));
			}else{
				return grantReferenceDate;
			}
	
		}
		
	}
	
	/**
	 * 期限日を取得する
	 * @param grantDate
	 * @param grantReferenceDate
	 * @param elapseNo
	 * @param elapseYear
	 * @return
	 */
	public GeneralDate getDeadLine(GeneralDate grantDate, Optional<GeneralDate> grantReferenceDate,
			Optional<Integer> elapseNo, Optional<ElapseYear> elapseYear) {
		return this.grantPeriodic.getDeadLine(grantDate, grantReferenceDate, elapseNo, elapseYear);
	}
	
	/**
	 * 蓄積上限日数を取得する
	 * @return
	 */
	public Optional<LimitCarryoverDays> getLimitCarryoverDays(){
		return this.grantPeriodic.getLimitCarryoverDays();
	}
}
