package nts.uk.shr.com.time.japanese;

import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.gul.util.Range;

/**
 * Japanese Era (元号)
 */
public class JapaneseEraName {

	/** code */
	@Getter
	private final String code;
	
	/** name (ex. "平成") */
	@Getter
	private String name;
	
	/** symbol (ex. "H") */
	@Getter
	private String symbol;
	
	/** span */
	private Range<GeneralDate> span;
	
	public JapaneseEraName(String code, String name, String symbol, Range<GeneralDate> span) {
		this.code = code;
		this.name = name;
		this.symbol = symbol;
		this.span = span;
	}
	
	public GeneralDate startDate() {
		return this.span.min();
	}
	
	public GeneralDate endDate() {
		return this.span.max();
	}
	
	public boolean contains(GeneralDate date) {
		return this.span.contains(date);
	}
}
