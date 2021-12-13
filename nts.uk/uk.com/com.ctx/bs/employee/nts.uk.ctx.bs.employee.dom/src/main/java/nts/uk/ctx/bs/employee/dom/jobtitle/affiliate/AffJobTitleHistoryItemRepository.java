package nts.uk.ctx.bs.employee.dom.jobtitle.affiliate;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

public interface AffJobTitleHistoryItemRepository {
	
	/**
	 * [3] 全ての職位履歴を取得する
	 * 
	 * 指定社員リストの期間内の所属職場履歴項目を取得する
	 * 
	 * @param sids List<社員ID>
	 * @param datePeriod 期間
	 * @return 職位履歴リスト List<期間付き職位履歴項目>
	 */
	List<AffJobTitleHistoryItemWithPeriod> getBySidAndDatePeriod(List<String> sids, DatePeriod datePeriod);
	
	/**
	 * @param employeeId
	 * @param referDate
	 * @return
	 */
	Optional<AffJobTitleHistoryItem> getByEmpIdAndReferDate(String employeeId, GeneralDate referDate);
	
	/**
	 * find with primary key
	 * @param historyId
	 * @return
	 */
	Optional<AffJobTitleHistoryItem> findByHitoryId(String historyId);
	
	/**
	 * ドメインモデル「職務職位」を新規登録する
	 * 
	 * @param domain
	 */
	void add(AffJobTitleHistoryItem domain);

	/**
	 * 取得した「職務職位」を更新する
	 * 
	 * @param domain
	 */
	void update(AffJobTitleHistoryItem domain);

	/**
	 * ドメインモデル「職務職位」を削除する
	 * 
	 * @param jobTitleMainId
	 */
	void delete(String jobTitleMainId);
	
	List<AffJobTitleHistoryItem> getByJobIdAndReferDate(String jobId, GeneralDate referDate);
	
	List<AffJobTitleHistoryItem> getAllBySid(String sid);
	
	List<AffJobTitleHistoryItem> getAllByListSidDate(List<String> lstSid, GeneralDate referDate);
	
	/**
	 * [2] Get*
	 * 
	 * 履歴IDリストに該当する所属職位履歴項目を取得する
	 * 
	 * get by historyId list 
	 * @param historyIds List<履歴ID>
	 * @return 履歴項目リスト List<所属職位履歴項目>
	 */
	List<AffJobTitleHistoryItem> findByHitoryIds(List<String> historyIds);
	
	// request list 551
	List<AffJobTitleHistoryItem> findHistJob(String companyId, GeneralDate baseDate, List<String> jobIds);
	
	/**
	 * ドメインモデル「職務職位」を新規登録する
	 * 
	 * @param domain
	 */
	void addAll(List<AffJobTitleHistoryItem> domains);
	
	/**
	 * 取得した「職務職位」を更新する
	 * 
	 * @param domain
	 */
	void updateAll(List<AffJobTitleHistoryItem> domains);

}
