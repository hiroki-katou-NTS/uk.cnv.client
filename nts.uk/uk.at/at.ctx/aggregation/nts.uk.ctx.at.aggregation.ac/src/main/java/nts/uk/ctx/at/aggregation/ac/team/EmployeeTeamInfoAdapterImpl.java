package nts.uk.ctx.at.aggregation.ac.team;

import nts.uk.ctx.at.aggregation.dom.adapter.team.EmployeeTeamInfoAdapter;
import nts.uk.ctx.at.aggregation.dom.adapter.team.EmployeeTeamInfoImported;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.employeeinfo.EmpAffTeamInfoPub;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class EmployeeTeamInfoAdapterImpl implements EmployeeTeamInfoAdapter {
    @Inject
    private EmpAffTeamInfoPub employmentHistoryPub;

    @Override
    public List<EmployeeTeamInfoImported> get(List<String> employeeIds) {
        return employmentHistoryPub.get(employeeIds).stream()
                .map(x -> new EmployeeTeamInfoImported(
                        x.getEmployeeID(),
                        Optional.ofNullable(x.getScheduleTeamCd()),
                        Optional.ofNullable(x.getScheduleTeamName())
                )).collect(Collectors.toList());
    }
}
