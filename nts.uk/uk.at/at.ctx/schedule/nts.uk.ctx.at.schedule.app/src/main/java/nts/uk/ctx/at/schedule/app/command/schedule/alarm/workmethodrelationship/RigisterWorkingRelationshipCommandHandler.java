package nts.uk.ctx.at.schedule.app.command.schedule.alarm.workmethodrelationship;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
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
 * 組織の勤務方法の関係性を新規する
 */
@Stateless
public class RigisterWorkingRelationshipCommandHandler extends CommandHandler<RigisterWorkingRelationshipCommand> {

    @Inject
    private WorkMethodRelationshipOrgRepo workMethodRelationshipOrgRepo;

    @Override
    protected void handle(CommandHandlerContext<RigisterWorkingRelationshipCommand> context) {

        RigisterWorkingRelationshipCommand command = context.getCommand();

        TargetOrgIdenInfor targetOrgIdenInfor = new TargetOrgIdenInfor(TargetOrganizationUnit.valueOf(command.getUnit()),
                Optional.of(command.getWorkplaceId()), Optional.of(command.getWorkplaceGroupId()));

        WorkMethodHoliday workMethodHoliday = new WorkMethodHoliday();
        WorkMethodContinuousWork methodContinuousWork = new WorkMethodContinuousWork();
        WorkMethodAttendance workMethodAttendance1 = new WorkMethodAttendance(new WorkTimeCode(command.getWorkTimeCode()));

        //1: 存在するか = exists(ログイン会社ID, 対象組織, 対象勤務方法) : boolean
        boolean checkExists = workMethodRelationshipOrgRepo.exists(AppContexts.user().companyId(), targetOrgIdenInfor,
                        command.getWorkMethodClassfication() == 0 ? workMethodAttendance1 : command.getWorkMethodClassfication() == 2 ? workMethodHoliday : methodContinuousWork);

        //2: 存在するか == true
        if (checkExists){
            throw new BusinessException("Msg_1785");
        }

        //3: insert
        List<WorkMethod> workMethods = command.getWorkMethods().stream().map(x -> new WorkMethodAttendance(new WorkTimeCode(x))).collect(Collectors.toList());
        WorkMethodRelationship relationship =
                WorkMethodRelationship.create(command.getWorkMethodClassfication() == 0 ? workMethodAttendance1 : command.getWorkMethodClassfication() == 2 ? workMethodHoliday : methodContinuousWork,
                        workMethods,
                        EnumAdaptor.valueOf(command.getSpecifiedMethod(),RelationshipSpecifiedMethod.class));
        WorkMethodRelationshipOrganization newOrganization1 = new WorkMethodRelationshipOrganization(targetOrgIdenInfor,relationship);

        workMethodRelationshipOrgRepo.insert(AppContexts.user().companyId(),newOrganization1);
    }
}
