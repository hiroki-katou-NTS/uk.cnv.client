package nts.uk.ctx.at.auth.dom.adapter.workplace;

import java.util.List;

import nts.arc.time.GeneralDate;

public interface AuthWorkPlaceAdapter {
	
	List<String> getListWorkPlaceID(String employeeID , GeneralDate referenceDate);
	
	WorkplaceInfoImport getWorkplaceListId(GeneralDate referenceDate, String employeeID, boolean referEmployee);
	
	/**
	 * 職場と基準日から所属職場履歴項目を取得する
	 * @param workPlaceId  職場ID
	 * @param baseDate 基準日
	 */
	List<AffWorkplaceHistoryItemImport> getWorkHisItemfromWkpIdAndBaseDate(String workPlaceId, GeneralDate baseDate);
	
	// 指定社員が参照可能な職場リストを取得する（職場管理者なし）
	List<String> getListWorkPlaceIDNoWkpAdmin(String employeeID , int empRange , GeneralDate referenceDate);
	
	// 職場管理者Repository.取得する(社員ID, 年月日)
	List<WorkplaceManagerImport> findListWkpManagerByEmpIdAndBaseDate(String employeeId, GeneralDate baseDate);
}

