package nts.uk.ctx.at.aggregation.ac.rank;

import nts.uk.ctx.at.aggregation.dom.adapter.rank.EmployeeRankInfoAdapter;
import nts.uk.ctx.at.aggregation.dom.adapter.rank.EmployeeRankInfoImported;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.employeeinfo.EmpRankInfoPub;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class EmployeeRankInfoAdapterImpl implements EmployeeRankInfoAdapter {
    @Inject
    private EmpRankInfoPub empRankInfoPub;

    @Override
    public List<EmployeeRankInfoImported> get(List<String> employeeIds) {
        return empRankInfoPub.get(employeeIds).stream()
                .map(x -> new EmployeeRankInfoImported(
                        x.getEmpId(),
                        Optional.ofNullable(x.getRankCode()),
                        Optional.ofNullable(x.getRankSymbol())
                )).collect(Collectors.toList());
    }
}
