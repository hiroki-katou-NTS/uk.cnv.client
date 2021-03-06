package nts.uk.ctx.at.function.dom.attendancerecord.item;

import java.util.List;
import java.util.Optional;

// TODO: Auto-generated Javadoc
/**
 * The Interface CalculateAttendanceRecordRepositoty.
 */
public interface CalculateAttendanceRecordRepositoty {

	/**
	 * Gets the calculate attendance record.
	 *
	 * @param companyId the company id
	 * @param exportSettingCode the export setting code
	 * @param columnIndex            the column index
	 * @param position            the position
	 * @param exportArt the export art
	 * @return the calculate attendance record
	 */
	Optional<CalculateAttendanceRecord> getCalculateAttendanceRecord(String layoutId, long columnIndex, long exportArt,  long position);

	/**
	 * Adds the calculate attendance record.
	 *
	 * @param companyId the company id
	 * @param exportSettingCode            the code
	 * @param columnIndex            the column index
	 * @param position            the position
	 * @param exportArt the export art
	 * @param calculateAttendanceRecord            the calculate attendance record
	 */
	void addCalculateAttendanceRecord(String layoutId, int columnIndex,
			int position, long exportArt, CalculateAttendanceRecord calculateAttendanceRecord);

	/**
	 * Update calculate attendance record.
	 *
	 * @param companyId the company id
	 * @param exportSettingCode the export setting code
	 * @param columnIndex            the column index
	 * @param position            the position
	 * @param exportArt the export art
	 * @param useAtr the use atr
	 * @param calculateAttendanceRecord            the calculate attendance record
	 */
	void updateCalculateAttendanceRecord(String layoutId, int columnIndex,
			int position, long exportArt, boolean useAtr, CalculateAttendanceRecord calculateAttendanceRecord);

	/**
	 * Delete calculate attendance record.
	 *
	 * @param companyId the company id
	 * @param exportSettingCode the export setting code
	 * @param columnIndex            the column index
	 * @param position            the position
	 * @param exportArt the export art
	 * @param calculateAttendanceRecord            the calculate attendance record
	 */
	void deleteCalculateAttendanceRecord(String layoutId, int columnIndex,
			int position, long exportArt, CalculateAttendanceRecord calculateAttendanceRecord);
	
	/**
	 * Gets the id calculate attendance record daily by position.
	 *
	 * @param companyId the company id
	 * @param exportCode the export code
	 * @param position the position
	 * @return the id calculate attendance record daily by position
	 */
	List<CalculateAttendanceRecord> getIdCalculateAttendanceRecordDailyByPosition(String layoutId, long position, int fontSize);
	
	/**
	 * Gets the id calculate attendance record monthly by position.
	 *
	 * @param companyId the company id
	 * @param exportCode the export code
	 * @param position the position
	 * @return the id calculate attendance record monthly by position
	 */
	List<CalculateAttendanceRecord> getIdCalculateAttendanceRecordMonthlyByPosition(String layoutId, long position, int fontSize);
}
