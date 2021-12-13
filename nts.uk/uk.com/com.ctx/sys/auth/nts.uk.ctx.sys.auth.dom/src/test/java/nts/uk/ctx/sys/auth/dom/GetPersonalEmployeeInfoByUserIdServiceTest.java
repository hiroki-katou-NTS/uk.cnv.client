package nts.uk.ctx.sys.auth.dom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.sys.auth.dom.adapter.employee.EmployeeInfoImport;
import nts.uk.ctx.sys.auth.dom.adapter.employee.PersonalEmployeeInfoImport;
import nts.uk.ctx.sys.shared.dom.user.ContractCode;
import nts.uk.ctx.sys.shared.dom.user.DisabledSegment;
import nts.uk.ctx.sys.shared.dom.user.LoginID;
import nts.uk.ctx.sys.shared.dom.user.User;
/**
 * ユーザIDから個人社員情報を取得するのUTコード
 * @author lan_lt
 *
 */
@RunWith(JMockit.class)
public class GetPersonalEmployeeInfoByUserIdServiceTest {
	
	@Injectable
	private GetPersonalEmployeeInfoByUserIdService.Require require;
	
	/**
	 * target: get
	 * pattern: ユーザが存在しない
	 * except: runtime
	 */
	@Test
	public void testGet_user_empty() {
		
		String userId = "userId";
		
		new Expectations() {
			{
				require.getUser(userId);
			}
		};
		
		//Assert
		NtsAssert.systemError(() -> {
			GetPersonalEmployeeInfoByUserIdService.get(require, userId);
		});
	}
	
	/**
	 * target: get
	 * pattern: ユーザ.紐付け先個人ID = empty
	 * except: Optional.empty
	 */
	@Test
	public void testGet_person_empty() {
		
		String userId = "userId";
		User user = Helper.createUser(userId, Optional.empty());
		new Expectations() {
			{
				require.getUser(userId);
				result = Optional.of(user);
			}
		};
		
		//Act
		val result = GetPersonalEmployeeInfoByUserIdService.get(require, userId);
		
		//Assert
		assertThat( result ).isEmpty();
	}
	
	/**
	 * target: get
	 * pattern: 個人の社員情報を取得する(個人ID) empty
	 * except: Optional.empty
	 */
	@Test
	public void testGet_person_personEmployeeInfo_empty() {
		
		String userId = "userId";
		String personId = "personId";
		User user = Helper.createUser(userId, Optional.of(personId));
		
		new Expectations() {
			{
				require.getUser(userId);
				result = Optional.of(user);
				
				require.getPersonalEmployeeInfo(personId);
			}
		};
		
		//Act
		val result = GetPersonalEmployeeInfoByUserIdService.get(require, userId);
		
		//Assert
		assertThat( result ).isEmpty();
	}
	
	/**
	 * target: get
	 * pattern: 個人の社員情報を取得する(個人ID) not empty
	 * 
	 */
	@Test
	public void testGet_personEmployeeInfo_not_empty() {
		
		String userId = "userId";
		String personId = "personId";
		User user = Helper.createUser(userId, Optional.of(personId));
		
		val employeeInfo = new EmployeeInfoImport("cid"
				,	personId
				,	"employeeId"
				,	"employeeCode"
				,	0
				,	GeneralDateTime.ymdhms(2000, 12, 1, 0, 0, 0)
				,	"removeReason"
				,	"externalCode");
		
		val personInfo = new PersonalEmployeeInfoImport(personId, "personName", "bussinessName", Arrays.asList(employeeInfo));
		
		new Expectations() {
			{
				require.getUser(userId);
				result = Optional.of(user);
				
				require.getPersonalEmployeeInfo(personId);
				result = Optional.of(personInfo);
			}
		};
		
		//Act
		val result = GetPersonalEmployeeInfoByUserIdService.get(require, userId);
		
		//Assert
		assertThat( result.get().getPersonId() ).isEqualTo( personId );
		assertThat( result.get().getPersonName() ).isEqualTo( "personName" );
		assertThat( result.get().getBussinessName() ).isEqualTo( "bussinessName" );
		
		assertThat( result.get().getEmployeeInfos() )
			.extracting(	p -> p.getCompanyId()
						,	p -> p.getPersonId()
						,	p -> p.getEmployeeId()
						,	p -> p.getEmployeeCode()
						,	p -> p.getDeletedStatus()
						,	p -> p.getDeleteDateTemporary()
						,	p -> p.getRemoveReason()
						,	p -> p.getExternalCode()
					)
			.containsOnly( 
					tuple(	"cid"
						,	personId
						,	"employeeId"
						,	"employeeCode"
						,	0
						,	GeneralDateTime.ymdhms(2000, 12, 1, 0, 0, 0)
						,	"removeReason"
						,	"externalCode"));
	}
	
	public static class Helper{
		/**
		 * 
		 * @param userID
		 * @param associatedPersonID
		 * @return
		 */
		public static User createUser(String userID, Optional<String> associatedPersonID) {
			return new User(userID, true,new LoginID("loginID"),
					new ContractCode("contractCode"), GeneralDate.ymd(9999, 12, 31), DisabledSegment.False,
					DisabledSegment.False, Optional.empty(), Optional.empty(), associatedPersonID);
		}
		
	}
}
