package nts.uk.ctx.exio.dom.input.canonicalize;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.function.Consumer;

import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.CanonicalizationMethodRequire;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.EmployeeCodeCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.result.IntermediateResult;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.setting.assembly.RevisedDataRecord;

public class CanonicalizeUtil {

	public static void forEachRow(
			CanonicalizationMethodRequire require,
			ExecutionContext context,
			Consumer<RevisedDataRecord> process) {

		int rowsCount = require.getMaxRowNumberOfRevisedData(context);
		
		for (int rowNo = 1; rowNo <= rowsCount; rowNo++) {
			require.getRevisedDataRecordByRowNo(context, rowNo).ifPresent(process);
		}
	}
	
	public static void forEachEmployee(
			CanonicalizationMethodRequire require,
			ExecutionContext context,
			EmployeeCodeCanonicalization employeeCodeCanonicalization,
			Consumer<List<IntermediateResult>> process) {
		
		List<String> employeeCodes = require.getStringsOfRevisedData(
				context,
				employeeCodeCanonicalization.getItemNoEmployeeCode());
		
		for (String employeeCode : employeeCodes) {
			
			employeeCodeCanonicalization.canonicalize(require, context, employeeCode)
				.map(stream -> stream.collect(toList()))
				.ifRight(process)
				.ifLeft(errors -> errors.forEach(error -> {
					require.add(ExternalImportError.of(context.getDomainId(), error));
				}));
		}
	}
}
