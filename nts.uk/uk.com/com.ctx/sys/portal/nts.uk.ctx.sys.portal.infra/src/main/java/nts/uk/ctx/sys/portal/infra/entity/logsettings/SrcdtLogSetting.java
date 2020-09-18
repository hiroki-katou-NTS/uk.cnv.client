package nts.uk.ctx.sys.portal.infra.entity.logsettings;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.uk.ctx.sys.portal.dom.logsettings.LogSetting;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * Entity ログ設定
 */
@Data
@Entity
@Table(name = "SRCDT_LOG_SETTING")
@EqualsAndHashCode(callSuper = true)
public class SrcdtLogSetting extends UkJpaEntity implements Serializable, LogSetting.MementoGetter, LogSetting.MementoSetter {
	private static final long serialVersionUID = 1L;
	
	// column 排他バージョン
	@Version
	@Column(name = "EXCLUS_VER")
	private long version;

	@EmbeddedId
	public SrcdtLogSettingPK srcdtLogSettingPK;

	@Basic(optional = false)
	@Column(name = "CONTRACT_CD")
	public String contractCd;
	
	// column メニュー分類
	@Basic(optional = false)
	@Column(name = "MENU_ATR")
	public Integer menuClassification;

	// column ログイン履歴記録
	@Basic(optional = false)
	@Column(name = "LOGIN_LOG_USE_ATR")
	public Integer loginHistoryRecord;

	// column 修正履歴（データ）記録
	@Basic(optional = false)
	@Column(name = "STARTUP_LOG_USE_ATR")
	public Integer startHistoryRecord;

	// column 起動履歴記録
	@Basic(optional = false)
	@Column(name = "UPDATE_LOG_USE_ATR")
	public Integer updateHistoryRecord;

	@Override
	protected Object getKey() {
		return this.srcdtLogSettingPK;
	}
	
	@Override
	public void setSystem(int system) {
		if (this.srcdtLogSettingPK == null) {
			this.srcdtLogSettingPK = new SrcdtLogSettingPK();
		}
		this.srcdtLogSettingPK.setSystem(system);
	}

	@Override
	public void setProgramId(String programId) {
		if (this.srcdtLogSettingPK == null) {
			this.srcdtLogSettingPK = new SrcdtLogSettingPK();
		}
		this.srcdtLogSettingPK.setProgramId(programId);
	}

	@Override
	public void setCompanyId(String companyId) {
		if (this.srcdtLogSettingPK == null) {
			this.srcdtLogSettingPK = new SrcdtLogSettingPK();
		}
		this.srcdtLogSettingPK.setCid(companyId);
	}

	@Override
	public Integer getSystem() {
		if (this.srcdtLogSettingPK != null) {
			return this.srcdtLogSettingPK.getSystem();
		}
		return 0;
	}

	@Override
	public String getProgramId() {
		if (this.srcdtLogSettingPK != null) {
			return this.srcdtLogSettingPK.getProgramId();
		}
		return null;
	}
	
	@Override
	public String getCompanyId() {
		if (this.srcdtLogSettingPK != null) {
			return this.srcdtLogSettingPK.getCid();
		}
		return null;
	}

}
