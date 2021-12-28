package nts.uk.ctx.at.record.ac.workrecord;

import nts.uk.ctx.at.record.dom.adapter.function.alarmworkplace.EmployeeInfoImport;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.EmployeeDisplayNameAdapter;
import nts.uk.ctx.bs.employee.pub.employee.SyEmployeePub;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class EmployeeDisplayNameAdapterImpl implements EmployeeDisplayNameAdapter {
    @Inject
    private SyEmployeePub syEmployeePub;

    @Override
    public List<EmployeeInfoImport> getByListSID(List<String> sIds) {
        return syEmployeePub.getByListSid(sIds).stream().map(x -> new EmployeeInfoImport(x.getSid(), x.getScd(), x.getBussinessName()))
                .sorted(Comparator.comparing(EmployeeInfoImport::getSid)).collect(Collectors.toList());
    }
}
