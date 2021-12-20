package nts.uk.ctx.sys.auth.dom.adapter.employee;

import lombok.Value;
import nts.arc.time.GeneralDateTime;
/**
 * 社員情報Imported
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.権限管理.Imported.contexts.社員.個人社員情報Imported.社員情報Imported
 * @author lan_lt
 *
 */
@Value
public class EmployeeInfoImport {
	/** 会社ID */
	private String companyId;

	/** 個人ID */
	private String personId;

	/** 社員ID */
	private String employeeId;

	/** 社員コード */
	private String employeeCode;

	/** 削除状況 */
	private int deletedStatus;

	/** 一時削除日時 */
	private GeneralDateTime deleteDateTemporary;

	/** 削除理由 */
	private String removeReason;

	/** 外部コード */
	private String externalCode;

}
