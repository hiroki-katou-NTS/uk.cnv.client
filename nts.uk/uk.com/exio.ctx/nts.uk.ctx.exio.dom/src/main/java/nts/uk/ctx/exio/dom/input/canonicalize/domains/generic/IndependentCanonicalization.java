package nts.uk.ctx.exio.dom.input.canonicalize.domains.generic;

import static java.util.stream.Collectors.*;
import static nts.uk.ctx.exio.dom.input.canonicalize.ImportingMode.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import nts.uk.ctx.exio.dom.input.canonicalize.result.IntermediateResult;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMeta;
import nts.uk.ctx.exio.dom.input.setting.assembly.RevisedDataRecord;
import nts.uk.ctx.exio.dom.input.workspace.domain.DomainWorkspace;

/**
 * 1レコードずつ完全に独立しているドメインの正準化
 */
public abstract class IndependentCanonicalization implements DomainCanonicalization {
	
	protected abstract String getParentTableName();
	
	protected abstract List<String> getChildTableNames();
	
	protected abstract List<DomainDataColumn> getDomainDataKeys();

	@Override
	public void canonicalize(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context) {

		val workspace = require.getDomainWorkspace(context.getDomainId());
		
		// 受入データ内の重複チェック
		Set<KeyValues> importingKeys = new HashSet<>();
		
		CanonicalizeUtil.forEachRow(require, context, revisedData -> {
			
			KeyValues key = getPrimaryKeys(revisedData, workspace);
			if (importingKeys.contains(key)) {
				require.add(ExternalImportError.record(revisedData.getRowNo(), context.getDomainId(),"受入データの中にキーの重複があります。"));
				return; // 次のレコードへ
			}
			
			importingKeys.add(key);
			
			// データ自体を正準化する必要は無い
			val intermResult = IntermediateResult.create(revisedData);
			canonicalize(require, context, intermResult, key);
		});
	}
	
	/**
	 * Record(CSV行番号, 編集済みの項目List)のListの方からworkspaceの項目Noに一致しているやつの値を取る 
	 */
	protected KeyValues getPrimaryKeys(RevisedDataRecord record, DomainWorkspace workspace) {
		
		return getPrimaryKeyItemNos(workspace).stream()
				.map(itemNo -> record.getItemByNo(itemNo).get())
				.map(item -> item.getValue())
				.collect(Collectors.collectingAndThen(toList(), KeyValues::new));
	}
	
	/**
	 * 既存データの補正に使用する主キーが格納される項目NOを返す（Workspaceとは別の主キーを指定したければOverrideすること）
	 * @return
	 */
	protected List<Integer> getPrimaryKeyItemNos(DomainWorkspace workspace) {
		return workspace.getItemsPk().stream()
				.map(k -> k.getItemNo())
				.collect(toList());
	}

	protected void canonicalize(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context,
			IntermediateResult intermResult,
			KeyValues keyValues) {
		
		val domainDataId = DomainDataId.createDomainDataId(getParentTableName(), getDomainDataKeys(), keyValues);
		boolean exists = require.existsDomainData(domainDataId);
		
		// 受け入れず無視するケース
		if (!context.getMode().canImport(exists)) {
			return;
		}
		
		if (context.getMode() == DELETE_RECORD_BEFOREHAND) {
			// 既存データがあれば削除する（DELETE文になるので、実際にデータがあるかどうかのチェックは不要）
			val workspace = require.getDomainWorkspace(context.getDomainId());
			require.save(context, toDelete(context, workspace, keyValues));
		}
		
		require.save(context, canonicalizeExtends(intermResult).complete());
	}
	
	/**
	 * 追加の正準化処理が必要ならoverrideすること
	 * @param targertResult
	 */
	protected IntermediateResult canonicalizeExtends(IntermediateResult targertResult) {
		return targertResult;
	}

	private AnyRecordToDelete toDelete(
			ExecutionContext context,
			DomainWorkspace workspace,
			KeyValues keyValues) {
		
		val toDelete = AnyRecordToDelete.create(context); 
		val keys = getPrimaryKeyItemNos(workspace);
		
		for (int i = 0; i < keys.size(); i++) {
			val pkItemNo = keys.get(i);
			val keyValue = keyValues.get(i);
			
			val stringified = StringifiedValue.create(keyValue);
			toDelete.addKey(pkItemNo, stringified);
		}
		
		return toDelete;
	}

	public static interface RequireCanonicalize {
		
		/** ドメイン側のテーブルに既存データがあるか */
		boolean existsDomainData(DomainDataId id);
		
		DomainWorkspace getDomainWorkspace(ImportingDomainId domainId);
	}

	@Override
	public ImportingDataMeta appendMeta(ImportingDataMeta source) {
		return source;
	}

	@Override
	public AtomTask adjust(
			RequireAdjsut require,
			ExecutionContext context,
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
				.map(tn -> DomainDataId.createDomainDataId(tn, keys, keyValues))
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
}
