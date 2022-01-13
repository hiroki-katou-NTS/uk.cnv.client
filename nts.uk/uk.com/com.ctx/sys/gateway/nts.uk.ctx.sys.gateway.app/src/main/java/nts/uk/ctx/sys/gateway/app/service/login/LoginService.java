/*
 * 
 */
package nts.uk.ctx.sys.gateway.app.service.login;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import nts.arc.error.BusinessException;
import nts.arc.error.RawErrorMessage;
import nts.arc.i18n.I18NText;
import nts.gul.mail.send.MailContents;
import nts.uk.ctx.sys.gateway.app.command.sendmail.dto.SendMailCCG007DReturnDto;
import nts.uk.ctx.sys.gateway.app.command.sendmail.dto.SendMailReturnDto;
import nts.uk.ctx.sys.gateway.dom.adapter.employee.EmployeeInfoAdapter;
import nts.uk.ctx.sys.gateway.dom.adapter.employee.EmployeeInfoDtoImport;
import nts.uk.ctx.sys.gateway.dom.adapter.user.UserAdapter;
import nts.uk.ctx.sys.gateway.dom.adapter.user.UserImportNew;
import nts.uk.ctx.sys.gateway.dom.login.password.EmployCodeEditType;
import nts.uk.ctx.sys.gateway.dom.login.password.EmployeeCodeSettingImport;
import nts.uk.ctx.sys.gateway.dom.loginold.adapter.MailDestinationAdapter;
import nts.uk.ctx.sys.gateway.dom.loginold.adapter.SysEmployeeCodeSettingAdapter;
import nts.uk.ctx.sys.gateway.dom.loginold.dto.AvailableMailAddressImport;
import nts.uk.ctx.sys.gateway.dom.loginold.dto.AvailableMailAddressImportDto;
import nts.uk.ctx.sys.gateway.dom.loginold.dto.MailAddressNotificationImport;
import nts.uk.ctx.sys.gateway.dom.loginold.dto.MailDestiImport;
import nts.uk.ctx.sys.gateway.dom.loginold.dto.MailDestiImportDto;
import nts.uk.ctx.sys.gateway.dom.loginold.dto.MailDestinationFunctionManageImport;
import nts.uk.ctx.sys.gateway.dom.loginold.dto.SentMailListImport;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.mail.MailSender;
import nts.uk.shr.com.mail.SendMailFailedException;
import nts.uk.shr.com.url.RegisterEmbededURL;

@Stateless
public class LoginService {

	/** The employee code setting adapter. */
	@Inject
	private SysEmployeeCodeSettingAdapter employeeCodeSettingAdapter;
	
	/** The user adapter. */
	@Inject
	private UserAdapter userAdapter;

	/** The mail sender. */
	@Inject
	private MailSender mailSender;

	/** The register embeded URL. */
	@Inject
	private RegisterEmbededURL registerEmbededURL;

	/** The employee info adapter. */
	@Inject
	private EmployeeInfoAdapter employeeInfoAdapter;

	/** The mail destination adapter. */
	@Inject
	private MailDestinationAdapter mailDestinationAdapter;
	
	/** The Constant LOGIN_FUNCTION_ID. */
	private static final Integer LOGIN_FUNCTION_ID =1;
	
	public String comanyId(String contractCode, String companyCode){
		return contractCode + "-" + companyCode;
	}
	
	public String employeeCodeEdit(String employeeCode, String companyId) {
		Optional<EmployeeCodeSettingImport> findEmployeeCodeSetting = employeeCodeSettingAdapter.getbyCompanyId(companyId);
		if (findEmployeeCodeSetting.isPresent()) {
			EmployeeCodeSettingImport employeeCodeSetting = findEmployeeCodeSetting.get();
			EmployCodeEditType editType = employeeCodeSetting.getEditType();
			Integer addNumberDigit = employeeCodeSetting.getNumberDigit();
			if (employeeCodeSetting.getNumberDigit() == employeeCode.length()) {
				// not edit employeeCode
				return employeeCode;
			}
			// update employee code
			switch (editType) {
			case ZeroBefore:
				employeeCode = StringUtils.leftPad(employeeCode, addNumberDigit, "0");
				break;
			case ZeroAfter:
				employeeCode = StringUtils.rightPad(employeeCode, addNumberDigit, "0");
				break;
			case SpaceBefore:
				employeeCode = StringUtils.leftPad(employeeCode, addNumberDigit);
				break;
			case SpaceAfter:
				employeeCode = StringUtils.rightPad(employeeCode, addNumberDigit);
				break;
			default:
				break;
			}
			return employeeCode;
		}
		return employeeCode;
	}
	
	public List<SendMailReturnDto> sendMail(String companyId, String employeeCode, String contractCode) {
		
		// Imported（GateWay）「社員」を取得する
		EmployeeInfoDtoImport employee = this.employeeInfoAdapter.getEmployeeInfo(companyId, employeeCode);

		if (employee != null) {
			// 社員のメールアドレスを取得する
			MailDestiImport mailDestinationImport = mailDestinationAdapter.getMailDestiOfEmployee(companyId,
					Arrays.asList(employee.getEmployeeId()), LOGIN_FUNCTION_ID);
			// get userInfo
			Optional<UserImportNew> user = this.userAdapter.findUserByAssociateId(employee.getPersonId());
			Optional<SentMailListImport> optMailList = mailDestinationImport.getSentMailLists().stream()
					.filter(data -> data.getSid().equals(employee.getEmployeeId())).findFirst();
			if (user.isPresent()) {
				if (!optMailList.isPresent() || optMailList.get().getMailAddresses().isEmpty()) {
					// check mail present
					if (user.get().getMailAddress().get().isEmpty()) {
						throw new BusinessException("Msg_1129");
					} else {
						// Send Mail アルゴリズム「メール送信実行」を実行する
						return this.sendMail(Arrays.asList(user.get().getMailAddress().get()), user.get().getLoginId(),
								contractCode, employee, companyId);
					}
					// return new SendMailReturnDto(null);
				} else {
					return this.sendMail(optMailList.get().getMailAddresses(), user.get().getLoginId(), contractCode,
							employee, companyId);
				}
			} else {
				return Arrays.asList(new SendMailReturnDto(null));
			}
		}
		// fixbug #101548 EA修正履歴 No.2891
		else {
			throw new BusinessException("Msg_176");
		}
	}
	
	/**
	 * Send mail CCG007D.
	 *
	 * @param companyId the company id
	 * @param employeeCode the employee code
	 * @param contractCode the contract code
	 * @return the list
	 */
	public SendMailCCG007DReturnDto sendMailCCG007D(String companyId, String employeeCode, String contractCode) {
		
		// Imported（GateWay）「社員」を取得する
		EmployeeInfoDtoImport employee = this.employeeInfoAdapter.getEmployeeInfo(companyId, employeeCode);

		if (employee != null) {
			// 社員のメールアドレス通知を取得する
			MailDestiImport mailDestiImport = mailDestinationAdapter.getMailDestiOfEmployee(companyId,
					Arrays.asList(employee.getEmployeeId()), LOGIN_FUNCTION_ID);
			MailDestiImportDto mailDestiImportDto = convertMailImportToImportDto(mailDestiImport);
			Optional<UserImportNew> user = this.userAdapter.findUserByAssociateId(employee.getPersonId());
			AvailableMailAddressImportDto mailAddressImport = null;
			
			// send mail
			List<SendMailReturnDto> sendMailReturnDtos = new ArrayList<>();
			if (mailDestiImportDto.getMailAddress().isPresent()) {
				List<String> mailAddresses = new ArrayList<>();
				mailAddressImport = mailDestiImportDto.getMailAddress().get();
				// add mail address from mailAddressImport
				mailAddresses.add(mailAddressImport.getCompanyMailAddress());
				mailAddresses.add(mailAddressImport.getCompanyMobileMailAddress());
				mailAddresses.add(mailAddressImport.getPersonalMailAddress());
				mailAddresses.add(mailAddressImport.getPersonalMobileMailAddress());
				mailAddresses.removeAll(Arrays.asList("", null));
				if (!mailAddresses.isEmpty()) {
					sendMailReturnDtos = this.sendMail(mailAddresses, user.get().getLoginId(),
						contractCode, employee, companyId);
				}
			}
			
			// get MailDestinationFunctionManageImport from mailDestiImport
			MailDestinationFunctionManageImport destinationFunctionManageImport = null;
			if (mailDestiImportDto.getMailDestinationFunctionManage().isPresent()) {
				destinationFunctionManageImport = mailDestiImportDto.getMailDestinationFunctionManage().get();
			}
			
			StringBuilder message = new StringBuilder("");
			
			// if send mail success
			if (!sendMailReturnDtos.isEmpty() && destinationFunctionManageImport != null) {
				message.append(TextResource.localize("Msg_3246"));
				message.append("\n");
				message.append(setMessageCCG007(destinationFunctionManageImport, mailAddressImport, true));
				return new SendMailCCG007DReturnDto(message.toString());
			}
			// if send mail failed
			else if (sendMailReturnDtos.isEmpty()) {
				if (!isSettingSendMail(destinationFunctionManageImport)) {
					message.append(TextResource.localize("Msg_3244"));
				} else {
					message.append(TextResource.localize("Msg_3245"));
					message.append("\n");
					message.append(setMessageCCG007(destinationFunctionManageImport, mailAddressImport, false));
				}
				RawErrorMessage errorMsg = new RawErrorMessage(message.toString());
				throw new BusinessException(errorMsg);
			}
			return new SendMailCCG007DReturnDto(message.toString());
			
		} else {
			throw new BusinessException("Msg_176");
		}
	}
	
	/**
	 * Send mail.
	 *
	 * @param mailto
	 *            the mailto
	 * @param command
	 *            the command
	 * @return true, if successful
	 */
	// Send Mail アルゴリズム「メール送信実行」を実行する
	private List<SendMailReturnDto> sendMail(List<String> toMails, String loginId, String contractCode,
			EmployeeInfoDtoImport employee,String companyId) {
		// get URL from CCG033
		String url = this.registerEmbededURL.embeddedUrlInfoRegis("CCG007", "H", 3, 24, employee.getEmployeeId(),
				contractCode, loginId, employee.getEmployeeCode(), 1, new ArrayList<>());
		// sendMail
		MailContents contents = new MailContents("", I18NText.getText("CCG007_21") + " \n" + url);
		List<SendMailReturnDto> dtos = new ArrayList<>();
		try {
			toMails.stream().forEach(item -> {
				mailSender.sendFromAdmin(item, contents,companyId);
				SendMailReturnDto dto = new SendMailReturnDto(url);
				dtos.add(dto);
			});
			return dtos;
		} catch (SendMailFailedException e) {
			// Send mail fail
			throw new BusinessException("Msg_208");
		}
	}
	
	/**
	 * Convert mail export to import.
	 *
	 * @param destinationExport the destination export
	 * @return the mail desti import dto
	 */
	private static MailDestiImportDto convertMailImportToImportDto(MailDestiImport destinationExport) {
		MailAddressNotificationImport addressNotificationImport = destinationExport.getMailAddressNotification();
		
		// set MailDestinationFunctionManageImport
		Optional<MailDestinationFunctionManageImport> mailDestinationFunctionManage;
		if (addressNotificationImport.getMailDestinationFunctionManage().isPresent()) {
			MailDestinationFunctionManageImport export = addressNotificationImport.getMailDestinationFunctionManage().get();
			mailDestinationFunctionManage = Optional.of(new MailDestinationFunctionManageImport(export.getFunctionId(), export.getUseCompanyMailAddress(), 
					export.getUseCompanyMobileMailAddress(), export.getUsePersonalMailAddress(), export.getUsePersonalMobileMailAddress()));
		} else {
			mailDestinationFunctionManage = Optional.empty();
		}
		
		// set AvailableMailAddressImport
		Optional<AvailableMailAddressImportDto> availableMailAddressImport;
		if (!addressNotificationImport.getMailAddresses().isEmpty()) {
			AvailableMailAddressImport addressExport = addressNotificationImport.getMailAddresses().get(0);
			String companyMailAddress = addressExport.getOptCompanyMailAddress().map(t -> t).orElse(null);
			String companyMobileMailAddress = addressExport.getOptCompanyMobileMailAddress().map(t -> t).orElse(null);
			String personalMailAddress = addressExport.getOptPersonalMailAddress().map(t -> t).orElse(null);
			String personalMobileMailAddress = addressExport.getOptPersonalMobileMailAddress().map(t -> t).orElse(null);
			availableMailAddressImport = Optional.of(new AvailableMailAddressImportDto(
					companyMailAddress,
					companyMobileMailAddress,
					personalMailAddress,
					personalMobileMailAddress));
		} else {
			availableMailAddressImport = Optional.empty();
		}
		
		return new MailDestiImportDto(availableMailAddressImport, mailDestinationFunctionManage);
	}
	
	/**
	 * Checks if is setting send mail.
	 *
	 * @param destinationFunctionManageImport the destination function manage import
	 * @return true, if is setting send mail
	 */
	private static boolean isSettingSendMail(MailDestinationFunctionManageImport destinationFunctionManageImport) {
		if ((destinationFunctionManageImport != null && 
			destinationFunctionManageImport.getUseCompanyMailAddress() == NotUseAtr.NOT_USE.value && 
			destinationFunctionManageImport.getUseCompanyMobileMailAddress() == NotUseAtr.NOT_USE.value &&
		    destinationFunctionManageImport.getUsePersonalMailAddress() == NotUseAtr.NOT_USE.value && 
		    destinationFunctionManageImport.getUsePersonalMobileMailAddress() == NotUseAtr.NOT_USE.value) ||
			destinationFunctionManageImport == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Sets the message CCG 007.
	 *
	 * @param destinationFunctionManageImport the destination function manage import
	 * @param mailAddressImport the mail address import
	 * @param isSuccess the is success
	 * @return the string
	 */
	private static String setMessageCCG007(MailDestinationFunctionManageImport destinationFunctionManageImport,
			AvailableMailAddressImportDto mailAddressImport, boolean isSuccess) {
		StringBuilder message = new StringBuilder("");
		if (isConditionValid(isSuccess, mailAddressImport.getCompanyMailAddress(), destinationFunctionManageImport.getUseCompanyMailAddress())) {
			message.append(I18NText.getText("CDL011_7"));
			message.append(", ");
		}
		if (isConditionValid(isSuccess, mailAddressImport.getCompanyMobileMailAddress(), destinationFunctionManageImport.getUseCompanyMobileMailAddress())) {
			message.append(I18NText.getText("CDL011_8"));
			message.append(", ");
		}
		if (isConditionValid(isSuccess, mailAddressImport.getPersonalMailAddress(), destinationFunctionManageImport.getUsePersonalMailAddress())) {
			message.append(I18NText.getText("CDL011_9"));
			message.append(", ");
		}
		if (isConditionValid(isSuccess, mailAddressImport.getPersonalMobileMailAddress(), destinationFunctionManageImport.getUsePersonalMobileMailAddress())) {
			message.append(I18NText.getText("CDL011_10"));
			message.append(", ");
		}
		return message.substring(0, message.length() - 2);
	}
	
	/**
	 * Checks if is condition valid.
	 *
	 * @param isSuccess the is success
	 * @param mailAddress the mail address
	 * @param isUse the is use
	 * @return true, if is condition valid
	 */
	private static boolean isConditionValid(boolean isSuccess, String mailAddress, int isUse) {
		if ((!isSuccess && isUse == NotUseAtr.USE.value) || (isSuccess && isUse == NotUseAtr.USE.value &&
				mailAddress != null && !mailAddress.equals(""))) {
			return true;
		}
		return false;
	}
}
