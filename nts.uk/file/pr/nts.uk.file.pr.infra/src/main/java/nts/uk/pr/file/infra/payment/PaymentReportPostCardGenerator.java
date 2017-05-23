/******************************************************************
 * Copyright (c) 2016 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.pr.file.infra.payment;

import java.util.List;

import com.aspose.cells.Cells;
import com.aspose.cells.Worksheet;

import nts.uk.file.pr.app.export.payment.data.PaymentReportData;
import nts.uk.file.pr.app.export.payment.data.dto.PaymentReportDto;
import nts.uk.file.pr.app.export.payment.data.dto.PaymentSalaryItemDto;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportContext;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;

/**
 * The Class PaymentReportPostCardGenerator.
 */
public class PaymentReportPostCardGenerator extends AsposeCellsReportGenerator
	implements PaymentGenerator {

	/** The work sheet. */
	protected Worksheet workSheet;

	/** The cells. */
	protected Cells cells;

	/** The Constant START_ROW_CATEGORY_ATTENDANCE. */
	public static final int START_ROW_CATEGORY_ATTENDANCE = 4;

	/** The Constant START_COL_CATEGORY_ATTENDANCE. */
	public static final int START_COL_CATEGORY_ATTENDANCE = 3;

	/** The Constant START_ROW_CATEGORY_PAYMENT. */
	public static final int START_ROW_CATEGORY_PAYMENT = 4;

	/** The Constant START_COL_CATEGORY_PAYMENT. */
	public static final int START_COL_CATEGORY_PAYMENT = 6;

	/** The Constant START_ROW_CATEGORY_DEDUCTION. */
	public static final int START_ROW_CATEGORY_DEDUCTION = 4;

	/** The Constant START_COL_CATEGORY_DEDUCTION. */
	public static final int START_COL_CATEGORY_DEDUCTION = 9;

	/** The Constant START_ROW_CATEGORY_ARTICLE. */
	public static final int START_ROW_CATEGORY_ARTICLE = 13;

	/** The Constant START_COL_CATEGORY_ARTICLE. */
	public static final int START_COL_CATEGORY_ARTICLE = 13;
	
	
	/** The Constant START_ROW_CATEGORY_OTHER. */
	public static final int START_ROW_CATEGORY_OTHER = 23;
	
	/** The Constant START_COL_CATEGORY_OTHER. */
	public static final int START_COL_CATEGORY_OTHER = 13;

	/** The Constant FIRST_SHEET. */
	public static final int FIRST_SHEET = 0;

	/** The current row. */
	public int currentRow = 0;

	/** The current col. */
	public int currentCol = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.pr.file.infra.payment.PaymentGenerator#generate(nts.uk.shr.infra.
	 * file.report.aspose.cells.AsposeCellsReportContext,
	 * nts.uk.file.pr.app.export.payment.data.PaymentReportData)
	 */
	@Override
	public void generate(AsposeCellsReportContext context, PaymentReportData data) {

		// Set worksheet name.
		workSheet = context.getWorkbook().getWorksheets().get(FIRST_SHEET);

		// Set data.
		cells = workSheet.getCells();

		List<PaymentReportDto> reportData = data.getReportData();

		PaymentReportDto dataFirst = reportData.get(FIRST_SHEET);

		setDataCategory(START_ROW_CATEGORY_ATTENDANCE, START_COL_CATEGORY_ATTENDANCE, 1,
			dataFirst.getAttendanceItems());

		setDataCategory(START_ROW_CATEGORY_PAYMENT, START_COL_CATEGORY_PAYMENT, 1,
			dataFirst.getPaymentItems());

		setDataCategory(START_ROW_CATEGORY_DEDUCTION, START_COL_CATEGORY_DEDUCTION, 2,
			dataFirst.getDeductionItems());

		setDataCategory(START_ROW_CATEGORY_ARTICLE, START_COL_CATEGORY_ARTICLE, 1,
			dataFirst.getArticleItems());
		
		setDataCategory(START_ROW_CATEGORY_OTHER, START_COL_CATEGORY_OTHER, 1,
			dataFirst.getOtherItems());

	}

	/**
	 * Sets the data category.
	 *
	 * @param startRow the start row
	 * @param startCol the start col
	 * @param merge the merge
	 * @param data the data
	 */
	public void setDataCategory(int startRow, int startCol, int merge,
		List<PaymentSalaryItemDto> data) {
		currentRow = 0;
		data.forEach(item -> {
			if (item.isView()) {
				cells.get(currentRow + startRow, startCol).setValue(item.getItemName());
				cells.get(currentRow + startRow, startCol + merge).setValue(item.getItemVal());
				currentRow++;
			}
		});
	}
}
