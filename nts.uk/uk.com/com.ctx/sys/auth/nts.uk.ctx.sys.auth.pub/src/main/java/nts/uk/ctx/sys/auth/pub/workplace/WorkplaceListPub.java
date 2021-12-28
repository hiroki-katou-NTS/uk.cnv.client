/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.pub.workplace;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.pub.wkpmanager.WorkPlaceAuthorityExport;

/**
 * The Interface WorkplaceListPub.
 */
public interface WorkplaceListPub {
	
	/**
	 * Gets the workplace list id.
	 *
	 * @param referenceDate the reference date
	 * @param employeeID the employee ID
	 * @param referEmployee the refer employee
	 * @return the workplace list id
	 */
	// RequestList478: 基準日、指定社員から参照可能な職場リストを取得する（時間外労働用）
	WorkplaceInfoExport getWorkplaceListId(GeneralDate referenceDate, String employeeID, boolean referEmployee);
	
	// RequestList613: [RQ613]指定社員の職場管理者の職場リストを取得する（配下含む）
	List<String> getWorkplaceId(GeneralDate baseDate, String employeeID);

	// 指定社員が参照可能な職場リストを取得する（職場管理者なし）
	List<String> getListWorkPlaceIDNoWkpAdmin(String employeeID, int empRange, GeneralDate referenceDate);
	
	// 職場管理者Repository.取得する(社員ID, 年月日)
	List<WorkplaceManagerExport> findListWkpManagerByEmpIdAndBaseDate(String employeeId, GeneralDate baseDate);
	
	/**
	 * Request list 679
	 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".システム.権限管理.職場管理権限.Export.[RQ679]所属職場権限を取得する.所属職場権限を取得する
	 * @param companyId
	 * @param roleId
	 * @param functionNo
	 * @return
	 */
	Optional<WorkPlaceAuthorityExport> getWorkPlaceAuthorityById(String companyId, String roleId, int functionNo);
	/**
	 * @name 参照可能社員の所属職場を取得するPublish
	 * @param userID ユーザID
	 * @param employeeID 社員ID
	 * @param date 基準日
	 */
	Map<String, String> getWorkPlace(String userID, String employeeID, GeneralDate date);
}

