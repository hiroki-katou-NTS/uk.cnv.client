package nts.uk.ctx.at.record.dom.dailyperformanceformat;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.workrule.businesstype.BusinessTypeCode;

/**
 * 
 * @author nampt
 * 月次表示項目一覧
 *
 */
@Getter
public class BusinessTypeFormatMonthly extends AggregateRoot {

	private String companyId;

	/**
	 * padding left "0"
	 * 勤務種別コード
	 */
	@Setter
	private BusinessTypeCode businessTypeCode;

	/**
	 * 表示する項目
	 */
	private int attendanceItemId;

	/**
	 * 並び順
	 */
	private int order;

	/**
	 * 列幅
	 */
	private BigDecimal columnWidth;

	public BusinessTypeFormatMonthly(String companyId, BusinessTypeCode businessTypeCode, int attendanceItemId, int order,
			BigDecimal columnWidth) {
		super();
		this.companyId = companyId;
		this.businessTypeCode = businessTypeCode;
		this.attendanceItemId = attendanceItemId;
		this.order = order;
		this.columnWidth = columnWidth;
	}

	public static BusinessTypeFormatMonthly createFromJavaType(String companyId, String businessTypeCode,
			int attendanceItemId, int order, BigDecimal columnWidth) {
		return new BusinessTypeFormatMonthly(companyId, new BusinessTypeCode(businessTypeCode), attendanceItemId, order,
				columnWidth);
	}
}
