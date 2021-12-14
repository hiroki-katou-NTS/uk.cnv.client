package nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.annualleave;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.val;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.GrantRemainRegisterType;
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
 * 年休付与残数データ 
 */
public class AnnualLeaveRemainingCanonicalization extends IndependentCanonicalization {
	
	private final EmployeeCodeCanonicalization employeeCodeCanonicalization;
	
	public AnnualLeaveRemainingCanonicalization() {
		this.employeeCodeCanonicalization = new EmployeeCodeCanonicalization(getItemNoMap());
	}

	@Override
	public ItemNoMap getItemNoMap() {
		return ItemNoMap.reflection(Items.class);
	}
	
	public static class Items {
		public static final int 社員コード = 1;
		public static final int 年休付与日 = 2;
		public static final int 年休期限日 = 3;
		public static final int 年休期限切れ状態 = 4;
		public static final int 年休付与日数使用状況 = 5;
		public static final int 年休付与時間使用状況 = 6;
		public static final int 年休使用数使用状況 = 7;
		public static final int 年休使用時間使用状況 = 8;
		public static final int 年休残数使用状況 = 9;
		public static final int 年休残時間使用状況 = 10;
		public static final int ID = 100;
		public static final int SID = 101;
		public static final int 登録種別 = 102;
		public static final int 積み崩し日数 = 103;
		public static final int 上限超過消滅日数 = 104;
		public static final int 使用率 = 105;
		public static final int 所定日数 = 106;
		public static final int 控除日数 = 107;
		public static final int 労働日数 = 108;
	}
	
	@Override
	protected String getParentTableName() {
		return "KRCDT_HDPAID_REM";
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

		//社員コード⇒IDの正準化
		CanonicalizeUtil.forEachEmployee(require, context, employeeCodeCanonicalization, interms ->{
			for(val interm : interms) {
				val keyValue = getPrimaryKeys(interm);
				if (importingKeys.contains(keyValue)) {
					require.add(context, ExternalImportError.record(interm.getRowNo(), "受入データの中にキーの重複があります。"));
					return; // 次のレコードへ
				}
				importingKeys.add(keyValue);
				
				super.canonicalize(require, context, interm, keyValue);
			}
		});
	}
	
	private KeyValues getPrimaryKeys(IntermediateResult interm) {
		return new KeyValues(Arrays.asList(
				interm.getItemByNo(Items.SID).get().getString(),
				interm.getItemByNo(Items.年休付与日).get().getDate()));
	}

	@Override
	protected IntermediateResult canonicalizeExtends(IntermediateResult targertResult) {
		return addFixedItems(targertResult);
	}

	/**
	 *  受入時に固定の値を入れる物たち
	 */
	private IntermediateResult addFixedItems(IntermediateResult interm) {
		return interm.addCanonicalized(CanonicalItem.of(Items.ID, IdentifierUtil.randomUniqueId()))
				  .addCanonicalized(CanonicalItem.of(Items.登録種別, GrantRemainRegisterType.MANUAL.value))
				  .addCanonicalized(CanonicalItem.of(Items.積み崩し日数, BigDecimal.ZERO))
				  .addCanonicalized(CanonicalItem.of(Items.上限超過消滅日数, BigDecimal.ZERO))
				  .addCanonicalized(CanonicalItem.of(Items.使用率, BigDecimal.ZERO))
				  .addCanonicalized(CanonicalItem.of(Items.所定日数, 0))
				  .addCanonicalized(CanonicalItem.of(Items.控除日数, 0))
				  .addCanonicalized(CanonicalItem.of(Items.労働日数, 0));
	}
	
	@Override
	protected List<Integer> getPrimaryKeyItemNos(DomainWorkspace workspace) {
		return Arrays.asList(Items.SID);
	}
	
	@Override
	public ImportingDataMeta appendMeta(ImportingDataMeta source) {
		return employeeCodeCanonicalization.appendMeta(source);
	}
}
