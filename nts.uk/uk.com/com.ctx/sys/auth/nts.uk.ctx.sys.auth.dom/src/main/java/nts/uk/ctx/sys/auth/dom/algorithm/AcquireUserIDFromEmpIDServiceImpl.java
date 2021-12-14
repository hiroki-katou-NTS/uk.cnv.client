package nts.uk.ctx.sys.auth.dom.algorithm;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.auth.dom.adapter.person.EmployeeBasicInforAuthImport;
import nts.uk.ctx.sys.auth.dom.adapter.person.PersonAdapter;
import nts.uk.ctx.sys.shared.dom.user.User;
import nts.uk.ctx.sys.shared.dom.user.UserRepository;

/**
 * 
 * @author hieult
 *
 */
@Stateless
public class AcquireUserIDFromEmpIDServiceImpl implements AcquireUserIDFromEmpIDService {

	@Inject
	private PersonAdapter personAdapter;

	@Inject
	private UserRepository userRepo;

	@Override
	public Optional<String> getUserIDByEmpID(String employeeID) {
		// imported（権限管理）「社員」を取得する
		// (lấy imported（権限管理）「employee」) Lấy Request No1
		Optional<EmployeeBasicInforAuthImport> optEmpInfor = personAdapter.getPersonInfor(employeeID);
		if (!optEmpInfor.isPresent()) {
			return Optional.empty();
		} else {
			// 紐付け先個人IDからユーザを取得する
			// (lấy userID từ kojinID tương ứng)
			Optional<User> user = userRepo.getByAssociatedPersonId(optEmpInfor.get().getPid());
			if (!user.isPresent()) {
				return Optional.empty();
			} else {
				String userID = user.get().getUserID();
				return Optional.of(userID);
			}
		}
	}

}
