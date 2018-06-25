package nts.uk.file.at.app.export.attendancerecord.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class AttendanceRecordReportEmployeeData.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRecordReportEmployeeData {

	/** The invidual. */
	private String invidual;

	/** The workplace. */
	private String workplace;

	/** The employment. */
	private String employment;

	/** The title. */
	private String title;

	/** The work type. */
	private String workType;

	/** The year month. */
	private String yearMonth;
	
	/** The report year month. */
	private String reportYearMonth;

	/** The monthly data. */
	private List<AttendanceRecordReportColumnData> employeeMonthlyData;

	/** The weekly datas. */
	private List<AttendanceRecordReportWeeklyData> weeklyDatas;

}
