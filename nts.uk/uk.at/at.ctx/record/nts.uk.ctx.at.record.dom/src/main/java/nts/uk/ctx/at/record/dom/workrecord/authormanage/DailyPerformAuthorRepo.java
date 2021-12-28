/**
 * 
 */
package nts.uk.ctx.at.record.dom.workrecord.authormanage;

import java.util.List;

/**
 * @author danpv
 *
 */
public interface DailyPerformAuthorRepo {

	public List<DailyPerformanceAuthority> get(String roleId);
	
	public List<DailyPerformanceAuthority> findByCid(String cid);
	
	public List<DailyPerformanceAuthority> findByCidAndRole(String cid, String roleId);

	public void save(DailyPerformanceAuthority daiPerAuthority);

	/**
	 * ログイン社員の就業帳票の権限を取得する
	 * 
	 * @param roleId ロールID
	 * @param functionNo: 機能NO
	 * @param available: 利用できる
	 * @return
	 */
	public boolean getAuthorityOfEmployee(String roleId, DailyPerformanceFunctionNo functionNo, boolean available);
	
	public void copy(String companyId, List<DailyPerformanceAuthority> sourceData, List<String> targetRoleList);
}
