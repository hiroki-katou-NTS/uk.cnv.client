package nts.uk.ctx.at.function.dom.alarm.sendemail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

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
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettings;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.mail.MailSender;
import nts.uk.shr.com.mail.SendMailFailedException;
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Stateless
public class AlarmSendEmailService implements SendEmailService {
	@Inject
	private IMailDestinationAdapter iMailDestinationAdapter;
	
	@Inject
	private MailSender mailSender;
	
//	@Inject
//	private EmployeeSprPubAlarmAdapter employeeSprPubAlarmAdapter;
	
	@Inject
	private EmployeePubAlarmAdapter employeePubAlarmAdapter;
	
	@Inject
	private AlarmListGenerator alarmListGenerator;
	
	@Inject
	private CreateErrorInfo createErrorInfo;
	
	public String alarmSendEmail(String companyID, GeneralDate executeDate, List<String> employeeTagetIds,
			List<String> managerTagetIds, List<ValueExtractAlarmDto> valueExtractAlarmDtos,
			MailSettingsParamDto mailSettingsParamDto,String currentAlarmCode,
			boolean useAuthentication,Optional<MailSettings> mailSetting,Optional<MailSettings> mailSettingAdmins,Optional<String> senderAddress) {
		return process(companyID, executeDate, employeeTagetIds, managerTagetIds, valueExtractAlarmDtos,mailSettingsParamDto,currentAlarmCode,
				useAuthentication,mailSetting,mailSettingAdmins,senderAddress);
	}
	//メール送信処理
	private String process(String companyID, GeneralDate executeDate, List<String> employeeTagetIds,
			List<String> managerTagetIds, List<ValueExtractAlarmDto> valueExtractAlarmDtos,
			MailSettingsParamDto mailSettingsParamDto,String currentAlarmCode,
			boolean useAuthentication,Optional<MailSettings> mailSetting,Optional<MailSettings> mailSettingAdmins,Optional<String> senderAddress){
		List<String> errors = new ArrayList<>();
		Integer functionID = 9; //function of Alarm list = 9
//		List<String> listEmpAdminError = new ArrayList<>();
		List<String> listworkplaceError = new ArrayList<>();
		boolean isErrorSendMailEmp = false;
		//Send mail for employee
		// get address email
		if (!CollectionUtil.isEmpty(employeeTagetIds)) {

			for (String employeeId : employeeTagetIds) {
				// 本人送信対象のアラーム抽出結果を抽出する
				List<ValueExtractAlarmDto> valueExtractAlarmEmpDtos = valueExtractAlarmDtos.stream()
						.filter(c -> employeeId.equals(c.getEmployeeID())).collect(Collectors.toList());
				try {
					// Do send email
					boolean isSucess = sendMail(companyID, employeeId, functionID,
							valueExtractAlarmEmpDtos, mailSettingsParamDto.getSubject(),
							mailSettingsParamDto.getText(), currentAlarmCode,
							useAuthentication,mailSetting,senderAddress);
					if (!isSucess) {
						errors.add(employeeId);
					}
				} catch (SendMailFailedException e) {
					throw e;
				}
			}
		}
		Map<String, List<String>> mapWorkplaceAndListSid = new HashMap<>();
		// Send mail for Manager
		// get list employeeId of manager
		if (!isErrorSendMailEmp && !CollectionUtil.isEmpty(managerTagetIds)) {
			// 管理者送信対象のアラーム抽出結果を抽出する
			List<ValueExtractAlarmDto> valueExtractAlarmManagerDtos = new ArrayList<>();
			Map<String, List<ValueExtractAlarmDto>> mapCheckAlarm = new HashMap<>();
			List<ValueExtractAlarmDto> listTemp = null;
			// Get list workplace to send
			List<String> workplaceIds = new ArrayList<>();
			for (ValueExtractAlarmDto obj : valueExtractAlarmDtos) {
				if(managerTagetIds.contains(obj.getEmployeeID())){
					String workplaceID = obj.getWorkplaceID();
					// 管理者送信対象のアラーム抽出結果を職場でグループ化する
					if(!mapCheckAlarm.containsKey(workplaceID)){
						workplaceIds.add(workplaceID);
						// New list alarm to send
						listTemp = new ArrayList<>();
					}else{
						listTemp = mapCheckAlarm.get(workplaceID);
					}
					listTemp.add(obj);
					mapCheckAlarm.put(workplaceID, listTemp);
				}
			}
			
			for (String workplaceId : workplaceIds) {
				List<String> listEmpAdminError = new ArrayList<>();
				// Call request list 218 return list employee Id
				List<String> listEmployeeId = employeePubAlarmAdapter.getListEmployeeId(workplaceId,executeDate);
				// 抽出結果：ループ中の職場単位のアラーム抽出結果 
				valueExtractAlarmManagerDtos = mapCheckAlarm.get(workplaceId);
				if (!CollectionUtil.isEmpty(listEmployeeId)) {
					// loop send mail
					for (String employeeId : listEmployeeId) {
						try {
							// Get subject , body mail
							boolean isSucess = sendMail(companyID, employeeId, functionID,
									valueExtractAlarmManagerDtos,mailSettingsParamDto.getSubjectAdmin(),
									mailSettingsParamDto.getTextAdmin(),currentAlarmCode,
									useAuthentication,mailSettingAdmins,senderAddress);
							if (!isSucess) {
								//errors.add(employeeId);
								listEmpAdminError.add(employeeId);
							}
						} catch (SendMailFailedException e) {
							throw e;
						}
					}
					mapWorkplaceAndListSid.put(workplaceId, listEmpAdminError);
					
				}else {
					listworkplaceError.add(workplaceId);
				}
			}
		}
		//String empployeeNameError = isErrorSendMailEmp + ";";// return status check display alert error
		
//		if (!CollectionUtil.isEmpty(errors)) {
//			int index = 0;
//			int errorsSize = errors.size();
//			for (String sId : errors) {
//				// save using request list 346
//				String empNames =  employeeSprPubAlarmAdapter.getEmployeeNameBySId(sId);
//				if (!StringUtils.isEmpty(empNames)) {
//					empployeeNameError += empNames;
//				}
//				index++;
//				if(index != errorsSize){
//					empployeeNameError += "<br/>";        
//                }   
//			}
//		}
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
			String bodyEmail,String currentAlarmCode,
			boolean useAuthentication,Optional<MailSettings> mailSetting,Optional<String> senderAddress) throws BusinessException {
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
				
				//for (OutGoingMailAlarm outGoingMailAlarm : emails) {
					// If not email to return false
//					if (StringUtils.isEmpty(emails.getEmailAddress())) {
//						return false;
//					}
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
				//}
			}
		}
		return true;
	}
}