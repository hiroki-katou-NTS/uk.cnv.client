package nts.uk.ctx.at.function.ac.alarm;

import nts.uk.ctx.at.function.dom.adapter.alarm.AdministratorReceiveAlarmMailAdapter;
import nts.uk.ctx.sys.auth.pub.employee.EmployeePublisher;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Stateless
public class AdministratorReceiveAlarmMailAdapterImpl implements AdministratorReceiveAlarmMailAdapter {
    @Inject
    private EmployeePublisher employeePublisher;

    @Override
    public Map<String, List<String>> getAdminReceiveAlarmMailByWorkplaceIds(List<String> workplaceIds) {
        return employeePublisher.getManagersByWorkplaceIds(workplaceIds);
    }
}
