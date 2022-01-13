package nts.uk.ctx.sys.auth.dom;

import java.util.Optional;

import nts.uk.ctx.sys.auth.dom.adapter.employee.PersonalEmployeeInfoImport;
import nts.uk.ctx.sys.shared.dom.user.User;

import javax.ejb.Stateless;

/**
 * ユーザIDから個人社員情報を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.権限管理.ユーザIDから個人社員情報を取得する
 * @author lan_lt
 *
 */
@Stateless
public class GetPersonalEmployeeInfoByUserIdService {
	/**
	 * 取得する
	 * @param require
	 * @param userId ユーザID
	 * @return Optional<個人社員情報Imported>
	 */
	public static Optional<PersonalEmployeeInfoImport> get(Require require, String userId){
		
		Optional<User> user = require.getUser(userId);
		
		if(!user.isPresent()) {
			throw new RuntimeException("This user doesn't exist");
		}
		
		Optional<String> personId = user.get().getAssociatedPersonID();
		
		if(!personId.isPresent()) {
			return Optional.empty();
		}
		
		return require.getPersonalEmployeeInfo(personId.get());
	}
	
	public static interface Require{
		/**
		 * ユーザを取得する
		 * @param userId ユーザID
		 * @return
		 */
		Optional<User> getUser(String userId);
		
		/**
		 * 個人の社員情報を取得する(個人ID)
		 * @param personId 個人ID
		 * @return
		 */
		Optional<PersonalEmployeeInfoImport> getPersonalEmployeeInfo(String personId);
		
	}

}
