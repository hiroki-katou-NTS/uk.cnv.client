package nts.uk.ctx.at.function.infra.entity.alarmworkplace.monthdayperiod;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.function.infra.entity.alarmworkplace.condition.KfnmtWkpCheckCondition;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * entity : 締め日指定
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KFNMT_ASSIGDEADLINE_START")
public class KfnmtAssignDatelineStart extends UkJpaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    public KfnmtAssignDatelineStartPk pk;

    @Column(name = "CONTRACT_CD")
    public String contractCode;

    @Column(name = "ASSIGN_WAY_DAY_START")
    public int startSpecify;

    @Column(name = "NUMOF_MON")
    public int monthNo;

    @Column(name = "THIS_MON")
    public boolean curentMonth;

    @Column(name = "BEFORE_AFTER_ATR")
    public int monthPrevious;

    @Override
    protected Object getKey() {
        return this.pk;
    }

    //class month

    @OneToOne(mappedBy = "kfnmtAssignDatelineStart", orphanRemoval = true)
    public KfnmtWkpCheckCondition checkCondition;

}
