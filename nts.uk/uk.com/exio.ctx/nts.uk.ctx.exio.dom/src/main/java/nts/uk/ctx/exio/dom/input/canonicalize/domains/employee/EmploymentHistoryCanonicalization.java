package nts.uk.ctx.exio.dom.input.canonicalize.domains.employee;

import java.util.Arrays;
import java.util.List;

import nts.uk.ctx.exio.dom.input.canonicalize.domains.ItemNoMap;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.generic.EmployeeHistoryCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.history.HistoryType;

/**
 * 雇用履歴の正準化 
 */
public class EmploymentHistoryCanonicalization extends EmployeeHistoryCanonicalization{

	public EmploymentHistoryCanonicalization() {
		super(HistoryType.PERSISTENERESIDENT);
	}

	@Override
	public ItemNoMap getItemNoMap() {
		return ItemNoMap.reflection(Items.class);
	}
	
	public static class Items {
		public static final int 社員コード = 1;
		public static final int 開始日 = 2;
		public static final int 終了日 = 3;
		public static final int 雇用コード = 4;
		public static final int SID = 101;
		public static final int HIST_ID = 102;
		public static final int 給与区分 = 103;
	}
	
	@Override
	protected String getParentTableName() {
		return "BSYMT_AFF_EMP_HIST";
	}

	@Override
	protected List<String> getChildTableNames() {
		return Arrays.asList("BSYMT_AFF_EMP_HIST_ITEM");
	}
}
