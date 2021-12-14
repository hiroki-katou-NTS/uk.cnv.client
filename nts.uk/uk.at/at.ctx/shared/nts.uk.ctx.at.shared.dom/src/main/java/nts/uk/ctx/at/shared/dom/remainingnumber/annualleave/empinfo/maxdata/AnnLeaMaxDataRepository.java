package nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata;

import java.util.List;
import java.util.Optional;

public interface AnnLeaMaxDataRepository {
	
	Optional<AnnualLeaveMaxData> get(String employeeId);
	
	void add(AnnualLeaveMaxData maxData);
	
	void addAll(List<AnnualLeaveMaxData> domains);
	
	void update(AnnualLeaveMaxData maxData);
	
	void updateAll(List<AnnualLeaveMaxData> domains);
	
	void delete(String employeeId);
	
	/**
	 * getAll
	 * @param cid
	 * @param sids
	 * @return
	 */
	List<AnnualLeaveMaxData> getAll(List<String> sids);

}
