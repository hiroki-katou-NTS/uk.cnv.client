package nts.uk.ctx.workflow.dom.resultrecord;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

/**
 * 
 * @author Doan Duy Hung
 *
 */
public interface AppRootInstanceRepository {
	
	public Optional<AppRootInstance> findByID(String rootID);
	
	public void insert(AppRootInstance appRootInstance);
	
	public void update(AppRootInstance appRootInstance);
	
	public void delete(AppRootInstance appRootInstance);
	
	public void deleteByIDLst(List<String> rootIDLst);
	
	public Optional<AppRootInstance> findByEmpDate(String companyID, String employeeID, GeneralDate recordDate, RecordRootType rootType);
	
	public Optional<AppRootInstance> findByEmpDateNewest(String companyID, String employeeID, RecordRootType rootType);
	
	public List<AppRootInstance> findByEmpLstPeriod(List<String> employeeIDLst, DatePeriod period, RecordRootType rootType);
	
	public List<AppRootInstance> findByEmpLstPeriod(String compID, List<String> employeeIDLst, DatePeriod period, RecordRootType rootType);
	
	public List<AppRootInstance> findByApproverPeriod(String approverID, DatePeriod period, RecordRootType rootType);
	
	public List<AppRootInstance> findByApproverEmployeePeriod(String companyId, String approverID, List<String> employeeIDs, DatePeriod period, RecordRootType rootType);
	
	public List<AppRootInstance> findByEmpFromDate(String companyID, String employeeID, GeneralDate recordDate, RecordRootType rootType);
	
	public List<String> findRootByEmpFromDate(String companyID, String employeeID, GeneralDate recordDate, RecordRootType rootType);
	
	public Optional<AppRootInstance> findByEmpDateNewestBelow(String companyID, String employeeID, GeneralDate recordDate, RecordRootType rootType);
	
	public Pair<String, DatePeriod> findIDByEmpDateNewestBelow(String companyID, String employeeID, GeneralDate recordDate, RecordRootType rootType);
	
	public void updateEndByID(String rootID, GeneralDate endDate);
	
	public Optional<AppRootInstance> findByContainDate(String companyID, String employeeID, GeneralDate recordDate, RecordRootType rootType);
	
	public String findIDByContainDate(String companyID, String employeeID, GeneralDate recordDate, RecordRootType rootType);
	
	public List<AppRootInstance> findByApproverDateCID(String companyID, String approverID, GeneralDate date, RecordRootType rootType);
	
	public List<String> findEmpLstByRq610(String approverID, DatePeriod period, RecordRootType rootType);
	
	public List<AppPhaseInsTmp> findPhaseTmpByID(String rootID);
	
	public List<AppFrameInsTmp> findFrameTmpByID(String rootID);
	
	public List<AppApproveInsTmp> findApproverTmpByID(String rootID);
	
}
