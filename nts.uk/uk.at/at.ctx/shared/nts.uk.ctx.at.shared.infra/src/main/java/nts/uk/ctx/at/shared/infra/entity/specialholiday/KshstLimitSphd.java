package nts.uk.ctx.at.shared.infra.entity.specialholiday;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSHST_LIMIT_SPH")
public class KshstLimitSphd extends UkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
		/* 主キー */
		@EmbeddedId
		public KshstLimitSphdPK kshstLimitSphdPK;
		
		/* 性別制限 */
		@Column(name = "USE_GENDER")
		public int useGender;
		
		/* 雇用制限 */
		@Column(name = "USE_EMP")
		public int useEmployee;
		
		/* 分類制限 */
		@Column(name = "USE_CLS")
		public int useCls;
		
		/* 年齢制限 */
		@Column(name = "USE_AGE")
		public int useAge;
		
		/* 性別区分 */
		@Column(name = "GENDER_ATR")
		public int genderAtr;
		
		/* 年齢上限 */
		@Column(name = "LIMIT_AGE_FROM")
		public int limitAgeFrom;
		
		/* 年齢下限 */
		@Column(name = "LIMIT_AGE_TO")
		public int limitAgeTo;
		
		/* 年齢基準区分 */
		@Column(name = "AGE_CRITERIA_ATR")
		public int ageCriteriaAtr;
		
		/* 年齢基準年区分 */
		@Column(name = "AGE_BASE_YEAR_ATR")
		public int ageBaseYearAtr;
		
		/* 年齢基準日 */
		@Column(name = "AGE_BASE_DATES")
		public int ageBaseDates;
	

	@Override
	protected Object getKey() {
		// TODO Auto-generated method stub
		return null;
	}
}
