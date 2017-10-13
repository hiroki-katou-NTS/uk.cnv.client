package nts.uk.ctx.at.request.infra.entity.application.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.common.Application;
import nts.uk.ctx.at.request.infra.entity.application.common.appapprovalphase.KrqdtAppApprovalPhase;
import nts.uk.ctx.at.request.infra.entity.application.lateorleaveearly.KrqdtAppLateOrLeave;
import nts.uk.ctx.at.request.infra.entity.application.stamp.KrqdpAppStamp;
import nts.uk.ctx.at.request.infra.entity.application.stamp.KrqdtAppStamp;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

@Entity
@Table(name = "KRQDT_APPLICATION")
@AllArgsConstructor
@NoArgsConstructor
public class KafdtApplication extends UkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@EmbeddedId
	public KafdtApplicationPK kafdtApplicationPK;
	
	@Version
	@Column(name="EXCLUS_VER")
	public Long version;
	
	/**
	 * 申請理由ID
	 */
	@Column(name="APP_REASON_ID")
	public String appReasonId;
	/**
	 * 事前事後区分
	 */
	@Column(name="PRE_POST_ATR")
	public int prePostAtr; 

	/**
	 * 入力日
	 */
	@Column(name="INPUT_DATE")
	public GeneralDate inputDate; 
	
	/**
	 * 入力者
	 */
	@Column(name="ENTERED_PERSON_SID")
	public String enteredPersonSID; 
	/**
	 * 差戻し理由
	 */
	@Column(name="REASON_REVERSION")
	public String reversionReason; 
	
	/**
	 * 申請日
	 */
	@Column(name="APP_DATE")
	public GeneralDate applicationDate; 
	
	///
	/**
	 * 申請理由
	 */
	@Column(name="APP_REASON")
	public String applicationReason;
	/**
	 * 申請種類
	 */
	@Column(name="APP_TYPE")
	public int applicationType;
	/**
	 * 申請者
	 */
	@Column(name="APPLICANTS_SID")
	public String  applicantSID;
	/**
	 * 予定反映不可理由
	 */
	@Column(name="REFLECT_PLAN_SCHE_REASON")
	public int reflectPlanScheReason;
    /**
     * 予定反映日時
     */
	@Column(name="REFLECT_PLAN_TIME")
	public GeneralDate reflectPlanTime;
	/**
	 * 予定反映状態
	 */
	@Column(name="REFLECT_PLAN_STATE")
	public int reflectPlanState;
	/**
	 * 予定強制反映
	 */
	@Column(name="REFLECT_PLAN_ENFORCE_ATR")
	public int reflectPlanEnforce;
	/**
	 * 実績反映不可理由
	 */
	@Column(name="REFLECT_PER_SCHE_REASON")
	public int  reflectPerScheReason;
	/**
	 * 実績反映日時
	 */
	@Column(name="REFLECT_PER_TIME")
	public GeneralDate reflectPerTime;
	/**
	 * 実績反映状態
	 */
	@Column(name="REFLECT_PER_STATE")
	public int reflectPerState;
	/**
	 * 実績強制反映
	 */
	@Column(name="REFLECT_PER_ENFORCE_ATR")
	public int reflectPerEnforce;
	
	/**
	 * 申請終了日
	 */
	@Column(name="APP_START_DATE")
	public GeneralDate startDate;
	
	/**
	 * 申請開始日
	 */
	@Column(name="APP_END_DATE")
	public GeneralDate endDate;
	
	@OneToMany(mappedBy="application", cascade = CascadeType.ALL)
	public List<KrqdtAppApprovalPhase> appApprovalPhases;
	
	@OneToOne(targetEntity=KrqdtAppStamp.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@PrimaryKeyJoinColumns({
		@PrimaryKeyJoinColumn(name="CID",referencedColumnName="CID"),
		@PrimaryKeyJoinColumn(name="APP_ID",referencedColumnName="APP_ID")
	})
	public KrqdtAppStamp krqdtAppStamp;
	
	@OneToOne(targetEntity=KrqdtAppLateOrLeave.class, cascade = CascadeType.ALL, mappedBy = "kafdtApplication", orphanRemoval = true)
	public KrqdtAppLateOrLeave krqdtAppLateOrLeave;
	
	
	@Override
	protected Object getKey() {
		return kafdtApplicationPK;
	}
	
	private static final String SEPERATE_REASON_STRING = ":";
	public static KafdtApplication toEntity(Application domain) {
		String applicationReason = domain.getApplicationReason().v();
		String appReasonID = "";
		String appReason = "";
		if (applicationReason.indexOf(SEPERATE_REASON_STRING) != -1) {
			appReasonID = applicationReason.split(SEPERATE_REASON_STRING)[0];
			appReason = applicationReason.substring(appReasonID.length() + SEPERATE_REASON_STRING.length());
		}
		return new KafdtApplication(
			new KafdtApplicationPK(
				domain.getCompanyID(), 
				domain.getApplicationID()), 
			domain.getVersion(),
			appReasonID,
			domain.getPrePostAtr().value, 
			domain.getInputDate() , 
			domain.getEnteredPersonSID(),
			domain.getReversionReason().v(), 
			domain.getApplicationDate(), 
			appReason,
			domain.getApplicationType().value, 
			domain.getApplicantSID(), 
			domain.getReflectPlanScheReason().value,
			domain.getReflectPlanTime(), 
			domain.getReflectPlanState().value, 
			domain.getReflectPlanEnforce().value,
			domain.getReflectPerScheReason().value, 
			domain.getReflectPerTime(), domain.getReflectPerState().value,
			domain.getReflectPerEnforce().value, 
			domain.getStartDate(), 
			domain.getEndDate(),
			domain.getListPhase().stream().map(c -> KrqdtAppApprovalPhase.toEntity(c)).collect(Collectors.toList()),
			null,
			null);
	}
}
