package nts.uk.shr.com.context.loginuser;

import java.io.Serializable;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.scoped.session.SessionContextProvider;
import nts.arc.security.ticket.DataTicket;
import nts.arc.time.GeneralDateTime;
import nts.gul.serialize.ObjectSerializer;
import nts.uk.shr.com.context.LoginUserContext;
import nts.uk.shr.com.context.loginuser.role.DefaultLoginUserRoles;
import nts.uk.shr.com.context.loginuser.role.LoginUserRoles;

@Stateless
public class DefaultLoginUserContextManager implements LoginUserContextManager {
	
	private static final int SECONDS_TO_EXPIRE_TICKET = 60 * 60 * 24;

	@Inject
	private SessionLowLayer sessionLowLayer;
	
	@Override
	public void loggedInAsEmployee(
			String userId,
			String personId,
			String contractCode,
			String companyId,
			String companyCode,
			String employeeId,
			String employeeCode) {
		
		val context = new DefaultLoginUserContext(userId, true);
		context.setPersonId(personId);
		context.setContractCode(contractCode);
		context.setCompanyId(companyId);
		context.setCompanyCode(companyCode);
		context.setEmployeeId(employeeId);
		context.setEmployeeCode(employeeCode);
		
		SessionContextProvider.get().put(LoginUserContext.KEY_SESSION_SCOPED, context);
		
		this.sessionLowLayer.loggedIn();
	}
	
	@Override
	public void loggedInAsUser(
			String userId,
			String personId,
			String contractCode,
			String companyId,
			String companyCode) {
		
		val context = new DefaultLoginUserContext(userId, false);
		context.setPersonId(personId);
		context.setContractCode(contractCode);
		context.setCompanyId(companyId);
		context.setCompanyCode(companyCode);
		
		SessionContextProvider.get().put(LoginUserContext.KEY_SESSION_SCOPED, context);
		
		this.sessionLowLayer.loggedIn();
	}
	
	@Override
	public void changeCompany(
			String userId,
			String personId,
			String contractCode,
			String companyId,
			String companyCode,
			String employeeId,
			String employeeCode,
			boolean isEmployee) {
		
		val context = new DefaultLoginUserContext(userId, isEmployee);
		context.setPersonId(personId);
		context.setContractCode(contractCode);
		context.setCompanyId(companyId);
		context.setCompanyCode(companyCode);
		if(isEmployee){
			context.setEmployeeId(employeeId);
			context.setEmployeeCode(employeeCode);
		}
		SessionContextProvider.get().put(LoginUserContext.KEY_SESSION_SCOPED, context);
	}

	@Override
	public RoleIdSetter roleIdSetter() {
		
		DefaultLoginUserContext context = SessionContextProvider.get().get(LoginUserContext.KEY_SESSION_SCOPED);
		DefaultLoginUserRoles roles = (DefaultLoginUserRoles)context.roles();

		return new RoleIdSetter() {
			@Override
			public RoleIdSetter forAttendance(String roleId) {
				roles.setRoleIdForAttendance(roleId);
				return this;
			}
			@Override
			public RoleIdSetter forPayroll(String roleId) {
				roles.setRoleIdForPayroll(roleId);
				return this;
			}
			@Override
			public RoleIdSetter forPersonnel(String roleId) {
				roles.setRoleIdForPersonnel(roleId);
				return this;
			}
			@Override
			public RoleIdSetter forPersonalInfo(String roleId) {
				roles.setRoleIdforPersonalInfo(roleId);
				return this;
			}
			@Override
			public RoleIdSetter forOfficeHelper(String roleId) {
				roles.setRoleIdforOfficeHelper(roleId);
				return this;
			}
			@Override
			public RoleIdSetter forSystemAdmin(String roleId) {
				roles.setRoleIdforSystemAdmin(roleId);
				return this;
			}
			@Override
			public RoleIdSetter forCompanyAdmin(String roleId) {
				roles.setRoleIdforCompanyAdmin(roleId);
				return this;
			}
			@Override
			public RoleIdSetter forGroupCompaniesAdmin(String roleId) {
				roles.setRoleIdforGroupCompaniesAdmin(roleId);
				return this;
			}
			@Override
			public RoleIdSetter isInChargeAttendance(boolean isInCharge) {
				roles.setIsInChargeAttendance(isInCharge);
				return this;
			}
			@Override
			public RoleIdSetter isInChargePayroll(boolean isInCharge) {
				roles.setIsInChargePayroll(isInCharge);
				return this;
			}
			@Override
			public RoleIdSetter isInChargePersonnel(boolean isInCharge) {
				roles.setIsInChargePersonnel(isInCharge);
				return this;
			}
			@Override
			public RoleIdSetter isInChargePersonalInfo(boolean isInCharge) {
				roles.setIsInChargePersonalInfo(isInCharge);
				return this;
			}

		};
	}

	@Override
	public void roleSet(LoginUserRoles roles) {
		DefaultLoginUserContext context = SessionContextProvider.get().get(LoginUserContext.KEY_SESSION_SCOPED);
		((DefaultLoginUserRoles)context.roles()).restore(roles);
	}

	@Override
	public void setLanguage(String basic, String forPersonName) {
		DefaultLoginUserContext context = SessionContextProvider.get().get(LoginUserContext.KEY_SESSION_SCOPED);
		context.language().changeBasicLanguage(basic);
		context.language().changePersonNameLanguage(forPersonName);
	}

	@Override
	public DataTicket toTicket() {
		val context = SessionContextProvider.get().get(LoginUserContext.KEY_SESSION_SCOPED);
		return DataTicket.issueNewTicket(context, SECONDS_TO_EXPIRE_TICKET * 1000);
	}

	@Override
	public void restore(DataTicket ticket) {
		if (!ticket.isValidAt(GeneralDateTime.now())) {
			throw new RuntimeException("the ticket has been expired.");
		}
		
		DefaultLoginUserContext restored = ticket.getData();
		
		this.loggedInAsEmployee(
				restored.userId(),
				restored.personId(),
				restored.contractCode(),
				restored.companyId(),
				restored.companyCode(),
				restored.employeeId(),
				restored.employeeCode());
		
		DefaultLoginUserContext context = SessionContextProvider.get().get(LoginUserContext.KEY_SESSION_SCOPED);
		((DefaultLoginUserRoles)context.roles()).restore(restored.roles());
	}

	@Override
	public Optional<String> toBase64() {
		val context = SessionContextProvider.get().get(LoginUserContext.KEY_SESSION_SCOPED);
		if (context == null) {
			return Optional.empty();
		}
		return Optional.of(ObjectSerializer.toBase64((Serializable) context));
	}

	@Override
	public void restoreBase64(String base64) {
		
		DefaultLoginUserContext restored = ObjectSerializer.restore(base64);
		
		if (restored.isEmployee()) {
			this.loggedInAsEmployee(
					restored.userId(),
					restored.personId(),
					restored.contractCode(),
					restored.companyId(),
					restored.companyCode(),
					restored.employeeId(),
					restored.employeeCode());
		} else {
			this.loggedInAsUser(
					restored.userId(), 
					restored.personId(), 
					restored.contractCode(), 
					restored.companyId(), 
					restored.companyCode());
		}
		
		DefaultLoginUserContext context = SessionContextProvider.get().get(LoginUserContext.KEY_SESSION_SCOPED);
		((DefaultLoginUserRoles)context.roles()).restore(restored.roles());
	}

	@Override
	public void loggedOut() {
		this.sessionLowLayer.loggedOut();
	}
}
