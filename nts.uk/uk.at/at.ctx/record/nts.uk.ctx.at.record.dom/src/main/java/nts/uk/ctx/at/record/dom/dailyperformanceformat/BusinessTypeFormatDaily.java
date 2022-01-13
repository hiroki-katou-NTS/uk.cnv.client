package nts.uk.ctx.at.record.dom.dailyperformanceformat;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.workrule.businesstype.BusinessTypeCode;

/**
 * 
 * @author nampt
 * 日次表示項目シート一覧
 *
 */
@Getter
public class BusinessTypeFormatDaily extends AggregateRoot {

	private String companyId;

	/**
	 * padding left "0"
	 * 勤務種別コード
	 */
	@Setter
	private BusinessTypeCode businessTypeCode;

	/**
	 * 日次表示項目一覧
	 */
	private int attendanceItemId;

	/**
	 * 並び順
	 */
	private BigDecimal sheetNo;

	/**
	 * 並び順
	 */
	private int order;

	/**
	 * 列幅
	 */
	private BigDecimal columnWidth;

	public BusinessTypeFormatDaily(String companyId, BusinessTypeCode businessTypeCode, int attendanceItemId,
			BigDecimal sheetNo, int order, BigDecimal columnWidth) {
		super();
		this.companyId = companyId;
		this.businessTypeCode = businessTypeCode;
		this.attendanceItemId = attendanceItemId;
		this.sheetNo = sheetNo;
		this.order = order;
		this.columnWidth = columnWidth;
	}

	public static BusinessTypeFormatDaily createFromJavaType(String companyId, String businessTypeCode,
			int attendanceItemId, BigDecimal sheetNo, int order, BigDecimal columnWidth) {
		return new BusinessTypeFormatDaily(companyId, new BusinessTypeCode(businessTypeCode), attendanceItemId, sheetNo,
				order, columnWidth);
	}

	public void setSheetNo(BigDecimal sheetNo) {
		this.sheetNo = sheetNo;
	}
	
	public BusinessTypeFormatDaily clone() {
		return new BusinessTypeFormatDaily(companyId, businessTypeCode,attendanceItemId,sheetNo,order,columnWidth);
	}
	
	
	
}
