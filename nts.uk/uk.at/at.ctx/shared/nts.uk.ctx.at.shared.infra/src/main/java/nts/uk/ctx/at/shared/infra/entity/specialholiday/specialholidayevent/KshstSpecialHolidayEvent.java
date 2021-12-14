package nts.uk.ctx.at.shared.infra.entity.specialholiday.specialholidayevent;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
//import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.specialholiday.specialholidayevent.SpecialHolidayEvent;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSHMT_HDSPEV")
// 事象に対する特別休暇
public class KshstSpecialHolidayEvent extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KshstSpecialHolidayEventPK pk;

	/* 上限日数の設定方法 */
	@Column(name = "MAX_NUMBER_DAYS_TYPE")
	public int maxNumberDayType;

	/* 固定上限日数 */
	@Column(name = "FIXED_DAY_GRANT")
	public Integer fixedDayGrant;

	/* 忌引とする */
	@Column(name = "MAKE_INVITATION_ATR")
	public int makeInvitation;

	/* 休日を取得日に含める */
	@Column(name = "INCLUDE_HOLIDAYS_ATR")
	public int includeHolidays;

	/* 年齢条件 */
	@Column(name = "AGE_LIMIT_ATR")
	public int ageLimit;

	/* 性別条件 */
	@Column(name = "GENDER_RESTRICT_ATR")
	public int genderRestrict;

	/* 雇用条件 */
	@Column(name = "RESTRICT_EMP_ATR")
	public int restrictEmployment;

	/* 分類条件 */
	@Column(name = "RESTRICT_CLS_ATR")
	public int restrictClassification;

	/* 性別 */
	@Column(name = "GENDER_ATR")
	public Integer gender;

	/* 年齢範囲 */
	@Column(name = "AGE_RANGE_LOWER_LIMIT")
	public Integer ageRangeLowerLimit;

	/* 年齢範囲 */
	@Column(name = "AGE_RANGE_HIGHER_LIMIT")
	public Integer ageRangeHigherLimit;

	/* 年齢基準年区分 */
	@Column(name = "AGE_STANDARD_YEAR_TYPE")
	public int ageStandard;

	/* 年齢基準月日 */
	@Column(name = "AGE_STANDARD_BASE_DATE")
	public Integer ageStandardBaseDate;

	/* メモ */
	@Column(name = "MEMO")
	public String memo;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public void updateEntity(SpecialHolidayEvent domain) {
		this.maxNumberDayType = domain.getMaxNumberDay().value;
		this.fixedDayGrant = domain.getFixedDayGrant().v();
		this.makeInvitation = domain.getMakeInvitation().value;
		this.includeHolidays = domain.getIncludeHolidays().value;
		this.ageLimit = domain.getAgeLimit().value;
		this.genderRestrict = domain.getGenderRestrict().value;
		this.restrictEmployment = domain.getRestrictEmployment().value;
		this.restrictClassification = domain.getRestrictClassification().value;
		this.gender = domain.getGender().value;
		this.ageRangeLowerLimit = domain.getAgeLowerLimit();
		this.ageRangeHigherLimit = domain.getAgeRangeHigherLimit();
		this.ageStandard = domain.getAgeStandard().value;
		this.ageStandardBaseDate = domain.getAgeStandardBaseDate();
		this.memo = domain.getMemo().v();
	}

}
