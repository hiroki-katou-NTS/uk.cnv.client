package nts.uk.ctx.at.schedule.dom.importschedule;

import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterImportCode;

/**
 * 1件分の取り込み結果
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.取込.1件分の取り込み結果
 * @author kumiko_otake
 */
@Value
public class ImportResultDetail {

	/** 社員ID **/
	private final EmployeeId employeeId;
	/** 年月日 **/
	private final GeneralDate ymd;
	/** 取り込みコード **/
	private final ShiftMasterImportCode importCode;
	/** 状態 **/
	private final ImportStatus status;



	/**
	 * 新規作成
	 * @param employeeId 社員ID
	 * @param ymd 年月日
	 * @param importCode 取り込みコード
	 * @return 1件分の取り込み結果(未チェック)
	 */
	public static ImportResultDetail createNew( EmployeeId employeeId, GeneralDate ymd, ShiftMasterImportCode importCode ) {
		// 新規作成時は必ず「未チェック」
		return new ImportResultDetail( employeeId, ymd, importCode, ImportStatus.UNCHECKED );
	}


	/**
	 * 状態を更新する
	 * @param status 状態
	 * @return 1件分の取り込み結果
	 */
	public ImportResultDetail updateStatus( ImportStatus status ) {
		return new ImportResultDetail( this.employeeId, this.ymd, this.importCode, status );
	}

}
