package nts.uk.ctx.pr.shared.app.command.payrollgeneralpurposeparameters;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.pr.shared.dom.salgenpurposeparam.SalGenHistoryService;
import nts.uk.ctx.pr.shared.dom.salgenpurposeparam.SalGenParaValue;
import nts.uk.ctx.pr.shared.dom.salgenpurposeparam.SalGenParaValueRepository;
import nts.uk.ctx.pr.shared.dom.salgenpurposeparam.SalGenParaYMHistRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

@Stateless
@Transactional
public class AddSalGenParaValueCommandHandler extends CommandHandler<SalGenParaYeahMonthValueCommand>
{
    
    @Inject
    private SalGenParaYMHistRepository repository;

    private final static int MODE_SCREEN_ADD = 1;

    @Inject
    private SalGenHistoryService salGenHistoryService;

    @Override
    protected void handle(CommandHandlerContext<SalGenParaYeahMonthValueCommand> context) {
        SalGenParaYeahMonthValueCommand command = context.getCommand();
        if (command.getMSalGenParaValueCommand().getModeScreen() == MODE_SCREEN_ADD) {
            String newHistID = IdentifierUtil.randomUniqueId();
            salGenHistoryService.addSalGenParam(newHistID, command.getParaNo(),
                    command.getStartTime(),
                    command.getEndTime(),
                    new SalGenParaValue(newHistID,
                            command.getMSalGenParaValueCommand().getSelection(),
                            command.getMSalGenParaValueCommand().getAvailableAtr(),
                            command.getMSalGenParaValueCommand().getNumValue(),
                            command.getMSalGenParaValueCommand().getCharValue(),
                            command.getMSalGenParaValueCommand().getTimeValue(),
                            command.getMSalGenParaValueCommand().getTargetAtr()),command.getModeHistory());


        }
        else{
            repository.updateSalGenParaValue(command.getParaNo(),new SalGenParaValue(command.getMSalGenParaValueCommand().getHistoryId(),
                    command.getMSalGenParaValueCommand().getSelection(),
                    command.getMSalGenParaValueCommand().getAvailableAtr(),
                    command.getMSalGenParaValueCommand().getNumValue(),
                    command.getMSalGenParaValueCommand().getCharValue(),
                    command.getMSalGenParaValueCommand().getTimeValue(),
                    command.getMSalGenParaValueCommand().getTargetAtr()));
        }

    }

}
