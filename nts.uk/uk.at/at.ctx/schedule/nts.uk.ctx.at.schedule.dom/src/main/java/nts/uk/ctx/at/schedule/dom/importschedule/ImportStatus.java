package nts.uk.ctx.at.schedule.dom.importschedule;

import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nts.uk.ctx.at.shared.dom.employeeworkway.WorkingStatus;

/**
 * 取り込み状態
 * @author kumiko_otake
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.取込.取り込み状態
 */
@Getter
@RequiredArgsConstructor
public enum ImportStatus {

	/** 未チェック **/
	UNCHECKED( 0, Optional.empty() ),
	/** 取込可能 **/
	IMPORTABLE( 1, Optional.empty() ),
	/** 参照範囲外 **/
	OUT_OF_REFERENCE( 2, Optional.of("#Msg_2176") ),
	/** 個人情報不備 **/
	EMPLOYEEINFO_IS_INVALID( 3, Optional.of("#Msg_2177") ),
	/** 在職していない **/
	EMPLOYEE_IS_NOT_ENROLLED( 4, Optional.of("#Msg_2178") ),
	/** スケジュール管理しない **/
	SCHEDULE_IS_NOTUSE( 5, Optional.of("#Msg_2179") ),
	/** シフトが存在しない **/
	SHIFTMASTER_IS_NOTFOUND( 6, Optional.of("#Msg_2180") ),
	/** シフトが不正 **/
	SHIFTMASTER_IS_ERROR( 7, Optional.of("#Msg_2181") ),
	/** 確定済み **/
	SCHEDULE_IS_COMFIRMED( 8, Optional.of("#Msg_2135") ),
	/** すでに勤務予定が存在する **/
	SCHEDULE_IS_EXISTS( 9, Optional.of("#Msg_2136") ),
	;

	private final int value;
	private final Optional<String> messageId;


	/**
	 * 取り込み不可か
	 * @return true:取り込み不可である / false:取り込み不可ではない
	 */
	public boolean isUnimportable() {

		switch( this ) {
			/* 取り込み可 */
			case IMPORTABLE:			// 取り込み可能
			case SCHEDULE_IS_EXISTS:	// すでに勤務予定が存在する
				return false;
			/* 取り込み不可 */
			default:
				return true;
		}

	}


	/**
	 * 予定管理状態から変換する
	 * @param status 予定管理状態
	 * @return 取り込み状態
	 */
	public static ImportStatus from( WorkingStatus status ) {

		switch( status ) {
			case NOT_ENROLLED:
				return ImportStatus.EMPLOYEE_IS_NOT_ENROLLED;
			case INVALID_DATA:
				return ImportStatus.EMPLOYEEINFO_IS_INVALID;
			case DO_NOT_MANAGE_SCHEDULE:
				return ImportStatus.SCHEDULE_IS_NOTUSE;
			default:
				return ImportStatus.UNCHECKED;

		}

	}

}
