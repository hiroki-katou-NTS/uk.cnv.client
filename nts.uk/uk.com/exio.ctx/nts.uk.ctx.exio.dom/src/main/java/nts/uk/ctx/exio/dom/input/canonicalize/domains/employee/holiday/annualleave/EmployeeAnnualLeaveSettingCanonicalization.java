package nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.annualleave;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.val;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalItem;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalizeUtil;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.DomainDataColumn;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.KeyValues;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.DomainCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.ItemNoMap;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.generic.IndependentCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.EmployeeCodeCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.IntermediateResult;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMeta;
import nts.uk.ctx.exio.dom.input.workspace.domain.DomainWorkspace;

/**
 * 社員の年休付与設定
 */
public class EmployeeAnnualLeaveSettingCanonicalization extends IndependentCanonicalization {
	
	private final EmployeeCodeCanonicalization employeeCodeCanonicalization;
	
	public EmployeeAnnualLeaveSettingCanonicalization() {
		this.employeeCodeCanonicalization = new EmployeeCodeCanonicalization(getItemNoMap());
	}

	@Override
	public ItemNoMap getItemNoMap() {
		return ItemNoMap.reflection(Items.class);
	}
	
	public static class Items {
		public static final int 社員コード = 1;
		public static final int 導入前労働日数 = 2;
		public static final int 年休付与基準日 = 3;
		public static final int 年休付与テーブル = 4;
		public static final int 年間所定労働日数 = 100;
		public static final int SID = 101;
	}

	@Override
	protected String getParentTableName() {
		return "KRCMT_HDPAID_BASIC";
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
	public void canonicalize(DomainCanonicalization.RequireCanonicalize require,ExecutionContext context) {
		// 受入データ内の重複チェック
		Set<KeyValues> importingKeys = new HashSet<>();

		//社員コード⇒IDの正準化
		CanonicalizeUtil.forEachEmployee(require, context, employeeCodeCanonicalization, interms ->{
			for(val interm : interms) {
				val keyValue = getPrimaryKeys(interm);
				if (importingKeys.contains(keyValue)) {
					require.add(context, ExternalImportError.record(interm.getRowNo(), "社員コードが重複しています。"));
					return; // 次のレコードへ
				}
				importingKeys.add(keyValue);
				
				super.canonicalize(require, context, interm, keyValue);
			}
		});
	}
	
	private KeyValues getPrimaryKeys(IntermediateResult interm) {
		return new KeyValues(Arrays.asList(interm.getItemByNo(Items.SID).get().getString()));
	}

	@Override
	protected List<Integer> getPrimaryKeyItemNos(DomainWorkspace workspace) {
		return Arrays.asList(Items.SID);
	}
	
	/**
	 * 追加の正準化処理が必要ならoverrideすること
	 * @param targetContainers
	 */
	protected IntermediateResult canonicalizeExtends(IntermediateResult targertResult) {
		return addFixedItems(targertResult);
	}
	
	/**
	 *  受入時に固定の値を入れる物たち
	 */
	private IntermediateResult addFixedItems(IntermediateResult interm) {
	    return interm.addCanonicalized(CanonicalItem.nullValue(100), 100)
	    		    		   .optionalItem(CanonicalItem.of(2, 0));
	}
	
	@Override
	public ImportingDataMeta appendMeta(ImportingDataMeta source) {
		return employeeCodeCanonicalization.appendMeta(source);
	}
}
