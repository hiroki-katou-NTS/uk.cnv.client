package nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.occurence.substitute;

import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.DomainCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.ItemNoMap;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.occurence.OccurenceHolidayCanonicalizationBase;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.IntermediateResult;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMeta;

/**
 * 振休管理データの正準化
 */
public class SubstituteHolidayCanonicalization extends OccurenceHolidayCanonicalizationBase {
	
	@Override
	public ItemNoMap getItemNoMap() {
		return ItemNoMap.reflection(Items.class);
	}
	
	public static class Items {
		public static final int 社員コード = 1;
		public static final int 振休日 = 2;
		public static final int 振休日数 = 3;
		public static final int 振休データID = 100;
		public static final int SID = 101;
		public static final int 日付不明 = 102;
	}

	@Override
	protected IntermediateResult canonicalizeExtends(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context, IntermediateResult interm) {
		
		// 追加処理不要
		return interm;
	}
	
	public static interface RequireAdjust {
		
		void deleteAllSubstitutionOfHDManagementData(String employeeId);
	}


	@Override
	protected void deleteExistingData(OccurenceHolidayCanonicalizationBase.RequireAdjust require, String employeeId) {
		require.deleteAllSubstitutionOfHDManagementData(employeeId);
	}
	
	@Override
	public ImportingDataMeta appendMeta(ImportingDataMeta source) {
		return super.appendMeta(source).addItem(getItemNameByNo(Items.振休データID));
	}
}
