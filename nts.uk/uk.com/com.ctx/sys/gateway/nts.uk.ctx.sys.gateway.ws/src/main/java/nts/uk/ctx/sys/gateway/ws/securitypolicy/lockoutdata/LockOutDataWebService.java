/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.gateway.ws.securitypolicy.lockoutdata;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.sys.gateway.app.command.securitypolicy.lockoutdata.AddLockOutDataCommand;
import nts.uk.ctx.sys.gateway.app.command.securitypolicy.lockoutdata.AddLockOutDataCommandHandler;
import nts.uk.ctx.sys.gateway.app.command.securitypolicy.lockoutdata.LockOutDataDeleteCommand;
import nts.uk.ctx.sys.gateway.app.command.securitypolicy.lockoutdata.LockOutDataDeleteCommandHandler;
import nts.uk.ctx.sys.gateway.app.find.securitypolicy.lockoutdata.LockOutDataUserFinder;
import nts.uk.ctx.sys.gateway.app.find.securitypolicy.lockoutdata.dto.LockOutDataDto;
import nts.uk.ctx.sys.gateway.app.find.securitypolicy.lockoutdata.dto.LockOutDataUserDto;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.acountlock.locked.SearchUser;

/**
 * The Class LockOutDataWebService.
 */
@Path("ctx/sys/gateway/securitypolicy/lockoutdata")
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class LockOutDataWebService extends WebService {
	@Inject
	private LockOutDataUserFinder lockOutDataUserFinder;

	/** The lock out data delete command handler. */
	@Inject
	private LockOutDataDeleteCommandHandler lockOutDataDeleteCommandHandler;
	@Inject
	private AddLockOutDataCommandHandler addUserToLockCommandHandler;

	/**
	 * Removes the lock out data.
	 *
	 * @param command
	 *            the command
	 */
	@POST
	@Path("remove")
	public void removeLockOutData(LockOutDataDeleteCommand command) {

		this.lockOutDataDeleteCommandHandler.handle(command);
	}
	
	/**
	 * Find lock out data.
	 *
	 * @return the list
	 */
	@POST
	@Path("find")
	public List<LockOutDataUserDto> findLockOutData() {

		 return this.lockOutDataUserFinder.findAll();
	}
	
	/**
	 * Find lock out by user id.
	 *
	 * @param UserId the user id
	 * @return the lock out data
	 */
	@POST
	@Path("findByUserId/{userId}")
	public LockOutDataUserDto findLockOutByUserId(@PathParam("userId") String userId) {
		
		 return this.lockOutDataUserFinder.findLockOutDataByUserId(userId);
	}

	/**
	 * Lockout User Data
	 * @author Nguyen Van Hanh
	 * @param command
	 * @return Integer the userId locked
	 */
	@POST
	@Path("lockUserByID")
	public SearchUser addLockOutData(AddLockOutDataCommand command){
		addUserToLockCommandHandler.handle(command);
		return lockOutDataUserFinder.findByUserId(command.getUserID());
	}
	
}
