package nts.uk.screen.com.app.find.cmm051;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceExportService;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceInforParam;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManager;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManagerRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Screen query: 職場情報一覧を取得する
 */
@Stateless
public class GetList0fWorkplaceInformationScreenQuery {
    @Inject
    private WorkplaceManagerRepository workplaceManagerRepository;
    @Inject
    private WorkplaceExportService workplaceExportService;

    public List<WorkplaceInforParam> getListWplInfo(String sid) {

        List<WorkplaceManager> workplaceManagerList = workplaceManagerRepository.getWkpManagerListBySid(sid);

        List<String> wplIds = workplaceManagerList.stream()
                .map(WorkplaceManager::getWorkplaceId).distinct().collect(Collectors.toList());
        if (wplIds.isEmpty())
            return new ArrayList<WorkplaceInforParam>();
        val baseDate = GeneralDate.today();
        val cid = AppContexts.user().companyId();
        return workplaceExportService.getWorkplaceInforFromWkpIds(cid,wplIds,baseDate);
    }
}
