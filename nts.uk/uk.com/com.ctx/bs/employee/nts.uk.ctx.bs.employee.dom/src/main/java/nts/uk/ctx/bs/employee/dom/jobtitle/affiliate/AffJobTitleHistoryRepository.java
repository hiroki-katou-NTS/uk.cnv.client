package nts.uk.ctx.bs.employee.dom.jobtitle.affiliate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.arc.time.calendar.period.DatePeriod;

public interface AffJobTitleHistoryRepository {
	
	/**
	 * [4] 基準日時点に所属職位履歴変更している社員を取得する
	 * 
	 * 基準日時点に所属職位履歴変更している社員を取得する
	 * 
	 * @param sids List<社員ID>
	 * @param baseDate 年月日
	 * @return 社員リスト List<社員ID>
	 */
	List<String> getBySidsAndBaseDate(List<String> sids, GeneralDate baseDate);
	
	/**
	 * [5] 期間内に履歴変更している社員を取得する
	 * 
	 * 開始日が期間内にが含む履歴がある社員を取得する
	 * 
	 * @param datePeriod 期間
	 * @return 社員リスト List<社員ID>
	 */
	List<String> getByDatePeriod(String cid, DatePeriod datePeriod);
	
	/**
	 * get with primary key
	 * @param historyId
	 * @return
	 */
	Optional<AffJobTitleHistory> getByHistoryId(String historyId);
	
	/**
	 * get with employeeId and standard date
	 * @param employeeId
	 * @param standardDate
	 * @return
	 */
	Optional<AffJobTitleHistory> getByEmpIdAndStandardDate(String employeeId, GeneralDate standardDate);
	
	// get listbypid
	Optional<AffJobTitleHistory> getListBySid(String cid, String sid);
	
	Optional<AffJobTitleHistory> getListBySidDesc(String cid, String sid);
	

	/**
	 * ドメインモッ�「�務�位」を新規登録する
	 * 
	 * @param item
	 * @param sid
	 * @param cid
	 */
	void add(String cid, String sid, DateHistoryItem item);

	/**
	 * 取得した「�務�位」を更新する
	 * 
	 * @param item
	 */
	void update(DateHistoryItem item);

	/**
	 * ドメインモッ�「�務�位」を削除する
	 * 
	 * @param histId
	 */
	void delete(String histId);
	
	List<AffJobTitleHistory> getAllBySid(String sid);
	
	Optional<AffJobTitleHistory> getListByHidSid(String hid, String sid);
	
	List<AffJobTitleHistory> getListByListHidSid(List<String> hid, List<String> sid);
	
	List<AffJobTitleHistory> getListByListHidSid(List<String> sid, GeneralDate targetDate);
	
	List<DateHistoryItem> getListByListSidsNoWithPeriod(String cid, List<String> sid);
	
	/**
	 * Search job title history.
	 *
	 * @param baseDate the base date
	 * @param employeeId the employee id
	 * @return the list
	 */
	List<AffJobTitleHistory> searchJobTitleHistory(GeneralDate baseDate, List<String> employeeId, List<String> jobTitleIds);
	
	 /**
 	 * Find all job title history.
 	 *
 	 * @param baseDate the base date
 	 * @param employeeIds the employee ids
 	 * @return the list
 	 */
 	List<AffJobTitleHistory> findAllJobTitleHistory(GeneralDate baseDate, List<String> employeeIds) ;
 	
 	/**
 	 * [1] Get*
 	 * 
 	 * 指定社員リストと期間のすべての所属職位履歴を取得する
 	 * 
	 * request-list 398
	 * @param employeeIds List<社員ID>
	 * @param period 期間
	 * @return List<所属職位履歴> 職位履歴
	 */
	List<AffJobTitleHistory> getByEmployeeListPeriod(List<String> employeeIds, DatePeriod period);
	
	/**
	 * request list 515
	 * @param employeeIds
	 * @param period
	 * @return
	 * @author yennth
	 */
	Optional<AffJobTitleHistory> getListEmployee(GeneralDate baseDate, String cid);
	
	Optional<SingleHistoryItem> getSingleHistoryItem(String employeeId, GeneralDate baseDate);
	
	/**
	 * getListBySids
	 * @author lanlt
	 * @param cid
	 * @param sids
	 * @return
	 */
	List<AffJobTitleHistory> getListBySids(String cid, List<String> sids);
	
	/**
	 * ドメインモッ�「�務�位」を新規登録する
	 * @author lanlt
	 * @param item
	 * @param sid
	 * @param cid
	 */
	void addAll(Map<String, DateHistoryItem> items);
	/**
	 * 取得した「�務�位」を更新する
	 * @author lanlt
	 * @param item
	 */
	void updateAll(List<DateHistoryItem> items);
	@Value
	public static class SingleHistoryItem {
		
		private final String employeeId;
		private final String historyId;
		private final DatePeriod period;
		private final String jobTitleId;
		private final String note;
	}
}
