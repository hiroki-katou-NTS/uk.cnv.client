package nts.uk.ctx.at.request.dom.application.overtime;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AppOverTimeRepository {
	
	public Optional<AppOverTime> find(String companyId, String appId);

	Optional<AppOverTime> findLatestMultipleOvertimeApp(String employeeId, GeneralDate appDate, PrePostAtr prePostAtr);
	
	public void add(AppOverTime appOverTime);
	
	public void update(AppOverTime appOverTime);
	
	public void remove(String companyID, String appID);
	
	public Map<String, Integer> getByAppIdAndOTAttr(String companyId, List<String> appIds);
	
	public Map<String, AppOverTime> getHashMapByID(String companyId, List<String> appIds);
}
