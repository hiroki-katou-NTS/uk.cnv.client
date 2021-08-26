package nts.uk.ctx.exio.dom.input.canonicalize.domains.generic;

import static java.util.stream.Collectors.*;
import static nts.uk.ctx.exio.dom.input.canonicalize.ImportingMode.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalizeUtil;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.DomainDataColumn;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.DomainDataId;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.KeyValues;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.DomainCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToChange;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToDelete;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.StringifiedValue;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.IntermediateResult;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMeta;
import nts.uk.ctx.exio.dom.input.setting.assembly.RevisedDataRecord;
import nts.uk.ctx.exio.dom.input.workspace.domain.DomainWorkspace;
import nts.uk.ctx.exio.dom.input.workspace.item.WorkspaceItem;

/**
 * 1レコードずつ完全に独立しているドメインの正準化
 */
@RequiredArgsConstructor
public abstract class IndependentCanonicalization implements DomainCanonicalization {
	
	protected final DomainWorkspace workspace;
	
	protected abstract String getParentTableName();
	
	protected abstract List<String> getChildTableNames();
	
	protected abstract List<DomainDataColumn> getDomainDataKeys();

	@Override
	public void canonicalize(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context) {
		
		// 受入データ内の重複チェック
		Set<List<Object>> importingKeys = new HashSet<>();
		
		CanonicalizeUtil.forEachRow(require, context, revisedData -> {
			
			val key = getPrimaryKeys(revisedData, workspace);
			if (importingKeys.contains(key)) {
				require.add(context, ExternalImportError.record(revisedData.getRowNo(), "受入データの中にキーの重複があります。"));
				return; // 次のレコードへ
			}
			
			importingKeys.add(key);
			
			// データ自体を正準化する必要は無い
			val intermResult = IntermediateResult.noChange(revisedData);
			canonicalize(require, context, intermResult, new KeyValues(key));
		});
	}
	
	/**
	 * Record(CSV行番号, 編集済みの項目List)のListの方からworkspaceの項目Noに一致しているやつの値を取る 
	 */
	private static List<Object> getPrimaryKeys(RevisedDataRecord record, DomainWorkspace workspace) {
		
		return workspace.getItemsPk().stream()
				.map(k -> record.getItemByNo(k.getItemNo()).get())
				.map(item -> item.getValue())
				.collect(toList());
	}

	protected void canonicalize(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context,
			IntermediateResult intermResult,
			KeyValues keyValues) {
		
		val domainDataId = createDomainDataId(getParentTableName(), getDomainDataKeys(), keyValues);
		boolean exists = require.existsDomainData(domainDataId);
		
		// 受け入れず無視するケース
		if (!context.getMode().canImport(exists)) {
			return;
		}
		
		if (context.getMode() == DELETE_RECORD_BEFOREHAND) {
			// 既存データがあれば削除する（DELETE文になるので、実際にデータがあるかどうかのチェックは不要）
			require.save(context, toDelete(context, workspace, keyValues));
		}
		
		require.save(context, intermResult.complete());
	}
	
	private static AnyRecordToDelete toDelete(
			ExecutionContext context,
			DomainWorkspace workspace,
			KeyValues keyValues) {
		
		val toDelete = AnyRecordToDelete.create(context); 
		
		for (int i = 0; i < workspace.getItemsPk().size(); i++) {
			val pkItem = workspace.getItemsPk().get(i);
			val keyValue = keyValues.get(i);
			
			val stringified = StringifiedValue.create(keyValue);
			toDelete.addKey(pkItem.getItemNo(), stringified);
		}
		
		return toDelete;
	}

	public static interface RequireCanonicalize {
		
		/** ドメイン側のテーブルに既存データがあるか */
		boolean existsDomainData(DomainDataId id);
	}

	@Override
	public ImportingDataMeta appendMeta(ImportingDataMeta source) {
		return source;
	}

	@Override
	public AtomTask adjust(
			RequireAdjsut require,
			List<AnyRecordToChange> recordsToChange,
			List<AnyRecordToDelete> recordsToDelete) {
		
		if (!recordsToChange.isEmpty()) {
			throw new RuntimeException("既存データの変更はありえない");
		}

		return AtomTask.of(() -> {
			for (val record : recordsToDelete) {
				toDomainDataIds(record).forEach(id -> require.deleteDomainData(id));
			}
		});
	}
	
	protected List<DomainDataId> toDomainDataIds(AnyRecordToDelete toDelete) {
		
		val keyValues = new KeyValues(toKeyValueObjects(toDelete));
		
		List<String> tableNames = new ArrayList<>();
		tableNames.add(getParentTableName());
		tableNames.addAll(getChildTableNames());
		
		val keys = getDomainDataKeys();
		return tableNames.stream()
				.map(tn -> createDomainDataId(tn, keys, keyValues))
				.collect(toList());
	}
	
	private List<Object> toKeyValueObjects(AnyRecordToDelete toDelete) {
		
		List<Object> keyValues = new ArrayList<>();
		
		val dataKeys = getDomainDataKeys();
		for (int i = 0; i < dataKeys.size(); i++) {
			val dataKey = dataKeys.get(i);
			val stringified = toDelete.getPrimaryKeys().get(i).getValue();
			
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
			default:
				throw new RuntimeException("unknown: " + dataKey);
			}

			keyValues.add(value);
		}
		
		return keyValues;
	}
	
	public static interface RequireAdjust {
		
		void deleteDomainData(DomainDataId id);
	}
	
	protected static DomainDataId createDomainDataId(String tableName, List<DomainDataColumn> keys, KeyValues keyValues) {
		
		val builder = DomainDataId.builder(tableName, keyValues);
		keys.forEach(k -> builder.addKey(k));
		
		return builder.build();
	}

	@Override
	public int getItemNoOfEmployeeId() {
		WorkspaceItem wsItem = this.workspace.getAllItemsSortedByItemNo().stream()
				.filter(item -> item.getName().equals("SID"))
				.findFirst()
				.orElseThrow(() -> new UnsupportedOperationException());

		return wsItem.getItemNo();
	}
}
