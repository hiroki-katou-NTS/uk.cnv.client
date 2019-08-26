package nts.uk.ctx.pr.shared.app.command.socialinsurance.employeesociainsur.emphealinsurbeneinfo;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.empbenepenpeninfor.welfPenInsLossIfRepository;
import nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.emphealinsurbeneinfo.EmpBasicPenNumInforRepository;
import nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.emphealinsurbeneinfo.HealthInsLossInfoRepository;
import nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.emphealinsurbeneinfo.MultiEmpWorkInfoRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;


@Stateless
public class LossInfoCommandHandler extends CommandHandler<LossInfoCommand> {
    @Inject
    private HealthInsLossInfoRepository handlerHealth;

    @Inject
    private welfPenInsLossIfRepository handlerWelPen;

    @Inject
    private MultiEmpWorkInfoRepository handlerMultiWork;

    @Inject
    private EmpBasicPenNumInforRepository handlerBasicPen;

    @Override
    protected void handle(CommandHandlerContext<LossInfoCommand> context) {

        if (context.getCommand().getScreenMode() == 0) {
            handlerHealth.insertHealthLossInfo(context.getCommand().getHealthInsLossInfo().fromCommandToDomain());
            handlerWelPen.insertWelfPenInsLossIf(context.getCommand().getWelfPenInsLossIf().fromCommandToDomain());
        } else {
            handlerHealth.updateHealthLossInfo(context.getCommand().getHealthInsLossInfo().fromCommandToDomain());
            handlerWelPen.updateWelfPenInsLossIf(context.getCommand().getWelfPenInsLossIf().fromCommandToDomain());
        }

        //check exist
        if (handlerMultiWork.getMultiEmpWorkInfoById(context.getCommand().getMultiEmpWorkInfo().getEmpId()).isPresent()) {
            handlerMultiWork.update(context.getCommand().getMultiEmpWorkInfo().fromCommandToDomain());
        } else {
            handlerMultiWork.add(context.getCommand().getMultiEmpWorkInfo().fromCommandToDomain());
        }

        //check exist empId
        if (handlerBasicPen.getEmpBasicPenNumInforById(context.getCommand().getEmpBasicPenNumInfor().getEmployeeId()).isPresent()) {
            handlerBasicPen.update(context.getCommand().getEmpBasicPenNumInfor().fromCommandToDomain());

        } else {
            handlerBasicPen.add(context.getCommand().getEmpBasicPenNumInfor().fromCommandToDomain());
        }
    }
}
