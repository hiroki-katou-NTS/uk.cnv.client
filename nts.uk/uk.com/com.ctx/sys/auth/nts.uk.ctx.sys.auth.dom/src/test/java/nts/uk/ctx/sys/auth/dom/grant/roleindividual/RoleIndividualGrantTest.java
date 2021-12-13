package nts.uk.ctx.sys.auth.dom.grant.roleindividual;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.role.RoleType;
@RunWith(JMockit.class)
public class RoleIndividualGrantTest {
	
	@Injectable
	RoleIndividualGrant.Require require;
	
	@Test
	public void getters() {
		
		val roleGrant = new RoleIndividualGrant( "userId"
				,	"companyId", RoleType.PERSONAL_INFO
				,	"roleId"
				,	new DatePeriod( GeneralDate.ymd(2000, 1, 1), GeneralDate.ymd( 9999, 12, 31))
					);
		
		NtsAssert.invokeGetters( roleGrant );
		
	}
	
	/**
	 * target: createFromRole
	 */
	@Test
	public void testCreateFromRole() {
		
		val role = RoleIndividualGrantHelper.createRole( "company-id-of-role", "roleId", RoleType.PERSONAL_INFO );
		val grantTargetUser = "userId";
		val grantTargetCompany = "cid";
		val validPeriod = new DatePeriod( GeneralDate.ymd(2000, 1, 1), GeneralDate.ymd( 9999, 12, 31));
		
		//Act
		RoleIndividualGrant result = NtsAssert.Invoke.staticMethod(	RoleIndividualGrant.class, "createFromRole"
				,	role, grantTargetUser, grantTargetCompany, validPeriod );
		
		//Assert
		assertThat( result.getUserId()).isEqualTo( grantTargetUser );
		assertThat( result.getCompanyId() ).isEqualTo( grantTargetCompany );
		assertThat( result.getRoleType() ).isEqualTo( role.getRoleType() );
		assertThat( result.getRoleId() ).isEqualTo( role.getRoleId() );
		assertThat( result.getValidPeriod().start() ).isEqualTo( validPeriod.start() );
		assertThat( result.getValidPeriod().end() ).isEqualTo( validPeriod.end() );
		
	}

	/**
	 * target : createGrantInfoOfSystemMananger
	 */
	@Test
	public void testCreateGrantInfoOfSystemMananger() {
		val grantTargetUser = "userId";
		val period = new DatePeriod( GeneralDate.ymd(2000, 1, 1), GeneralDate.ymd( 9999, 12, 31));
		val role = RoleIndividualGrantHelper.createRole("cid", "roleId", RoleType.SYSTEM_MANAGER);
		
		new Expectations( ) {
			{
				require.getRoleByRoleType( RoleType.SYSTEM_MANAGER );
				result = role;
			}
		};
		
		//Act
		val result = RoleIndividualGrant.createGrantInfoOfSystemMananger( require, grantTargetUser, period );
		
		//Assert
		assertThat( result.getUserId() ).isEqualTo( grantTargetUser );
		assertThat( result.getCompanyId() ).isEqualTo( role.getCompanyId() );
		assertThat( result.getRoleType() ).isEqualTo( RoleType.SYSTEM_MANAGER );
		assertThat( result.getRoleId() ).isEqualTo( role.getRoleId() );
		assertThat( result.getValidPeriod().start() ).isEqualTo( period.start() );
		assertThat( result.getValidPeriod().end() ).isEqualTo( period.end() );
		
	}
	
	/**
	 * target : createGrantInfoOfCompanyManager
	 */	
	@Test
	public void testCreateGrantInfoOfCompanyManager() {
		val grantCompanyId = "cid";
		val grantTargetUser = "userId";
		val validPeriod = new DatePeriod( GeneralDate.ymd(2000, 1, 1), GeneralDate.ymd( 9999, 12, 31));
		val role = RoleIndividualGrantHelper.createRole("company-id-of-role", "role-id", RoleType.COMPANY_MANAGER);
		
		new Expectations() {
			{
				require.getRoleByRoleType( RoleType.COMPANY_MANAGER );
				result = role ;
			}
		};
		
		//Act
		val result = RoleIndividualGrant.createGrantInfoOfCompanyManager(require, grantTargetUser, grantCompanyId, validPeriod );
		
		//Assert
		assertThat( result.getUserId() ).isEqualTo( grantTargetUser );
		assertThat( result.getCompanyId() ).isEqualTo( grantCompanyId );
		assertThat( result.getRoleType() ).isEqualTo( RoleType.COMPANY_MANAGER );
		assertThat( result.getRoleId() ).isEqualTo( role.getRoleId() );
		assertThat( result.getValidPeriod().start() ).isEqualTo( validPeriod.start() );
		assertThat( result.getValidPeriod().end() ).isEqualTo( validPeriod.end() );
		
	}
	
	/**
	 * target : checkStatusNormal
	 * pattern : 管理者ロールか = false
	 * excepted: system error
	 */
	@Test
	public void testCheckStatusNormal_inv_1() {
		
		val validPeriod = new DatePeriod(GeneralDate.ymd(2000, 1, 1), GeneralDate.ymd(9999, 12, 31));
		val roleGrant = RoleIndividualGrantHelper.createRoleIndividualGrant(
					"userId" //dummy
				,	RoleType.PERSONAL_INFO //管理者ロールか = false
				,	validPeriod );//dummy
		
		NtsAssert.systemError(() -> {
			
			roleGrant.checkStatusNormal(require);
			
		});
		
	}
	
	/**
	 * target : checkStatusNormal
	 * pattern : 管理者ロールか = true, ユーザ = empty
	 * excepted: Msg_2210
	 */
	@Test
	public void testCheckStatusNormal_user_empty() {
		
		val validPeriod = new DatePeriod(GeneralDate.ymd(2000, 1, 1), GeneralDate.ymd(9999, 12, 31));
		val roleGrant = RoleIndividualGrantHelper.createRoleIndividualGrant(
					"userId" //dummy
				,	RoleType.SYSTEM_MANAGER //システム管理者
				,	validPeriod ); //dummy
		
		new Expectations (){
			{
				require.getUser( (String) any );
			}
		};
		
		NtsAssert.businessException( "Msg_2210", () -> {
			
			roleGrant.checkStatusNormal(require);
			
		});
	}
	
	/**
	 * target : checkStatusNormal
	 * pattern : 管理者ロールか = true, ユーザがデフォルトユーザ
	 * excepted: system error
	 */
	@Test	
	public void testCheckStatusNormal_default_user() {
		
		val user = RoleIndividualGrantHelper.createUser( true);// デフォルトユーザ
		val validPeriod = new DatePeriod(GeneralDate.ymd(2000, 1, 1), GeneralDate.ymd(9999, 12, 31));
		val roleGrant = RoleIndividualGrantHelper.createRoleIndividualGrant(
					"userId" //dummy
				,	RoleType.SYSTEM_MANAGER //システム管理者
				,	validPeriod ); //dummy
		
		new Expectations (){
			{
				require.getUser( (String) any );
				result = Optional.of( user );
			}
		};
		
		NtsAssert.systemError( () -> {
			
			roleGrant.checkStatusNormal(require);
			
		});
	}
	
	/**
	 * target : checkStatusNormal
	 * pattern : $ユーザ.有効期限 < @有効期間.開始日
	 * excepted: Msg_2211 
	 */	
	@Test
	public void testCheckStatusNormal_user_expired() {
		
		val user = RoleIndividualGrantHelper.createUser( "userId", GeneralDate.ymd(2020, 12, 31) );//有効期限= 2020/12/31
		val validPeriod = new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2020, 12, 31));//有効期間.開始日 = 2021/01/01
		val roleGrant = RoleIndividualGrantHelper.createRoleIndividualGrant(
					"userId" //dummy
				,	RoleType.SYSTEM_MANAGER //システム管理者
				,	validPeriod ); //dummy
		
		new Expectations (){
			{
				require.getUser( (String) any );
				result = Optional.of( user );
			}
		};
		
		NtsAssert.businessException( "Msg_2211", () -> {
			
			roleGrant.checkStatusNormal(require);
			
		});
	}
	
	
	/**
	 * target : checkStatusNormal
	 * pattern : ロール種類 = 会社管理者, not デフォルトユーザ
	 * excepted: error がない
	 */
	@Test	
	public void testCheckStatusNormal_not_default_user() {
		
		val user = RoleIndividualGrantHelper.createUser( false );// デフォルトユーザ
		val validPeriod = new DatePeriod(GeneralDate.ymd(2000, 1, 1), GeneralDate.ymd(9999, 12, 31));
		val roleGrant = RoleIndividualGrantHelper.createRoleIndividualGrant(
					"userId" //dummy
				,	RoleType.COMPANY_MANAGER //会社管理者
				,	validPeriod ); //dummy
		
		new Expectations (){
			{
				require.getUser( (String) any );
				result = Optional.of( user );
			}
		};
		
		roleGrant.checkStatusNormal(require);
	}
	
	/**
	 * target: getCorrectedValidPeriodByUserInfo
	 * pattern: ユーザ = empty
	 * excepted: empty
	 */
	@Test
	public void testGetCorrectedValidPeriodByUserInfo_user_empty() {
		
		val validPeriod = new DatePeriod(GeneralDate.ymd(2000, 1, 1), GeneralDate.ymd(9999, 12, 31));
		val roleGrant = RoleIndividualGrantHelper.createRoleIndividualGrant(
					"userId" //dummy
				,	RoleType.COMPANY_MANAGER //dummy
				,	validPeriod );//dummy
		
		new Expectations (){
			{
				require.getUser( (String) any );
			}
		};
		
		//Act
		val result = roleGrant.getCorrectedValidPeriodByUserInfo( require );
		
		//Assert
		assertThat( result ).isEmpty();
		
	}
	
	/**
	 * target: getCorrectedValidPeriodByUserInfo
	 * pattern: $ユーザ.有効期限 < @有効期間.開始日
	 * excepted: empty
	 */
	@Test
	public void testGetCorrectedValidPeriodByUserInfo_expirationDateOfUser_before_startDateOfRole() {
		
		//ユーザ.有効期限 = 2000/12/31
		val user = RoleIndividualGrantHelper.createUser( "userId", GeneralDate.ymd(2000, 12, 31) );
		//有効期間.開始日 = 2016/01/01
		val validPeriod = new DatePeriod( GeneralDate.ymd(2016, 1, 1), GeneralDate.ymd(9999, 12, 31) );
		val roleGrant = RoleIndividualGrantHelper.createRoleIndividualGrant(
					"userId" //dummy
				,	RoleType.COMPANY_MANAGER //dummy
				,	validPeriod );
		
		new Expectations (){
			{
				require.getUser( (String) any );
				result = Optional.of( user );
			}
		};
		
		//Act
		val result = roleGrant.getCorrectedValidPeriodByUserInfo( require );
		
		//Assert
		assertThat( result ).isEmpty();
		
	}
	
	/**
	 * target: getCorrectedValidPeriodByUserInfo
	 * pattern: $ユーザ.有効期限 < @有効期間.終了日
	 * excepted: 開始日 = @有効期間.開始日, 終了日 = 有効期間.終了日
	 */
	@Test
	public void testGetCorrectedValidPeriodByUserInfo_endDateOfRole_before_expirationDateOfUser() {
		
		//ユーザ.有効期限 = 9999/12/31
		val user = RoleIndividualGrantHelper.createUser( "userId", GeneralDate.ymd(9999, 12, 31) );
		//有効期間.終了日 = 2030/12/31
		val validPeriod = new DatePeriod( GeneralDate.ymd(2016, 1, 1), GeneralDate.ymd(2030, 12, 31) );
		val roleGrant = RoleIndividualGrantHelper.createRoleIndividualGrant(
					"userId" //dummy
				,	RoleType.COMPANY_MANAGER //dummy
				,	validPeriod );
		
		new Expectations (){
			{
				require.getUser( (String) any );
				result = Optional.of( user );
			}
		};
		
		//Act
		val result = roleGrant.getCorrectedValidPeriodByUserInfo( require );
		
		//Assert
		assertThat( result.get().start() ).isEqualTo( GeneralDate.ymd(2016, 1, 1) );
		assertThat( result.get().end() ).isEqualTo( GeneralDate.ymd(2030, 12, 31) );
		
	}
	
	/**
	 * target: getCorrectedValidPeriodByUserInfo
	 * pattern: @有効期間.終了日 > $ユーザ.有効期限  
	 * excepted: 開始日 = @有効期間.開始日, 終了日 = $ユーザ.有効期限  
	 */
	@Test
	public void testGetCorrectedValidPeriodByUserInfo_endDateOfRole_after_expirationDateOfUser() {
		
		//ユーザ.有効期限 = 2030/1/1
		val user = RoleIndividualGrantHelper.createUser( "userId", GeneralDate.ymd(2030, 1, 1) );
		//有効期間.終了日 = 2030/12/31
		val validPeriod = new DatePeriod( GeneralDate.ymd(2016, 1, 1), GeneralDate.ymd(2030, 12, 31) );
		val roleGrant = RoleIndividualGrantHelper.createRoleIndividualGrant(
					"userId" //dummy
				,	RoleType.COMPANY_MANAGER //dummy
				,	validPeriod );
		
		new Expectations (){
			{
				require.getUser( (String) any );
				result = Optional.of( user );
			}
		};
		
		//Act
		val result = roleGrant.getCorrectedValidPeriodByUserInfo( require );
		
		//Assert
		assertThat( result.get().start() ).isEqualTo( GeneralDate.ymd(2016, 1, 1) );
		assertThat( result.get().end() ).isEqualTo( GeneralDate.ymd(2030, 1, 1) );
		
	}
	
	/**
	 * target: getCorrectedValidPeriodByUserInfo
	 * pattern: @有効期間.終了日 = $ユーザ.有効期限  
	 * excepted: 開始日 = @有効期間.開始日, 終了日 = $ユーザ.有効期限  
	 */
	@Test
	public void testGetCorrectedValidPeriodByUserInfo_endDateOfRole_equal_expirationDateOfUser() {
		
		//ユーザ.有効期限 = 2030/12/31
		val user = RoleIndividualGrantHelper.createUser( "userId", GeneralDate.ymd(2030, 12, 31) );
		//有効期間.終了日 = 2030/12/31
		val validPeriod = new DatePeriod( GeneralDate.ymd(2016, 1, 1), GeneralDate.ymd(2030, 12, 31) );
		val roleGrant = RoleIndividualGrantHelper.createRoleIndividualGrant(
					"userId" //dummy
				,	RoleType.COMPANY_MANAGER //dummy
				,	validPeriod );
		
		new Expectations (){
			{
				require.getUser( (String) any );
				result = Optional.of( user );
			}
		};
		
		//Act
		val result = roleGrant.getCorrectedValidPeriodByUserInfo( require );
		
		//Assert
		assertThat( result.get().start() ).isEqualTo( GeneralDate.ymd(2016, 1, 1) );
		assertThat( result.get().end() ).isEqualTo( GeneralDate.ymd(2030, 12, 31) );
		
	}
	
}
