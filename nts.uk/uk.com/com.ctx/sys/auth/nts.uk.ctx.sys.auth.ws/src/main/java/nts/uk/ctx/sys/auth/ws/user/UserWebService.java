package nts.uk.ctx.sys.auth.ws.user;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.sys.auth.app.find.user.UserAuthDto;
import nts.uk.ctx.sys.auth.app.find.user.UserDto;
import nts.uk.ctx.sys.auth.app.find.user.UserFinder;
import nts.uk.ctx.sys.auth.app.find.user.UserKeyDto;

@Path("ctx/sys/auth/user")
@Produces("application/json")
public class UserWebService extends WebService {
	
	@Inject
	private UserFinder userFinder;
	
	@POST
	@Path("searchUser")
	public List<UserAuthDto> searchUser(String userNameID) {
		return this.userFinder.searchUser(userNameID);
	}
	
	@POST
	@Path("getlistUser")
	public List<UserDto> getListUser() {
		return this.userFinder.getAllUser();
	}
	
	@POST
	@Path("findByKey")
	public List<UserDto> FindByKey(UserKeyDto userKeyDto) {
		return this.userFinder.findByKey(userKeyDto);
	}
}