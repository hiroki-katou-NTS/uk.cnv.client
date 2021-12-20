package nts.uk.ctx.at.function.dom.adapter.periodofspecialleave;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AggrResultOfAnnualLeaveEachMonthKdr;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AggrResultOfAnnualLeaveKdr;

public interface ComplileInPeriodOfSpecialLeaveAdapter {

	/**
	 * RequestList273 期間内の特別休暇残を集計する
	 * @param cid
	 * @param sid
	 * @param complileDate ・集計開始日 ・集計終了日
	 * @param model ・モード（月次か、その他か） TRUE: 月次, FALSE: その他  
	 * 月次モード：当月以降は日次のみ見るが、申請とスケは見ない
	 * その他モード：当月以降は申請日次スケを見る
	 * @param baseDate ・基準日
	 * @param specialLeaveCode ・特別休暇コード
	 * @param mngAtr true: 翌月管理データ取得区分がする, false: 翌月管理データ取得区分がしない。 
	 * @return
	 */
	SpecialVacationImported complileInPeriodOfSpecialLeave(String cid, String sid, DatePeriod complileDate, boolean mode, GeneralDate baseDate, int specialLeaveCode, boolean mngAtr);
	
	
	/**
	 * RequestList263: 社員の月毎の確定済み特別休暇を取得する
	 * @param sid
	 * @param startMonth
	 * @param endMonth
	 * @return
	 */
	List<SpecialHolidayImported> getSpeHoliOfConfirmedMonthly(String sid, YearMonth startMonth, YearMonth endMonth);
	/**
	 * 
	 * @param sid
	 * @param startMonth
	 * @param endMonth
	 * @param speCode
	 * @return
	 */
	public List<SpecialHolidayImported> getSpeHoliOfConfirmedMonthly(String sid, YearMonth startMonth, YearMonth endMonth, List<Integer> listSpeCode);
	/**
	 * @author hoatt
	 * Doi ung response KDR001
	 * RequestList263 ver2
	 * @param sid
	 * @param startMonth
	 * @param endMonth
	 * @return
	 */
	public List<SpecialHolidayImported> getSpeHdOfConfMonVer2(String sid, YearMonth startMonth, YearMonth endMonth);


	/**
	 * 273 new
	 * @param cid
	 * @param sid
	 * @param complileDate
	 * @param mode
	 * @param baseDate
	 * @param specialLeaveCode
	 * @param mngAtr
	 * @return
	 */
	public SpecialVacationImportedKdr get273New(String cid, String sid, DatePeriod complileDate,
												boolean mode, GeneralDate baseDate, int specialLeaveCode, boolean mngAtr);
}
