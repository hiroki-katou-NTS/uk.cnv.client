package nts.uk.ctx.at.function.dom.dailyperformanceformat;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.primitivevalue.DailyPerformanceFormatCode;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.primitivevalue.DailyPerformanceFormatName;

/**
 * 
 * @author nampt
 *
 */
@Getter
public class AuthorityDailyPerformanceFormat extends AggregateRoot {

	private String companyId;

	private DailyPerformanceFormatCode dailyPerformanceFormatCode;

	private DailyPerformanceFormatName dailyPerformanceFormatName;

	public AuthorityDailyPerformanceFormat(String companyId, DailyPerformanceFormatCode dailyPerformanceFormatCode,
			DailyPerformanceFormatName dailyPerformanceFormatName) {
		super();
		this.companyId = companyId;
		this.dailyPerformanceFormatCode = dailyPerformanceFormatCode;
		this.dailyPerformanceFormatName = dailyPerformanceFormatName;
	}

	public static AuthorityDailyPerformanceFormat createFromJavaType(String companyId,
			String dailyPerformanceFormatCode, String dailyPerformanceFormatName) {
		return new AuthorityDailyPerformanceFormat(companyId,
				new DailyPerformanceFormatCode(dailyPerformanceFormatCode),
				new DailyPerformanceFormatName(dailyPerformanceFormatName));
	}

}
