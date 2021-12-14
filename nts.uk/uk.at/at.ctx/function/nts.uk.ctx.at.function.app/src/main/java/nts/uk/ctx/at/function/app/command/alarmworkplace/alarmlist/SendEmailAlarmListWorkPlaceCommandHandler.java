package nts.uk.ctx.at.function.app.command.alarmworkplace.alarmlist;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.mailserver.MailServerAdapter;
import nts.uk.ctx.at.function.dom.adapter.wkpmanager.WkpManagerAdapter;
import nts.uk.ctx.at.function.dom.adapter.wkpmanager.WkpManagerImport;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.*;
import nts.uk.ctx.at.function.dom.alarm.sendemail.MailSettingsParamDto;
import nts.uk.ctx.at.function.dom.alarm.sendemail.SendEmailService;
import nts.uk.ctx.at.function.dom.alarmworkplace.sendemail.WorkplaceSendEmailService;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * メール送信をする
 */
@Stateless
public class SendEmailAlarmListWorkPlaceCommandHandler extends CommandHandlerWithResult<SendEmailAlarmListWorkPlaceCommand, String> {

    @Inject
    private MailSettingNormalRepository mailSettingNormalRepository;

    @Inject
    private MailServerAdapter mailServerAdapter;

    @Inject
    private WkpManagerAdapter wkpManagerAdapter;

    @Inject
    private SendEmailService sendEmailService;

    @Inject
    private WorkplaceSendEmailService workplaceSendEmailService;

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    protected String handle(CommandHandlerContext<SendEmailAlarmListWorkPlaceCommand> context) {
        SendEmailAlarmListWorkPlaceCommand command = context.getCommand();
        GeneralDate executeDate = GeneralDate.today();
        String companyId = AppContexts.user().companyId();

        //エラーメッセージ(#Msg_719)を表示する
        if (command.getWorkplaceIds().size() == 0) {
            throw new BusinessException("Msg_719");
        }

        //ドメインモデル「アラームリスト通常用メール設定」を取得する
        Optional<MailSettingNormal> mailSettingNormal = mailSettingNormalRepository.findByCompanyId(companyId);
        if (isNotHaveMailSetting(mailSettingNormal)) {
            throw new BusinessException("Msg_1169");
        }

        //ドメインモデル「メールサーバ」を取得する
        boolean useAuthentication = mailServerAdapter.findBy(companyId);
        //メール設定(本人宛)：アラームリスト通常用メール設定.本人宛メール設定
        Optional<MailSettings> mailSetting = mailSettingNormal.get().getMailSettings();
        //メール設定(管理者宛)：アラームリスト通常用メール設定.管理者宛メール設定
        Optional<MailSettings> mailSettingAdmins = mailSettingNormal.get().getMailSettingAdmins();
        MailSettingsParamDto mailSettingsParamDto = buildMailSend(mailSettingNormal.get());// メール送信設定

        List<String> lstEmployeeId = new ArrayList<>();

        return workplaceSendEmailService.alarmWorkplacesendEmail(
                command.getWorkplaceIds(),
                command.listValueExtractAlarmDto,
                mailSettingNormal.get(),
                command.getCurrentAlarmCode(),
                useAuthentication
                );
    }

    private boolean isNotHaveMailSetting(Optional<MailSettingNormal> mailSetting) {
        return !mailSetting.isPresent() || !(mailSetting.get().getMailSettings().isPresent()
                && mailSetting.get().getMailSettingAdmins().isPresent());
    }

    private MailSettingsParamDto buildMailSend(MailSettingNormal mailSetting) {

        Optional<MailSettings> mailSetings = mailSetting.getMailSettings(),
                mailSetingAdmins = mailSetting.getMailSettingAdmins();
        String subject = "", text = "", subjectAdmin = "", textAdmin = "";
        if (mailSetings.isPresent()) {
            subject = mailSetings.get().getSubject().orElseGet(() -> new Subject("")).v();
            text = mailSetings.get().getText().orElseGet(() -> new Content("")).v();
        }
        if (mailSetingAdmins != null) {
            subjectAdmin = mailSetingAdmins.get().getSubject().orElseGet(() -> new Subject("")).v();
            textAdmin = mailSetingAdmins.get().getText().orElseGet(() -> new Content("")).v();
        }
        // setting subject , body mail
        return new MailSettingsParamDto(subject, text, subjectAdmin, textAdmin);
    }

}