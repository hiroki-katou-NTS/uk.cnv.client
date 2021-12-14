package nts.uk.ctx.at.shared.infra.entity.vacation.setting.compensatoryleave;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSHMT_HDCOM_CMP")
public class KshmtHdComCmp extends UkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KshmtHdComCmpPk pk;
	
	/** 管理区分 **/
	@Column(name = "MANAGE_ATR")
	public int manageAtr;
	
	/** 紐付け管理区分 **/
	@Column(name = "LINK_MNG_ATR")
	public int linkMngAtr;
	
	/** 休暇使用期限 **/
	@Column(name = "EXPIRATION_USE_SET")
	public int expirationUseSet;
	
	/** 先取り許可 **/
	@Column(name = "PREPAID_GET_ALLOW")
	public int prepaidGetAllow;
	
	/** 期限日の管理方法 **/
	@Column(name = "EXP_DATE_MNG_METHOD")
	public int expDateMngMethod;

	/** 代休経過月数 **/
	@Column(name = "EXP_CHECK_MONTH_NUMBER")
	public int expCheckMonthNumber;
	
	// TIME_MANAGE_ATR
	@Column(name = "TIME_MANAGE_ATR")
	public int timeManageAtr;
	
	/** 時間休暇消化単位 */
	@Column(name = "DIGESTION_UNIT")
	public int digestionUnit;
	
	/** 使用区分  - 代休管理設定.発生設定.代休発生に必要な残業時間 **/
	@Column(name = "OCCURR_OT_USE_ATR")
	public int occurrOtUseAtr;
	
	/** 時間区分 **/
	@Column(name = "OCCURR_OT_TIME_ATR")
	public int occurrOtTimeAtr;
	
	/** 一日の時間**/
	@Column(name = "DES_OT_ONEDAY_TIME")
	public int desOtOnedaytime;
	
	/** 半日の時間**/
	@Column(name = "DES_OT_HALFDAY_TIME")
	public int desOtHalfdaytime;
	
	/** 一定時間 **/
	@Column(name = "CERTAIN_OT_TIME")
	public int certainOtTime;
	
	/** 使用区分- 代休管理設定.発生設定.代休発生に必要な休日出勤時間 **/
	@Column(name = "OCCURR_HD_WORK_USE_ATR")
	public int occurrHdWorkUseAtr;
	
	/** 時間区分 -- 代休管理設定.発生設定.代休発生に必要な休日出勤時間.時間設定 **/ 
	@Column(name = "OCCURR_HD_WORK_TIME_ATR")
	public int occurrHdWorkTimeAtr;
	
	/** 一日の時間 --- 代休管理設定.発生設定.代休発生に必要な休日出勤時間.時間設定.指定時間**/
	@Column(name = "DES_HD_WORK_ONEDAY_TIME")
	public int desHdWorkOneDayTime;
	
	/** 半日の時間 --- 代休管理設定.発生設定.代休発生に必要な休日出勤時間.時間設定.指定時間**/ 
	@Column(name = "DES_HD_WORK_HALFDAY_TIME")
	public int desHdWorkHalfDayTime;
	
	/** 一定時間 --- 代休管理設定.発生設定.代休発生に必要な休日出勤時間.時間設定.一定時間 **/
	@Column(name = "CERTAIN_HD_WORK_TIME")
	public int certainHdWorkTime;
	
	@Override
	protected Object getKey() {
		return pk;
	}
	
	public static KshmtHdComCmp toEntity(CompensatoryLeaveComSetting domain){
		
		/*return new KshmtHdComCmp(
				new KshmtHdComCmpPk(domain.getCompanyId()), 
				domain.getIsManaged(), 
				domain.getLinkingManagementATR(),
				domain.getCompensatoryAcquisitionUse().getExpirationTime(),
				domain.getCompensatoryAcquisitionUse(), 
				domain.getCompensatoryAcquisitionUse().getTermManagement() , 
				domain.getCompensatoryAcquisitionUse().getDeadlCheckMonth(),
				domain.getCompensatoryDigestiveTimeUnit().getIsManageByTime(), 
				domain.getCompensatoryDigestiveTimeUnit().getDigestiveUnit(),
				domain.getCompensatoryOccurrenceSetting().getOvertimeHourRequired().isUseAtr(),
				domain.getCompensatoryOccurrenceSetting().getOvertimeHourRequired().getTimeSetting().getEnumTimeDivision().value,
				domain.getCompensatoryOccurrenceSetting().getOvertimeHourRequired().getTimeSetting().getDesignatedTime().,//
				domain.getCompensatoryOccurrenceSetting().getOvertimeHourRequired().getTimeSetting(), 
				certainOtTime, 
				occurrHdWorkUseAtr,
				occurrHdWorkTimeAtr,
				desHdWorkOneDayTime, 
				desHdWorkHalfDayTime, 
				certainHdWorkTime)*/
		return null;
	}
    //toDomain 
	//toEntity 
	
}
