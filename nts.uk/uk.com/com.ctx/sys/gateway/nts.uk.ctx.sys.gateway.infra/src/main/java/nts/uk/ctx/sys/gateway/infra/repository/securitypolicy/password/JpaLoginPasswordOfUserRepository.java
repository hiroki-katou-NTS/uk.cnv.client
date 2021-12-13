package nts.uk.ctx.sys.gateway.infra.repository.securitypolicy.password;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.sys.gateway.dom.login.password.userpassword.LoginPasswordOfUser;
import nts.uk.ctx.sys.gateway.dom.login.password.userpassword.LoginPasswordOfUserRepository;
import nts.uk.ctx.sys.gateway.dom.login.password.userpassword.PasswordChangeLogDetail;
import nts.uk.ctx.sys.gateway.infra.entity.securitypolicy.password.SgwdtUserLoginPassword;
import nts.uk.ctx.sys.gateway.infra.entity.securitypolicy.password.SgwdtUserLoginPasswordChangeLog;

@Stateless
public class JpaLoginPasswordOfUserRepository extends JpaRepository implements LoginPasswordOfUserRepository{
	
	@Override
	public void save(LoginPasswordOfUser password) {
		delete(password.getUserId());
		this.commandProxy().insert(SgwdtUserLoginPassword.toEntity(password));
		this.commandProxy().insertAll(SgwdtUserLoginPasswordChangeLog.toEntity(password));
	}
	
	@Override
	public Optional<LoginPasswordOfUser> find(String userId) {
		
		String query 	= " select p "
						+ " from SgwdtUserLoginPassword p"
						+ " where p.userId =:userId ";
		
		val entities = this.queryProxy().query(query, SgwdtUserLoginPassword.class)
				.setParameter("userId", userId)
				.getSingle();
		
		if(!entities.isPresent()) {
			return Optional.empty();
		}
		
		val changeLog = getChangeLog(userId);
		
		return Optional.of(entities.get().toDomain(changeLog));
	}
	
	private List<PasswordChangeLogDetail> getChangeLog(String userId){
		
		String query 	= " select l "
						+ " from SgwdtUserLoginPasswordChangeLog l"
						+ " where l.pk.userId =:userId ";
		
		return this.queryProxy().query(query, SgwdtUserLoginPasswordChangeLog.class)
				.setParameter("userId", userId)
				.getList(rec -> rec.toDomain());
	}
	
	@Override
	public void delete(String userId) {
		
		String query1 	= " delete "
						+ " from SGWDT_USER_LOGIN_PASSWORD "
						+ " where USER_ID = @userId1 ";
		new NtsStatement(query1, this.jdbcProxy())
			.paramString("userId1", userId)
			.execute();
		
		String query2 	= " delete "
						+ " from SGWDT_USER_LOGIN_PASSWORD_CHANGE_LOG "
						+ " where USER_ID = @userId2 ";
		new NtsStatement(query2, this.jdbcProxy())
			.paramString("userId2", userId)
			.execute();
	}
}
