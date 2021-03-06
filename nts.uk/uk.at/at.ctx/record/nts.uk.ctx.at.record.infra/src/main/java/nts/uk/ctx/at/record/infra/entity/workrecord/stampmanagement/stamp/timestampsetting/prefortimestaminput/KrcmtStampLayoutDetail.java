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

import org.apache.commons.lang3.BooleanUtils;

import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.AssignmentMethod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.AudioType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonDisSet;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonName;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonNameSet;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonPositionNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonSettings;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SupportWplSet;
import nts.uk.ctx.at.shared.dom.common.color.ColorCode;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * ???????????????????????????
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
	 * ???????????? 0:??????????????? 1:????????????
	 */
	@Column(name ="USE_ATR")
	public int useArt;
	
	/** ??????????????? */
	@Column(name ="BUTTON_NAME")
	public String buttonName;
	
	/**
	 * ?????????????????? 0:?????? 1:?????? 2:?????? 3:?????? 4:???????????? 5:???????????? 6:???????????? 7:?????? 8:?????? 9:??????+????????????
	 * 10:???????????? 11:???????????? 12:PC???????????? 13:PC????????????
	 */
	@Column(name ="CHANGE_CLOCK_ATR")
	public int changeClockArt;
	
	/**
	 * ???????????????????????? 0:?????? 1:?????? 2:?????? 3:?????? 4:???????????????
	 */
	@Column(name ="CHANGE_CAL_ATR")
	public int changeCalArt;
	
	/**
	 * ??????????????????????????? 0:?????? 1:?????? 2:??????
	 */
	@Column(name ="SET_PRE_CLOCK_ATR")
	public int setPreClockArt;
	
	/**
	 * ???????????????????????????????????? 0:False 1:True
	 */
	@Column(name ="CHANGE_HALF_DAY")
	public boolean changeHalfDay;
	
	/**
	 * ???????????? 0:?????? 1:?????? 2:?????? 3:??????
	 */
	@Column(name ="GO_OUT_ATR")
	public Integer goOutArt;
	
	/** ??????????????? */
	@Column(name ="TEXT_COLOR")
	public String textColor;
	
	/** ??????????????? */
	@Column(name ="BACK_GROUND_COLOR")
	public String backGroundColor;
	
	/**
	 * ?????????????????? 0:?????? 1:??????????????????????????? 2:?????????????????????
	 */
	@Column(name ="AUDIO_TYPE")
	public int aidioType;
	
	/**
	 * ????????????????????????
	 * 0:?????????????????????
	 * 1:??????????????????
	 */
	@Column(name ="SUPPORT_WPL_SET")
	public Integer supportWplSet;
	
	/**
	 * ??????????????????
	 * 0???????????????
	 * 1?????????????????????
	 */
	@Column(name = "TASK_CHOICE_ATR")
	public Integer taskChoiceArt;
	
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
	
	public KrcmtStampLayoutDetail(KrcmtStampLayoutDetailPk pk, int useArt, String buttonName,
			Integer changeClockArt, Integer changeCalArt, Integer setPreClockArt, Integer changeHalfDay, Integer goOutArt,
			String textColor, String backGroundColor, int aidioType, Integer supportWplSet, Integer taskChoiceArt) {
		super();
		this.pk = pk;
		this.useArt = useArt;
		this.buttonName = buttonName;
		this.changeClockArt = changeClockArt;
		this.changeCalArt = changeCalArt;
		this.setPreClockArt = setPreClockArt;
		this.changeHalfDay = BooleanUtils.toBoolean(changeHalfDay);
		this.goOutArt = goOutArt;
		this.textColor = textColor;
		this.backGroundColor = backGroundColor;
		this.aidioType = aidioType;
		this.supportWplSet = supportWplSet;
		this.taskChoiceArt = taskChoiceArt;
	}
	
	public ButtonSettings toDomain(){
		StampType stampType = null;
		stampType = StampType.getStampType(this.changeHalfDay,
				this.goOutArt == null ? null : EnumAdaptor.valueOf(this.goOutArt, GoingOutReason.class),
				EnumAdaptor.valueOf(this.setPreClockArt, SetPreClockArt.class),
				EnumAdaptor.valueOf(this.changeClockArt, ChangeClockAtr.class),
				EnumAdaptor.valueOf(this.changeCalArt, ChangeCalArt.class));
		 
//		
//		ButtonType buttonType = new ButtonType(
//				EnumAdaptor.valueOf(this.reservationArt, ReservationArt.class), Optional.ofNullable(stampType));
		
		return new ButtonSettings(
				new ButtonPositionNo(pk.buttonPositionNo), 
				EnumAdaptor.valueOf(this.useArt, NotUseAtr.class),
				new ButtonDisSet(
						new ButtonNameSet(new ColorCode(this.textColor),
								this.buttonName == null ? null : new ButtonName(this.buttonName)),
						new ColorCode(this.backGroundColor)),
				stampType, EnumAdaptor.valueOf(this.aidioType, AudioType.class),
				Optional.ofNullable(this.supportWplSet == null ? null : SupportWplSet.valueOf(this.supportWplSet)),
				Optional.ofNullable(this.taskChoiceArt == null ? null : AssignmentMethod.valueOf(this.taskChoiceArt)));
	}
	
	public static KrcmtStampLayoutDetail toEntity(ButtonSettings settings, String companyId, Integer pageNo, int stampMeans) {

		return new KrcmtStampLayoutDetail(
				new KrcmtStampLayoutDetailPk(companyId, stampMeans, pageNo, settings.getButtonPositionNo().v()),
				settings.getUsrArt().value,
				settings.getButtonDisSet().getButtonNameSet().getButtonName().isPresent()
						? settings.getButtonDisSet().getButtonNameSet().getButtonName().get().v()
						: null
				, settings.getType().getChangeClockArt().value
				, settings.getType().getChangeCalArt().value
				, settings.getType().getSetPreClockArt().value
				, settings.getType().isChangeHalfDay() ? 1 : 0
				, settings.getType().getGoOutArt().map(m -> m.value).orElse(null)
				, settings.getButtonDisSet().getButtonNameSet().getTextColor().v()
				,settings.getButtonDisSet().getBackGroundColor().v()
				, settings.getAudioType().value
				, settings.getSupportWplSet().map(c->c.value).orElse(null)
				, settings.getTaskChoiceArt().map(c -> c.value).orElse(null));
	}
}
