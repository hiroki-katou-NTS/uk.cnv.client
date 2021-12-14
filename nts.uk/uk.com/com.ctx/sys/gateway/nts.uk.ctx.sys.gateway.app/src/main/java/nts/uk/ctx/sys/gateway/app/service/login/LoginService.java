package nts.uk.ctx.sys.gateway.app.service.login;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import nts.arc.error.BusinessException;
import nts.arc.i18n.I18NText;
import nts.gul.mail.send.MailContents;
import nts.uk.ctx.sys.gateway.app.command.sendmail.dto.SendMailReturnDto;
import nts.uk.ctx.sys.gateway.dom.adapter.employee.EmployeeInfoAdapter;
import nts.uk.ctx.sys.gateway.dom.adapter.employee.EmployeeInfoDtoImport;
import nts.uk.ctx.sys.gateway.dom.adapter.user.UserAdapter;
import nts.uk.ctx.sys.gateway.dom.adapter.user.UserImportNew;
import nts.uk.ctx.sys.gateway.dom.login.password.EmployCodeEditType;
import nts.uk.ctx.sys.gateway.dom.login.password.EmployeeCodeSettingImport;
import nts.uk.ctx.sys.gateway.dom.loginold.adapter.MailDestinationAdapter;
import nts.uk.ctx.sys.gateway.dom.loginold.adapter.SysEmployeeCodeSettingAdapter;
import nts.uk.ctx.sys.gateway.dom.loginold.dto.MailDestinationImport;
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
			MailDestinationImport mailDestinationImport = mailDestinationAdapter.getMailofEmployee(companyId,
					Arrays.asList(employee.getEmployeeId()), LOGIN_FUNCTION_ID);
			// get userInfo
			Optional<UserImportNew> user = this.userAdapter.findUserByAssociateId(employee.getPersonId());

			if (user.isPresent()) {
				if (mailDestinationImport.getOutGoingMails().isEmpty()) {
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
					return this.sendMail(mailDestinationImport.getOutGoingMails(), user.get().getLoginId(), contractCode,
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
}
