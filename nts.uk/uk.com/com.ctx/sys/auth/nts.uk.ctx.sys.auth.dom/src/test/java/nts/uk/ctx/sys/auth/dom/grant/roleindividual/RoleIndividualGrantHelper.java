package nts.uk.ctx.sys.auth.dom.grant.roleindividual;

import java.util.Optional;

import mockit.Injectable;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.role.EmployeeReferenceRange;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleAtr;
import nts.uk.ctx.sys.auth.dom.role.RoleCode;
import nts.uk.ctx.sys.auth.dom.role.RoleName;
import nts.uk.ctx.sys.auth.dom.role.RoleType;
import nts.uk.ctx.sys.shared.dom.user.ContractCode;
import nts.uk.ctx.sys.shared.dom.user.DisabledSegment;
import nts.uk.ctx.sys.shared.dom.user.LoginID;
import nts.uk.ctx.sys.shared.dom.user.User;

public class RoleIndividualGrantHelper {
	@Injectable
	private static DatePeriod validPeriod;
	/**
	 * ロールを作る
	 * @param cid 会社ID
	 * @param roleId ロールID
	 * @param roleType ロール種類
	 * @return
	 */
	public static Role createRole(String cid, String roleId, RoleType  roleType) {
		
		return new Role ( roleId
				,	new nts.uk.ctx.sys.auth.dom.role.ContractCode("contractCode")
				,	cid
				,	new RoleCode( "code" )
				,	new RoleName( "name" )
				,	roleType
				,	RoleAtr.GENERAL
				,	EmployeeReferenceRange.DEPARTMENT_ONLY
				,	Optional.empty() );
	}
	
	/**
	 * ロール個人別付与を作る
	 * @param userId ユーザID
	 * @param roleType ロール種類
	 * @param validPeriod 期間
	 * @return
	 */
	public static RoleIndividualGrant createRoleIndividualGrant(
				String userId
			,	RoleType roleType
			,	DatePeriod validPeriod ) {
		
		return new RoleIndividualGrant( userId, "companyId", roleType, "roleId", validPeriod );
		
	}
	
	/**
	 * ユーザを作る
	 * @param defaultUser デフォルトユーザ
	 * @return
	 */
	public static User createUser( boolean defaultUser ) {
		
		return new User( "userID"
				,	defaultUser
				,	new LoginID("loginID")
				,	new ContractCode( "contractCode" )
				,	GeneralDate.ymd(9999, 12, 21)
				,	DisabledSegment.True
				,	DisabledSegment.True
				,	Optional.empty()
				,	Optional.empty()
				,	Optional.empty());
	}
	
	/**
	 * ユーザを作る
	 * @param userId ユーザID
	 * @param expirationDate 有効期限
	 * @return
	 */
	public static User createUser( String userId, GeneralDate expirationDate) {
		
		return new User( userId
				,	false
				,	new LoginID("loginID")
				,	new ContractCode( "contractCode" )
				,	expirationDate
				,	DisabledSegment.True
				,	DisabledSegment.True
				,	Optional.empty()
				,	Optional.empty()
				,	Optional.empty());
	}
	
	/**
	 * システム管理者ロールの付与情報を作成する
	 * @param userId ユーザID
	 * @param period 期間
	 * @return
	 */
	public static RoleIndividualGrant createGrantInfoOfSystemMananger(
				String userId
			,	DatePeriod period) {
		
		return new RoleIndividualGrant(
					userId
				,	"companyId" //DUMMY
				,	RoleType.SYSTEM_MANAGER //DUMMY
				,	"roleId" //DUMMY
				,	period );
	}
	
	/**
	 * システム管理者ロールの付与情報を作成する
	 * @param userId ユーザID
	 * @return
	 */
	public static RoleIndividualGrant createGrantInfoOfSystemMananger( String userId ) {
		
		return new RoleIndividualGrant(
					userId
				,	"companyId" //DUMMY
				,	RoleType.SYSTEM_MANAGER //DUMMY
				,	"roleId" //DUMMY
				,	validPeriod );
	}
	
	/**
	 * ロールの付与情報を作成する
	 * @param userId ユーザID
	 * @param cid 会社ID
	 * @param roleType ロール種類
	 * @return
	 */
	public static RoleIndividualGrant createRoleIndividualGrant( String userId, String cid, RoleType roleType ) {
		
		return new RoleIndividualGrant(
					userId
				,	cid
				,	roleType
				,	"roleId" //DUMMY
				,	validPeriod );
	}

}
