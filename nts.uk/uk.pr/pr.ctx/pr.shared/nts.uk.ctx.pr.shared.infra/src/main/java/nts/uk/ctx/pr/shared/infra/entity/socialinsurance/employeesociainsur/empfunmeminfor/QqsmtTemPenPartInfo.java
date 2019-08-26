package nts.uk.ctx.pr.shared.infra.entity.socialinsurance.employeesociainsur.empfunmeminfor;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.empfunmeminfor.EmPensionFundPartiPeriodInfor;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.com.time.calendar.period.DatePeriod;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;


/**
* 厚生年金基金加入期間情報
*/
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "QQSMT_TEM_PEN_PART_INFO")
public class QqsmtTemPenPartInfo extends UkJpaEntity implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    /**
    * ID
    */
    @EmbeddedId
    public QqsmtTemPenPartInfoPk temPenPartInfoPk;
    
    /**
    * 開始日
    */
    @Basic(optional = false)
    @Column(name = "START_DATE")
    public GeneralDate startDate;
    
    /**
    * 終了日
    */
    @Basic(optional = false)
    @Column(name = "END_DATE")
    public GeneralDate endDate;
    
    /**
    * 基金加入員番号
    */
    @Basic(optional = false)
    @Column(name = "MEMBER_NUMBER")
    public String memberNumber;
    
    @Override
    protected Object getKey()
    {
        return temPenPartInfoPk;
    }

    public EmPensionFundPartiPeriodInfor toDomain() {
        DatePeriod period = new DatePeriod(this.startDate,endDate);
        DateHistoryItem historyItem = new DateHistoryItem(this.temPenPartInfoPk.historyId,period);
        return new EmPensionFundPartiPeriodInfor(this.temPenPartInfoPk.employeeId,historyItem);

    }


    public static QqsmtTemPenPartInfo toEntity(EmPensionFundPartiPeriodInfor domain) {
        return null;
    }

}
