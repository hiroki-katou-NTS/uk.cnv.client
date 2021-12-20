package nts.uk.ctx.sys.shared.dom.employee;

import java.util.Optional;

/**
 * 社員を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.shared.社員.社員を取得する.社員を取得する
 * @author chungnt
 *
 */

public interface GetAnEmployeeAdapter {

	/**
	 * [1] 取得する
	 * @param cid
	 * @param employeeCode
	 * @return
	 */
	Optional<GetAnEmployeeImported> getEmployee(String cid, String employeeCode);
}
