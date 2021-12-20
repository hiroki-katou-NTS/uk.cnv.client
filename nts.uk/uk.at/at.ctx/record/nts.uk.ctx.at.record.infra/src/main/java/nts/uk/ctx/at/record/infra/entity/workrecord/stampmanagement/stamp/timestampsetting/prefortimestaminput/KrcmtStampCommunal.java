package nts.uk.ctx.at.record.infra.entity.workrecord.stampmanagement.stamp.timestampsetting.prefortimestaminput;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Customizer;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.CorrectionInterval;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.DisplaySettingsStampScreen;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.NumberAuthenfailures;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ResultDisplayTime;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SettingDateTimeColorOfStampScreen;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampSetCommunal;
import nts.uk.ctx.at.shared.dom.common.color.ColorCode;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * @author chungnt
 *UKDesign.データベース.ER図.就業.勤務実績.<<core>> 勤務実績.打刻管理.打刻設定.打刻入力の前準備.KRCMT_STAMP_COMMUNAL
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="KRCMT_STAMP_COMMUNAL")
@Customizer(KrcmtStampCommunalCustomizer.class)
public class KrcmtStampCommunal extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 会社ID									
	 */
	@Id
	@Basic(optional = false)
	@Column(name = "CID")
	public String cid;

	/**
	 * 打刻画面のサーバー時刻補正間隔
	 */
	@Basic(optional = false)
	@Column(name = "CORRECTION_INTERVAL")
	public int correctionInterval;
	
	/**
	 * 打刻結果自動閉じる時間
	 */
	@Basic(optional = false)
	@Column(name = "RESULT_DISPLAY_TIME")
	public int resultDisplayTime;
	
	/**
	 * 文字色
	 */
	@Basic(optional = false)
	@Column(name = "TEXT_COLOR")
	public String textColor;
	
	/**
	 * 氏名選択利用する
	 */
	@Basic(optional = false)
	@Column(name = "NAME_SELECT_ART")
	public boolean nameSelectArt;
	
	/**
	 * パスワード必須区分
	 */
	@Basic(optional = false)
	@Column(name = "PASSWORD_REQUIRED_ART")
	public boolean passwordRequiredArt;
	
	/**
	 * 社員コード認証利用するか
	 */
	@Basic(optional = false)
	@Column(name = "EMPLOYEE_AUTHC_USE_ART")
	public boolean employeeAuthcUseArt;
	
	/**
	 * 指認証失敗回数
	 */
	@Basic(optional = true)
	@Column(name = "AUTHC_FAIL_CNT")
	public Integer authcFailCnt;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "krcmtStampCommunal",  orphanRemoval = true)
	public List<KrcmtStampPageLayout> listKrcmtStampPageLayout;
	
	@Override
	protected Object getKey() {
		return this.cid;
	}
	
	@PreUpdate
    private void setUpdateContractInfo() {
		this.contractCd = AppContexts.user().contractCode();
	}
	
	public static KrcmtStampCommunal toEntity(StampSetCommunal domain){
		return new KrcmtStampCommunal(
				domain.getCid(), 
				domain.getDisplaySetStampScreen().getCorrectionInterval().v(), 
				domain.getDisplaySetStampScreen().getResultDisplayTime().v(), 
				domain.getDisplaySetStampScreen().getSettingDateTimeColor().getTextColor().v(), 
				domain.isNameSelectArt(), 
				domain.isPasswordRequiredArt(), 
				domain.isEmployeeAuthcUseArt(), 
				domain.getAuthcFailCnt().isPresent()?domain.getAuthcFailCnt().get().v():null,
				domain.getLstStampPageLayout().stream().map(c-> KrcmtStampPageLayout.toEntity(c, domain.getCid(), 0)).collect(Collectors.toList()));
	}
	
	public StampSetCommunal toDomain(){
		return new StampSetCommunal(
					this.cid,
					new DisplaySettingsStampScreen(
						new CorrectionInterval(this.correctionInterval), 
						new SettingDateTimeColorOfStampScreen(
							new ColorCode(this.textColor)),
						new ResultDisplayTime(this.resultDisplayTime)),
					this.listKrcmtStampPageLayout.stream().map(c->c.toDomain()).collect(Collectors.toList()),
					this.nameSelectArt,
					this.passwordRequiredArt,
					this.employeeAuthcUseArt,
					this.authcFailCnt == null ? Optional.empty() : Optional.of(new NumberAuthenfailures(this.authcFailCnt))
				);
	}

}
