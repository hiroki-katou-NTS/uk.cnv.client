package nts.uk.ctx.office.dom.equipment.data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import nts.uk.ctx.office.dom.equipment.data.domainservice.InsertUsageRecordDomainServiceTest;
import nts.uk.ctx.office.dom.equipment.data.domainservice.UpdateUsageRecordDomainServiceTest;

@RunWith(Suite.class)
@SuiteClasses({
	EquipmentDataTest.class, 
	ResultDataTest.class,
	RegisterResultTest.class,
	InsertUsageRecordDomainServiceTest.class,
	UpdateUsageRecordDomainServiceTest.class
})
public class EquipmentDataTestSuite {

}