package nts.uk.ctx.at.schedule.dom.importschedule;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheModifyStartDateService;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.GetEmpCanReferService;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterImportCode;

/**
 * スケジュールを取り込む
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.取込.スケジュールを取り込む
 * @author kumiko_otake
 */
public class WorkScheduleImportService {

	/**
	 * 取り込む
	 * @param require Require
	 * @param rawData 取り込み内容
	 * @return 取り込み結果
	 */
	public static ImportResult importFrom(Require require, CapturedRawData rawData) {

		// 取り込み可能な内容かチェックする
		val importable = WorkScheduleImportService.checkIfIsImportableData( require, rawData );
		// 取り込み対象の社員かチェックする
		val targetEmployees = WorkScheduleImportService.checkIfEmployeeIsTarget( require, importable );
		// 取り込み内容の整合性をチェックする
		val integrated = WorkScheduleImportService.checkForContentIntegrity( require, targetEmployees );
		// 取り込み内容の勤務予定をチェックする
		val corrected = WorkScheduleImportService.checkForExistingWorkSchedule( require, integrated );


		return corrected;

	}


	/**
	 * 取込可能な内容かチェックする
	 * @param require Require
	 * @param intermediate 取り込み内容
	 * @return 取り込み結果
	 */
	private static ImportResult checkIfIsImportableData(Require require, CapturedRawData rawData) {

		/* 日付のチェック */
		// 修正可能開始日との比較
		val modifiableStartDate = ScheModifyStartDateService.getModifyStartDate(require, require.getOwnAttendanceRoleId());
		// チェック結果：取り込み不可日
		val unmodifiableDateList = rawData.getYmdList().stream()
				.filter( e -> e.before( modifiableStartDate ) )
				.collect(Collectors.toList());


		/* 社員コードのチェック */
		// 社員情報を取得
		val empCdIdMap = require.getEmployeeIds( rawData.getEmployeeCodes() ).entrySet().stream()
				.collect(Collectors.toMap( Map.Entry::getKey, entry -> new EmployeeId( entry.getValue() ) ));

		// 社員の存在チェック
		// チェック結果：存在しない社員
		val unexistsEmployeeList = rawData.getEmployeeCodes().stream()
				.filter( e -> !empCdIdMap.containsKey(e) )
				.collect(Collectors.toList());


		/* チェック後処理 */
		// エラーではない結果のみ抽出
		// チェック結果：未チェック
		val uncheckedContents = rawData.getContents().stream()
				.filter( detail -> !unmodifiableDateList.contains( detail.getYmd() ) )			// 年月日
				.filter( detail -> !unexistsEmployeeList.contains( detail.getEmployeeCode() ) )	// 社員
				.map( detail -> ImportResultDetail.createNew(
						empCdIdMap.get( detail.getEmployeeCode() )
					,	detail.getYmd()
					,	detail.getImportCode()
				) )
				.collect(Collectors.toList());

		// 社員の並び順の変換：社員コード⇒ID
		val orderOfEmployeeId = rawData.getEmployeeCodes().stream()
				.filter( empCdIdMap::containsKey )
				.map( empCdIdMap::get )
				.collect(Collectors.toList());

		// チェック結果を返す
		return new ImportResult(
						uncheckedContents		// 未チェック
					,	unmodifiableDateList	// 修正不可日
					,	unexistsEmployeeList	// 存在しない社員
					,	orderOfEmployeeId		// 社員の並び順
				);

	}

	/**
	 * 取込対象の社員かチェックする
	 * @param require Require
	 * @param interimResult 取り込み結果(中間)
	 * @return 取り込み結果
	 */
	private static ImportResult checkIfEmployeeIsTarget(Require require, ImportResult interimResult) {

		/* チェック対象の存在判定 */
		if ( !interimResult.existsUncheckedResults() ) {
			// チェック対象なし⇒終了
			return interimResult;
		}


		/* 参照範囲チェック */
		// 参照可能な社員を取得する
		val importableDates = interimResult.getImportableDates();
		val referableEmployees = GetEmpCanReferService.getAll(
					require, require.getOwnEmployeeId().v()
				,	GeneralDate.today(), new DatePeriod( importableDates.get(0), importableDates.get(importableDates.size() - 1) )
			);
		// 参照可否でグループ化
		// [Key] true: 参照範囲内(正常) / false: 参照範囲外(エラー)
		val referableStatus = interimResult.getUncheckedResults().stream()
				.collect(Collectors.groupingBy( detail -> referableEmployees.contains( detail.getEmployeeId().v() ) ));

		// チェック結果：参照範囲外
		val outOfReference = referableStatus.getOrDefault( false, Collections.emptyList() ).stream()
				.map( e -> e.updateStatus(ImportStatus.OUT_OF_REFERENCE) )
				.collect(Collectors.toList());


		/* 予定管理状態チェック */
		// チェック結果
		val checkedScheMngStatus = referableStatus.getOrDefault( true, Collections.emptyList() ).stream()
				.map( detail -> {
					// 予定管理状態を取得
					val status = EmployeeWorkingStatus.create( require, detail.getEmployeeId().v(), detail.getYmd() )
									.getWorkingStatus();
					// 勤務予定が必要か
					if( !status.needCreateWorkSchedule() ) {
						// 必要ではない
						// チェック結果：予定管理状態に応じた状態に更新
						return detail.updateStatus( ImportStatus.from(status) );
					}
					// チェック結果：未チェック(更新しない)
					return detail;
				} ).collect(Collectors.toList());


		// チェック結果を返す
		return interimResult.updateUncheckedResults(Stream.of(
						outOfReference			// 参照範囲外
					,	checkedScheMngStatus	// 予定管理状態チェック結果
				).flatMap(Collection::stream).collect(Collectors.toList()));

	}

	/**
	 * 取り込み内容の整合性をチェックする
	 * @param require Require
	 * @param interimResult 取り込み結果(中間)
	 * @return 取り込み結果
	 */
	private static ImportResult checkForContentIntegrity(Require require, ImportResult interimResult) {

		/* チェック対象の存在判定 */
		if ( !interimResult.existsUncheckedResults() ) {
			// チェック対象なし⇒終了
			return interimResult;
		}


		/* シフトマスタのチェック */
		// 取り込みコードを取得
		val importCodes = interimResult.getUncheckedResults().stream()
				.map( ImportResultDetail::getImportCode )
				.distinct()
				.collect(Collectors.toList());
		// シフトマスタの状態を取得
		// [Value] true: 正常 / false: 異常
		val shiftMasterStatuses = require.getShiftMasters(importCodes).stream()
				.collect(Collectors.toMap(
								detail -> detail.getImportCode().get()
							,	detail -> detail.checkNormalCondition(require)
						));

		// チェック結果
		val checkedShiftMaster = interimResult.getUncheckedResults().stream()
				.map( detail -> {
					// シフトマスタの状態チェック
					if( !shiftMasterStatuses.containsKey( detail.getImportCode() ) ) {
						// 対応するシフトマスタなし
						// チェック結果：シフトが存在しない
						return detail.updateStatus( ImportStatus.SHIFTMASTER_IS_NOTFOUND );
					} else if ( !shiftMasterStatuses.get( detail.getImportCode() ) ) {
						// 対応するシフトマスタが異常
						// チェック結果：シフトが不正
						return detail.updateStatus( ImportStatus.SHIFTMASTER_IS_ERROR );
					}
					// シフトマスタが正常
					// チェック結果：未チェック(更新しない)
					return detail;
				} ).collect(Collectors.toList());


		// チェック結果を返す
		return interimResult.updateUncheckedResults( checkedShiftMaster );

	}

	/**
	 * 取込対象の勤務予定をチェックする
	 * @param require Require
	 * @param interimResult 取り込み結果(中間)
	 * @return 取り込み結果
	 */
	private static ImportResult checkForExistingWorkSchedule(Require require, ImportResult interimResult) {

		/* チェック対象の存在判定 */
		if ( !interimResult.existsUncheckedResults() ) {
			// チェック対象なし⇒終了
			return interimResult;
		}


		/* 既存の勤務予定をチェック */
		// チェック結果
		val checkedWorkSchedule = interimResult.getUncheckedResults().stream()
				.map( detail -> {
					// 勤務予定の存在チェック
					if( !require.isWorkScheduleExisted( detail.getEmployeeId(), detail.getYmd() ) ) {
						// 既存の勤務予定が存在しない
						// チェック結果：取り込み可能
						return detail.updateStatus( ImportStatus.IMPORTABLE );
					}
					// 勤務予定の確定済みチェック
					if( require.isWorkScheduleComfirmed( detail.getEmployeeId(), detail.getYmd() ) ) {
						// 既存の勤務予定が存在する(確定済)
						// チェック結果：確定済み
						return detail.updateStatus( ImportStatus.SCHEDULE_IS_COMFIRMED );
					}
					// 既存の勤務予定が存在する(未確定)
					// チェック結果：すでに勤務予定が存在する
					return detail.updateStatus( ImportStatus.SCHEDULE_IS_EXISTS );
				} ).collect(Collectors.toList());

		// チェック結果を返す
		return interimResult.updateUncheckedResults( checkedWorkSchedule );

	}



	public interface Require extends	ScheModifyStartDateService.Require
									,	GetEmpCanReferService.Require
									,	EmployeeWorkingStatus.Require
									,	ShiftMaster.Require
	{
		/**
		 * ログイン者の就業ロールIDを取得する
		 * @return 就業ロールID
		 */
		String getOwnAttendanceRoleId();
		/**
		 * ログイン者の社員IDを取得する
		 * @return 社員ID
		 */
		EmployeeId getOwnEmployeeId();
		/**
		 * 社員コードから社員IDを取得する
		 * @param employeeCodes 社員コードリスト
		 * @return 社員コード/社員ID(Map)
		 */
		Map<String, String> getEmployeeIds(List<String> employeeCodes);
		/**
		 * シフトマスタを取得する
		 * @param importCodes 取り込みコードリスト
		 * @return シフトマスタ(List)
		 */
		List<ShiftMaster> getShiftMasters(List<ShiftMasterImportCode> importCodes);
		/**
		 * 勤務予定が登録されているか
		 * @param employeeId 社員ID
		 * @param ymd 年月日
		 * @return true:登録されている/false:登録されていない
		 */
		boolean isWorkScheduleExisted(EmployeeId employeeId, GeneralDate ymd);
		/**
		 * 勤務予定が確定されているか
		 * @param employeeId 社員ID
		 * @param ymd 年月日
		 * @return true:確定されている/false:確定されていない
		 */
		boolean isWorkScheduleComfirmed(EmployeeId employeeId, GeneralDate ymd);
	}

}
