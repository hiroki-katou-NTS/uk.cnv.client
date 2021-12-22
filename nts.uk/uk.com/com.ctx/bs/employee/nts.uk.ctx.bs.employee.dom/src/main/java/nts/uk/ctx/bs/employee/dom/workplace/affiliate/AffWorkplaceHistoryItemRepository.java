package nts.uk.ctx.bs.employee.dom.workplace.affiliate;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

import java.util.List;
import java.util.Optional;

public interface AffWorkplaceHistoryItemRepository {
	/**
	 * ドメインモッ�「所属�場」を新規登録する
	 * @param domain
	 */
	void add(AffWorkplaceHistoryItem domain);
	/**
	 * ドメインモッ�「所属�場」を削除する
	 * @param domain
	 */
	void delete(String histID);
	
	/**
	 * ドメインモッ�「所属�場」を取得す�
	 * @param domain
	 */
	void update(AffWorkplaceHistoryItem domain);
	
	Optional<AffWorkplaceHistoryItem> getByHistId(String historyId);
	
	List<AffWorkplaceHistoryItem> getAffWrkplaHistItemByListEmpIdAndDate(GeneralDate basedate, List<String> employeeId);
	
	List<AffWorkplaceHistoryItem> getAffWrkplaHistItemByListEmpIdAndDateV2(GeneralDate basedate, List<String> employeeId);
	
	List<AffWorkplaceHistoryItem> getAffWrkplaHistItemByListWkpIdAndDate(GeneralDate basedate, List<String> workplaceId);
	
	List<AffWorkplaceHistoryItem> getAffWrkplaHistItemByEmpIdAndDate(GeneralDate basedate, String employeeId);
	
	List<AffWorkplaceHistoryItem> findByHistIds(List<String> hisIds);
	
	List<AffWorkplaceHistoryItem> findeByWplIDs(List<String> wplIDs);
	
	List<AffWorkplaceHistoryItem> getAffWkpHistItemByListWkpIdAndDatePeriod(DatePeriod basedate, List<String> workplaceId);
	
	List<String> getSidByListWkpIdAndDatePeriod(DatePeriod basedate, List<String> workplaceId);

	/**
	 * @author lanlt
	 * ドメインモッ�「所属�場」を新規登録する
	 * @param domain
	 */
	void addAll(List<AffWorkplaceHistoryItem> domain);
	
	/**
	 * @author lanlt
	 * ドメインモッ�「所属�場」を取得す�
	 * @param domain
	 */
	void updateAll(List<AffWorkplaceHistoryItem> domain);
	
	List<String> getHistIdLstBySidAndPeriod(String sid, DatePeriod period);
	
	List<String> getHistIdLstByWorkplaceIdsAndPeriod(List<String> workplaceIds, DatePeriod period);

//	List<String> getSIDByListWklocationId(List<String> workLocationCDS);

	//for file query : 注文情報を作る
	List<String> getSIDByListWklocationCode(List<String> workLocationCode);
}
