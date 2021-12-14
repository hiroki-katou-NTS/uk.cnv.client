package nts.uk.ctx.at.record.infra.entity.workrecord.stampmanagement.stamp.timestampsetting.prefortimestaminput;


import java.io.Serializable;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.AudioType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonDisSet;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonName;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonNameSet;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonPositionNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonSettings;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ReservationArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SupportWplSet;
import nts.uk.ctx.at.shared.dom.common.color.ColorCode;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 打刻ボタン詳細設定
 * @author phongtq
 *
 */

@Entity
@NoArgsConstructor
@Table(name="KRCMT_STAMP_LAYOUT_DETAIL")
public class KrcmtStampLayoutDetail extends ContractUkJpaEntity implements Serializable{
private static final long serialVersionUID = 1L;
	
	@EmbeddedId
    public KrcmtStampLayoutDetailPk pk;
	
	/**
	 * 使用区分 0:使用しない 1:使用する
	 */
	@Column(name ="USE_ART")
	public int useArt;
	
	/** ボタン名称 */
	@Column(name ="BUTTON_NAME")
	public String buttonName;
	
	/**
	 * 予約区分 0:なし 1:予約 2:予約取消
	 */
	@Column(name ="RESERVATION_ART")
	public int reservationArt;
	
	/**
	 * 時刻変更区分 0:出勤 1:退勤 2:入門 3:退門 4:応援開始 5:応援終了 6:応援出勤 7:外出 8:戻り 9:臨時+応援出勤
	 * 10:臨時出勤 11:臨時退勤 12:PCログオン 13:PCログオフ
	 */
	@Column(name ="CHANGE_CLOCK_ART")
	public Integer changeClockArt;
	
	/**
	 * 計算区分変更対象 0:なし 1:早出 2:残業 3:休出 4:ﾌﾚｯｸｽ
	 */
	@Column(name ="CHANGE_CAL_ART")
	public Integer changeCalArt;
	
	/**
	 * 所定時刻セット区分 0:なし 1:直行 2:直帰
	 */
	@Column(name ="SET_PRE_CLOCK_ART")
	public Integer setPreClockArt;
	
	/**
	 * 勤務種類を半休に変更する 0:False 1:True
	 */
	@Column(name ="CHANGE_HALF_DAY")
	public Integer changeHalfDay;
	
	/**
	 * 外出区分 0:私用 1:公用 2:有償 3:組合
	 */
	@Column(name ="GO_OUT_ART")
	public Integer goOutArt;
	
	/** コメント色 */
	@Column(name ="TEXT_COLOR")
	public String textColor;
	
	/** コメント色 */
	@Column(name ="BACK_GROUND_COLOR")
	public String backGroundColor;
	
	/**
	 * 音声使用方法 0:なし 1:おはようございます 2:お疲れ様でした
	 */
	@Column(name ="AUDIO_TYPE")
	public int aidioType;
	
	/**
	 * 応援職場設定方法
	 * 0:打刻職場を利用
	 * 1:打刻時に選択
	 */
	@Column(name ="SUPPORT_WPL_SET")
	public Integer supportWplSet;
	
	@ManyToOne
    @PrimaryKeyJoinColumns({
    	@PrimaryKeyJoinColumn(name = "CID", referencedColumnName = "CID"),
    	@PrimaryKeyJoinColumn(name = "STAMP_MEANS", referencedColumnName = "STAMP_MEANS"),
    	@PrimaryKeyJoinColumn(name = "PAGE_NO", referencedColumnName = "PAGE_NO")
    })
	public KrcmtStampPageLayout krcmtStampPageLayout;
	
	@ManyToOne
	@JoinColumns({
    	@JoinColumn(name = "CID", insertable = false,  updatable = false),
    	@JoinColumn(name = "STAMP_MEANS", insertable = false,  updatable = false)
    })
	public KrcmtSrampPortal krcmtSrampPortal;
	
	@Override
	protected Object getKey() {
		return this.pk;
	}
	
	@PreUpdate
    private void setUpdateContractInfo() {
		this.contractCd = AppContexts.user().contractCode();
	}
	
	public KrcmtStampLayoutDetail(KrcmtStampLayoutDetailPk pk, int useArt, String buttonName, int reservationArt,
			Integer changeClockArt, Integer changeCalArt, Integer setPreClockArt, Integer changeHalfDay, Integer goOutArt,
			String textColor, String backGroundColor, int aidioType, Integer supportWplSet) {
		super();
		this.pk = pk;
		this.useArt = useArt;
		this.buttonName = buttonName;
		this.reservationArt = reservationArt;
		this.changeClockArt = changeClockArt;
		this.changeCalArt = changeCalArt;
		this.setPreClockArt = setPreClockArt;
		this.changeHalfDay = changeHalfDay;
		this.goOutArt = goOutArt;
		this.textColor = textColor;
		this.backGroundColor = backGroundColor;
		this.aidioType = aidioType;
		this.supportWplSet = supportWplSet;
	}
	
	public ButtonSettings toDomain(){
		StampType stampType = null;
		if(changeHalfDay != null && setPreClockArt != null && changeClockArt != null && changeCalArt != null) {
			stampType = StampType.getStampType(
					this.changeHalfDay == null ? null : this.changeHalfDay == 0 ? false : true  , 
					this.goOutArt == null ? null : EnumAdaptor.valueOf(this.goOutArt, GoingOutReason.class), 
					this.setPreClockArt == null ? null :EnumAdaptor.valueOf(this.setPreClockArt, SetPreClockArt.class), 
					this.changeClockArt == null ? null : EnumAdaptor.valueOf(this.changeClockArt, ChangeClockArt.class), 
					this.changeCalArt == null ? null : EnumAdaptor.valueOf(this.changeCalArt, ChangeCalArt.class));
		}
		 
		
		ButtonType buttonType = new ButtonType(
				EnumAdaptor.valueOf(this.reservationArt, ReservationArt.class), Optional.ofNullable(stampType));
		
		return new ButtonSettings(
				new ButtonPositionNo(pk.buttonPositionNo), 
				new ButtonDisSet(
						new ButtonNameSet(
								new ColorCode(this.textColor), 
								this.buttonName == null ? null : new ButtonName(this.buttonName)), 
						new ColorCode(this.backGroundColor)),buttonType 
				,
				EnumAdaptor.valueOf(this.useArt, NotUseAtr.class), 
				EnumAdaptor.valueOf(this.aidioType, AudioType.class),
				Optional.ofNullable(this.supportWplSet == null? null: SupportWplSet.valueOf(this.supportWplSet)));
	}
	
	public static KrcmtStampLayoutDetail toEntity(ButtonSettings settings, String companyId, Integer pageNo, int stampMeans) {
		Integer changeClockArt = null, changeCalArt = null, setPreClockArt = null, changeHalfDay = null,
				goOutArt = null;

		if (settings.getButtonType().getStampType().isPresent()) {
			if (settings.getButtonType().getStampType().get().getChangeClockArt() != null) {
				changeClockArt = settings.getButtonType().getStampType().get().getChangeClockArt().value;
			}

			if (settings.getButtonType().getStampType().get().getChangeCalArt() != null) {
				changeCalArt = settings.getButtonType().getStampType().get().getChangeCalArt().value;
			}

			if (settings.getButtonType().getStampType().get().getSetPreClockArt() != null) {
				setPreClockArt = settings.getButtonType().getStampType().get().getSetPreClockArt().value;
			}

			changeHalfDay = settings.getButtonType().getStampType().get().isChangeHalfDay() ? 1 : 0;

			if (settings.getButtonType().getStampType().get().getGoOutArt().isPresent()) {
				goOutArt = settings.getButtonType().getStampType().get().getGoOutArt().get().value;
			}
		}

		return new KrcmtStampLayoutDetail(
				new KrcmtStampLayoutDetailPk(companyId, stampMeans, pageNo, settings.getButtonPositionNo().v()),
				settings.getUsrArt().value,
				settings.getButtonDisSet().getButtonNameSet().getButtonName().isPresent()
						? settings.getButtonDisSet().getButtonNameSet().getButtonName().get().v()
						: null,
				settings.getButtonType().getReservationArt().value
				, changeClockArt
				, changeCalArt
				, setPreClockArt
				,changeHalfDay
				, goOutArt
				, settings.getButtonDisSet().getButtonNameSet().getTextColor().v()
				,settings.getButtonDisSet().getBackGroundColor().v()
				, settings.getAudioType().value
				, settings.getSupportWplSet().map(c->c.value).orElse(null));
	}
}
