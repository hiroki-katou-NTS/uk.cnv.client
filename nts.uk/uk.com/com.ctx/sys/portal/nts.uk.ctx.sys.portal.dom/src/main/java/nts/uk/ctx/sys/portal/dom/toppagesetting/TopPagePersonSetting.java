package nts.uk.ctx.sys.portal.dom.toppagesetting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.uk.ctx.sys.portal.dom.enums.MenuClassification;
import nts.uk.ctx.sys.portal.dom.enums.System;

/**
 * The Class TopPagePersonSetting.
 * Domain 個人別トップページ設定
 */
@Getter
@Builder
@AllArgsConstructor
public class TopPagePersonSetting extends TopPageSettings implements DomainAggregate {

	/**
	 * 社員ID
	 */
	private String employeeId;
	
	public TopPagePersonSetting(
			String employeeId, 
			LoginMenuCode loginMenuCode,
			TopMenuCode topMenuCode,
			MenuClassification menuClassification, 
			System system) {
		super(topMenuCode, new MenuLogin(system, menuClassification, loginMenuCode));
		this.employeeId = employeeId;
	}
	
	public static TopPagePersonSetting createFromMemento(MementoGetter memento) {
		TopPagePersonSetting domain = new TopPagePersonSetting();
		domain.getMemento(memento);
		return domain;
	}
	
	public void getMemento(MementoGetter memento) {
		this.employeeId = memento.getEmployeeId();
		this.topMenuCode = new TopMenuCode(memento.getTopMenuCode());
		this.menuLogin = new MenuLogin(
				EnumAdaptor.valueOf(memento.getSystem(), System.class),
				EnumAdaptor.valueOf(memento.getMenuClassification(), MenuClassification.class),
				new LoginMenuCode(memento.getLoginMenuCode()));
	}
	
	public void setMemento(MementoSetter memento) {
		memento.setEmployeeId(employeeId);
		memento.setSystem(menuLogin.getSystem().value);
		memento.setMenuClassification(menuLogin.getMenuClassification().value);
		memento.setLoginMenuCode(menuLogin.getLoginMenuCode().v());
		memento.setTopMenuCode(topMenuCode.v());
	}
	
	private TopPagePersonSetting() {}

	public static interface MementoSetter {
		void setEmployeeId(String employeeId);
		void setSystem(int system);
		void setMenuClassification(int menuClassification);
		void setLoginMenuCode(String loginMenuCode);
		void setTopMenuCode(String topMenuCode);
	}
	
	public static interface MementoGetter {
		String getEmployeeId();
		int getSystem();
		int getMenuClassification();
		String getLoginMenuCode();
		String getTopMenuCode();
	}
}
