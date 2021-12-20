package nts.uk.ctx.exio.dom.input.canonicalize.domains.employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.val;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.DomainCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.ItemNoMap;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.generic.EmployeeHistoryCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.history.HistoryType;
import nts.uk.ctx.exio.dom.input.canonicalize.result.CanonicalItemList;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMeta;

/**
 * 休職休業履歴の正準化
 */
public class TempAbsenceHistoryCanonicalization extends EmployeeHistoryCanonicalization {
	
	public TempAbsenceHistoryCanonicalization() {
		super(HistoryType.UNDUPLICATABLE);
	}

	@Override
	public ItemNoMap getItemNoMap() {
		return ItemNoMap.reflection(Items.class);
	}
	
	public static class Items {
		public static final int 社員コード = 1;
		public static final int 開始日 = 2;
		public static final int 終了日 = 3;
		public static final int 休職休業区分 = 4;
		public static final int 備考 = 5;
		public static final int SID = 101;
		public static final int HIST_ID = 102;
		public static final int 社会保険支給対象区分 = 103;
		public static final int 家族メンバーID = 104;
		public static final int 多胎妊娠区分 = 105;
		public static final int 同一家族の休業有無 = 106;
		public static final int 子の区分 = 107;
		public static final int 縁組成立の年月日 = 108;
		public static final int 配偶者が休業しているか = 109;
		public static final int 同一家族の短時間勤務日数 = 110;
	}
	
	@Override
	protected List<Container> canonicalizeExtends(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context,
			String employeeId,
			List<Container> targetContainers) {
		
		List<Container> canonicalizeds = new ArrayList<>();
		
		for (val container : targetContainers) {
			canonicalizeds.add(new Container(
					container.getInterm().addCanonicalized(getFixedItems()), 
					container.getAddingHistoryItem()));
		}
		
		return canonicalizeds;
	}
	
	private static CanonicalItemList getFixedItems() {
		return new CanonicalItemList()
			.add(Items.HIST_ID, IdentifierUtil.randomUniqueId().toString());
	}
	
	@Override
	protected String getParentTableName() {
		return "BSYMT_TEMP_ABS_HIST";
	}
	
	@Override
	protected List<String> getChildTableNames() {
		return Arrays.asList("BSYMT_TEMP_ABS_HIST_ITEM");
	}
	
	@Override
	public ImportingDataMeta appendMeta(ImportingDataMeta source) {
		return super.appendMeta(source);
	}
}
