package nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday;

import lombok.Value;
import lombok.val;
import nts.arc.layer.dom.objecttype.DomainValue;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.common.days.FourWeekDays;
import nts.uk.ctx.at.shared.dom.common.days.WeeklyDays;
import nts.uk.shr.com.time.calendar.MonthDay;

/**
 * 月日起算の休日取得管理
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.休日管理.休日の扱い.月日起算の休日取得管理
 * @author tutk
 *
 */
@Value
public class HolidayAcqManageByMD implements FourWeekHolidayAcqMana, DomainValue {
	
	/**
	 * 起算月日
	 */
	private MonthDay startingMonthDay;
	
	/**
	 * 4週間の休日日数
	 */
	private FourWeekDays  fourWeekHoliday;
	
	/**
	 * 最終週の休日日数
	 */
	private WeeklyDays numberHolidayLastweek;
	
	
	/**
	 * [1] 管理期間の単位を取得する
	 */

	/**
	 * [2] 管理期間を取得する
	 */
	@Override
	public HolidayAcqManaPeriod getManagementPeriod(Require require, GeneralDate baseDate) {
		
		GeneralDate startingDate = this.startingMonthDay.toDate( baseDate.year() );
		
		if( startingDate.after( baseDate ) ) {
			
			startingDate = startingDate.addYears( -1 );
			
		}
		
		val cycleDays = 7*4;
		val numberOfCycles = ( new DatePeriod(startingDate, baseDate).datesBetween().size() - 1) /cycleDays; 
		
		GeneralDate startDate, endDate;
		switch( numberOfCycles ) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
			startDate = startingDate.addDays( numberOfCycles * cycleDays );
			endDate = startDate.addDays( cycleDays - 1);
			
			return new HolidayAcqManaPeriod( new DatePeriod( startDate, endDate )
					, new FourWeekDays( this.fourWeekHoliday.v() ) );
		
		case 12:
			startDate = startingDate.addDays( numberOfCycles * cycleDays );
			endDate = startingDate.addYears( 1 ).addDays( -1 );
			
			return new HolidayAcqManaPeriod( new DatePeriod( startDate, endDate )
					, new FourWeekDays( this.fourWeekHoliday.v() + this.numberHolidayLastweek.v() ) );
		
		case 13:
			startDate = startingDate.addDays( ( numberOfCycles -1 ) * cycleDays );
			endDate = startingDate.addYears( 1 ).addDays( -1 );
			
			return new HolidayAcqManaPeriod( new DatePeriod( startDate, endDate )
					, new FourWeekDays( this.fourWeekHoliday.v() + this.numberHolidayLastweek.v() ) );
		
		default:
			throw new RuntimeException( " this value not existed!!! ");
		}
		
	}

	/**
	 * [3] 起算日の種類を取得する
	 */
	@Override
	public StartDateClassification getStartDateType() {
		return StartDateClassification.SPECIFY_MD;
	}
	
}
