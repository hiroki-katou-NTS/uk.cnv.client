package nts.uk.ctx.at.shared.dom.specialholiday.grantinformation;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.ctx.at.shared.dom.specialholiday.NextSpecialHolidayGrantParameter;
import nts.uk.shr.com.time.calendar.MonthDay;

/**
 * 期間付与
 * @author masaaki_jinno
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PeriodGrantDate {

	/** 期間 */
	private DatePeriod period;

	/** 付与日数 */
	private RegularGrantDays grantDays;

	static public PeriodGrantDate of(
			/** 期間 */
			DatePeriod period
			/** 付与日数 */
			, RegularGrantDays grantDays
		){
			PeriodGrantDate c = new PeriodGrantDate();
			/** 期間 */
			c.period=period;
			/** 付与日数 */
			c.grantDays=grantDays;

			return c;
		}

	public DatePeriod getPeriod(int year) {

		int start = this.period.start().month() * 100 + this.period.start().day();
		int end = this.period.end().month() * 100 + this.period.end().day();

		GeneralDate grantDate = GeneralDate.ymd(
				year,
				this.period.start().month(),
				this.period.start().day());

		GeneralDate expireDate = GeneralDate.ymd(
				start <= end ? year : year+1,
				this.period.end().month(),
				this.period.end().day());

		return new DatePeriod(grantDate, expireDate);
	}


	/**
	 * 付与基準日を求める
	 * @param require
	 * @param cacheCarrier
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param grantDate 付与日
	 * @param expireDate 期限日
	 * @return 付与日
	 */
	public Optional<GeneralDate> getPeriodSpecialLeaveGrantInfo(
			Require require,
			CacheCarrier cacheCarrier,
			NextSpecialHolidayGrantParameter parameter) {

		DatePeriod grantPeriod = getNextGrantPerood(parameter, cacheCarrier, require);

		

		//社員の入社日を取得
		Optional<GeneralDate> empEnrollDate = parameter.getEntryDate(require, cacheCarrier);
	
		if (!empEnrollDate.isPresent()){
			return Optional.empty();
		}
		

		//入社日が付与～期限の間に含まれるかを確認
		if(grantPeriod.contains(empEnrollDate.get())) {
			return Optional.of(empEnrollDate.get());
		}
		else {
			return Optional.of(grantPeriod.start());
		}

	}
	
	
	/**
	 * 期限日を取得する
	 * @param grantDate
	 * @return
	 */
	public GeneralDate getDeadLine(GeneralDate grantDate) {
		return this.findClosestDateAfterSpecifiedDate(grantDate, new MonthDay(this.period.end().month(),this.period.end().day()));
	}
	
	
	/**
	 * 次回付与期間を求める
	 * @param parameter
	 * @param cacheCarrier
	 * @param require
	 * @return
	 */
	public DatePeriod getNextGrantPerood(NextSpecialHolidayGrantParameter parameter, CacheCarrier cacheCarrier, Require require){
		DatePeriod specifiedGrantPeriod = getSpecifiedGrantPeriod(parameter.getPeriod().start());
		
		if(isPeriodContainsClosureDay(cacheCarrier, require, parameter, specifiedGrantPeriod)){
			return getSpecifiedGrantPeriod(parameter.getPeriod().start().addYears(1));
		}
		
		return specifiedGrantPeriod; 
	}
	
	/**
	 * 指定日から一番近い付与期間を求める
	 * @param designatedDate
	 * @return
	 */
	private DatePeriod getSpecifiedGrantPeriod(GeneralDate designatedDate){
		GeneralDate specifiedEndDate = findClosestDateAfterSpecifiedDate(designatedDate, 
				new MonthDay(this.period.end().month(),this.period.end().day()));
		
		GeneralDate specifiedStartDate = findClosestDateBeforeSpecifiedDate(specifiedEndDate, 
				new MonthDay(this.period.start().month(),this.period.start().day()));
		
		return new DatePeriod(specifiedStartDate, specifiedEndDate);
		
	}
	

	
	/**
	 * 期間内に締め開始日があるか
	 * @param cacheCarrier
	 * @param require
	 * @param parameter
	 * @param grantPeriod
	 * @return
	 */
	public boolean isPeriodContainsClosureDay(CacheCarrier cacheCarrier, Require require,
			NextSpecialHolidayGrantParameter parameter, DatePeriod grantPeriod){
		
		if(!parameter.getEmployeeId().isPresent()){
			return false;
		}
		
		Optional<ClosureStatusManagement> closure = require.latestClosureStatusManagement(parameter.getEmployeeId().get());
		
		if(!closure.isPresent()){
			return false;
		}
		
		if(!grantPeriod.contains(closure.get().getPeriod().start())){
			return false;
		}
		
		//社員の入社日を取得
		Optional<GeneralDate> empEnrollDate = parameter.getEntryDate(require, cacheCarrier);
		
		if(!empEnrollDate.isPresent()){
			return false;
		}
		
		if(closure.get().getPeriod().start().before(empEnrollDate.get())){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 指定日以降で一番近い月日の日を求める
	 * @param designatedDate 指定日
	 * @param monthDay 月日
	 * @return
	 */
	private GeneralDate findClosestDateAfterSpecifiedDate(GeneralDate designatedDate, MonthDay monthDay){
		GeneralDate specifiedDate = monthDay.toDate(designatedDate.year());
		
		if(designatedDate.after(specifiedDate)){
			specifiedDate = monthDay.toDate(designatedDate.year()+1);
		}
		
		return specifiedDate;
	}
	
	/**
	 * 指定日以前で一番近い月日の日を求める
	 * @param designatedDate
	 * @param monthDay
	 * @return
	 */
	private GeneralDate findClosestDateBeforeSpecifiedDate(GeneralDate designatedDate, MonthDay monthDay){
		GeneralDate specifiedDate = monthDay.toDate(designatedDate.year());
		
		if(designatedDate.before(specifiedDate)){
			specifiedDate = monthDay.toDate(designatedDate.year()-1);
		}
		
		return specifiedDate;
	}
	
	
	public static interface Require extends NextSpecialHolidayGrantParameter.Require{
		Optional<ClosureStatusManagement> latestClosureStatusManagement(String employeeId);
	}
}
