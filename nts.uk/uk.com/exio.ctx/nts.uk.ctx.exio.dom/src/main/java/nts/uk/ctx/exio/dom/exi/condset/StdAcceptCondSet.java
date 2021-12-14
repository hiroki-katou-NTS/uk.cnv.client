package nts.uk.ctx.exio.dom.exi.condset;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.exio.dom.exi.csvimport.ExiCharset;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.外部入出力.外部受入.受入条件設定.受入条件設定（定型）
 *
 * @author nws-minhnb
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StdAcceptCondSet extends AggregateRoot {

	/**
	 * 会社ID
	 */
	private String companyId;
	/**
	 * 条件設定コード
	 */
	private AcceptanceConditionCode conditionSetCode;
	/**
	 * 条件設定名称
	 */
	private AcceptanceConditionName conditionSetName;
	/**
	 * システム種類
	 */
	private Optional<SystemType> systemType;

	/**
	 * カテゴリID
	 */
	private Optional<Integer> categoryId;
	/**
	 * 既存データの削除
	 */
	private NotUseAtr deleteExistData;
	/**
	 * CSVデータの項目名行
	 */
	private Optional<AcceptanceLineNumber> csvDataItemLineNumber;

	/**
	 * CSVデータの取込開始行
	 */
	private Optional<AcceptanceLineNumber> csvDataStartLine;

	/**
	 * 文字コード
	 */
	private Optional<ExiCharset> characterCode;

	/**
	 * チェック完了
	 */
	private NotUseAtr checkCompleted;

	/**
	 * 既存データの削除方法
	 */
	private Optional<DeleteExistDataMethod> deleteExistDataMethod;
	/**
	 * 受入モード
	 */
	private Optional<AcceptMode> acceptMode;

}
