package nts.uk.ctx.sys.auth.dom.grant.rolesetjob;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.sys.auth.dom.roleset.RoleSetCode;

/**
 * 
 * @author HungTT
 *
 */

public interface RoleSetGrantedJobTitleRepository {
	
	/**
	 * get*
	 * @param companyId 会社ID
	 * @return List<ロールセット職位別付与>
	 */
	public List<RoleSetGrantedJobTitle> getByCompanyId(String companyId);
	
	/**
	 * get
	 * @param companyId 会社ID
	 * @param jobTitleId 職位ID
	 * @return Optional<ロールセット職位別付与>
	 */
	public Optional<RoleSetGrantedJobTitle> getByJobTitleId(String companyId, String jobTitleId);

	/**
	 * insert
	 * @param domain ロールセット職位別付与
	 */
	public void insert(RoleSetGrantedJobTitle domain);
	
	/**
	 * update
	 * @param domain ロールセット職位別付与
	 */
	public void update(RoleSetGrantedJobTitle domain);
	
	/**
	 * 指定するロールセットが付与されているか
	 * @param companyId 会社ID
	 * @param roleSetCd ロールセットコード
	 * @return boolean
	 */
	public boolean checkRoleSetCdExist(String companyId, RoleSetCode roleSetCd);
	
	// 1次のメソッド
	public List<String> findJobTitleByRoleCDLst(String companyID, List<String> roleCDLst);
}
