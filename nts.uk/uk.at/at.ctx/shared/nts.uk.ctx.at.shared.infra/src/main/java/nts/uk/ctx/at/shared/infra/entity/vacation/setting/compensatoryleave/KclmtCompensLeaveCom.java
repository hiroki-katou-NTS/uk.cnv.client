/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.entity.vacation.setting.compensatoryleave;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.common.TimeOfDay;
import nts.uk.ctx.at.shared.dom.vacation.setting.ApplyPermission;
import nts.uk.ctx.at.shared.dom.vacation.setting.ExpirationTime;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.TimeDigestiveUnit;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CertainPeriodOfTime;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryAcquisitionUse;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryDigestiveTimeUnit;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.DeadlCheckMonth;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EnumTimeDivision;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.HolidayWorkHourRequired;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.OvertimeHourRequired;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.SubstituteHolidaySetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.TermManagement;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.TimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.DesignatedTime;
import nts.uk.ctx.at.shared.dom.worktime.common.OneDayTime;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The Class KclmtCompensLeaveCom.
 */
@Setter
@Getter
@Entity
@Table(name = "KSHMT_HDCOM_CMP")
public class KclmtCompensLeaveCom extends ContractUkJpaEntity implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** 会社ID */
    @Id
    @Column(name = "CID")
    public String cid;

    /** 管理区分 */
    @Column(name = "MANAGE_ATR")
    public boolean manageAtr;
    
    /** 紐付け管理区分 */
    @Column(name = "LINK_MNG_ATR")
    public boolean linkMngAtr;
    
    /** 代休管理設定.取得と使用方法.休暇使用期限 */
    @Column(name = "EXPIRATION_USE_SET")
    public int expirationUseSet;
    
    /** 代休管理設定.取得と使用方法.先取り許可 */
    @Column(name = "PREPAID_GET_ALLOW")
    public int prepaidGetAllow;
    
    /** 代休管理設定.取得と使用方法.期限日の管理方法 */
    @Column(name = "EXP_DATE_MNG_METHOD")
    public int expDateMngMethod;
    
    /** 代休管理設定.取得と使用方法.代休期限チェック月数 */
    @Column(name = "EXP_CHECK_MONTH_NUMBER")
    public int expCheckMonthNumber;
    
    /** 代休管理設定.時間代休の消化単位.管理区分 */
    @Column(name = "TIME_MANAGE_ATR")
    public boolean timeManageAtr;
    
    /** 代休管理設定.時間代休の消化単位.消化単位 */
    @Column(name = "DIGESTION_UNIT")
    public int digestionUnit;
    
    /** 代休管理設定.発生設定.代休発生に必要な残業時間.使用区分 */ 
    @Column(name = "OCCURR_OT_USE_ATR")
    public boolean occurrOtUseAtr;
    
    /** 代休管理設定.発生設定.代休発生に必要な残業時間.時間設定.時間区分 */
    @Column(name = "OCCURR_OT_TIME_ATR")
    public boolean deadlCheckMonth;
    
    /** 代休管理設定.発生設定.代休発生に必要な残業時間.時間設定.指定時間.一日の時間 */
    @Column(name = "DES_OT_ONEDAY_TIME")
    public int desOtOneDayTime;
    
    /** 代休管理設定.発生設定.代休発生に必要な残業時間.時間設定.指定時間.半日の時間 */
    @Column(name = "DES_OT_HALFDAY_TIME")
    public int desOtHalfDayTime;
    
    /** 代休管理設定.発生設定.代休発生に必要な残業時間.時間設定.一定時間.一定時間 */
    @Column(name = "CERTAIN_OT_TIME")
    public int certainOtTime;
    
    /** 代休管理設定.発生設定.代休発生に必要な休日出勤時間.使用区分 */
    @Column(name = "OCCURR_HD_WORK_USE_ATR")
    public boolean occurrHDWorkUseAtr;
    
    /** 代休管理設定.発生設定.代休発生に必要な休日出勤時間.時間設定.時間区分 */
    @Column(name = "OCCURR_HD_WORK_TIME_ATR")
    public boolean occurrHDWorkTimeAtr;
    
    /** 代休管理設定.発生設定.代休発生に必要な休日出勤時間.時間設定.指定時間.一日の時間 */
    @Column(name = "DES_HD_WORK_ONEDAY_TIME")
    public int desHDWorkOneDayTime;
    
    /** 代休管理設定.発生設定.代休発生に必要な休日出勤時間.時間設定.指定時間.半日の時間 */
    @Column(name = "DES_HD_WORK_HALFDAY_TIME")
    public int desHDWorkHalfDayTime;
    
    /** 代休管理設定.発生設定.代休発生に必要な休日出勤時間.時間設定.一定時間.一定時間 */
    @Column(name = "CERTAIN_HD_WORK_TIME")
    public int cerTainHDWorkTime;
    
    public static KclmtCompensLeaveCom toEntity(CompensatoryLeaveComSetting domain) {
    	return new KclmtCompensLeaveCom(
    			domain.getCompanyId(),
    			domain.getIsManaged().value,
    			domain.getLinkingManagementATR().value, 
    			domain.getCompensatoryAcquisitionUse().getExpirationTime().value, 
    			domain.getCompensatoryAcquisitionUse().getPreemptionPermit().value,
    			domain.getCompensatoryAcquisitionUse().getTermManagement().value,
    			domain.getCompensatoryAcquisitionUse().getDeadlCheckMonth().value,
    			domain.getCompensatoryDigestiveTimeUnit().getIsManageByTime().value,
    			domain.getCompensatoryDigestiveTimeUnit().getDigestiveUnit().value ,
    			domain.getSubstituteHolidaySetting().getOvertimeHourRequired().isUseAtr()?1:0,
				domain.getSubstituteHolidaySetting().getOvertimeHourRequired().getTimeSetting().getEnumTimeDivision().value,
				domain.getSubstituteHolidaySetting().getOvertimeHourRequired().getTimeSetting().getDesignatedTime().getOneDayTime().v().intValue(), 
				domain.getSubstituteHolidaySetting().getOvertimeHourRequired().getTimeSetting().getDesignatedTime().getHalfDayTime().v().intValue(),
				domain.getSubstituteHolidaySetting().getOvertimeHourRequired().getTimeSetting().getCertainPeriodofTime().getCertainPeriodofTime().v().intValue(),
				domain.getSubstituteHolidaySetting().getHolidayWorkHourRequired().isUseAtr()?1:0,
				domain.getSubstituteHolidaySetting().getHolidayWorkHourRequired().getTimeSetting().getEnumTimeDivision().value,
				domain.getSubstituteHolidaySetting().getHolidayWorkHourRequired().getTimeSetting().getDesignatedTime().getOneDayTime().v().intValue(),
				domain.getSubstituteHolidaySetting().getHolidayWorkHourRequired().getTimeSetting().getDesignatedTime().getHalfDayTime().v().intValue(),
				domain.getSubstituteHolidaySetting().getHolidayWorkHourRequired().getTimeSetting().getCertainPeriodofTime().getCertainPeriodofTime().v().intValue());
	}
    
    public CompensatoryLeaveComSetting toDomain() {
    	// 取得と使用方法
    	CompensatoryAcquisitionUse compensatoryAcquisitionUse = new CompensatoryAcquisitionUse(
    			ExpirationTime.valueOf(this.expirationUseSet), 
    			ApplyPermission.valueOf(this.prepaidGetAllow), 
    			DeadlCheckMonth.valueOf(this.expCheckMonthNumber),
    			TermManagement.valueOf(this.expDateMngMethod));
    	
    	
    	
    	HolidayWorkHourRequired holidayWorkHourRequired = new HolidayWorkHourRequired(
    			this.occurrHDWorkUseAtr, 
				new TimeSetting(
						new CertainPeriodOfTime(new TimeOfDay(this.cerTainHDWorkTime)),
						new DesignatedTime(new OneDayTime(this.desHDWorkOneDayTime), new OneDayTime(this.desHDWorkHalfDayTime)),
						EnumTimeDivision.valueOf(BooleanUtils.toInteger(this.occurrHDWorkTimeAtr))));
    	OvertimeHourRequired overtimeHourRequired = new OvertimeHourRequired(
				this.occurrOtUseAtr, 
				new TimeSetting(
						new CertainPeriodOfTime(new TimeOfDay(this.certainOtTime)),
						new DesignatedTime(new OneDayTime(this.desOtOneDayTime), new OneDayTime(this.desOtHalfDayTime)),
						EnumTimeDivision.valueOf(BooleanUtils.toInteger(this.deadlCheckMonth))));
    	//発生設定
    	SubstituteHolidaySetting substituteHolidaySetting = new SubstituteHolidaySetting(holidayWorkHourRequired, overtimeHourRequired);
    	
    	// 時間代休の消化単位
    	CompensatoryDigestiveTimeUnit compensatoryDigestiveTimeUnit = new CompensatoryDigestiveTimeUnit(
    			ManageDistinct.valueOf(BooleanUtils.toInteger(this.timeManageAtr)), 
    			TimeDigestiveUnit.valueOf(this.digestionUnit));
    	return new CompensatoryLeaveComSetting(
    			this.cid, 
    			ManageDistinct.valueOf(BooleanUtils.toInteger(this.manageAtr)), 
    			compensatoryAcquisitionUse,
    			substituteHolidaySetting, 
    			compensatoryDigestiveTimeUnit, 
    			ManageDistinct.valueOf(BooleanUtils.toInteger(this.linkMngAtr)));
    }

   
//    /** The list occurrence. */
//    @JoinColumns(@JoinColumn(name = "CID", referencedColumnName = "CID", insertable = true, updatable = true))
//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.LAZY)
//    private List<KocmtOccurrenceSet> listOccurrence;

    /**
     * Instantiates a new kclmt compens leave com.
     */
    public KclmtCompensLeaveCom() {
    	super();
    }

    /**
     * Instantiates a new kclmt compens leave com.
     *
     * @param cid
     *            the cid
     */
    public KclmtCompensLeaveCom(String cid) {
        this.cid = cid;
    }

    /**
     * Instantiates a new kclmt compens leave com.
     *
     * @param cid
     *            the cid
     * @param manageAtr
     *            the manage atr
     */
    public KclmtCompensLeaveCom(String cid, Integer manageAtr) {
        this.cid = cid;
        this.manageAtr = BooleanUtils.toBoolean(manageAtr);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.arc.layer.infra.data.entity.JpaEntity#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cid != null ? cid.hashCode() : 0);
        return hash;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.arc.layer.infra.data.entity.JpaEntity#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        // not set
        if (!(object instanceof KclmtCompensLeaveCom)) {
            return false;
        }
        KclmtCompensLeaveCom other = (KclmtCompensLeaveCom) object;
        if ((this.cid == null && other.cid != null) || (this.cid != null && !this.cid.equals(other.cid))) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
     */
    @Override
    protected Object getKey() {
        return this.cid;
    }

	public KclmtCompensLeaveCom(String cid, int manageAtr, int linkMngAtr, int expirationUseSet, int prepaidGetAllow,
			int expDateMngMethod, int expCheckMonthNumber, int timeManageAtr, int digestionUnit, int occurrOtUseAtr,
			int deadlCheckMonth, int desOtOneDayTime, int desOtHalfDayTime, int certainOtTime, int occurrHDWorkUseAtr,
			int occurrHDWorkTimeAtr, int desHDWorkOneDayTime, int desHDWorkHalfDayTime, int cerTainHDWorkTime) {
		super();
		this.cid = cid;
		this.manageAtr = BooleanUtils.toBoolean(manageAtr);
		this.linkMngAtr = BooleanUtils.toBoolean(linkMngAtr);
		this.expirationUseSet = expirationUseSet;
		this.prepaidGetAllow = prepaidGetAllow;
		this.expDateMngMethod = expDateMngMethod;
		this.expCheckMonthNumber = expCheckMonthNumber;
		this.timeManageAtr = BooleanUtils.toBoolean(timeManageAtr);
		this.digestionUnit = digestionUnit;
		this.occurrOtUseAtr = BooleanUtils.toBoolean(occurrOtUseAtr);
		this.deadlCheckMonth = BooleanUtils.toBoolean(deadlCheckMonth);
		this.desOtOneDayTime = desOtOneDayTime;
		this.desOtHalfDayTime = desOtHalfDayTime;
		this.certainOtTime = certainOtTime;
		this.occurrHDWorkUseAtr = BooleanUtils.toBoolean(occurrHDWorkUseAtr);
		this.occurrHDWorkTimeAtr = BooleanUtils.toBoolean(occurrHDWorkTimeAtr);
		this.desHDWorkOneDayTime = desHDWorkOneDayTime;
		this.desHDWorkHalfDayTime = desHDWorkHalfDayTime;
		this.cerTainHDWorkTime = cerTainHDWorkTime;
	}
    

}
