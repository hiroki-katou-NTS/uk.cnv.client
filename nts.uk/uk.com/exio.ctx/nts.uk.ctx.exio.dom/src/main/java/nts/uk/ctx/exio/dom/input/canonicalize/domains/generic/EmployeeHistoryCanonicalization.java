package nts.uk.ctx.exio.dom.input.canonicalize.domains.generic;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;
import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalizeUtil;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.DomainDataColumn;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.DomainDataId;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.KeyValues;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.SystemImportingItems;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.DomainCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.AffCompanyHistoryCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordTo;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToChange;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToDelete;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.StringifiedValue;
import nts.uk.ctx.exio.dom.input.canonicalize.history.ExternalImportHistory;
import nts.uk.ctx.exio.dom.input.canonicalize.history.HistoryKeyColumnNames;
import nts.uk.ctx.exio.dom.input.canonicalize.history.HistoryType;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.CanonicalizationMethodRequire;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.EmployeeCodeCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.result.CanonicalItemList;
import nts.uk.ctx.exio.dom.input.canonicalize.result.IntermediateResult;
import nts.uk.ctx.exio.dom.input.errors.ErrorMessage;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMeta;
import nts.gul.util.Either;
import nts.uk.shr.com.history.DateHistoryItem;

/**
 * 社員の履歴の正準化
 * 
 * 社員の履歴を正準化するための処理がまとまっている
 */
@Getter
@ToString
public abstract class EmployeeHistoryCanonicalization implements DomainCanonicalization {
	
	/** 履歴開始日の項目No */
	private final int itemNoStartDate;
	
	/** 履歴終了日の項目No */
	private final int itemNoEndDate;
	
	/** 履歴IDの項目No */
	private final int itemNoHistoryId;
	
	/** 社員コードの正準化 */
	private final EmployeeCodeCanonicalization employeeCodeCanonicalization;

	/** どんな履歴か*/
	private final HistoryType historyType;

	public EmployeeHistoryCanonicalization(HistoryType historyType) {
		
		itemNoStartDate = this.getItemNoByName(Names.START);
		itemNoEndDate = this.getItemNoByName(Names.END);
		itemNoHistoryId = this.getItemNoByName(Names.HIST_ID);
		
		this.historyType = historyType;
		
		this.employeeCodeCanonicalization = new EmployeeCodeCanonicalization(this.getItemNoMap());
	}
	
	protected static class Names {
		protected static final String SCD = "社員コード";
		protected static final String SID = "社員ID";
		protected static final String HIST_ID = "HIST_ID";
		protected static final String START = "開始日";
		protected static final String END = "終了日";
		
	}
	
	protected abstract String getParentTableName();
	
	protected abstract List<String> getChildTableNames();

	@Override
	public void canonicalize(DomainCanonicalization.RequireCanonicalize require, ExecutionContext context) {
		
		CanonicalizeUtil.forEachEmployee(require, context, employeeCodeCanonicalization, interm -> {
			
			val results = canonicalizeHistory(require, context, interm);
			
			results.forEach(result -> {
				require.save(context, result.complete());
			});
		});
	}

	/**
	 * 履歴を正準化する
	 * @param require
	 * @param context
	 * @param employeeCanonicalized
	 * @return
	 */
	protected List<IntermediateResult> canonicalizeHistory(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context,
			List<IntermediateResult> employeeCanonicalized) {
		
		if (employeeCanonicalized.isEmpty()) {
			return Collections.emptyList();
		}
		
		// 複数レコードあったとしても同じ社員のデータなので、社員IDは先頭レコードから取り出せば良い
		String employeeId = employeeCanonicalized.get(0)
				.getItemByNo(this.getItemNoOfEmployeeId())
				.get().getString();

		DomainDataId id = new DomainDataId(this.getParentTableName(), Arrays.asList(new DomainDataId.Key(DomainDataColumn.SID, employeeId)));
		
		// 既存履歴
		val existingHistory = require.getHistory(id, this.historyType, getKeyColumnNames());
		
		// 暫定仕様として、既存履歴は常に削除
		deleteAllExistingHistory(require, context, employeeId, existingHistory);
		
		/*
		 *  複数の履歴を追加する場合、全て追加し終えるまで補正結果が確定しない点に注意が必要。
		 *  例えば永続する履歴であれば、追加するたびにその履歴項目の終了日が9999/12/31に変更されるが、
		 *  次の項目を追加した時点で、次の項目の開始日の前日へと更に変更される。
		 *  そこだけ考えれば、単純に追加する履歴項目を開始日昇順でループすれば良いが、そうもいかない。
		 *  最終的には「社員コードを正準化した中間結果」に対してaddCanonicalizedをしなければならないため、
		 *  「社員コードを正準化した中間結果」と「追加する履歴項目」を束ねたもの = Containerを、開始日昇順で処理する必要がある。
		 */
		List<Container> containers = new ArrayList<>();
		
		employeeCanonicalized.stream()
				.sorted(Comparator.comparing(c -> c.getItemByNo(itemNoStartDate).get().getDate()))
				.forEach(interm -> getPeriod(interm)
						.map(p -> new Container(interm, DateHistoryItem.createNewHistory(p)))
						.ifRight(c -> containers.add(c))
						.ifLeft(e -> require.add(ExternalImportError.record(interm.getRowNo(), context.getDomainId(), e.getText()))));

		// エラー行が除外された結果、空になったらここで終了
		if (containers.isEmpty()) {
			return Collections.emptyList();
		}
		
		// 追加する分と重複する未来の履歴は全て削除
		removeDuplications(require, context, employeeId, containers, existingHistory);
		
		try {
			//既存データと受入れようとしてるデータで補正
			//未来履歴は↑で消えているため、
			//既存データの一番未来のやつと受入れようとしてる一番過去のやつを見て、補正すればいい
			adjustExistingHistory(require, context, containers.get(0).addingHistoryItem, existingHistory);
			
			//受入れようとしてる履歴同士で補正
			adjustAddingHistory(existingHistory, containers);
		} catch (BusinessException ex) {
			// どのデータで失敗しようと１社員分すべて受け入れるか、全て受け入れないかのどちらかとする
			containers.forEach(c -> require.add(
					ExternalImportError.record(c.interm.getRowNo(), context.getDomainId(), ex.getMessage())));
			
			return Collections.emptyList();
		}
		
		val newContainers = canonicalizeExtends(require, context, employeeId, containers);
		
		return newContainers.stream()
				.map(c -> c.complete())
				.collect(toList());
	}

	/**
	 * 既存履歴をすべて削除する
	 * @param require
	 * @param context
	 * @param employeeId
	 * @param existingHistory
	 */
	private void deleteAllExistingHistory(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context,
			String employeeId,
			ExternalImportHistory existingHistory) {
		
		// 削除の補正データを作る
		existingHistory.items().stream()
			.map(e -> new EmployeeHistoryItem(employeeId, e))
			.map(e -> e.toDelete(context))
			.forEach(d -> require.save(context, d));
		
		// インスタンスからも除去
		existingHistory.items().clear();
	}
	
	/**
	 * テーブルの列名が違う場合はoverrideすること
	 * @return
	 */
	protected HistoryKeyColumnNames getKeyColumnNames() {
		return new HistoryKeyColumnNames("START_DATE", "END_DATE");
	}
	
	/**
	 * 追加の正準化処理が必要ならoverrideすること
	 * @param targetContainers
	 */
	protected List<Container> canonicalizeExtends(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context,
			String employeeId,
			List<Container> targetContainers) {
		// 何もしない
		return targetContainers;
	}
	
	@Value
	protected class Container {
		IntermediateResult interm;
		DateHistoryItem addingHistoryItem;
		
		public IntermediateResult complete() {
			
			// 正準化した結果を格納
			// 開始日・終了日は変わらないかもしれないし、変わるかもしれない
			val canonicalizedItems = new CanonicalItemList()
					.add(itemNoHistoryId, addingHistoryItem.identifier())
					.add(itemNoStartDate, addingHistoryItem.start())
					.add(itemNoEndDate, addingHistoryItem.end());
			
			return interm.addCanonicalized(canonicalizedItems);
		}
	}
	
	/**
	 * 期間を取り出す
	 * @param revisedData
	 * @return
	 */
	private Either<ErrorMessage, DatePeriod> getPeriod(IntermediateResult interm) {
		
		val startDate = interm.getItemByNo(itemNoStartDate).get().getDate();
		val endDate = interm.getItemByNo(itemNoEndDate).get().getDate();
		
		val period = new DatePeriod(startDate, endDate);
		if (period.isReversed()) {
			return Either.left(new ErrorMessage("開始日と終了日が逆転しています。"));
		}
		
		return Either.right(period);
	}
	
	
	/**
	 * 追加する履歴より未来の履歴を全て削除する
	 * @param require
	 * @param context
	 * @param employeeId
	 * @param addingItems
	 */
	private void removeDuplications(
			CanonicalizationMethodRequire require,
			ExecutionContext context,
			String employeeId,
			List<Container> addings,
			ExternalImportHistory existingHistory) {
		
		// 追加する履歴の開始日のうち最も過去の日付
		GeneralDate baseDate = addings.stream()
				.map(adding -> adding.addingHistoryItem.start())
				.min(Comparator.comparing(d -> d))
				.get();
		
		val itemsToRemove = existingHistory.items().stream()
				.filter(item -> item.start().afterOrEquals(baseDate))
				.collect(toList());

		itemsToRemove.forEach(item -> {
			
			existingHistory.removeForcively(item);
			
			AnyRecordToDelete toDelete = new EmployeeHistoryItem(employeeId,item).toDelete(context);
			require.save(context, toDelete);
		});
	}
	
	
	/**
	 * 受入データを補正
	 * @param existingHistory 受入データが入る前の既存履歴
	 * @param addingHistories 受入れる履歴
	 */
	private void adjustAddingHistory(ExternalImportHistory existingHistory, List<Container> addingHistories) {
		addingHistories.forEach(c -> existingHistory.add(c.addingHistoryItem));
	}

	/**
	 * 既存データを補正
	 * @param require
	 * @param context
	 * @param addingItem 追加する履歴
	 * @param existingHistory 受入データが入る前の既存履歴
	 */
	private void adjustExistingHistory(
			CanonicalizationMethodRequire require,
			ExecutionContext context,
			DateHistoryItem addingItem,
			ExternalImportHistory existingHistory) {
		
		if(existingHistory.isEmpty()) return;
		
		//履歴補正
		existingHistory.add(addingItem);
		existingHistory.removeForcively(addingItem);
		//↑で受入る履歴を消してるから末尾が既存の最新だろうという意
		
		existingHistory.latestStartItem().ifPresent(	existing ->{
				AnyRecordToChange toChange = new EmployeeHistoryItem(existingHistory.getEmployeeId(),existing)
						.toChange(context);
				require.save(context, toChange);
		});

	}

	public static interface RequireCanonicalize extends AffCompanyHistoryCanonicalization.RequireCanonicalizeExtends {
		ExternalImportHistory getHistory(DomainDataId id, HistoryType historyTypea, HistoryKeyColumnNames keyColumnNames);
	}
	
	@Override
	public AtomTask adjust(
			RequireAdjsut require,
			ExecutionContext context,
			List<AnyRecordToChange> recordsToChange,
			List<AnyRecordToDelete> recordsToDelete) {
		
		return AtomTask.of(() -> {

			for (AnyRecordToDelete record : recordsToDelete) {
				toDomainDataIds(record).forEach(id -> require.delete(id));
			}

			for (AnyRecordToChange record : recordsToChange) {
				//項目Noから何をどの値に変更するのか特定できないとここから進めない
				val period = new DatePeriod(
						record.getChange(this.itemNoStartDate).asGeneralDate(),
						record.getChange(this.itemNoEndDate).asGeneralDate());
				
				toDomainDataIds(record).forEach(id -> require.update(id, period));
			}
		});
	}
	
	private List<DomainDataId> toDomainDataIds(AnyRecordToDelete toDelete) {

		val keyValues = new KeyValues(toKeyValueObjects(toDelete));
		
		List<String> tableNames = new ArrayList<>();
		tableNames.add(getParentTableName());
		tableNames.addAll(getChildTableNames());
		
		val keys = getDomainDataKeys();
		return tableNames.stream()
				.map(tn -> DomainDataId.createDomainDataId(tn, keys, keyValues))
				.collect(toList());
	}
	
	private List<DomainDataId> toDomainDataIds(AnyRecordToChange record) {
		
		val keyValues = new KeyValues(toKeyValueObjects(record));
		
		List<String> tableNames = new ArrayList<>();
		tableNames.add(getParentTableName());
		
		val keys = getDomainDataKeys();
		return tableNames.stream()
				.map(tn -> DomainDataId.createDomainDataId(tn, keys, keyValues))
				.collect(toList());
	}
	
	private List<Object> toKeyValueObjects(AnyRecordTo record) {
		
		List<Object> keyValues = new ArrayList<>();
		
		val dataKeys = getDomainDataKeys();
		for (int index = 0; index < dataKeys.size(); index++) {
			val dataKey = dataKeys.get(index);
			
			StringifiedValue stringified = SystemImportingItems.getStringifiedValue(record, dataKey.getColumnName(), index);
			
			Object value;
			switch (dataKey.getDataType()) {
			case STRING:
				value = stringified.asString();
				break;
			case INT:
				value = stringified.asInteger();
				break;
			case REAL:
				value = stringified.asBigDecimal();
				break;
			case DATE:
				value = stringified.asGeneralDate();
				break;
			case DATETIME:
				value = stringified.asGeneralDateTime();
				break;
			default:
				throw new RuntimeException("unknown: " + dataKey);
			}

			keyValues.add(value);
		}
		
		return keyValues;
	}

	protected List<DomainDataColumn> getDomainDataKeys() {
		return Arrays.asList(DomainDataColumn.HIST_ID);
	}
	
	public static interface RequireAdjust{
		void delete(DomainDataId id);
		
		void update(DomainDataId id ,DatePeriod period) ;
	}
	
	
	@RequiredArgsConstructor
	@Getter
	protected class EmployeeHistoryItem {
		final String employeeId;
		final String historyId;
		final DatePeriod period;
		
		EmployeeHistoryItem(String employeeId, DateHistoryItem item) {
			this(employeeId, item.identifier(), item.span());
		}
		
		EmployeeHistoryItem(AnyRecordToChange record) {
			this(record.getKey(employeeCodeCanonicalization.getItemNoEmployeeId()).asString(),
					record.getKey(itemNoHistoryId).asString(),
					new DatePeriod(
							record.getChange(itemNoStartDate).asGeneralDate(),
							record.getChange(itemNoEndDate).asGeneralDate()));
		}
		
		EmployeeHistoryItem(AnyRecordToDelete record) {
			this(record.getKey(employeeCodeCanonicalization.getItemNoEmployeeId()).asString(),
					record.getKey(itemNoHistoryId).asString(),
					null);
		}
		
		AnyRecordToDelete toDelete(ExecutionContext context) {
			return AnyRecordToDelete.create(context)
				.addKey(itemNoHistoryId, StringifiedValue.of(historyId))
				.addKey(employeeCodeCanonicalization.getItemNoEmployeeId(), StringifiedValue.of(employeeId));
			
		}
		
		AnyRecordToChange toChange(ExecutionContext context) {
			return AnyRecordToChange.create(context)
					.addKey(itemNoHistoryId, StringifiedValue.of(historyId))
					.addKey(employeeCodeCanonicalization.getItemNoEmployeeId(), StringifiedValue.of(employeeId))
					.addChange(itemNoStartDate, StringifiedValue.of(period.start()))
					.addChange(itemNoEndDate, StringifiedValue.of(period.end()));
		}
		
		public DateHistoryItem toDateHistoryItem() {
			return new DateHistoryItem(historyId, period);
		}
	}
	
	@Override
	public int getItemNoOfEmployeeId() {
		return employeeCodeCanonicalization.getItemNoEmployeeId();
	}
	
	@Override
	public ImportingDataMeta appendMeta(ImportingDataMeta source) {
		return employeeCodeCanonicalization.appendMeta(source)
				.addItem("HIST_ID");
	}

}
