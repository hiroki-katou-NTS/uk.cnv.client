package nts.uk.ctx.at.function.dom.alarmworkplace.sendemail;

import nts.arc.error.BusinessException;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.gul.mail.send.MailAttachedFileItf;
import nts.gul.mail.send.MailAttachedFilePath;
import nts.gul.mail.send.MailContents;
import nts.gul.mail.send.MailSendOptions;
import nts.uk.ctx.at.function.dom.adapter.alarm.EmployeePubAlarmAdapter;
import nts.uk.ctx.at.function.dom.adapter.alarm.IMailDestinationAdapter;
import nts.uk.ctx.at.function.dom.adapter.alarm.MailDestinationAlarmImport;
import nts.uk.ctx.at.function.dom.adapter.alarm.OutGoingMailAlarm;
import nts.uk.ctx.at.function.dom.alarm.createerrorinfo.CreateErrorInfo;
import nts.uk.ctx.at.function.dom.alarm.createerrorinfo.OutputErrorInfo;
import nts.uk.ctx.at.function.dom.alarm.export.AlarmExportDto;
import nts.uk.ctx.at.function.dom.alarm.export.AlarmListGenerator;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.Content;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettingNormal;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettings;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.Subject;
import nts.uk.ctx.at.function.dom.alarm.sendemail.MailSettingsParamDto;
import nts.uk.ctx.at.function.dom.alarm.sendemail.ValueExtractAlarmDto;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.mail.MailSender;
import nts.uk.shr.com.mail.SendMailFailedException;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class AlarmWorkplaceSendEmailService implements WorkplaceSendEmailService {

    @Inject
    private EmployeePubAlarmAdapter employeePubAlarmAdapter;

    @Inject
    private MailSender mailSender;

    @Inject
    private AlarmListGenerator alarmListGenerator;

    @Inject
    private IMailDestinationAdapter iMailDestinationAdapter;

    @Inject
    private CreateErrorInfo createErrorInfo;

    @Override
    public String alarmWorkplacesendEmail(List<String> workplaceIds,
                                        List<ValueExtractAlarmDto> listValueExtractAlarmDto,
                                        MailSettingNormal mailSettingsNormal,
                                        String currentAlarmCode,
                                        boolean useAuthentication) {

        Integer functionID = 9; //function of Alarm list = 9
        MailSettingsParamDto mailSettingsParamDto = buildMailSend(mailSettingsNormal);
        List<String> errors = new ArrayList<>();
        List<String> listworkplaceError = new ArrayList<>();

        Map<String, List<ValueExtractAlarmDto>> mapAlarm = listValueExtractAlarmDto
                .stream()
                .collect(Collectors.groupingBy(ValueExtractAlarmDto::getWorkplaceID));
        Map<String, List<String>> mapWorkplaceAndListSid = new HashMap<>();

        workplaceIds.forEach(i -> {
            // Call request list 218 return list employee Id
            List<String> listEmployeeId = employeePubAlarmAdapter.getListEmployeeId(i, GeneralDate.today());

            if (!listEmployeeId.isEmpty()) {
                mapWorkplaceAndListSid.put(i , listEmployeeId);
            } else {
                listworkplaceError.add(i);
            }
        });

        mapWorkplaceAndListSid.forEach((key, value) -> {
            value.forEach(i -> {
                try {
                    boolean isSucess = this.sendMail(
                            AppContexts.user().companyId(),
                            i,
                            9,
                            mapAlarm.get(key) == null ? Collections.emptyList() : mapAlarm.get(key),
                            mailSettingsParamDto.getSubject(),
                            mailSettingsParamDto.getText(),
                            currentAlarmCode,
                            useAuthentication,
                            mailSettingsNormal.getMailSettings(),
                            Optional.empty()
                    );
                    if (!isSucess) {
                        errors.add(i);
                        System.out.println("send failed");
                    } else {
                        System.out.println("success send email");
                    }
                } catch (SendMailFailedException e) {
                    throw e;
                }
            });
        });

        OutputErrorInfo outputErrorInfo = createErrorInfo.getErrorInfo(GeneralDate.today(),errors,mapWorkplaceAndListSid,listworkplaceError);
        String errorInfo = "";
        if(!outputErrorInfo.getError().equals("")) {
            errorInfo +=outputErrorInfo.getError();
        }
        if(!outputErrorInfo.getErrorWkp().equals("")) {
            errorInfo +=outputErrorInfo.getErrorWkp();
        }
        return errorInfo;

    }

    /**
     * 対象者にメールを送信する
     * Send mail flow employeeId
     * @param companyID
     * @param employeeId
     * @param functionID
     * @param listDataAlarmExport
     * @param subjectEmail
     * @param bodyEmail
     * @return true : send mail successful/false : send mail error
     */
    private boolean sendMail(String companyID, String employeeId, Integer functionID,
                             List<ValueExtractAlarmDto> listDataAlarmExport, String subjectEmail,
                             String bodyEmail, String currentAlarmCode,
                             boolean useAuthentication, Optional<MailSettings> mailSetting, Optional<String> senderAddress) throws BusinessException {
        // call request list 397 return email address
        MailDestinationAlarmImport mailDestinationAlarmImport = iMailDestinationAdapter
                .getEmpEmailAddress(companyID, employeeId, functionID);
        if (mailDestinationAlarmImport != null) {
            // Get all mail address
            List<OutGoingMailAlarm> emails = mailDestinationAlarmImport.getOutGoingMails().stream().filter(c->c.getEmailAddress() !=null).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(emails)) {
                return false;
            } else {
                if(StringUtils.isEmpty(subjectEmail)){
                    subjectEmail = TextResource.localize("KAL010_300");
                }
                // Genarate excel
                AlarmExportDto alarmExportDto = alarmListGenerator.generate(new FileGeneratorContext(), listDataAlarmExport,currentAlarmCode);
                // Create file attach
                List<MailAttachedFileItf> attachedFiles = new ArrayList<MailAttachedFileItf>();
                attachedFiles.add(new MailAttachedFilePath(alarmExportDto.getPath(), alarmExportDto.getFileName()));

                // Create mail content
                //メール内容を作成する
                MailContents mailContent = new MailContents(subjectEmail, bodyEmail,attachedFiles);

                List<String> replyToList = new ArrayList<>();
                List<String> toList = emails.stream().map(c-> c.getEmailAddress()).collect(Collectors.toList());
                List<String> ccList = new ArrayList<>();
                List<String> bccList = new ArrayList<>();
                String senderAddressInput = "";
                if(senderAddress.isPresent()) {
                    senderAddressInput = senderAddress.get();
                }
                if(mailSetting.isPresent()) {
                    ccList = mailSetting.get().getMailAddressCC();
                    bccList = mailSetting.get().getMailAddressBCC();
                    if(mailSetting.get().getMailRely().isPresent()) {
                        if(!mailSetting.get().getMailRely().get().v().equals("") && mailSetting.get().getMailRely().get().v() != null ) {
                            replyToList.add(mailSetting.get().getMailRely().get().v());
                        }
                    }
                }
                // Do send mail
                MailSendOptions mailSendOptions = new MailSendOptions(senderAddressInput, replyToList, toList, ccList, bccList);
                try {
                    if(useAuthentication) {
                        mailSender.sendFromAdmin(mailContent, companyID, mailSendOptions);
                    }else {
                        if(senderAddress.isPresent() && !senderAddress.get().equals("")) {
                            mailSender.send(mailContent, companyID, mailSendOptions);
                        }else {
                            mailSender.sendFromAdmin(mailContent, companyID, mailSendOptions);

                        }

                    }
                } catch (SendMailFailedException e) {
                    throw e;
                }

            }
        }
        return true;
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
