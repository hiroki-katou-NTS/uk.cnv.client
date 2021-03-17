package nts.uk.ctx.at.record.dom.stampmanagement.workplace;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.uk.shr.com.net.Ipv4Address;
/**
 * 
 * @author hieult
 *
 */
public interface WorkLocationRepository {
	
	/**
	 * [1]  insert(勤務場所)
	 */
	void insertWorkLocation(WorkLocation workLocation);
	
	/**
	 * [2]  update(勤務場所)
	 */
	void updateWorkLocation(WorkLocation workLocation);
	
	/**
	 * [３]  delete(勤務場所)
	 */
	void deleteWorkLocation(String contractCode, String workLocationCD);
	
	/**
	 * [4] 契約コード条件として勤務場所を取得する
	 */
	List<WorkLocation> findAll (String contractCode);
	
	/**
	 * [5] 契約コードと勤務場所コードで勤務場所を取得する
	 */
	Optional<WorkLocation> findByCode (String contractCode, String workLocationCD); 
	
	/**
	 * [6] 勤務場所を選択する時、IPアドレス設定を取得する
	 */
	List<Ipv4Address> getIPAddressSettings(String contractCode, String workLocationCD);
	
	/**
	 * [7] 契約コード、startIPとendIPでIPアドレスを取得する。
	 */
	List<Ipv4Address> getIPAddressByStartEndIP(String contractCode, int net1, int net2, int host1, int host2, int endIP);
	
	/**
	 * [8] IPで、IPアドレス設定を取得する。
	 */
	List<Ipv4Address> getIPAddressByIP(String contractCode, int net1, int net2, int host1, int host2);
	
	/**
	 * [9] IPアドレス設定を削除する。
	 */
	void deleteByIP(String contractCode, String workLocationCD, int net1, int net2, int host1, int host2);
	
	/**
	 * [10] 取得する
	 */
	List<WorkLocation> findByCodes(String contractCode, List<String> codes);

	Map<String, String> getNameByCode(String contractCode, List<String> listWorkLocationCd);
	
}
