package nts.uk.ctx.sys.auth.dom.permission.roleId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;

/**
 * 社員IDListから就業ロールIDを取得
 * @author hoangnd
 *
 */

@Stateless
public class RoleIdWorkDomService {


	/**
	 * [1] 取得する
	 * @param require 
	 * @param sids 社員IDList
	 * @param date 基準日
	 *  return Map<社員ID、ロールID>
	 */
	public static Map<String, String> get(
			Require require,
			List<String> sids,
			GeneralDate date) {
		//社員IDからユーザIDを取得する：　Map<社員ID、ユーザID>
		Map<String, String> userIds = sids.stream()
										.collect(Collectors.toMap(
												x -> x,
												x -> require.getUserIDByEmpID(x).orElse("")))
										.entrySet()
										.stream()
										.filter(x -> !x.getValue().equals(""))
										.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		//ユーザIDからロールセットを取得する：　Map<社員ID、ロールID> 
		Map<String, String> roles = 
				userIds.entrySet()
					 .stream()
					 .collect(Collectors.toMap(
							 x -> x.getKey(), 
							 x -> require.getRoleSetFromUserId(x.getValue(), date)
							 					.map(y -> y.getEmploymentRoleId().orElse(""))
							 					.orElse("")))
					 .entrySet()
					 .stream()
					 .filter(x -> !x.getValue().equals(""))
					 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
										 
		
		
		
		return roles;
		
	}
	
	public static interface Require {
		
		Optional<String> getUserIDByEmpID(String employeeID);
		
		Optional<RoleSet> getRoleSetFromUserId(String userId, GeneralDate baseDate);
	}
	
}
