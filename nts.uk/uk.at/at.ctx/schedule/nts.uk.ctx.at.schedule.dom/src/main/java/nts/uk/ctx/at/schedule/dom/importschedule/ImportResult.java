package nts.uk.ctx.at.schedule.dom.importschedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;

/**
 * 取り込み結果
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.取込.取り込み結果
 * @author kumiko_otake
 */
@Getter
@ToString(callSuper=true)
@EqualsAndHashCode
public class ImportResult {

	/** 1件分の取り込み結果 **/
	private final List<ImportResultDetail> results;
	/** 取り込み不可日 **/
	private final List<GeneralDate> unimportableDates;
	/** 存在しない社員 **/
	private final List<String> unexistsEmployees;

	/** 社員の並び順(OrderdList) **/
	private final List<EmployeeId> orderOfEmployees;



	/**
	 * コンストラクタ
	 * @param results 1件分の取り込み結果(List)
	 * @param unimportableDates 取り込み不可日(List)
	 * @param unexistsEmployees 存在しない社員(List)
	 * @param orderOfEmployees 社員の並び順(OrderedList)
	 */
	public ImportResult(
			List<ImportResultDetail> results
		,	List<GeneralDate> unimportableDates
		,	List<String> unexistsEmployees
		,	List<EmployeeId> orderOfEmployees
	) {

		this.results = results;
		this.unimportableDates = unimportableDates;
		this.unexistsEmployees = unexistsEmployees;
		this.orderOfEmployees = Collections.unmodifiableList(new ArrayList<>( orderOfEmployees ));

	}



	/**
	 * 取込可能な年月日リストを取得する
	 * ※『取込可能』は以下を満たす状態のこと
	 * 　・「取り込み不可日」に該当しない
	 * 　・「存在しない社員」に該当しない
	 * 　⇒取り込み結果内の年月日リスト
	 * @return 年月日リスト(昇順)
	 */
	public List<GeneralDate> getImportableDates() {
		return this.results.stream()
				.map( ImportResultDetail::getYmd )
				.distinct()
				.sorted()
				.collect(Collectors.toList());
	}

	/**
	 * 取込可能な社員IDリストを取得する
	 * ※『取込可能』は以下を満たす状態のこと
	 * 　・「取り込み不可日」に該当しない
	 * 　・「存在しない社員」に該当しない
	 * 　⇒取り込み結果内の社員IDリスト
	 * @return 社員IDリスト("並び順"順)
	 */
	public List<EmployeeId> getImportableEmployees() {

		return this.results.stream()
				.map( ImportResultDetail::getEmployeeId )
				.distinct()
				.sorted(Comparator.comparing( this.orderOfEmployees::indexOf ))
				.collect(Collectors.toList());
	}


	/**
	 * 未チェックの取り込み結果が存在するか
	 * @return true: 存在する / false: 存在しない
	 */
	public boolean existsUncheckedResults() {
		return !this.getUncheckedResults().isEmpty();
	}

	/**
	 * 未チェックの取り込み結果を取得する
	 * @return 取り込み結果リスト(状態＝未チェック)
	 */
	public List<ImportResultDetail> getUncheckedResults() {
		return this.results.stream()
				.filter( e -> e.getStatus() == ImportStatus.UNCHECKED )
				.collect(Collectors.toList());
	}


	/**
	 * 未チェックの取り込み結果を入れ替える
	 * @param replaceResults 入れ替える取込結果
	 * @return 取り込み結果
	 */
	public ImportResult updateUncheckedResults( List<ImportResultDetail> replaceResults ) {

		// チェック済みの取り込み結果を抽出
		val checkedResults = this.results.stream()
				.filter( e -> e.getStatus() != ImportStatus.UNCHECKED )
				.collect(Collectors.toList());
		// マージ：チェック済み＋入れ替え対象
		val mergedResults = Stream.of( checkedResults, replaceResults )
				.flatMap(Collection::stream)
				.collect(Collectors.toList());

		// 結果を入れ替えて返す
		return new ImportResult( mergedResults, this.unimportableDates, this.unexistsEmployees, this.orderOfEmployees );

	}

}
