/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.gateway.app.command.login;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.gateway.dom.login.EmployCodeEditType;
import nts.uk.ctx.sys.gateway.dom.login.Employee;
import nts.uk.ctx.sys.gateway.dom.login.EmployeeCodeSetting;
import nts.uk.ctx.sys.gateway.dom.login.EmployeeCodeSettingRepository;
import nts.uk.ctx.sys.gateway.dom.login.GateWayEmployeeRepository;
import nts.uk.ctx.sys.gateway.dom.login.User;
import nts.uk.ctx.sys.gateway.dom.login.UserRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class SubmitLoginFormTwoCommandHandler extends CommandHandler<SubmitLoginFormTwoCommand> {

	/** The user repository. */
	@Inject
	UserRepository userRepository;

	@Inject
	EmployeeCodeSettingRepository employeeCodeSettingRepo;

	@Inject
	GateWayEmployeeRepository employeeRepository;

	@Override
	protected void handle(CommandHandlerContext<SubmitLoginFormTwoCommand> context) {

		SubmitLoginFormTwoCommand command = context.getCommand();
		String companyCode = command.getCompanyCode();
		String employeeCode = command.getEmployeeCode();
		String password = command.getPassword();
		String companyId = AppContexts.user().contractCode() + "-" + companyCode;
		// check validate input
		this.checkInput(command);

		// TODO edit employee code
		employeeCode = this.employeeCodeEdit(employeeCode,companyId);
		// TODO get domain 社員
		Employee em = this.getEmployee(employeeCode);
		// TODO get User by associatedPersonId
		User user = this.getUser(em.getEmployeeId().toString());// TODO
		// check password
		this.compareHashPassword(user, password);
		// check time limit
		this.checkLimitTime(user);

		// TODO check save to local storage
		// if check -> save
		// else -> remove

	}

	private void checkInput(SubmitLoginFormTwoCommand command) {

		// check input company code
		if (command.getCompanyCode() == null) {
			throw new BusinessException("#Msg_311");
		}
		// check input employee code
		if (command.getEmployeeCode() == null) {
			throw new BusinessException("#Msg_312");
		}
		// check input password
		if (command.getPassword() == null) {
			throw new BusinessException("#Msg_310");
		}
	}

	private String employeeCodeEdit(String employeeCode, String companyId) {
		EmployeeCodeSetting employeeCodeSetting = employeeCodeSettingRepo.getbyCompanyId(companyId);
		EmployCodeEditType editType = employeeCodeSetting.getEditType();
		Integer addNumberDigit = employeeCodeSetting.getNumberDigit() - employeeCode.length();
		if (employeeCodeSetting.getNumberDigit() == employeeCode.length()) {
			// not edit employeeCode
			return employeeCode;
		} else {
			switch (editType) {
			case ZeroBefore:
				StringUtils.leftPad(employeeCode, addNumberDigit, "0");
				break;
			case ZeroAfter:
				StringUtils.rightPad(employeeCode, addNumberDigit, "0");
				break;
			case SpaceBefore:
				StringUtils.leftPad(employeeCode, addNumberDigit);
				break;
			case SpaceAfter:
				StringUtils.rightPad(employeeCode, addNumberDigit);
				break;
			default:
				break;
			}
			return employeeCode;
		}
	}

	private Employee getEmployee(String employeeCode) {
		Optional<Employee> em = employeeRepository.getByEmployeeCode(employeeCode);
		if (em.isPresent()) {
			return em.get();
		} else {
			throw new BusinessException("#Msg_301");
		}
	}

	private User getUser(String associatedPersonId) {
		Optional<User> user = userRepository.getByAssociatedPersonId(associatedPersonId);
		if (user.isPresent()) {
			return user.get();
		} else {
			throw new BusinessException("#Msg_301");
		}
	}

	private void compareHashPassword(User user, String password) {
		// TODO compare hash string here
		if (!user.getPassword().equals(password)) {
			throw new BusinessException("#Msg_302");
		}
	}

	private void checkLimitTime(User user) {
		if (user.getExpirationDate().after(GeneralDate.today())) {
			throw new BusinessException("#Msg_316");
		}
	}
}
