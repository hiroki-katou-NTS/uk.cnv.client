package nts.uk.file.at.infra.schedule;

import com.aspose.cells.Worksheet;

import lombok.Data;
import nts.uk.file.at.app.export.dailyschedule.FormOutputType;
import nts.uk.file.at.app.export.monthlyschedule.MonthlyWorkScheduleCondition;

/**
 * The Class RowPageTracker.
 *
 * @author HoangNDH
 * Class for tracking remaining row and page count and perform page break when there is insufficient row
 */
@Data
public class RowPageTracker {
	
	/** The remaining row. */
	int remainingRow;
	
	/** The page count. */
	int pageCount;
	
	/** The max row allowed. */
	int maxRowAllowed;
	
	public static final int MAX_ROW_PER_PAGE_EMPLOYEE_1 = 33;
	public static final int MAX_ROW_PER_PAGE_EMPLOYEE_2 = 31;
	public static final int MAX_ROW_PER_PAGE_EMPLOYEE_3 = 27;
	
	public static final int MAX_ROW_PER_PAGE_DATE_1 = 35;
	public static final int MAX_ROW_PER_PAGE_DATE_2 = 31;
	public static final int MAX_ROW_PER_PAGE_DATE_3 = 28;
	
	private static final int MAX_ROW_PER_PAGE_MONTHLY_EMPLOYEE_1 = 32;
	private static final int MAX_ROW_PER_PAGE_MONTHLY_EMPLOYEE_2 = 32;
	private static final int MAX_ROW_PER_PAGE_MONTHLY_EMPLOYEE_3 = 27;
	
	private static final int MAX_ROW_PER_PAGE_MONTHLY_DATE_1 = 28;
	private static final int MAX_ROW_PER_PAGE_MONTHLY_DATE_2 = 27;
	private static final int MAX_ROW_PER_PAGE_MONTHLY_DATE_3 = 23;
	
	/**
	 * Check remaining row sufficient.
	 *
	 * @param rowUse the row use
	 * @return the int
	 */
	public int checkRemainingRowSufficient(int rowUse) {
		return remainingRow - rowUse;
	}
	
	/**
	 * Adds the page count.
	 */
	public void addPageCount () {
		pageCount++;
	}
	
	/**
	 * Inits the max row allowed.
	 *
	 * @param dataRowCount the data row count
	 */
	public void initMaxRowAllowed(int dataRowCount, FormOutputType outputType) {
		switch(dataRowCount) {
		case 1:
			if (outputType == FormOutputType.BY_EMPLOYEE)
				maxRowAllowed = MAX_ROW_PER_PAGE_EMPLOYEE_1;
			else
				maxRowAllowed = MAX_ROW_PER_PAGE_DATE_1;
			break;
		case 2:
			if (outputType == FormOutputType.BY_EMPLOYEE)
				maxRowAllowed = MAX_ROW_PER_PAGE_EMPLOYEE_2;
			else
				maxRowAllowed = MAX_ROW_PER_PAGE_DATE_2;
			break;
		case 3:
			if (outputType == FormOutputType.BY_EMPLOYEE)
				maxRowAllowed = MAX_ROW_PER_PAGE_EMPLOYEE_3;
			else
				maxRowAllowed = MAX_ROW_PER_PAGE_DATE_3;
			break;
		}
		remainingRow = maxRowAllowed;
	}
	
	public void initMaxRowAllowed(int dataRowCount, FormOutputType outputType, int maxRow) {
		switch(dataRowCount) {
		case 1:
			if (outputType == FormOutputType.BY_EMPLOYEE)
				maxRowAllowed = maxRow;
			else
				maxRowAllowed = MAX_ROW_PER_PAGE_DATE_1;
			break;
		case 2:
			if (outputType == FormOutputType.BY_EMPLOYEE)
				maxRowAllowed = MAX_ROW_PER_PAGE_EMPLOYEE_2;
			else
				maxRowAllowed = MAX_ROW_PER_PAGE_DATE_2;
			break;
		case 3:
			if (outputType == FormOutputType.BY_EMPLOYEE)
				maxRowAllowed = MAX_ROW_PER_PAGE_EMPLOYEE_3;
			else
				maxRowAllowed = MAX_ROW_PER_PAGE_DATE_3;
			break;
		}
		remainingRow = maxRowAllowed;
	}
	
	/**
	 * Inits the max row allowed monthly.
	 *
	 * @param dataRowCount the data row count
	 * @param outputType the output type
	 */
	public void initMaxRowAllowedMonthly(int dataRowCount, Integer outputType) {
		switch(dataRowCount) {
		case 1:
			if (outputType == MonthlyWorkScheduleCondition.EXPORT_BY_EMPLOYEE)
				maxRowAllowed = MAX_ROW_PER_PAGE_MONTHLY_EMPLOYEE_1;
			else
				maxRowAllowed = MAX_ROW_PER_PAGE_MONTHLY_DATE_1;
			break;
		case 2:
			if (outputType == MonthlyWorkScheduleCondition.EXPORT_BY_EMPLOYEE)
				maxRowAllowed = MAX_ROW_PER_PAGE_MONTHLY_EMPLOYEE_2;
			else
				maxRowAllowed = MAX_ROW_PER_PAGE_MONTHLY_DATE_2;
			break;
		case 3:
			if (outputType == MonthlyWorkScheduleCondition.EXPORT_BY_EMPLOYEE)
				maxRowAllowed = MAX_ROW_PER_PAGE_MONTHLY_EMPLOYEE_3;
			else
				maxRowAllowed = MAX_ROW_PER_PAGE_MONTHLY_DATE_3;
			break;
		}
		remainingRow = maxRowAllowed;
	}
	
	/**
	 * Use remaining row.
	 *
	 * @param dataRowCount the data row count
	 */
	public void useRemainingRow(int dataRowCount) {
		remainingRow -= dataRowCount;
	}
	
	/**
	 * Use one row and check reset remaining row.
	 */
	public void useOneRowAndCheckResetRemainingRow(Worksheet worksheet, int currentRow) {
		remainingRow--;
		if (remainingRow == 0) {
			worksheet.getHorizontalPageBreaks().add(currentRow);
			resetRemainingRow();
		}
	}
	
	/**
	 * Reset remaining row.
	 */
	public void resetRemainingRow() {
		remainingRow = maxRowAllowed;;
	}
	
	public int getUseRowCanSetData(int countItem, int dataRowCount) {
		if (this.remainingRow < countItem) {
			int countDataCanPrint = countItem - remainingRow;
			// check available row by dataRowCount
			return countDataCanPrint % dataRowCount == 0 ? countDataCanPrint : countDataCanPrint - (countDataCanPrint % dataRowCount);
		}
		return countItem;
	}
	
	public boolean isNewPage() {
		return this.remainingRow == this.maxRowAllowed;
	}
}
