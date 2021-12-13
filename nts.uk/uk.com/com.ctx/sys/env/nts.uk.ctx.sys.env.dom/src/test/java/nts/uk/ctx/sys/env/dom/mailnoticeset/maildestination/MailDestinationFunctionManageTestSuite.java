package nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.domainservice.GetEmailNotificationDomainServiceTest;


@RunWith(Suite.class)
@SuiteClasses({
	MailDestinationFunctionManageTest.class,
	GetEmailNotificationDomainServiceTest.class
})
public class MailDestinationFunctionManageTestSuite {

}
