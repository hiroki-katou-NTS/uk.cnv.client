package nts.uk.ctx.pr.core.infra.entity.socialinsurance.healthinsurance;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
* 等級毎報酬月額範囲: 主キー情報
*/
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class QpbmtHealthInsuranceGradePerRewardMonthlyRangePk implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    /**
    * 対象期間
    */
    @Basic(optional = false)
    @Column(name = "TARGET_START_YM")
    public int targetStartYm;
    
    /**
    * 対象期間
    */
    @Basic(optional = false)
    @Column(name = "TARGET_END_YM")
    public int targetEndYm;
    
    /**
    * 健康保険等級
    */
    @Basic(optional = false)
    @Column(name = "HEALTH_INSURANCE_GRADE")
    public int healthInsuranceGrade;
    
}
