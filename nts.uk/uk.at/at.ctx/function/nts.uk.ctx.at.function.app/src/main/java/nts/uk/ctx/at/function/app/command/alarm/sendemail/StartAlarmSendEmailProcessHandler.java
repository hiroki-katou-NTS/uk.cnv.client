package nts.uk.ctx.at.function.app.command.alarm.sendemail;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.app.find.alarm.alarmlist.ErAlExtractResultFinder;
import nts.uk.ctx.at.function.dom.adapter.mailserver.MailServerAdapter;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.Content;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettingNormal;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettingNormalRepository;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettings;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.Subject;
import nts.uk.ctx.at.function.dom.alarm.sendemail.MailSettingsParamDto;
import nts.uk.ctx.at.function.dom.alarm.sendemail.SendEmailService;
import nts.uk.ctx.at.function.dom.alarm.sendemail.ValueExtractAlarmDto;
import nts.uk.shr.com.context.AppContexts;


@Stateless
public class StartAlarmSendEmailProcessHandler extends CommandHandlerWithResult<ParamAlarmSendEmailCommand, String> {

	@Inject
	private SendEmailService sendEmailService;
	
	@Inject
	private MailSettingNormalRepository mailSettingNormalRepository;
	
	@Inject
	private MailServerAdapter mailServerAdapter;

	@Inject
	private ErAlExtractResultFinder extractResultFinder;

	@Override
	protected String handle(CommandHandlerContext<ParamAlarmSendEmailCommand> context) {

		String companyID = AppContexts.user().companyId(); // ログイン社員の会社ID
		Optional<MailSettingNormal> mailSettingNormal =mailSettingNormalRepository.findByCompanyId(companyID);
		if (isNotHaveMailSetting(mailSettingNormal)) {
			throw new BusinessException("Msg_1169");
		}
		ParamAlarmSendEmailCommand command = context.getCommand();
		GeneralDate executeDate = GeneralDate.today(); // システム日付
		String currentAlarmCode = command.getCurrentAlarmCode();
		//ドメインモデル「アラームリスト通常用メール設定」を取得する
		//ドメインモデル「メールサーバ」を取得する
		boolean useAuthentication =  mailServerAdapter.findBy(companyID);
		//メール設定(本人宛)：アラームリスト通常用メール設定.本人宛メール設定
		Optional<MailSettings> mailSetting = mailSettingNormal.get().getMailSettings();
		//メール設定(管理者宛)：アラームリスト通常用メール設定.管理者宛メール設定
		Optional<MailSettings> mailSettingAdmins = mailSettingNormal.get().getMailSettingAdmins();
		List<String> listEmployeeTagetId = command.getListEmployeeSendTaget(); // 本人送信対象：List<社員ID>
		List<String> listManagerTagetId = command.getListManagerSendTaget(); // 管理者送信対象：List<社員ID>
		List<ValueExtractAlarmDto> listValueExtractAlarmDto = extractResultFinder.getResultDto(command.getProcessId());// アラーム抽出結果
		MailSettingsParamDto mailSettingsParamDto = buildMailSend(mailSettingNormal.get());// メール送信設定

		return sendEmailService.alarmSendEmail(companyID, executeDate, listEmployeeTagetId, listManagerTagetId,
				listValueExtractAlarmDto, mailSettingsParamDto,currentAlarmCode,
				useAuthentication,mailSetting,mailSettingAdmins,Optional.empty());
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

	private boolean isNotHaveMailSetting(Optional<MailSettingNormal> mailSetting) {
		return !mailSetting.isPresent() || !(mailSetting.get().getMailSettings().isPresent()
												&& mailSetting.get().getMailSettingAdmins().isPresent());
	}

}
