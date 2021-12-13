package nts.uk.ctx.at.record.dom.adapter.employee;

import java.util.Optional;

/**
 * 社員を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.shared.社員.社員を取得する.社員を取得する
 * @author chungnt
 *
 */

public interface GetAnEmployeeAdapter {

	/**
	 * name: [1] 取得する
	 * @param cid
	 * @param employeeCode
	 * @return 	Optional<社員Imported>
	 */
	Optional<EmployeeImport> get(String cid, String employeeCode);
	
}
