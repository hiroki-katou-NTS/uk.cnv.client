package nts.uk.ctx.sys.auth.dom.grant.rolesetperson;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;

/**
 * 
 * @author HungTT
 *
 */

public interface RoleSetGrantedPersonRepository {

	public boolean checkRoleSetCdExist(String roleSetCd, String companyId);
	
	public List<RoleSetGrantedPerson> getAll(String roleSetCd, String companyId);
	
	public Optional<RoleSetGrantedPerson> getByEmployeeId(String employeeId);
	
	// update EAP 2709
	public Optional<RoleSetGrantedPerson> getByEmployeeDate(String employeeId, GeneralDate baseDate);
	
	public void insert(RoleSetGrantedPerson domain);
	
	public void update(RoleSetGrantedPerson domain);
	
	public void delete(String employeeId);
	
	public Optional<RoleSetGrantedPerson> findByIDAndDate (String companyId , String employeeID , GeneralDate date);
	
	public Optional<RoleSetGrantedPerson> findByDetail (String companyID , String employeeID , List<String> roleSetCDLst, GeneralDate date);
	/**
	 * @author hoatt
	 * phuc vu RQ139
	 * get list employeeId by:
	 * @param companyID 会社ID←ログイン会社ID
	 * @param lstSid 社員ID←取得した社員ID（list）（１）
	 * @param roleSetCDLst ロールセットコード←取得したロールセット．コード(list)
	 * @param baseDate 期間From <= 基準日 <= 期間To
	 * @return ロールセット個人別付与．社員ID（list）・・・（２）
	 */
	public List<String> getSidByRoleSidDate(String companyID , List<String> lstSid , List<String> roleSetCDLst, GeneralDate date);
	
}
