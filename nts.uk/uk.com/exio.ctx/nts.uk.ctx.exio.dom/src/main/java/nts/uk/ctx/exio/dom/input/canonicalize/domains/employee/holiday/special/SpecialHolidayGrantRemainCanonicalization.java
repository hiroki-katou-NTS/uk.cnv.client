package nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.special;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.val;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalItemList;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalizeUtil;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.DomainDataColumn;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.KeyValues;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.DomainCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.ItemNoMap;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.generic.EmployeeIndependentCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.EmployeeCodeCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.IntermediateResult;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMeta;
import nts.uk.ctx.exio.dom.input.workspace.domain.DomainWorkspace;

/**
 * 特別休暇付与残数データの正準化
 */
public class SpecialHolidayGrantRemainCanonicalization extends EmployeeIndependentCanonicalization {
	
	private final EmployeeCodeCanonicalization employeeCodeCanonicalization;
	
	public SpecialHolidayGrantRemainCanonicalization() {
		this.employeeCodeCanonicalization = new EmployeeCodeCanonicalization(getItemNoMap());
	}

	@Override
	public ItemNoMap getItemNoMap() {
		return ItemNoMap.reflection(Items.class);
	}
	
	public static class Items {
		public static final int 社員コード = 1;
		public static final int 特別休暇コード = 2;
		public static final int 特別休暇付与日 = 3;
		public static final int 特別休暇期限日 = 4;
		public static final int 特別休暇期限切れ状態 = 5;
		public static final int 特別休暇付与日数 = 6;
		public static final int 特別休暇付与時間 = 7;
		public static final int 特別休暇使用数 = 8;
		public static final int 特別休暇使用時間 = 9;
		public static final int 特別休暇残数 = 10;
		public static final int 特別休暇残時間 = 11;
		public static final int ID = 101;
		public static final int SID = 102;
		public static final int 登録種別 = 103;
		public static final int 積み崩し日数 = 104;
		public static final int 上限超過消滅日数 = 105;
		public static final int 上限超過消滅時間 = 106;
		public static final int 使用率 = 107;
	}
	
	@Override
	public void canonicalize(DomainCanonicalization.RequireCanonicalize require,ExecutionContext context) {
		// 受入データ内の重複チェック用
		Set<KeyValues> importingKeys = new HashSet<>();
		
		CanonicalizeUtil.forEachEmployee(require, context, employeeCodeCanonicalization, interms ->{
			for(val interm : interms) {
				val keyValue = getPrimaryKeys(interm);
				if (importingKeys.contains(keyValue)) {
					require.add(context, ExternalImportError.record(interm.getRowNo(), "受入データの中にキーの重複があります。"));
					return; // 次のレコードへ
				}
				importingKeys.add(keyValue);
				val addedInterm = interm.addCanonicalized(getFixedItems());
				super.canonicalize(require, context, addedInterm, keyValue);
			}
		});
	}
	
	@Override
	protected IntermediateResult canonicalizeExtends(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context, IntermediateResult interm) {
		return interm;
	}
	
	private static CanonicalItemList getFixedItems() {
		return new CanonicalItemList()
			.add(Items.SID, IdentifierUtil.randomUniqueId().toString())
			.add(Items.登録種別, 0)
			.add(Items.積み崩し日数, new BigDecimal(0.0))
			.add(Items.上限超過消滅日数, new BigDecimal(0.0))
			.add(Items.上限超過消滅時間, 0);
	}
	
	private KeyValues getPrimaryKeys(IntermediateResult interm) {
		return new KeyValues(Arrays.asList(
				interm.getItemByNo(Items.SID).get().getString(),
				interm.getItemByNo(Items.特別休暇コード).get(),
				interm.getItemByNo(Items.特別休暇付与日).get()));
	}
	
	@Override
	protected List<Integer> getPrimaryKeyItemNos(DomainWorkspace workspace){
		return Arrays.asList(Items.SID);
	}
	
	@Override
	protected String getParentTableName() {
		return "KRCDT_HD_SP_REMAIN";
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
	public ImportingDataMeta appendMeta(ImportingDataMeta source) {
		return super.appendMeta(source);
	}
}
