package nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.annualleave;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.val;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalizeUtil;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.DomainDataColumn;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.KeyValues;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.DomainCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.ItemNoMap;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.generic.IndependentCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.EmployeeCodeCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.result.IntermediateResult;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.errors.RecordError;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMeta;
import nts.uk.ctx.exio.dom.input.workspace.domain.DomainWorkspace;

/**
 * 年休上限データ 
 */
public class MaxAnnualLeaveCanonicalization extends IndependentCanonicalization {
	
	/** 社員コードの正準化 */
	private final EmployeeCodeCanonicalization employeeCodeCanonicalization;
	
	public MaxAnnualLeaveCanonicalization() {
		this.employeeCodeCanonicalization = new EmployeeCodeCanonicalization(getItemNoMap());
	}

	@Override
	public ItemNoMap getItemNoMap() {
		return ItemNoMap.reflection(Items.class);
	}
	
	public static class Items {
		public static final int 社員コード = 1;
		public static final int 半日上限回数 = 2;
		public static final int 半休使用回数 = 3;
		public static final int 残回数 = 4;
		public static final int 時間年休上限時間 = 5;
		public static final int 時間年休使用時間 = 6;
		public static final int 残時間 = 7;
		public static final int SID = 100;
	}

	@Override
	protected String getParentTableName() {
		return "KRCDT_HDPAID_MAX";
	}

	@Override
	protected List<String> getChildTableNames() {
		return Collections.emptyList();
	}

	@Override
	protected List<DomainDataColumn> getDomainDataKeys() {
		return Arrays.asList(DomainDataColumn.SID);
	}

	@Override
	public void canonicalize(DomainCanonicalization.RequireCanonicalize require, ExecutionContext context) {
		// 受入データ内の重複チェック
		Set<KeyValues> importingKeys = new HashSet<>();
		
		CanonicalizeUtil.forEachEmployee(require, context, employeeCodeCanonicalization, interms -> {
			for(val interm : interms) {
					val error = FixedItem.getLackItemError(interm);
					if(error.isPresent()) {
						require.add(ExternalImportError.of(context.getDomainId(), error.get()));
						continue;
					}
					val keyValue = getPrimaryKeys(interm);
					if (importingKeys.contains(keyValue)) {
						require.add(ExternalImportError.record(interm.getRowNo(), context.getDomainId(), "社員コードが重複しています。"));
						return; // 次のレコードへ
					}

					//既存データのチェックと保存は継承先に任せる
					super.canonicalize(require, context, interm, getPrimaryKeys(interm));
					importingKeys.add(keyValue);
				}
		});
	}
	
	private KeyValues getPrimaryKeys(IntermediateResult interm) {
		//このドメインのKeyはSIDなので、Stringで取り出す。
		return new KeyValues(Arrays.asList(interm.getItemByNo(Items.SID).get().getString()));
	}
	
	@Override
	protected List<Integer> getPrimaryKeyItemNos(DomainWorkspace workspace) {
		return Arrays.asList(Items.SID);
	}

	private static class FixedItem{
		private static final Map<Integer,String> timesNumbers = new HashMap<>();
		static {
			timesNumbers.put(Items.半日上限回数, "半日上限回数");
			timesNumbers.put(Items.半休使用回数, "半休使用回数");
			timesNumbers.put(Items.残回数, "残回数");
		}
		private static final Map<Integer,String> timeNumbers = new HashMap<>();
		static {
			timeNumbers.put(Items.時間年休上限時間, "時間年休上限時間");
			timeNumbers.put(Items.時間年休使用時間, "時間年休使用時間");
			timeNumbers.put(Items.残時間, "残時間");
		}
		
		/**
		 * 受入れる時は1セットとして考えなければならない
		 * 項目を歯抜けで受入れようとしている
		 * @param interm 
		 */
		private static Optional<RecordError> getLackItemError(IntermediateResult interm) {
			if(!hasTimeAllItemNoOrAllNothing(interm, timesNumbers.keySet())) {
				return Optional.of(RecordError.record(interm.getRowNo(),
							timesNumbers.values().stream().collect(Collectors.joining("、")) 
							+ "は同時に受入れなければなりません。"
						));
			}
			else if(!hasTimeAllItemNoOrAllNothing(interm, timeNumbers.keySet())) {
				return Optional.of(RecordError.record(interm.getRowNo(),
						timeNumbers.values().stream().collect(Collectors.joining("、")) 
						+ "は同時に受入れなければなりません。"
					));
			}
			return Optional.empty();
		}
		
		/**
		 * All or Nothing でTrue,歯抜けの時はは抜けてる項目名 
		 */
		private static boolean hasTimeAllItemNoOrAllNothing(IntermediateResult interm, Set<Integer> items) {
			return (items.stream().allMatch(t -> interm.getItemByNo(t).get().isNull())
			|| items.stream().allMatch(t -> !interm.getItemByNo(t).get().isNull()));
		}
	}
	@Override
	public ImportingDataMeta appendMeta(ImportingDataMeta source) {
		return employeeCodeCanonicalization.appendMeta(source);
	}
}
