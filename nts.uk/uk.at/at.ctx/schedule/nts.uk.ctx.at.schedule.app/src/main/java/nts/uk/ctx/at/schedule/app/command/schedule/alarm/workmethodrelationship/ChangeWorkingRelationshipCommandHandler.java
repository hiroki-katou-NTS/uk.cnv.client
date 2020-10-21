package nts.uk.ctx.at.schedule.app.command.schedule.alarm.workmethodrelationship;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.schedule.dom.schedule.alarm.workmethodrelationship.*;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 組織の勤務方法の関係性を変更する
 */
@Stateless
public class ChangeWorkingRelationshipCommandHandler extends CommandHandler<ChangeWorkingRelationshipCommand> {

    @Inject
    private WorkMethodRelationshipOrgRepo workMethodRelationshipOrgRepo;

    @Override
    protected void handle(CommandHandlerContext<ChangeWorkingRelationshipCommand> context) {

        ChangeWorkingRelationshipCommand command = context.getCommand();

        TargetOrgIdenInfor targetOrgIdenInfor = new TargetOrgIdenInfor(TargetOrganizationUnit.valueOf(command.getUnit()),
                Optional.of(command.getWorkplaceId()), Optional.of(command.getWorkplaceGroupId()));

        WorkMethodHoliday workMethodHoliday = new WorkMethodHoliday();
        WorkMethodContinuousWork methodContinuousWork = new WorkMethodContinuousWork();
        WorkMethodAttendance workMethodAttendance1 = new WorkMethodAttendance(new WorkTimeCode(command.getWorkTimeCode()));

        //1: get(ログイン会社ID, 対象組織, 対象勤務方法) : Optional<組織の勤務方法の関係性>
        Optional<WorkMethodRelationshipOrganization> organization =
                workMethodRelationshipOrgRepo.getWithWorkMethod(AppContexts.user().companyId(), targetOrgIdenInfor,
                        command.getWorkMethodClassfication() == 0 ? workMethodAttendance1 : command.getWorkMethodClassfication() == 2 ? workMethodHoliday : methodContinuousWork);

        if (organization.isPresent()){
            List<WorkMethod> workMethods = command.getWorkMethods().stream().map(x -> new WorkMethodAttendance(new WorkTimeCode(x))).collect(Collectors.toList());
            WorkMethodRelationship relationship =
                    WorkMethodRelationship.create(command.getWorkMethodClassfication() == 0 ? workMethodAttendance1 : command.getWorkMethodClassfication() == 2 ? workMethodHoliday : methodContinuousWork,
                            workMethods,
                            EnumAdaptor.valueOf(command.getSpecifiedMethod(),RelationshipSpecifiedMethod.class));
            WorkMethodRelationshipOrganization newOrganization1 = new WorkMethodRelationshipOrganization(targetOrgIdenInfor,relationship);

            workMethodRelationshipOrgRepo.update(AppContexts.user().companyId(),newOrganization1);
        }
    }
}
