package nts.uk.ctx.pr.core.infra.entity.laborinsurance;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.pr.core.dom.laborinsurance.EmpInsurHis;
import nts.uk.ctx.pr.core.dom.laborinsurance.OccAccIsHis;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
* 労災保険履歴
*/
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "QPBMT_OCC_ACC_IS_HIS")
public class QpbmtOccAccIsHis extends UkJpaEntity implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    /**
    * ID
    */
    @EmbeddedId
    public QpbmtOccAccIsHisPk occAccIsHisPk;
    
    /**
    * 年月期間
    */
    @Basic(optional = false)
    @Column(name = "START_YEAR_MONTH")
    public int startDate;
    
    /**
    * 年月期間
    */
    @Basic(optional = false)
    @Column(name = "END_YEAR_MONTH")
    public int endDate;
    
    @Override
    protected Object getKey()
    {
        return occAccIsHisPk;
    }
    public static List<QpbmtOccAccIsHis> toEntity(OccAccIsHis domain) {
        List<QpbmtOccAccIsHis> qpbmtEmpInsurHisList = domain.getHistory().stream().map(item -> {
            return new QpbmtOccAccIsHis(new QpbmtOccAccIsHisPk(domain.getCid(),item.identifier()),item.start().v(),item.end().v());
        }).collect(Collectors.toList());

        return qpbmtEmpInsurHisList;
    }




}
