package nts.uk.screen.com.app.command.workflow.agent;

import java.util.Arrays;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.gul.mail.send.MailContents;
import nts.uk.ctx.sys.gateway.dom.loginold.adapter.MailDestinationAdapter;
import nts.uk.ctx.sys.gateway.dom.loginold.dto.MailDestiImport;
import nts.uk.ctx.sys.gateway.dom.loginold.dto.SentMailListImport;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.mail.MailSender;

@Stateless
@Transactional
public class SendMailToApproverCommandHandler extends CommandHandler<SendEmailCommand> {
    @Inject
    private MailSender mailSender;

    @Inject
    private MailDestinationAdapter mailDestinationAdapter;

    @Override
    protected void handle(CommandHandlerContext<SendEmailCommand> commandHandlerContext) {
        String companyId = AppContexts.user().companyId();
        SendEmailCommand command = commandHandlerContext.getCommand();
        //imported（申請承認）「社員メールアドレス」を取得する  - Rq225 (419)
        MailDestiImport lstApplicantMail = mailDestinationAdapter.getMailDestiOfEmployee(companyId, Arrays.asList(command.getApproverId()), 6);
        Optional<SentMailListImport> optMailList = lstApplicantMail.getSentMailLists().stream()
        		.filter(data -> data.getSid().equals(command.getApproverId())).findFirst();
        if(!optMailList.isPresent()
                || optMailList.get().getMailAddresses().stream().allMatch(StringUtils::isEmpty)){
            throw new BusinessException("Msg_791");
        }
        //代行承認者へ代行依頼メールを送信する
        optMailList.get().getMailAddresses().stream().filter(email -> !StringUtils.isEmpty(email))
                .findFirst().ifPresent(email -> {
            mailSender.sendFromAdmin(
                    email,
                    new MailContents(
                            TextResource.localize("CMM044_41"),
                            commandHandlerContext.getCommand().getEmailContent()
                    )
            );
        });

    }
}
