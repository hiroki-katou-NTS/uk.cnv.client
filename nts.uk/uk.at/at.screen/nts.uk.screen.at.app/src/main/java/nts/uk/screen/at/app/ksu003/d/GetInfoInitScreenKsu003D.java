package nts.uk.screen.at.app.ksu003.d;

import lombok.val;
import nts.arc.primitive.PrimitiveValueBase;
import nts.uk.ctx.at.schedule.dom.displaysetting.functioncontrol.ScheFunctionControlRepository;
import nts.uk.ctx.at.shared.dom.workmanagementmultiple.WorkManagementMultipleRepository;
import nts.uk.screen.at.app.kmk.kmk017.worktimeworkplace.WorkManagementMultipleDto;
import nts.uk.screen.at.app.ksu003.d.dto.ScheFuncControlDto;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.stream.Collectors;

@Stateless
public class GetInfoInitScreenKsu003D {
    @Inject
    private ScheFunctionControlRepository scheFuncControlRepo;

    @Inject
    private WorkManagementMultipleRepository workMngtMultiRepo;

    public InitKsu003DInfoDto getData() {
        String cid = AppContexts.user().companyId();
        val workManagerMulti = this.workMngtMultiRepo.findByCode(cid).map(x -> new WorkManagementMultipleDto(x.getUseATR().value))
                .orElse(null);

        val scheFuncCtrl = this.scheFuncControlRepo.get(cid).map(x -> new ScheFuncControlDto(
                x.getChangeableWorks().stream().map(i -> i.value).collect(Collectors.toList()),
                x.isDisplayActual(),
                x.getDisplayWorkTypeControl().value,
                x.getDisplayableWorkTypeCodeList().stream().map(PrimitiveValueBase::v).collect(Collectors.toList())
        )).orElse(null);

        return new InitKsu003DInfoDto(workManagerMulti, scheFuncCtrl);
    }
}
