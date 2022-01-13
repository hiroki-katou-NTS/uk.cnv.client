package nts.uk.ctx.at.function.app.command.alarm.sendemail;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.function.app.find.alarm.alarmlist.ErAlExtractResultFinder;
import nts.uk.ctx.at.function.dom.adapter.mailserver.MailServerAdapter;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.*;
import nts.uk.ctx.at.function.dom.alarm.sendemail.MailSettingsParamDto;
import nts.uk.ctx.at.function.dom.alarm.sendemail.SendEmailService;
import nts.uk.ctx.at.function.dom.alarm.sendemail.ValueExtractAlarmDto;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Stateless
public class StartAlarmSendEmailProcessHandler extends CommandHandlerWithResult<ParamAlarmSendEmailCommand, String> {
	@Inject
	private SendEmailService sendEmailService;

	@Inject
	private MailSettingNormalRepository alarmExeMailSettingRepository;

	@Inject
	private MailServerAdapter mailServerAdapter;

	@Inject
	private ErAlExtractResultFinder extractResultFinder;

	@Inject
	private AlarmListExecutionMailSettingRepository alarmExeMailSettingRepo;

	@Override
	protected String handle(CommandHandlerContext<ParamAlarmSendEmailCommand> context) {
		String companyID = AppContexts.user().companyId(); // ログイン社員の会社ID

		// Get domain アラームリスト実行メール設定 (通常自動区分　＝　通常,　個人職場区分　＝　個人)
		// EAP: This time, automatic is not yet supported
		List<AlarmListExecutionMailSetting> alarmExeMailSetting = alarmExeMailSettingRepo.findBy(companyID, IndividualWkpClassification.INDIVIDUAL.value,
				NormalAutoClassification.NORMAL.value);
		// List＜アラームリスト実行メール設定＞　＝＝　Empty => エラーメッセージ(#Msg_1169)を表示する
		if (CollectionUtil.isEmpty(alarmExeMailSetting)) throw new BusinessException("Msg_1169");

		ParamAlarmSendEmailCommand command = context.getCommand();
		GeneralDate executeDate = GeneralDate.today(); // システム日付
		String currentAlarmCode = command.getCurrentAlarmCode();

        //ドメインモデル「メールサーバ」を取得する
        boolean useAuthentication = mailServerAdapter.findBy(companyID);
        if (!useAuthentication) throw new BusinessException("Msg_2205");

        // メール設定(本人宛)：内容メール設定　（条件：アラームリスト実行メール設定．本人管理区分．本人宛メール設定
        val alarmMailSettingPerson = alarmExeMailSetting.stream().filter(x -> x.getPersonalManagerClassify().value ==
                PersonalManagerClassification.EMAIL_SETTING_FOR_PERSON.value).findFirst();
        Optional<MailSettings> mailSettingPerson = Optional.empty();
        if (alarmMailSettingPerson.isPresent()) {
            mailSettingPerson = alarmMailSettingPerson.get().getContentMailSettings();
        }

        // メール設定(管理者宛)：内容メール設定　（条件：アラームリスト実行メール設定．本人管理区分．管理者宛メール設定
        val alarmMailSettingAdmin = alarmExeMailSetting.stream().filter(x -> x.getPersonalManagerClassify().value ==
                PersonalManagerClassification.EMAIL_SETTING_FOR_ADMIN.value).findFirst();
        Optional<MailSettings> mailSettingAdmin = Optional.empty();
        if (alarmMailSettingAdmin.isPresent()) {
            mailSettingAdmin = alarmMailSettingAdmin.get().getContentMailSettings();
        }
		List<String> listEmployeeTagetId = command.getListEmployeeSendTaget(); // 本人送信対象：List<社員ID>
		List<String> listManagerTagetId = command.getListManagerSendTaget(); // 管理者送信対象：List<社員ID>
		val managerTargetList = command.getListManagerSelected();
		List<ValueExtractAlarmDto> listValueExtractAlarmDto = extractResultFinder.getResultDto(command.getProcessId());// アラーム抽出結果
		MailSettingsParamDto mailSettingsParamDto = buildMailSend(mailSettingPerson, mailSettingAdmin);// メール送信設定

		return sendEmailService.alarmSendEmail(companyID, executeDate, listEmployeeTagetId, listManagerTagetId,
				listValueExtractAlarmDto, mailSettingsParamDto, currentAlarmCode,
				useAuthentication, mailSettingPerson, mailSettingAdmin, Optional.empty(), managerTargetList, alarmExeMailSetting);
	}

	private MailSettingsParamDto buildMailSend(Optional<MailSettings> mailSetingPerson, Optional<MailSettings> mailSetingAdmin) {
		String subject = "", text = "", subjectAdmin = "", textAdmin = "";
		if (mailSetingPerson.isPresent()) {
			subject = mailSetingPerson.get().getSubject().orElseGet(() -> new Subject("")).v();
			text = mailSetingPerson.get().getText().orElseGet(() -> new Content("")).v();
		}
		if (mailSetingAdmin.isPresent()) {
			subjectAdmin = mailSetingAdmin.get().getSubject().orElseGet(() -> new Subject("")).v();
			textAdmin = mailSetingAdmin.get().getText().orElseGet(() -> new Content("")).v();
		}
		// setting subject , body mail
		return new MailSettingsParamDto(subject, text, subjectAdmin, textAdmin);
	}
}
