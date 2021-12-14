package nts.uk.ctx.exio.dom.input.canonicalize.domains.employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfo;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalItem;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalItemList;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.DomainDataColumn;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.DomainCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.ItemNoMap;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.generic.EmployeeHistoryCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.history.HistoryType;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.IntermediateResult;
import nts.uk.ctx.exio.dom.input.workspace.datatype.DataType;

/**
 * 入社退職履歴の正準化
 */
public class AffCompanyHistoryCanonicalization extends EmployeeHistoryCanonicalization{
	
	public AffCompanyHistoryCanonicalization() {
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
		public static final int CID = 100;
		public static final int SID = 101;
		public static final int HIST_ID = 102;
		public static final int PID = 103;
		public static final int 出向先データである = 104;
		public static final int 採用区分コード = 105;
		public static final int 本採用年月日 = 106;
		public static final int 退職金計算開始日 = 107;
	}
	
	@Override
	protected String getParentTableName() {
		return "BSYMT_AFF_COM_HIST";
	}
	
	@Override
	protected List<DomainDataColumn> getDomainDataKeys() {
		return Arrays.asList(new DomainDataColumn("HIST_ID", DataType.STRING));
	}
	
	@Override
	protected List<String> getChildTableNames() {
		return Arrays.asList("BSYMT_AFF_COM_HIST_ITEM");
	}
	
	@Override
	protected List<Container> canonicalizeExtends(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context,
			String employeeId,
			List<Container> targetContainers) {

		List<Container> results = new ArrayList<>();
		
		val employee = require.getEmployeeDataMngInfoByEmployeeId(employeeId).get();
		String personId = employee.getPersonId();
		
		for (val container : targetContainers) {
			
			IntermediateResult interm = container.getInterm();
			
			interm = interm.addCanonicalized(new CanonicalItemList()
					.add(Items.PID, personId)
					.add(Items.出向先データである, 0)
					.add(Items.採用区分コード, "")
					);
			
			results.add(new Container(interm, container.getAddingHistoryItem()));
		}
		
		return results;
	}
	
	@Override
	protected List<IntermediateResult> canonicalizeHistory(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context, List<IntermediateResult> employeeCanonicalized) {
		
		List<IntermediateResult> addedRetireDay = employeeCanonicalized.stream()
				.map(interm -> interm.optionalItem(CanonicalItem.of(Items.終了日, GeneralDate.ymd(9999, 12, 31))))
				.collect(Collectors.toList());
				
		return super.canonicalizeHistory(require, context, addedRetireDay);
	}
	
	public static interface RequireCanonicalizeExtends {
		Optional<EmployeeDataMngInfo> getEmployeeDataMngInfoByEmployeeId(String employeeId);
	}
}
