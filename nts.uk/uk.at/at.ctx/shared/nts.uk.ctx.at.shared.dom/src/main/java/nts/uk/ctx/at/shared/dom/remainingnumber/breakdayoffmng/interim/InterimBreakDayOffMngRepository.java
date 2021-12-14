package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.DataManagementAtr;

public interface InterimBreakDayOffMngRepository {
	
	/**
	 * 暫定休出管理データ
	 * @param breakManaId
	 * @return
	 */
	Optional<InterimBreakMng> getBreakManaBybreakMngId(String breakManaId);
	/**
	 * 暫定休出管理データ by Sid and date period
	 * @param sid
	 * @param period
	 * @return
	 */
	List<InterimBreakMng> getBySidPeriod(String sid, DatePeriod period);
	/**
	 * 暫定代休管理データ
	 * @param dayOffManaId
	 * @return
	 */
	Optional<InterimDayOffMng> getDayoffById(String dayOffManaId);
	/**
	 * get 暫定代休管理データ by sid and date period
	 * @param sid
	 * @param period
	 * @return
	 */
	List<InterimDayOffMng> getDayOffBySidPeriod(String sid, DatePeriod period);
	/**
	 * 暫定休出代休紐付け管理
	 * @param mngId
	 * @param breakDay: True: 休出管理データ, false: 代休管理データ
	 * @return
	 */
	List<InterimBreakDayOffMng> getBreakDayOffMng(String mngId, boolean breakDay, DataManagementAtr mngAtr);
	
	/**
	 * 
	 * @param mngId
	 * @param unUseDays ・未使用日数＞unUseDays
	 * @param dateData ・使用期限日が指定期間内
	 * ・使用期限日≧INPUT.期間.開始年月日
	 * ・使用期限日≦INPUT.期間.終了年月日
	 * @return
	 */
	List<InterimBreakMng> getByPeriod(String sid, DatePeriod ymd, double unUseDays, DatePeriod dateData);

	/**
	 * KDL036 ドメインモデル「暫定休出管理データ」を取得する
	 * @param mngIds
	 * @return
	 */
	List<InterimBreakMng> getBreakByIds(List<String> mngIds);
	
	/**
	 * 暫定休出管理データ 　を追加および更新
	 * @param domain
	 */
	void persistAndUpdateInterimBreakMng(InterimBreakMng domain);
	
	/**
	 * 暫定休出管理データ  を削除
	 * @param mngId
	 */
	void deleteInterimBreakMng(String mngId);
	
	/**
	 * 暫定休出管理データ  を削除
	 * @param list mngId
	 */
	void deleteInterimBreakMng(List<String> mngIds);
	
	/**
	 * 暫定代休管理データ 　を追加および更新
	 * @param domain
	 */
	void persistAndUpdateInterimDayOffMng(InterimDayOffMng domain);
	
	/**
	 * 暫定代休管理データ  を削除
	 * @param mngId
	 */
	void deleteInterimDayOffMng(String mngId);
	
	/**
	 * 暫定代休管理データ  を削除
	 * @param list mngId
	 */
	void deleteInterimDayOffMng(List<String> mngIds);
	
	/**
	 * 暫定休出代休紐付け管理 　を追加および更新
	 * @param domain
	 */
	void persistAndUpdateInterimBreakDayOffMng(InterimBreakDayOffMng domain);
	
	/**
	 * 暫定休出代休紐付け管理  を削除
	 * @param mngId
	 * @param isBreak：True：　休出、False：代休
	 */
	void deleteBreakDayOffById(String mngId, boolean isBreak);
	
	/**
	 * 暫定休出代休紐付け管理 を削除
	 * @param breakId　休出
	 * @param dayOffId　代休
	 * @param breakAtr　休出管理データ区分
	 * @param dayOffAtr　休出管理データ区分
	 */
	void deleteBreakDayOfByIdAndAtr(String breakId, String dayOffId, DataManagementAtr breakAtr, DataManagementAtr dayOffAtr);
	
	/**
	 * 暫定休出代休紐付け管理  を削除
	 * @param mngId
	 * @param mngAtr
	 * @param isBreak
	 */
	void deleteBreakDayOfById(String mngId, DataManagementAtr mngAtr, boolean isBreak);
	/**
	 * ドメインモデル「暫定休出代休紐付け管理」を取得する
	 * @param breakAtr 休出状態
	 * @param dayOffAtr 代休状態
	 * @param dayOffId 代休＝INPUT.代休管理データ.代休データID
	 * @return
	 */
	List<InterimBreakDayOffMng> getDayOffByIdAndDataAtr(DataManagementAtr breakAtr, DataManagementAtr dayOffAtr, String dayOffId);
	/**
	 * ドメインモデル「暫定休出代休紐付け管理」を取得する
	 * @param breakAtr 休出状態
	 * @param dayOffAtr 代休状態
	 * @param breakId 休出ID
	 * @return
	 */
	List<InterimBreakDayOffMng> getBreakByIdAndDataAtr(DataManagementAtr breakAtr, DataManagementAtr dayOffAtr, String breakId);
	
	void deleteInterimDayOffMngBySidAndYmd(String sid, GeneralDate ymd);
	void deleteInterimBreakMngBySidAndYmd(String sid, GeneralDate ymd);
	
	// [4] 削除する 暫定休出管理データ
	void deleteBreakoffWithPeriod(String sid, DatePeriod period);

	// [5] Insert(List<暫定休出管理データ>) 暫定休出管理データ
	void insertBreakoffList(List<InterimBreakMng> lstDomain);
	
	// [4] 削除する 	暫定代休管理データ
	void deleteDayoffWithPeriod(String sid, DatePeriod period);
	
	// [5] Insert(List<暫定休出管理データ>) 	暫定代休管理データ
	void insertDayoffList(List<InterimDayOffMng> lstDomain);
	
	// [6] 削除する 暫定休出管理データ
	void deleteBreakoffWithDateList(String sid, List<GeneralDate> lstDate);
	
	// [7] 削除する 暫定代休管理データ
	void deleteDayoffWithDateList(String sid, List<GeneralDate> lstDate);
	
	List<InterimBreakMng> getBreakBySidDateList(String sid, List<GeneralDate> lstDate);
	
	List<InterimDayOffMng> getDayOffDateList(String sid, List<GeneralDate> lstDate);
	
	Optional<InterimDayOffMng> getDayOffByDate(String sid, GeneralDate date);
	
	List<InterimDayOffMng> getDayOffByIds(List<String> remainManaIds);
}
