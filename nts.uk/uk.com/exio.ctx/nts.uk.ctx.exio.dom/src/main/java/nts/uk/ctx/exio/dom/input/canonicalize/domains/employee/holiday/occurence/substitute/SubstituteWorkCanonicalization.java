package nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.occurence.substitute;

import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalItem;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.DomainCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.ItemNoMap;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.occurence.OccurenceHolidayCanonicalizationBase;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.IntermediateResult;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMeta;

/**
 * 振出管理データの正準化
 */
public class SubstituteWorkCanonicalization extends OccurenceHolidayCanonicalizationBase {

	@Override
	public ItemNoMap getItemNoMap() {
		return ItemNoMap.reflection(Items.class);
	}
	
	public static class Items {
		public static final int 社員コード = 1;
		public static final int 振出日 = 2;
		public static final int 使用期限日 = 3;
		public static final int 振出日数 = 4;
		public static final int 振出データID = 100;
		public static final int SID = 101;
		public static final int 日付不明 = 102;
		public static final int 振休消化区分 = 103;
		public static final int 消滅日 = 104;
		public static final int 法定内外区分 = 105;
	}

	@Override
	protected IntermediateResult canonicalizeExtends(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context, IntermediateResult interm) {
		
		return interm
				.addCanonicalized(CanonicalItem.of(Items.振休消化区分, 0))
				.addCanonicalized(CanonicalItem.of(Items.法定内外区分, 0));
	}
	
	public static interface RequireAdjust {
		void deleteAllPayoutManagementData(String employeeId);
	}

	@Override
	protected void deleteExistingData(OccurenceHolidayCanonicalizationBase.RequireAdjust require, String employeeId) {
		require.deleteAllPayoutManagementData(employeeId);
	}
	
	@Override
	public ImportingDataMeta appendMeta(ImportingDataMeta source) {
		return super.appendMeta(source).addItem(getItemNameByNo(Items.振出データID));
	}
}
