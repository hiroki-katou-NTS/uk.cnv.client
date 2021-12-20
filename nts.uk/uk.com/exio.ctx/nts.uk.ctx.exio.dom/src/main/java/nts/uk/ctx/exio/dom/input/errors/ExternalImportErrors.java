package nts.uk.ctx.exio.dom.input.errors;

import static java.util.stream.Collectors.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.val;
import nts.uk.ctx.exio.dom.input.ExecutionContext;

public class ExternalImportErrors {

	private final List<ExternalImportError> errors;
	
	public ExternalImportErrors(List<ExternalImportError> errors) {
		
		this.errors = errors.stream()
				.sorted(Comparator.comparing(e -> e.getCsvRowNo() != null ? e.getCsvRowNo() : 0))
				.collect(toList());
	}
	
	public int count() {
		return errors.size();
	}
	
	public boolean isExecution() {
		return errors.stream()
				.filter(e -> e.isExecution() == true)
				.collect(Collectors.toList())
				.size() > 0;
	}
	
	public String toText(RequireToText require) {
		
		val sb = new StringBuilder();
		
		errors.forEach(e -> {
			e.toText(require, sb);
			sb.append("\r\n");
		});
		
		return sb.toString();
	}
	
	public static interface RequireToText extends ExternalImportError.RequireToText {
	}
}
