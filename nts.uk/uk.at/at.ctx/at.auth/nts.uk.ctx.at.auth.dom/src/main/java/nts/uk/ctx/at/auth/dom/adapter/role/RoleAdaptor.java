package nts.uk.ctx.at.auth.dom.adapter.role;

import java.util.Optional;
import java.util.OptionalInt;

import nts.arc.time.GeneralDate;

public interface RoleAdaptor {
	
	public Optional<RoleImport> findByRoleId(String roleId);
	
	/**
	 * [No.XXX]ユーザIDからロールを区分を含めて取得する
	 * @param userId  ユーザID：ユーザID
	 * @param rollType ロール種類：ロール種類
	 * @param baseDate 基準日：年月日
	 * @param companyId 会社ID：会社ID
	 * @return ・ロール情報：
				　　├　担当ロールか：boolean
				　　└　ロールID：ロールID
	 */
	
	RoleInformationImport getRoleIncludCategoryFromUserID(String userId, int roleType, GeneralDate baseDate, String companyId);

	/**
	 * 社員参照範囲を取得する
	 */
	OptionalInt findEmpRangeByRoleID(String roleID);
	
	//社員参照範囲を取得する(ユーザID,ロール種類 .就業,基準日)	
	Integer getEmployeeReferenceRange(String userId, int roleType, GeneralDate baseDate);
}
