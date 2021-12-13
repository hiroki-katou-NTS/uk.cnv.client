package nts.uk.ctx.sys.auth.dom.wplmanagementauthority.domservice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.permission.roleId.RoleIdWorkDomService;
import nts.uk.ctx.sys.auth.dom.role.Role;


/**
 * 指定職場の承認者一覧を取得（就業）
 * @author hoangnd
 *
 */
@Stateless
public class AprrovalWorkPlaceDomService {

	/**
	 * [1] 取得する
	 * @param require require
	 * @param workPlaceId 職場ID
	 * @param date 基準日
	 * @return 承認者一覧	
	 */
	public static List<String> get(
			Require require,
			List<String> workPlaceIds,
			GeneralDate date,
			String cid
			) {
		
		// 取得する(職場ID, 期間)
		List<String> sids = require.getSids(
				workPlaceIds,
				new DatePeriod(date, date));
		// 取得する(会社ID, 社員ID, 年月日)
		Map<String, String> roles = RoleIdWorkDomService.get(
				require,
				sids,
				date);
		
		// 承認権限がある就業ロールを取得する
		List<Role> roleList = require.getRoles(cid);
		
		
		
		
		return roles.entrySet()
					.stream()
					.filter(x -> 
						roleList.stream()
								.filter(y -> y.getRoleId().equals(x.getValue()))
								.findFirst()
								.isPresent())
					.map(x -> x.getKey())
					.collect(Collectors.toList());
	}
	
	public static interface Require extends RoleIdWorkDomService.Require {
		// [R-1]期間内に特定の職場（List）に所属している社員一覧を取得する
		List<String> getSids(List<String> wplIds, DatePeriod date);
		
		// [R-2]承認権限がある就業ロールを取得する
		List<Role> getRoles(String cid);
		
	}
	
	
}
