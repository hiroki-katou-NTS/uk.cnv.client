package nts.uk.ctx.at.record.dom.workrecord.erroralarm;

import java.util.List;
import java.util.Map;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;

public interface EmployeeDailyPerErrorRepository {
	
	void insert(EmployeeDailyPerError employeeDailyPerformanceError);
	
	void insert(List<EmployeeDailyPerError> employeeDailyPerformanceError);
	
	boolean checkExistErrorCode(String employeeID, GeneralDate processingDate, String errorCode);
	
	boolean checkExistErrorCodeByPeriod(String employeeID, DatePeriod datePeriod, String errorCode);
	
	List<EmployeeDailyPerError> find(String employeeID, GeneralDate processingDate);
	
	List<EmployeeDailyPerError> findList(String companyID,String employeeID);
	
	List<EmployeeDailyPerError> findByPeriodOrderByYmd(String employeeId, DatePeriod datePeriod);
	
	List<EmployeeDailyPerError> finds(List<String> employeeID, DatePeriod processingDate);
	
	List<EmployeeDailyPerError> finds(Map<String, List<GeneralDate>> param);
	
	List<EmployeeDailyPerError> findsWithLeftJoin(List<String> employeeID, DatePeriod processingDate);
	
	void removeParam(String sid, GeneralDate date);
	
	void removeParam(Map<String, List<GeneralDate>> param);
	
	void removeContinuosErrorIn(String sid, DatePeriod date, String code);
	
	boolean checkExistRecordErrorListDate(String companyID, String employeeID, List<GeneralDate> lstDate);
	
	boolean checkEmployeeHasErrorOnProcessingDate(String employeeID, GeneralDate processingDate);
	
	boolean checkExistErrorByDate(String companyID, String employeeID, GeneralDate date);
	
	void removeByCidSidDateAndCode(String companyID, String employeeID, GeneralDate date, String errorCode);
	
	/**
	 * Add by ThanhPV
	 */
	boolean checkErrorByPeriodDate(String companyID, String employeeID, GeneralDate strDate, GeneralDate endDate);
	
	List<EmployeeDailyPerError> getByEmpIDAndPeriod(List<String> employeeID, DatePeriod processingDate);
	
	void removeNotOTK(Map<String, List<GeneralDate>> param);
	
	void update(List<EmployeeDailyPerError> employeeDailyPerformanceError);
	
	List<EmployeeDailyPerError> findsByCodeLst(List<String> employeeIDLst, DatePeriod period, List<String> codeLst);
	
	List<EmployeeDailyPerError> findsByCodeLstForCcg005(List<String> employeeIDLst, DatePeriod period, List<String> codeLst);
	
	void deleteByErrorCode(String sid, GeneralDate date, String errorCode);
}
