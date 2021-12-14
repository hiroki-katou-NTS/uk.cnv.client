package nts.uk.ctx.exio.dom.input.canonicalize.domains.employee;

import static nts.uk.ctx.exio.dom.input.workspace.datatype.DataType.*;

import java.util.Arrays;
import java.util.List;

import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.DomainDataColumn;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.ItemNoMap;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.generic.EmployeeHistoryCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.history.HistoryType;

/**
 *分類履歴グループの正準化用定義 
 */
public class AffClassHistoryCanonicalization extends EmployeeHistoryCanonicalization {
	
	public AffClassHistoryCanonicalization() {
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
		public static final int 分類コード = 4;
		public static final int SID = 101;
		public static final int HIST_ID = 102;
	}
	
	@Override
	protected String getParentTableName() {
		return "BSYMT_AFF_CLASS_HIST";
	}

	@Override
	protected List<String> getChildTableNames() {
		return Arrays.asList("BSYMT_AFF_CLASS_HIST_ITEM");
	}

	@Override
	protected List<DomainDataColumn> getDomainDataKeys() {
		return Arrays.asList(
				new DomainDataColumn("HIST_ID", STRING));
	}
}
