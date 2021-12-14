package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday.param;


import java.util.ArrayList;
import java.util.List;

import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveGrantDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.ClosureStatus;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.publicholiday.PublicHolidayRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * 
 * @author hayata_maekawa
 *
 *		公休の集計結果
 *
 */

public class AggrResultOfPublicHoliday {

	/*
	 * 公休情報
	 */
	public List<PublicHolidayInformation> publicHolidayInformation;
	
	/*
	 * 繰越データ
	 */
	public List<PublicHolidayCarryForwardData> publicHolidayCarryForwardData;
	
	/**
	 * コンストラクタ
	 */
	public AggrResultOfPublicHoliday(){
		this.publicHolidayInformation = new ArrayList<>();
		this.publicHolidayCarryForwardData = new ArrayList<>();
	}
	
	public AggrResultOfPublicHoliday(List<PublicHolidayInformation> publicHolidayInformation, 
			List<PublicHolidayCarryForwardData> publicHolidayCarryForwardData){
		this.publicHolidayInformation = publicHolidayInformation;
		this.publicHolidayCarryForwardData = publicHolidayCarryForwardData;
	}
	
	/**
	 * 月別残数データを作成
	 * @param employeeId
	 * @param yearMonth
	 * @param closureId
	 * @param closureDate
	 * @return
	 */
	public PublicHolidayRemNumEachMonth createPublicHolidayRemainData(
			String employeeId,
			YearMonth yearMonth,
			ClosureId closureId,
			ClosureDate closureDate){
		
		return new PublicHolidayRemNumEachMonth(
				employeeId,
				yearMonth,
				closureId,
				closureDate,
				ClosureStatus.UNTREATED,
				publicHolidayInformation.stream()
					.filter(x ->x.getYearMonth().equals(yearMonth))
					.findFirst()
					.map(x -> x.getPublicHolidayDigestionInformation().getPublicHolidayday())
					.orElse(new LeaveGrantDayNumber(0.0)),
				publicHolidayInformation.stream()
					.filter(x ->x.getYearMonth().equals(yearMonth))
					.findFirst()
					.map(x -> x.getPublicHolidayDigestionInformation().getCarryForwardNumber())
					.orElse(new LeaveRemainingDayNumber(0.0)),				
				publicHolidayInformation.stream()
					.filter(x ->x.getYearMonth().equals(yearMonth))
					.findFirst()
					.map(x -> x.getPublicHolidayDigestionInformation().getNumberOfAcquisitions())
					.orElse(new LeaveUsedDayNumber(0.0)),					
				publicHolidayInformation.stream()
					.filter(x ->x.getYearMonth().equals(yearMonth))
					.findFirst()
					.map(x -> x.getPublicHolidayCarryForwardInformation().getCarryForwardNumber())
					.orElse(new LeaveRemainingDayNumber(0.0)),	
				publicHolidayInformation.stream()
					.filter(x ->x.getYearMonth().equals(yearMonth))
					.findFirst()
					.map(x -> x.getPublicHolidayCarryForwardInformation().getUnusedNumber())
					.orElse(new LeaveRemainingDayNumber(0.0))					
					
				);
	}
}
