package nts.uk.ctx.exio.dom.input.canonicalize.domains.organization.workplace;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.task.tran.AtomTask;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceConfiguration;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceInformation;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.DomainCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.ItemNoMap;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToChange;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToDelete;
import nts.uk.ctx.exio.dom.input.canonicalize.result.CanonicalItem;
import nts.uk.ctx.exio.dom.input.canonicalize.result.IntermediateResult;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.errors.RecordError;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMeta;
import nts.gul.util.Either;

/**
 * 職場マスタの正準化
 */
public class WorkplaceCanonicalization implements DomainCanonicalization {

	@Override
	public ItemNoMap getItemNoMap() {
		return ItemNoMap.reflection(Items.class);
	}

	public static class Items {
		public static final int 開始日 = 1;
		public static final int 終了日 = 2;
		public static final int 職場コード = 3;
		public static final int 職場外部コード = 4;
		public static final int 職場名称 = 5;
		public static final int 職場表示名 = 6;
		public static final int 職場総称 = 7;
		public static final int 職場階層コード = 8;
		public static final int 職場階層コード1 = 9;
		public static final int 職場階層コード2 = 10;
		public static final int 職場階層コード3 = 11;
		public static final int 職場階層コード4 = 12;
		public static final int 職場階層コード5 = 13;
		public static final int 職場階層コード6 = 14;
		public static final int 職場階層コード7 = 15;
		public static final int 職場階層コード8 = 16;
		public static final int 職場階層コード9 = 17;
		public static final int 職場階層コード10 = 18;
		public static final int 削除フラグ = 19;
		public static final int HIST_ID = 100;
		public static final int 職場ID = 101;
	}

	/**
	 * 正準化する
	 * 手順は以下の通り：
	 *   1. 期間の逆転をチェックし、エラー行を除外する
	 *   2. 階層コードを正準化し、エラー行を除外する
	 *   3. 同一履歴内での職場コードの重複をチェックし、エラー行を除外する
	 *   4. 同一履歴内での職場階層コードの重複をチェックし、エラー行を除外する
	 *   5. 履歴同士の重複をチェックし、エラー行を除外する
	 *   6. 履歴同士の連続性をチェックし、エラー行を除外し、最後の履歴の終了日を99991231に補正
	 *   7. 受入データに合わせて既存履歴を補正する
	 *   8. 受入データの職場IDを決定する（既存データから引き継いだり新規生成したり）
	 *   9. 受入データの履歴IDを生成、削除フラグの既定値を埋める
	 *   10. 永続化
	 */
	@Override
	public void canonicalize(DomainCanonicalization.RequireCanonicalize require, ExecutionContext context) {
		
		List<IntermediateResult> revisedRecords = require.getAllRevisedDataRecords(context).stream()
				.map(r -> IntermediateResult.create(r))
				.collect(toList());
		
		if (revisedRecords.isEmpty()) {
			return;
		}
		
		Consumer<RecordError> saveError = error -> {
			require.add(ExternalImportError.of(context.getDomainId(), error));
		};
		
		// 1. 期間の逆転をチェックし、エラー行を除外する
		List<RecordWithPeriod> records = Either.sequenceOf(revisedRecords)
				.mapEither(r -> RecordWithPeriod.build(r))
				.ifLeft(saveError)
				.listRight();
		
		// 2. 階層コードを正準化し、エラー行を除外する
		records = Either.sequenceOf(records)
				.mapEither(r -> CanonicalizeHierarchyCode.canonicalize(r))
				.ifLeft(saveError)
				.listRight();
		
		// 3. 同一履歴内での職場コードの重複をチェックし、エラー行を除外する
		records = CheckCodeDuplications.check(records, Items.職場コード)
				.ifLeft(saveError)
				.listRight();
		
		// 4. 同一履歴内での職場階層コードの重複をチェックし、エラー行を除外する
		records = CheckCodeDuplications.check(records, Items.職場階層コード)
				.ifLeft(saveError)
				.listRight();

		// 5. 履歴同士の重複をチェックし、エラー行を除外する
		records = CheckHistoryDuplications.check(records)
				.ifLeft(saveError)
				.listRight();
		
		// 6. 履歴同士の連続性をチェックし、エラー行を除外し、最後の履歴の終了日を99991231に補正
		records = CanonicalizeHistoryContinuity.canonicalize(records)
				.ifLeft(saveError)
				.listRight();
		
		if (records.isEmpty()) {
			return;
		}
		
		// 7. 受入データに合わせて既存履歴を補正する
		val existingHistory = getExistingHistory(require, context);
		existingHistory.canonicalize(require, context, records);
		
		// 8. 受入データの職場IDを決定する（既存データから引き継いだり新規生成したり）
		records = CanonicalizeWorkplaceId.canonicalize(records, existingHistory);
		
		// 9. 受入データの履歴IDを生成、削除フラグの既定値を埋める
		records = putHistoryId(records);
		records = putDeleteFlag(records);
		
		// 10. 永続化
		for (val record : records) {
			require.save(context, record.interm.complete());
		}
	}

	private static List<RecordWithPeriod> putHistoryId(List<RecordWithPeriod> records) {
		
		return records.stream()
				.collect(Collectors.groupingBy(r -> r.period.start()))
				.entrySet()
				.stream()
				.flatMap(e -> {
					val historyId = CanonicalItem.of(Items.HIST_ID, IdentifierUtil.randomUniqueId());
					return e.getValue().stream()
							.map(r -> r.canonicalize(i -> i.addCanonicalized(historyId)));
				})
				.collect(toList());
	}

	private List<RecordWithPeriod> putDeleteFlag(List<RecordWithPeriod> records) {
		
		return records.stream()
				.map(r -> r.canonicalize(i -> i.optionalItem(CanonicalItem.of(Items.削除フラグ, false))))
				.collect(toList());
	}
	
	public static interface RequireCanonicalize extends RequireGetExistingHistory {
	}

	@Override
	public ImportingDataMeta appendMeta(ImportingDataMeta source) {
		return source
				.addItem(this.getItemNameByNo(Items.HIST_ID))
				.addItem(this.getItemNameByNo(Items.職場ID));
	}

	@Override
	public AtomTask adjust(
			DomainCanonicalization.RequireAdjsut require,
			ExecutionContext context,
			List<AnyRecordToChange> recordsToChange,
			List<AnyRecordToDelete> recordsToDelete) {

		return AtomTask.of(() -> {
			ExistingWorkplaceHistory.adjust(require, context, recordsToChange, recordsToDelete);
		});
	}
	
	
	public static interface RequireAdjust extends ExistingWorkplaceHistory.RequireAdjust {
	}

	
	/**
	 * 既存履歴を取得する
	 * @param require
	 * @param context
	 * @return
	 */
	private static ExistingWorkplaceHistory getExistingHistory(
			RequireGetExistingHistory require,
			ExecutionContext context) {
		
		val config = require.getWorkplaceConfiguration(context.getCompanyId()).get();
		val informations = require.getAllWorkplaceInformations(context.getCompanyId());
		
		return new ExistingWorkplaceHistory(config, informations);
	}
	
	public static interface RequireGetExistingHistory {
		
		Optional<WorkplaceConfiguration> getWorkplaceConfiguration(String companyId);
		
		List<WorkplaceInformation> getAllWorkplaceInformations(String companyId);
		
	}
}
