package nts.uk.ctx.exio.app.command.exo.authset;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.exio.dom.exo.authset.ExOutCtgAuthSet;
import nts.uk.ctx.exio.dom.exo.authset.ExOutCtgAuthSetRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.stream.Collectors;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RegisterOrUpdateExOutCtgAuthCommandHandler extends CommandHandler<RegisterExOutCtgAuthCommand> {
    @Inject
    private ExOutCtgAuthSetRepository exOutCtgAuthRepo;

    @Override
    protected void handle(CommandHandlerContext<RegisterExOutCtgAuthCommand> commandHandlerContext) {
        String companyId = AppContexts.user().companyId();
        val command = commandHandlerContext.getCommand();
        if (command == null) return;

        val functionNoList = command.getFunctionAuthSettings().stream().map(x -> String.valueOf(x.getFunctionNo())).collect(Collectors.toList());
        val oldData = exOutCtgAuthRepo.find(companyId, command.getRoleId(), functionNoList);

        val exOutCtgAuthSettings = command.getFunctionAuthSettings().stream().
                map(item -> new ExOutCtgAvailabilityPermissionImpl(companyId, command.getRoleId(), item.getFunctionNo(), item.isAvailable()))
                .collect(Collectors.toList());

        exOutCtgAuthSettings.forEach(item -> {
            val oldSettingOpt = oldData.stream().filter(x -> x.getFunctionNo() == item.getFunctionNo()).findAny();
            if (oldSettingOpt.isPresent())
                exOutCtgAuthRepo.update(new ExOutCtgAuthSet(item));
            else
                exOutCtgAuthRepo.add(new ExOutCtgAuthSet(item));
        });
    }
}
