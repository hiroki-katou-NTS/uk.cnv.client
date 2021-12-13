package nts.uk.ctx.exio.dom.input.canonicalize.domains.employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.val;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.DomainCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.ItemNoMap;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.generic.EmployeeHistoryCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.history.HistoryType;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.JobTitleCodeCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.result.IntermediateResult;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMeta;

/**
 * 所属職位履歴グループの正準化用定義 
 */
public class AffJobTitleHistoryCanonicalization extends EmployeeHistoryCanonicalization {
	
	private final JobTitleCodeCanonicalization jobTitleCodeCanonicalization;

	public AffJobTitleHistoryCanonicalization() {
		super(HistoryType.PERSISTENERESIDENT);
		jobTitleCodeCanonicalization = new JobTitleCodeCanonicalization(this.getItemNoMap());
	}
	
	@Override
	public ItemNoMap getItemNoMap() {
		return ItemNoMap.reflection(Items.class);
	}
	
	public static class Items {
		public static final int 社員コード = 1;
		public static final int 開始日 = 2;
		public static final int 終了日 = 3;
		public static final int 職位コード = 4;
		public static final int JOB_TITLE_ID = 5;
		public static final int SID = 101;
		public static final int HIST_ID = 102;
	}
	
	@Override
	protected List<Container> canonicalizeExtends(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context,
			String employeeId,
			List<Container> targetContainers) {

		List<Container> results = new ArrayList<>();
		for (val container : targetContainers) {
			IntermediateResult interm = container.getInterm();
			
			jobTitleCodeCanonicalization.canonicalize(require, interm, interm.getRowNo())
					.ifRight(canonicalized -> results.add(new Container(canonicalized, container.getAddingHistoryItem())))
					.ifLeft(error -> require.add(ExternalImportError.of(context.getDomainId(), error)));
		}
		return results;
	}
			

	@Override
	protected String getParentTableName() {
		return "BSYMT_AFF_JOB_HIST";
	}
	@Override
	protected List<String> getChildTableNames() {
		return Arrays.asList("BSYMT_AFF_JOB_HIST_ITEM");
	}

	@Override
	public ImportingDataMeta appendMeta(ImportingDataMeta source) {
		return super.appendMeta(source)
				.addItem(getItemNameByNo(Items.JOB_TITLE_ID));
	}

	public interface RequireCanonicalize extends JobTitleCodeCanonicalization.Require{
	}
	public interface RequireAdjust extends EmployeeHistoryCanonicalization.RequireAdjust{
	}
}
