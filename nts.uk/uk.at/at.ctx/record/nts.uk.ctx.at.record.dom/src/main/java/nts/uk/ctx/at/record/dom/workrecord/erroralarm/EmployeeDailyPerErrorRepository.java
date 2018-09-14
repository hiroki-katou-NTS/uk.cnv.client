package nts.uk.ctx.at.record.dom.workrecord.erroralarm;

import java.util.List;
import java.util.Map;

import nts.arc.time.GeneralDate;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

public interface EmployeeDailyPerErrorRepository {
	
	void insert(EmployeeDailyPerError employeeDailyPerformanceError);
	
	void insert(List<EmployeeDailyPerError> employeeDailyPerformanceError);
	
	boolean checkExistErrorCode(String employeeID, GeneralDate processingDate, String errorCode);
	
	boolean checkExistErrorCodeByPeriod(String employeeID, DatePeriod datePeriod, String errorCode);
	
	List<EmployeeDailyPerError> find(String employeeID, GeneralDate processingDate);
	
	List<EmployeeDailyPerError> findList(String companyID,String employeeID);
	
	List<EmployeeDailyPerError> findByPeriodOrderByYmd(String employeeId, DatePeriod datePeriod);
	
	List<EmployeeDailyPerError> finds(List<String> employeeID, DatePeriod processingDate);
	
	void removeParam(String sid, GeneralDate date);
	
	void removeParam(Map<String, List<GeneralDate>> param);
	
	boolean checkExistRecordErrorListDate(String companyID, String employeeID, List<GeneralDate> lstDate);
	
	boolean checkEmployeeHasErrorOnProcessingDate(String employeeID, GeneralDate processingDate);
	
	boolean checkExistErrorByDate(String companyID, String employeeID, GeneralDate date);
	
	void removeByCidSidDateAndCode(String companyID, String employeeID, GeneralDate date, String errorCode);
	
	/**
	 * Add by ThanhPV
	 */
	boolean checkErrorByPeriodDate(String companyID, String employeeID, GeneralDate strDate, GeneralDate endDate);
	
}
