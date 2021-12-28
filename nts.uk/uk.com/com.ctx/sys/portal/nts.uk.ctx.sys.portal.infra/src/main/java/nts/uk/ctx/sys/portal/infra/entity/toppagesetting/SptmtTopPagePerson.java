package nts.uk.ctx.sys.portal.infra.entity.toppagesetting;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.sys.portal.dom.enums.MenuClassification;
import nts.uk.ctx.sys.portal.dom.enums.System;
import nts.uk.ctx.sys.portal.dom.toppagesetting.LoginMenuCode;
import nts.uk.ctx.sys.portal.dom.toppagesetting.SwitchingDate;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopMenuCode;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPagePersonSetting;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * The Class SptmtTopPagePerson.
 * Entity 個人別トップページ設定
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SPTMT_TOPPAGE_PERSON")
@EqualsAndHashCode(callSuper = true)
public class SptmtTopPagePerson extends UkJpaEntity implements TopPagePersonSetting.MementoGetter, TopPagePersonSetting.MementoSetter {

	/** The pk. */
	@EmbeddedId
	private SptmtTopPagePersonPK pk;
	
	/** The version. */
    @Version
    @Column(name = "EXCLUS_VER")
    private long version;

    /** The contract cd. */
    @Basic(optional = false)
    @Column(name = "CONTRACT_CD")
    private String contractCd;
    
    /**  Company ID. */
	@Column(name = "CID")
	private String companyID;
	
	@Column(name = "TOP_MENU_CD")
	private String topMenuCode;
	
	@Column(name = "LOGIN_MENU_CD")
	private String loginMenuCode;
	
	@Column(name = "LOGIN_SYSTEM")
	private int system;
	
	@Column(name = "LOGIN_MENU_CLS")
	private int menuClassification;
    
	@Override
	protected Object getKey() {
		return pk;
	}

	@Override
	public void setEmployeeId(String employeeId) {
		if (this.pk == null) {
			this.pk = new SptmtTopPagePersonPK();
		}
		this.pk.setEmployeeId(employeeId);
	}

	@Override
	public String getEmployeeId() {
		if (this.pk != null) {
			return this.pk.employeeId;
		}
		return null;
	}

	public TopPagePersonSetting toDomain() {
		return new TopPagePersonSetting(
				this.getEmployeeId(),
				new LoginMenuCode(this.loginMenuCode),
				new TopMenuCode(this.topMenuCode),
				EnumAdaptor.valueOf(this.menuClassification, MenuClassification.class), 
				EnumAdaptor.valueOf(this.system, System.class));
	}

	@Override
	public void setSystem(int system) {
		this.system = system;
	}

	@Override
	public void setMenuClassification(int menuClassification) {
		this.menuClassification = menuClassification;
	}

	@Override
	public void setLoginMenuCode(String loginMenuCode) {
		this.loginMenuCode = loginMenuCode;
	}

	@Override
	public int getSystem() {
		return this.system;
	}

	@Override
	public int getMenuClassification() {
		return this.menuClassification;
	}

	@Override
	public String getLoginMenuCode() {
		return this.loginMenuCode;
	}
}
