package nts.uk.ctx.sys.portal.dom.toppagesetting;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import mockit.Mocked;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.sys.portal.dom.enums.MenuClassification;
import nts.uk.ctx.sys.portal.dom.enums.System;

public class TopPagePersonSettingTest {
	
	private static final String EMPLOYEE_ID = "employeeId";

	@Mocked
	private static TopPagePersonSettingDto mockDto = TopPagePersonSettingDto.builder()
		.employeeId(EMPLOYEE_ID)
		.topMenuCode("topMenuCode")
		.loginMenuCode("loginMenuCode")
		.switchingDate(0)
		.system(0)
		.menuClassification(0)
		.build();
	
	@Test
	public void testContructorAndGetter() {
		TopPagePersonSetting domain = new TopPagePersonSetting(
				EMPLOYEE_ID, 
				new LoginMenuCode("loginMenuCode"), 
				new TopMenuCode("topMenuCode"), 
				EnumAdaptor.valueOf(0, MenuClassification.class), 
				EnumAdaptor.valueOf(0, System.class),
				new SwitchingDate(0));
		assertThat(domain.getEmployeeId()).isEqualTo(EMPLOYEE_ID);
	}
	
	@Test
	public void testAllContructor() {
		TopPagePersonSetting domain = new TopPagePersonSetting("12");
		assertThat(domain.getEmployeeId()).isEqualTo("12");
	}
	
	@Test
	public void createFromMementoAndGetMemento() {
		//When
		TopPagePersonSetting domain = TopPagePersonSetting.createFromMemento(mockDto);
		//Then
		assertThat(domain.getEmployeeId()).isEqualTo(mockDto.getEmployeeId());
	}
	
	@Test
	public void testSetMemento() {
		//Given
		TopPagePersonSettingDto dto = TopPagePersonSettingDto.builder().build();
		TopPagePersonSetting domain = TopPagePersonSetting.createFromMemento(mockDto);
		
		//When
		domain.setMemento(dto);
		
		//Then
		assertThat(domain.getEmployeeId()).isEqualTo(mockDto.getEmployeeId());
	}
	
	@Test
	public void testMementoNull() {
		//Given
		TopPagePersonSettingDto dtoNull = TopPagePersonSettingDto.builder().build();
		
		//When
		TopPagePersonSetting domain = TopPagePersonSetting.createFromMemento(dtoNull);
		
		//Then
		assertThat(domain.getEmployeeId()).isEqualTo(null);
	}
	
	@Test
	public void testBuilder() {
		//When
		TopPagePersonSetting domain = TopPagePersonSetting.builder()
				.employeeId(EMPLOYEE_ID)
				.build();
		
		//Then
		assertThat(domain.getEmployeeId()).isEqualTo(EMPLOYEE_ID);
		
		String domain2 = TopPagePersonSetting.builder()
				.employeeId(EMPLOYEE_ID)
				.toString();
		assertThat(domain2.isEmpty()).isFalse();
	}
}
