package nts.uk.ctx.sys.assist.dom.datarestoration;

import java.util.Optional;

import nts.arc.time.GeneralDate;

/**
 * データ復旧動作管理
 */
public interface DataRecoveryMngRepository {

	Optional<DataRecoveryMng> getDataRecoveryMngById(String dataRecoveryProcessId);

	void add(DataRecoveryMng domain);

	void update(DataRecoveryMng domain);

	void remove(String dataRecoveryProcessId);
	
	void updateByOperatingCondition(String dataRecoveryProcessId, int operatingCondition);
	
	void updateTotalNumOfProcesses(String dataRecoveryProcessId, int totalNumOfProcesses);
	
	void updateProcessTargetEmpCode(String dataRecoveryProcessId, String processTargetEmpCode);
	
	Optional<DataRecoveryMng> getByUploadId(String dataRecoveryProcessId);
	
	void updateRecoveryDate(String dataRecoveryProcessId, GeneralDate date);
}
