package nts.uk.ctx.at.record.dom.stamp.card.stampcard;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 
 * 打刻カードRepository
 */
public interface StampCardRepository {


	/**
	 * 追加する(打刻カード)
	 * 
	 * [1] insert(打刻カード)
	 * 
	 * @param domain
	 */
	void add(StampCard domain);
	
	/**
	 * [2] update(打刻カード)

	 * @param domain
	 */
	void update(StampCard domain);
	
	/**
	 * 	[3] delete(打刻カード)
	 * @param domain
	 */
	void delete(StampCard domain);
	
	/**
	 * 	[4] 取得する
     * 	打刻カード番号から打刻カード情報を取得する
	 * @param contractCd
	 * @return
	 */
	Optional<StampCard> getByCardNoAndContractCode(String cardNo , String contractCd);
	
	/**
	 * [5] 取得する 社員IDから打刻カード情報を取得する
	 * 
	 * @param contractCd
	 * @param sid
	 * @return
	 */
	List<StampCard> getLstStampCardBySidAndContractCd(String contractCd, String sid);
	
	
	List<StampCard> getListStampCard(String sid);
	
	List<String> getListStampCardByContractCode(String contractCode);
	
	List<StampCard> getLstStampCardByContractCode(String contractCode);
	
	List<StampCard> getLstStampCardByLstSid(List<String> sids);

	Optional<StampCard> getByStampCardId(String stampCardId);
	
	Map<String, List<StampCard>> getByStampCardId(String contractCd, List<String> stampCardId);
	
	Map<String, StampCard> getByCardNoAndContractCode(Map<String, String> cardNos , String contractCd);
	
	Optional<String> getLastCardNo(String contractCode, String startCardNoLetters, int length);
	
	Optional<StampCard> getStampCardByEmployeeCardNumber(String employeeId, String cardNumber);
	
	Optional<StampCard> getStampCardByContractCdEmployeeCardNumber(String contractCd,String sid, String cardNumber);

	List<StampCard> getListStampCardByCardNumber(String cardNos);
	/**
	 * @author lanlt
	 * @param domains
	 */
	void addAll(List<StampCard> domains);
	
	
	/**
	 * @author lanlt
	 * @param domains
	 */
	void updateAll(List<StampCard> domains);

	void delete(String stampCardId);
	
	void deleteByTenantCode(String tenantCode);
	
	void deleteBySid(String sid);
	
	/**
	 * @author lanlt
	 * @param sids
	 * @param contractCd
	 * @return
	 */
	List<StampCard> getLstStampCardByLstSidAndContractCd(List<String> sids, String contractCd);

	List<StampCard> getListStampCardByCardNumbers(List<String> cardNos);
	
	List<StampCard> getListStampCardByCardNumbersAndContract(List<String> cardNos, String contractCode);

}
