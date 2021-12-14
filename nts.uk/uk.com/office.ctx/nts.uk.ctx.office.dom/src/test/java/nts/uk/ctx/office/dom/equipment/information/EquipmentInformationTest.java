package nts.uk.ctx.office.dom.equipment.information;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;

@RunWith(JMockit.class)
public class EquipmentInformationTest {

	@Test
	public void getters() {
		// when
		EquipmentInformation domain = EquipmentInformationTestHelper.mockDomain(null);
		
		// then
		NtsAssert.invokeGetters(domain);
	}
	
	@Test
	public void testMemento() {
		// given
		EquipmentInformationDto dto = new EquipmentInformationDto();
		EquipmentInformation domain = EquipmentInformationTestHelper.mockDomain(null);
		
		// when
		domain.setMemento(dto);
		
		// then
		assertThat(dto.getCode()).isEqualTo(domain.getEquipmentCode().v());
	}
	
	@Test
	public void testRemarkNotNull() {
		// when
		EquipmentInformation domain = EquipmentInformationTestHelper.mockDomain("remark");
		
		// then
		assertThat(domain.getEquipmentRemark().map(EquipmentRemark::v)).isEqualTo(Optional.of("remark"));
	}
	
	/**
	 * [1] 有効期限内か = true
	 */
	@Test
	public void testIsValid() {
		// when
		EquipmentInformation domain = EquipmentInformationTestHelper.mockDomain(null);
		
		// then
		assertThat(domain.isValid(GeneralDate.today())).isTrue();
	}
	
	/**
	 * [1] 有効期限内か = false
	 */
	@Test
	public void testIsNotValid() {
		// when
		EquipmentInformation domain = EquipmentInformationTestHelper.mockDomain(null);
		
		// then
		assertThat(domain.isValid(GeneralDate.today().addDays(1))).isFalse();
	}
}
