package nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination;

import java.util.Optional;

import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.EmployeeMailAddressImport;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.PersonalMailAddressImport;
import nts.uk.ctx.sys.env.dom.mailserver.MailAddress;
import nts.uk.shr.com.enumcommon.NotUseAtr;

public class MailDestinationFunctionManageTestHelper {
	
	public static final String CID = "cid";
	public static final String SID = "sid";
	public static final MailAddress MAIL_ADDRESS = new MailAddress("mail");

	public static MailDestinationFunctionManage mockDomain(FunctionType functionId) {
		NotUseAtr notUseAtr = NotUseAtr.NOT_USE;
		return new MailDestinationFunctionManage(functionId, notUseAtr, notUseAtr, notUseAtr, notUseAtr);
	}
	
	public static EmployeeMailAddressImport mockEmployeeMailAddress(boolean hasMail) {
		Optional<MailAddress> optMailAddress = hasMail ? Optional.of(MAIL_ADDRESS) : Optional.empty();
		return new EmployeeMailAddressImport(SID, optMailAddress, optMailAddress);
	}
	
	public static PersonalMailAddressImport mockPersonalMailAddress(boolean hasMail) {
		Optional<MailAddress> optMailAddress = hasMail ? Optional.of(MAIL_ADDRESS) : Optional.empty();
		return new PersonalMailAddressImport(SID, optMailAddress, optMailAddress);
	}
}
