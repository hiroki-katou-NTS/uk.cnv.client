package nts.uk.ctx.at.record.ac.workrecord;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.WorkplaceInformationAdapter;
import nts.uk.ctx.at.shared.dom.adapter.workplace.config.info.WorkplaceInfor;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplaceInforExport;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class WorkplaceInformationAdapterIml implements WorkplaceInformationAdapter {
    @Inject
    private WorkplacePub workplacePub;

    @Override
    public List<WorkplaceInfor> getWorkplaceInfor(String companyId, List<String> listWorkplaceId, GeneralDate baseDate) {
        List<WorkplaceInforExport> listWorkPlaceInfoExport = workplacePub.getWorkplaceInforByWkpIds(companyId, listWorkplaceId, baseDate);

        return listWorkPlaceInfoExport.stream().map(e -> new WorkplaceInfor(e.getWorkplaceId(),
                e.getHierarchyCode(), e.getWorkplaceCode(), e.getWorkplaceName(), e.getWorkplaceDisplayName(),
                e.getWorkplaceGenericName(), e.getWorkplaceExternalCode()
        )).sorted(Comparator.comparing(WorkplaceInfor::getWorkplaceCode)).collect(Collectors.toList());
    }
}
