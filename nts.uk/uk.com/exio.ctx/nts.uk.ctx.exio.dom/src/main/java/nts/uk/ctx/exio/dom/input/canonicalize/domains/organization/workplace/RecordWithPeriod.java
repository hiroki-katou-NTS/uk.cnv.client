package nts.uk.ctx.exio.dom.input.canonicalize.domains.organization.workplace;

import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalItem;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.organization.workplace.WorkplaceCanonicalization.Items;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.IntermediateResult;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.util.Either;

@AllArgsConstructor()
class RecordWithPeriod {

	final DatePeriod period;
	
	final IntermediateResult interm;
	
	public static Either<ExternalImportError, RecordWithPeriod> build(IntermediateResult interm) {
		
		val period = PeriodUtil.getPeriod(interm);
		
		if (period.isReversed()) {
			return Either.left(ExternalImportError.record(interm.getRowNo(), "開始日と終了日が逆転しています。"));
		}
		
		return Either.right(new RecordWithPeriod(period, interm));
	}
	
	public int getRowNo() {
		return interm.getRowNo();
	}
	
	public RecordWithPeriod changePeriod(DatePeriod newPeriod) {
		
		val newInterm = interm
				.addCanonicalized(CanonicalItem.of(Items.開始日, newPeriod.start()))
				.addCanonicalized(CanonicalItem.of(Items.終了日, newPeriod.end()));
		
		return new RecordWithPeriod(newPeriod, newInterm);
	}
	
	public RecordWithPeriod canonicalize(Function<IntermediateResult, IntermediateResult> process) {
		return new RecordWithPeriod(period, process.apply(interm));
	}
}
