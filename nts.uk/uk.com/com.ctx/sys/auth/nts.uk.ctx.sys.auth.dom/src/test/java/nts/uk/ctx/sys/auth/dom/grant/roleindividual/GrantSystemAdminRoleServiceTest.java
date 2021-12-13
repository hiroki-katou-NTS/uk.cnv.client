package nts.uk.ctx.sys.auth.dom.grant.roleindividual;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.error.BusinessException;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.role.RoleType;
@RunWith(JMockit.class)
public class GrantSystemAdminRoleServiceTest {

	@Injectable
	private GrantSystemAdminRoleService.Require require;
	
	@Test
	public void testGrant_msg3() {
		val userId = "userId";
		val validPeriod = new DatePeriod( GeneralDate.ymd(2000, 01, 01) , GeneralDate.ymd(2030, 12, 31) );
		val grantInfo = RoleIndividualGrantHelper.createRoleIndividualGrant( userId, RoleType.SYSTEM_MANAGER, 
				new DatePeriod( GeneralDate.ymd(2020, 01, 01) , GeneralDate.ymd(2020, 12, 31) ));
		
		new Expectations( RoleIndividualGrant.class ) {
			{
				require.getGrantInfoByRoleTypeOfUser( userId, RoleType.SYSTEM_MANAGER );
				result = Optional.of(grantInfo);
			}
		};
		
		//Act
		NtsAssert.businessException( "Msg_61", () -> GrantSystemAdminRoleService.grant( require, userId, validPeriod ) );
	}

	/**
	 * target: grant
	 * pattern: 管理者ロール以外が指定されている
	 * excepted: Msg_2210
	 */
	@Test
	public void testGrant_not_role_manager() {
		val userId = "userId";
		val validPeriod = new DatePeriod( GeneralDate.ymd(2000, 01, 01) , GeneralDate.ymd(2030, 12, 31) );
		val grantInfo = RoleIndividualGrantHelper.createRoleIndividualGrant( userId
				, RoleType.PERSONAL_INFO // not manager
				, validPeriod);
		
		new Expectations( RoleIndividualGrant.class ) {
			{
				RoleIndividualGrant.createGrantInfoOfSystemMananger( require, (String) any, (DatePeriod) any );
				result = grantInfo;
				
				grantInfo.checkStatusNormal( require );
				result = new RuntimeException("this role is not manager role!!!");
			}
		};
		
		//Act
		NtsAssert.systemError( () -> GrantSystemAdminRoleService.grant( require, userId, validPeriod ) );
	}	
	
	/**
	 * target: grant
	 * pattern: 対応するユーザが存在しない
	 * excepted: Msg_2210
	 */
	@Test
	public void testGrant_user_empty() {
		val userId = "userId";
		val validPeriod = new DatePeriod( GeneralDate.ymd(2000, 01, 01) , GeneralDate.ymd(2030, 12, 31) );
		val grantInfo = RoleIndividualGrantHelper.createRoleIndividualGrant( userId, RoleType.SYSTEM_MANAGER, validPeriod);
		
		new Expectations( RoleIndividualGrant.class ) {
			{
				RoleIndividualGrant.createGrantInfoOfSystemMananger( require, (String) any, (DatePeriod) any );
				result = grantInfo;
				
				grantInfo.checkStatusNormal( require );
				result = new BusinessException( "Msg_2210" );
			}
		};
		
		//Act
		NtsAssert.businessException( "Msg_2210", () -> GrantSystemAdminRoleService.grant( require, userId, validPeriod ) );
	}
	
	/**
	 * target: grant
	 * pattern: デフォルトユーザへの付与
	 * excepted: runtimeException
	 */
	@Test
	public void testGrant_user_default() {
		val userId = "userId";
		val validPeriod = new DatePeriod( GeneralDate.ymd(2000, 01, 01) , GeneralDate.ymd(2030, 12, 31) );
		val grantInfo = RoleIndividualGrantHelper.createRoleIndividualGrant( userId, RoleType.SYSTEM_MANAGER, validPeriod);
		
		new Expectations( RoleIndividualGrant.class ) {
			{
				RoleIndividualGrant.createGrantInfoOfSystemMananger( require, (String) any, (DatePeriod) any );
				result = grantInfo;
			
				grantInfo.checkStatusNormal( require );
				result = new RuntimeException("this user is default user!!!");
			}
		};
		
		//Act
		NtsAssert.systemError( () -> GrantSystemAdminRoleService.grant( require, userId, validPeriod ) );
		
	}
	
	/**
	 * target: grant
	 * pattern: ユーザの有効期限切れ	
	 * excepted: Msg_2211 
	 */
	@Test
	public void testGrant_user_expired() {
		val userId = "userId";
		val validPeriod = new DatePeriod( GeneralDate.ymd(2000, 01, 01) , GeneralDate.ymd(2030, 12, 31) );
		val grantInfo = RoleIndividualGrantHelper.createRoleIndividualGrant( userId, RoleType.SYSTEM_MANAGER, validPeriod );
		
		new Expectations( RoleIndividualGrant.class ) {
			{
				RoleIndividualGrant.createGrantInfoOfSystemMananger( require, (String) any, (DatePeriod) any );
				result = grantInfo;
			
				grantInfo.checkStatusNormal( require );
				result = new BusinessException("Msg_2211");
			}
		};
		
		//Act
		NtsAssert.businessException( "Msg_2211", () -> GrantSystemAdminRoleService.grant( require, userId, validPeriod ) );
		
	}
	
	/**
	 * target: grant
	 * pattern: エラーがない
	 * excepted: success
	 */
	@Test
	public void testGrant_success() {
		val userId = "userId";
		val validPeriod = new DatePeriod( GeneralDate.ymd(2000, 01, 01) , GeneralDate.ymd(2030, 12, 31) );
		val grantInfo = RoleIndividualGrantHelper.createRoleIndividualGrant( userId, RoleType.SYSTEM_MANAGER, validPeriod);
		val grantRole = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( userId, validPeriod );
		
		new Expectations( RoleIndividualGrant.class ) {
			{
				RoleIndividualGrant.createGrantInfoOfSystemMananger( require, (String) any, (DatePeriod) any );
				result = grantInfo;
			
				grantInfo.checkStatusNormal( require );
			}
		};
		
		//Act
		NtsAssert.atomTask( () -> GrantSystemAdminRoleService.grant( require, userId, validPeriod )
				,	any -> require.registerGrantInfo( grantRole ) );
	}
	
	/**
	 * target: updateValidPeriod
	 * pattern: 管理者ロール以外が指定されている
	 * excepted: Msg_2211
	 */
	@Test
	public void testUpdateValidPeriod_not_role_manager() {
		val userId = "userId";
		val validPeriod = new DatePeriod( GeneralDate.ymd(2000, 01, 01) , GeneralDate.ymd(2030, 12, 31) );
		val grantRole = RoleIndividualGrantHelper.createRoleIndividualGrant( userId, RoleType.EMPLOYMENT, validPeriod );
		
		new Expectations( RoleIndividualGrant.class ) {
			{
				require.getGrantInfoByRoleTypeOfUser( userId, RoleType.SYSTEM_MANAGER );
				result = Optional.of(grantRole);
			
				grantRole.checkStatusNormal( require );
				result = new RuntimeException("this role is not manager role!!!");
			}
		};
		
		//Act
		NtsAssert.systemError( () -> GrantSystemAdminRoleService.updateValidPeriod( require, userId, validPeriod ) );

	}
	
	/**
	 * target: updateValidPeriod
	 * pattern: 対応するユーザが存在しない
	 * excepted: Msg_2210
	 */
	@Test
	public void testUpdateValidPeriod_user_empty() {
		val userId = "userId";
		val validPeriod = new DatePeriod( GeneralDate.ymd(2000, 01, 01) , GeneralDate.ymd(2030, 12, 31) );
		val grantRole = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( userId, validPeriod );
		
		new Expectations( RoleIndividualGrant.class ) {
			{
				require.getGrantInfoByRoleTypeOfUser( userId, RoleType.SYSTEM_MANAGER );
				result = Optional.of(grantRole);
			
				grantRole.checkStatusNormal( require );
				result = new BusinessException( "Msg_2210" );
			}
		};
		
		//Act
		NtsAssert.businessException( "Msg_2210", () -> GrantSystemAdminRoleService.updateValidPeriod( require, userId, validPeriod ) );
		
	}
	
	/**
	 * target: updateValidPeriod
	 * pattern: デフォルトユーザへの付与
	 * excepted: RuntimeException
	 */
	@Test
	public void testUpdateValidPeriod_user_default() {
		val userId = "userId";
		val validPeriod = new DatePeriod( GeneralDate.ymd(2000, 01, 01) , GeneralDate.ymd(2030, 12, 31) );
		val grantRole = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( userId );
		
		new Expectations( RoleIndividualGrant.class ) {
			{
				require.getGrantInfoByRoleTypeOfUser( userId, RoleType.SYSTEM_MANAGER );
				result = Optional.of(grantRole);
			
				grantRole.checkStatusNormal( require );
				result = new RuntimeException("this user is default user!!!");
			}
		};
		
		//Act
		NtsAssert.systemError( () -> GrantSystemAdminRoleService.updateValidPeriod( require, userId, validPeriod ) );

	}
	
	/**
	 * target: updateValidPeriod
	 * pattern: ユーザの有効期限切れ	
	 * excepted: Msg_2211
	 */
	@Test
	public void testUpdateValidPeriod_user_expired() {
		val userId = "userId";
		val validPeriod = new DatePeriod( GeneralDate.ymd(2000, 01, 01) , GeneralDate.ymd(2030, 12, 31) );
		val grantRole = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( userId );
		
		new Expectations( RoleIndividualGrant.class ) {
			{
				require.getGrantInfoByRoleTypeOfUser( userId, RoleType.SYSTEM_MANAGER );
				result = Optional.of(grantRole);
			
				grantRole.checkStatusNormal( require );
				result = new BusinessException("Msg_2211");
			}
		};
		
		//Act
		NtsAssert.businessException( "Msg_2211", () -> GrantSystemAdminRoleService.updateValidPeriod( require, userId, validPeriod ) );

	}
	
	/**
	 * target: updateValidPeriod
	 * pattern: システム管理者が常に存在しない 
	 * システム日付:	2021/10/05										<-------------------------------------------------->
	 * userID_1:	2021/07/01~2021/08/31					<--->
	 * userID_2:	2021/09/01~2021/10/01						<--->
	 * userID_3:	2021/11/01~9999/12/31										<-------------------------------------->
	 * excepted: Msg_330
	 */
	@Test
	public void testUpdateValidPeriod_Msg_330() {
		val validPeriod = new DatePeriod( GeneralDate.ymd(2000, 1, 1) , GeneralDate.ymd(2021, 12, 31) );
		val validPeriod_1 = new DatePeriod( GeneralDate.ymd(2021, 7, 1) , GeneralDate.ymd(2021, 8, 31) );
		val validPeriod_2 = new DatePeriod( GeneralDate.ymd(2021, 9, 1) , GeneralDate.ymd(2021, 10, 01) );
		val validPeriod_3 = new DatePeriod( GeneralDate.ymd(2021, 12, 1) , GeneralDate.ymd(9999, 12, 31) );
		val grantRole_1 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_1" );
		val grantRole_2 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_2" );
		val grantRole_3 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_3" );
		val grantRoles = Arrays.asList( grantRole_1, grantRole_2, grantRole_3 );
		
		//システム日付:	2021/10/05
		new MockUp< GeneralDate >() {
			@Mock
			public GeneralDate today() {
				return  GeneralDate.ymd( 2021, 10, 05 );
			}
		};
		
		
		new Expectations( RoleIndividualGrant.class ) {
			{
				require.getGrantInfoByRoleTypeOfUser( "userID_2", RoleType.SYSTEM_MANAGER );
				result = Optional.of(grantRole_2);
			
				grantRole_2.checkStatusNormal( require );
				
				grantRole_2.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_2 );//userID_2:	2021/09/01~2021/10/01
				
				require.getGrantInfoByRoleType( RoleType.SYSTEM_MANAGER);
				result = grantRoles;
				
				grantRole_1.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_1 );//userID_1:	2021/08/01~2021/09/31
				
				grantRole_3.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_3 );//userID_3:	2021/11/01~9999/12/31
			}
		};
		
		//Act
		NtsAssert.businessException( "Msg_330", () -> GrantSystemAdminRoleService.updateValidPeriod( require, "userID_2", validPeriod ) );
	}
	
	/**
	 * target: updateValidPeriod
	 * pattern: システム管理者が常に存在する
	 * システム日付: 2021/10/05											<-------------------------------------------------->
	 * userID_1: 2021/06/01~2021/08/31			<---------->
	 * userID_2: 2021/10/05~2021/10/31								<------->
	 * userID_3: 2021/11/01~9999/12/31										<------------------------------------------>
	 * excepted: update success
	 */
	@Test
	public void testUpdateValidPeriod_update_success() {
		val validPeriod = new DatePeriod( GeneralDate.ymd(2021, 1, 1) , GeneralDate.ymd(2025, 05, 31) );
		val validPeriod_1 = new DatePeriod( GeneralDate.ymd(2021, 6, 1) , GeneralDate.ymd(2021, 8, 31) );
		val validPeriod_2 = new DatePeriod( GeneralDate.ymd(2021, 10, 5) , GeneralDate.ymd(2021, 10, 31) );
		val validPeriod_3 = new DatePeriod( GeneralDate.ymd(2021, 11, 1) , GeneralDate.ymd(9999, 12, 31) );
		val grantRole_1 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_1" );
		val grantRole_2 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_2" );
		val grantRole_3 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_3" );
		val grantRoles = Arrays.asList( grantRole_1, grantRole_2, grantRole_3 );
		val grantRoleUpdate = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_2" , validPeriod);
		
		//システム日付:	2021/10/05
		new MockUp< GeneralDate >() {
			@Mock
			public GeneralDate today() {
				return  GeneralDate.ymd( 2021, 10, 05 );
			}
		};
		
		new Expectations(grantRole_1, grantRole_2, grantRole_3) {
			{
				require.getGrantInfoByRoleTypeOfUser( "userID_2", RoleType.SYSTEM_MANAGER );
				result = Optional.of(grantRole_2);
			
				grantRole_2.checkStatusNormal( require );
				
				grantRole_2.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_2 );//userID_2: 2021/10/05~2021/10/31
				
				require.getGrantInfoByRoleType( RoleType.SYSTEM_MANAGER);
				result = grantRoles;
				
				grantRole_1.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_1 );//userID_1: 2021/06/01~2021/08/31
				
				grantRole_3.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_3 );//userID_3: 2021/11/01~9999/12/31
				
			}
		};
		
		//Act
		NtsAssert.atomTask( () -> GrantSystemAdminRoleService.updateValidPeriod( require, "userID_2", validPeriod )
				,	any -> require.updateGrantInfo( grantRoleUpdate ) );
		
	}
	
	/**
	 * target: deprive
	 * pattern: システム日付からシステム管理者が存在しない期間がある
	 * システム日付 = 2021/10/05												<-------------------------------------------------->
	 * userID_1: 2021/07/01~ 2021/10/04							<----------->
	 * userID_3: 2021/12/01~ 9999/12/31														<---------------------------------->
	 * excepted: Msg_331
	 */
	@Test
	public void testDeprive_Msg_331() {
		val grantRole_1 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_1" );
		val grantRole_2 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_2" );
		val grantRole_3 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_3" );
		val validPeriod_1 = new DatePeriod( GeneralDate.ymd(2021, 7, 1) , GeneralDate.ymd(2021, 10, 4) );
		val validPeriod_3 = new DatePeriod( GeneralDate.ymd(2021, 12, 1) , GeneralDate.ymd(9999, 12, 31) );
		val grantRoles = Arrays.asList( grantRole_1, grantRole_2, grantRole_3 );
		
		//システム日付:	2021/10/05
		new MockUp< GeneralDate >() {
			@Mock
			public GeneralDate today() {
				return  GeneralDate.ymd( 2021, 10, 05 );
			}
		};
		
		new Expectations( RoleIndividualGrant.class, grantRole_1, grantRole_3 ) {
			{
				require.getGrantInfoByRoleTypeOfUser( "userID_2", RoleType.SYSTEM_MANAGER );
				result = Optional.of(grantRole_2);
				
				require.getGrantInfoByRoleType( RoleType.SYSTEM_MANAGER);
				result = grantRoles;
				
				grantRole_1.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_1 );//userID_1: 2021/07/01~ 2021/10/04
				
				grantRole_3.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_3 );//userID_3: 2021/12/01~ 9999/12/31
			}
		};
		
		//Act
		NtsAssert.businessException( "Msg_331", () -> GrantSystemAdminRoleService.deprive( require, "userID_2" ) );
		
	}
	
	/**
	 * target: deprive
	 * pattern: システム日付からシステム管理者が存在しない期間がない
	 * システム日付 = 2021/10/05								<------------------------------------------>
	 * userID_1: 2021/10/01~ 2021/10/31					<------->
	 * userID_3: 2021/11/01~ 9999/12/31							<-------------------------------------->
	 * excepted: delete success
	 */
	@Test
	public void testDeprive_delete_success() {
		String cid = "companyId";
		val grantRole_1 = RoleIndividualGrantHelper.createRoleIndividualGrant( "userID_1", cid, RoleType.SYSTEM_MANAGER );
		val grantRole_2 = RoleIndividualGrantHelper.createRoleIndividualGrant( "userID_2", cid, RoleType.SYSTEM_MANAGER  );
		val grantRole_3 = RoleIndividualGrantHelper.createRoleIndividualGrant( "userID_3", cid, RoleType.SYSTEM_MANAGER  );
		val validPeriod_1 = new DatePeriod( GeneralDate.ymd(2021, 10, 1) , GeneralDate.ymd(2021, 10, 31) );
		val validPeriod_3 = new DatePeriod( GeneralDate.ymd(2021, 11, 1) , GeneralDate.ymd(9999, 12, 31) );
		val grantRoles = Arrays.asList( grantRole_1, grantRole_2, grantRole_3 );
		
		//システム日付:	2021/10/05
		new MockUp< GeneralDate >() {
			@Mock
			public GeneralDate today() {
				return  GeneralDate.ymd( 2021, 10, 05 );
			}
		};
		
		new Expectations( RoleIndividualGrant.class , grantRole_1, grantRole_3) {
			{
				require.getGrantInfoByRoleTypeOfUser( "userID_2", RoleType.SYSTEM_MANAGER );
				result = Optional.of(grantRole_2);
				
				require.getGrantInfoByRoleType( RoleType.SYSTEM_MANAGER );
				result = grantRoles;
				
				grantRole_1.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_1 );//userID_1: 2021/10/01~ 2021/10/31
				
				grantRole_3.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_3 );//userID_3: 2021/11/01~ 9999/12/31
				
			}
		};
		
		//Act
		NtsAssert.atomTask( () -> GrantSystemAdminRoleService.deprive( require, "userID_2" )
				,	any -> require.deleteGrantInfo( "userID_2", cid, RoleType.SYSTEM_MANAGER) );
				
	}
	
	/**
	 * target: isAlwaysASystemAdmin
	 * pattern: 有効期間 =empty, not system admin in check period target 
	 * システム日付: 2021/10/05										<---------------------------------------------------->
	 * userID_1	: 2021/10/01 ~ 2021/10/15				<--------->
	 * userID_3	: 2021/11/15 ~ 2021/12/15									<------->
	 * userID_4	: 2021/12/16 ~ 9999/12/31											<-------------------------------->
	 * excepted: false
	 */
	@Test
	public void testIsAlwaysASystemAdmin_dont_have_manager_period_in_checkTargetPeriod() {
		val grantRole_1 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_1" );
		val grantRole_2 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_2" );
		val grantRole_3 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_3" );
		val grantRole_4 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_4" );
		val grantRoles = Arrays.asList( grantRole_1, grantRole_2, grantRole_3, grantRole_4 );
		
		val validPeriod_1 = new DatePeriod( GeneralDate.ymd(2021, 10, 1) , GeneralDate.ymd(2021, 10, 15) );
		val validPeriod_3 = new DatePeriod( GeneralDate.ymd(2021, 11, 15) , GeneralDate.ymd(2021, 12, 15) );
		val validPeriod_4 = new DatePeriod( GeneralDate.ymd(2021, 12, 16) , GeneralDate.ymd(9999, 12, 31) );
		
		GeneralDateTime.FAKED_NOW = GeneralDateTime.ymdhms(2021, 10, 5, 0, 0, 0);
		
		new Expectations( grantRole_1, grantRole_2, grantRole_3, grantRole_4 ) {
			{
				require.getGrantInfoByRoleType( RoleType.SYSTEM_MANAGER);
				result = grantRoles;
				
				grantRole_1.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_1 );
				
				grantRole_3.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_3 );
				
				grantRole_4.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_4 );
			}
		};
		
		//Act
		boolean result = NtsAssert.Invoke.staticMethod(	GrantSystemAdminRoleService.class, "isAlwaysASystemAdmin"
				,	require, "userID_2", Optional.empty());//有効期間 =empty
		
		//Assert
		assertThat( result ).isFalse();
		
	}
	
	/**
	 * target: isAlwaysASystemAdmin
	 * pattern: 有効期間 =empty, all time have system admin
	 * システム日付: 2021/10/05									<-------------------------------------------------------->
	 * userID_1	: 2021/10/01~2021/10/31				<-------------->
	 * userID_3	: 2021/10/15~9999/12/31												<-------------------------------->
	 * userID_4	: 2021/11/15~2021/12/15							<---------------------------->
	 * excepted: true
	 */
	@Test
	public void testIsAlwaysASystemAdmin_have_manager_period_in_checkTargetPeriod() {
		
		val grantRole_1 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_1" );
		val grantRole_2 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_2" );
		val grantRole_3 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_3" );
		val grantRole_4 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_4" );
		val grantRoles = Arrays.asList( grantRole_1, grantRole_2, grantRole_3, grantRole_4 );
		
		val validPeriod_1 = new DatePeriod( GeneralDate.ymd(2021, 10, 1) , GeneralDate.ymd(2021, 10, 31) );
		val validPeriod_3 = new DatePeriod( GeneralDate.ymd(2021, 10, 15) , GeneralDate.ymd(9999, 12, 31) );
		val validPeriod_4 = new DatePeriod( GeneralDate.ymd(2021, 11, 15) , GeneralDate.ymd(2021, 12, 15) );
		
		//システム日付:	2021/10/05
		new MockUp< GeneralDate >() {
			@Mock
			public GeneralDate today() {
				return  GeneralDate.ymd( 2021, 10, 05 );
			}
		};
		
		new Expectations( grantRole_1, grantRole_3, grantRole_4 ) {
			{
				require.getGrantInfoByRoleType( RoleType.SYSTEM_MANAGER);
				result = grantRoles;
				
				grantRole_1.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_1 );
				
				grantRole_3.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_3 );
				
				grantRole_4.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_4 );
			}
		};
		
		//Act
		boolean result = NtsAssert.Invoke.staticMethod(	GrantSystemAdminRoleService.class, "isAlwaysASystemAdmin"
				,	require, "userID_2", Optional.empty());//有効期間 =empty
		
		//Assert
		assertThat( result ).isTrue();
		
	}
	
	/**
	 * target: isAlwaysASystemAdmin
	 * pattern: 有効期間 =empty, all time  have system admin in check period target, past time also system admin
	 * システム日付: 2021/10/05	~9999/12/31								<-------------------------------------------------------->
	 * userID_1: 2000/01/01~ 2020/12/31		<----------->
	 * userID_3: 2021/10/05~ 2021/12/05								<------------->
	 * userID_5: 2021/11/01~ 9999/12/31											<-------------------------------------------->
	 * excepted: true
	 */
	@Test
	public void testIsAlwaysASystemAdmin_all_time_have_system_admin_in_checkTargetPeriod() {
		
		val grantRole_1 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_1" );
		val grantRole_2 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_2" );
		val grantRole_3 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_3" );
		val grantRole_4 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_4" );
		val validPeriod_1 = new DatePeriod( GeneralDate.ymd(2000, 1, 1) , GeneralDate.ymd(2020, 12, 31) );
		val validPeriod_2 = new DatePeriod( GeneralDate.ymd(2021, 10, 5) , GeneralDate.ymd(2021, 12, 5) );
		val validPeriod_3 = new DatePeriod( GeneralDate.ymd(2021, 11, 1) , GeneralDate.ymd(9999, 12, 31) );
		val grantRoles = Arrays.asList( grantRole_1, grantRole_2, grantRole_3, grantRole_4 );
		
		//システム日付:	2021/10/05
		new MockUp< GeneralDate >() {
			@Mock
			public GeneralDate today() {
				return  GeneralDate.ymd( 2021, 10, 05 );
			}
		};
		
		new Expectations( grantRole_1, grantRole_3, grantRole_4 ) {
			{
				require.getGrantInfoByRoleType( RoleType.SYSTEM_MANAGER);
				result = grantRoles;
				
				grantRole_1.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_1 );//userID_1
				
				grantRole_3.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_2 );//userID_3
				
				grantRole_4.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_3 );//userID_4
			}
		};
		
		//Act
		boolean result = NtsAssert.Invoke.staticMethod(	GrantSystemAdminRoleService.class, "isAlwaysASystemAdmin"
				,	require, "userID_2", Optional.empty());//有効期間 =empty
		
		//Assert
		assertThat( result ).isTrue();
		
	}
	
	/**
	 * target: isAlwaysASystemAdmin
	 * pattern: 有効期間 not empty, チェック対象の期間中に システム管理者が存在する
	 * システム日付:	2021/10/05~9999/12/31			<-------------------------------------------------------->
	 * userID_1: 	2021/10/05~2021/12/05			<-------------->
	 * userID_3: 	2021/11/05~2022/1/05						<------------------->
	 * 有効期間: 		2022/01/01~9999/12/31										<--------------------------->
	 * excepted: true
	 */
	@Test
	public void testIsAlwaysASystemAdmin_validPeriod_not_empty_and_have_system_admin_in_checkPeriodTarget() {
		val grantRole_1 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_1" );
		val grantRole_2 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_2" );
		val grantRole_3 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_3" );
		val grantRoles = Arrays.asList( grantRole_1, grantRole_2, grantRole_3 );
		
		val validPeriod_1 = new DatePeriod( GeneralDate.ymd(2021, 10, 5) , GeneralDate.ymd(2021, 12, 5) );
		val validPeriod_3 = new DatePeriod( GeneralDate.ymd(2021, 1, 1) , GeneralDate.ymd(2022, 1, 5) );
		//有効期間
		val validPeriod = new DatePeriod( GeneralDate.ymd(2021, 12, 01), GeneralDate.ymd(9999, 12, 31) );
		
		
		//システム日付:	2021/10/05
		new MockUp< GeneralDate >() {
			@Mock
			public GeneralDate today() {
				return  GeneralDate.ymd( 2021, 10, 05 );
			}
		};
		
		new Expectations( grantRole_1, grantRole_3 ) {
			{
				require.getGrantInfoByRoleType( RoleType.SYSTEM_MANAGER);
				result = grantRoles;
				
				grantRole_1.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_1 );//userID_1
				
				grantRole_3.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_3 );//userID_3
			}
		};
		
		//Act
		boolean result = NtsAssert.Invoke.staticMethod(	GrantSystemAdminRoleService.class, "isAlwaysASystemAdmin"
				,	require, "userID_2", Optional.of( validPeriod ));
		
		//Assert
		assertThat( result ).isTrue();
		
	}

	/**
	 * target: isAlwaysASystemAdmin
	 * pattern: 有効期間 not empty, all time have system admin in check target period, past time has also system admin
	 * システム日付:	2021/10/05~9999/12/31					<----------------------------------------------->
	 * userID_1: 	2021/09/01~2021/10/01 <------>
	 * userID_3: 	2021/11/05~2021/12/05					<------------------->
	 * 有効期間: 		2021/12/15~9999/12/31										<--------------------------->
	 * excepted: true
	 */
	@Test
	public void testIsAlwaysASystemAdmin_validPeriod_not_empty_and_all_time_have_system_admin_in_checkTargetPeriod() {
		val grantRole_1 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_1" );
		val grantRole_2 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_2" );
		val grantRole_3 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_3" );
		val grantRoles = Arrays.asList( grantRole_1, grantRole_2, grantRole_3 );
		
		val validPeriod_1 = new DatePeriod( GeneralDate.ymd(2021, 10, 5) , GeneralDate.ymd(2021, 12, 5) );
		val validPeriod_3 = new DatePeriod( GeneralDate.ymd(2021, 1, 1) , GeneralDate.ymd(2022, 1, 5) );
		//有効期間
		val validPeriod = new DatePeriod( GeneralDate.ymd(2021, 12, 01), GeneralDate.ymd(9999, 12, 31) );
		
		
		//システム日付:	2021/10/05
		new MockUp< GeneralDate >() {
			@Mock
			public GeneralDate today() {
				return  GeneralDate.ymd( 2021, 10, 05 );
			}
		};
		
		new Expectations( grantRole_1, grantRole_3 ) {
			{
				require.getGrantInfoByRoleType( RoleType.SYSTEM_MANAGER);
				result = grantRoles;
				
				grantRole_1.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_1 );//userID_1
				
				grantRole_3.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_3 );//userID_3
			}
		};
		
		//Act
		boolean result = NtsAssert.Invoke.staticMethod(	GrantSystemAdminRoleService.class, "isAlwaysASystemAdmin"
				,	require, "userID_2", Optional.of( validPeriod ));//userID_2, 有効期間 not empty
		
		//Assert
		assertThat( result ).isTrue();
		
	}
	
	/**
	 * target: isAlwaysASystemAdmin
	 * pattern: 有効期間 not empty, チェック対象の期間中に システム管理者が存在しない
	 * システム日付: 2021/10/05	~9999/12/31								<-------------------------------------------------------->
	 * userID_1: 2021/08/01~ 2021/09/01					<-------->
	 * userID_3: 2021/09/15~ 2021/10/15							<--------->
	 * 有効期間:  2021/12/01~ 9999/12/31													<------------------------------------>
	 * excepted: false
	 */
	@Test
	public void testIsAlwaysASystemAdmin_validPeriod_not_empty_and_dont_have_system_admin_in_checkPeriodTarget() {
		
		val grantRole_1 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_1" );
		val grantRole_2 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_2" );
		val grantRole_3 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_3" );
		val grantRoles = Arrays.asList( grantRole_1, grantRole_2, grantRole_3 );
		
		val validPeriod_1 = new DatePeriod( GeneralDate.ymd(2021, 8, 1) , GeneralDate.ymd(2020, 9, 1) );
		val validPeriod_3 = new DatePeriod( GeneralDate.ymd(2021, 9, 15) , GeneralDate.ymd(2021, 10, 15) );
		//有効期間
		val validPeriod = new DatePeriod( GeneralDate.ymd(2021, 12, 01), GeneralDate.ymd(9999, 12, 31) );
		
		//システム日付:	2021/10/05
		new MockUp< GeneralDate >() {
			@Mock
			public GeneralDate today() {
				return  GeneralDate.ymd( 2021, 10, 05 );
			}
		};
		
		new Expectations( grantRole_1, grantRole_3 ) {
			{
				require.getGrantInfoByRoleType( RoleType.SYSTEM_MANAGER);
				result = grantRoles;
				
				grantRole_1.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_1 );//userID_1
				
				grantRole_3.getCorrectedValidPeriodByUserInfo( require );
				result = Optional.of( validPeriod_3 );//userID_3
			}
		};
		
		//Act
		boolean result = NtsAssert.Invoke.staticMethod(	GrantSystemAdminRoleService.class, "isAlwaysASystemAdmin"
				,	require, "userID_2", Optional.of( validPeriod ));
		
		//Assert
		assertThat( result ).isFalse();
	}
	
	/**
	 * target: isAlwaysASystemAdmin
	 * pattern: 有効期間リストがない
	 * excepted: false
	 */
	@Test
	public void testIsAlwaysASystemAdmin_validPeriod_empty() {
		
		val grantRole_1 = RoleIndividualGrantHelper.createGrantInfoOfSystemMananger( "userID_1" );
		val grantRoles = Arrays.asList( grantRole_1 );
		
		//システム日付:	2021/10/05
		new MockUp< GeneralDate >() {
			@Mock
			public GeneralDate today() {
				return  GeneralDate.ymd( 2021, 10, 05 );
			}
		};
		
		new Expectations( ) {
			{
				require.getGrantInfoByRoleType( RoleType.SYSTEM_MANAGER);
				result = grantRoles;
			}
		};
		
		//Act
		boolean result = NtsAssert.Invoke.staticMethod(	GrantSystemAdminRoleService.class, "isAlwaysASystemAdmin"
				,	require, "userID_1", Optional.empty());
		
		//Assert
		assertThat( result ).isFalse();
		
	}
	
}
