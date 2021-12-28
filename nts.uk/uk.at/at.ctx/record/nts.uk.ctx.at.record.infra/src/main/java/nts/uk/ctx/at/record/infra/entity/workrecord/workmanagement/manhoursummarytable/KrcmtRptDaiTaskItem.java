package nts.uk.ctx.at.record.infra.entity.workrecord.workmanagement.manhoursummarytable;

import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.SummaryItem;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.SummaryItemType;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@Entity
@Table(name = "KRCMT_RPT_DAI_TASK_ITEM")
public class KrcmtRptDaiTaskItem extends ContractUkJpaEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    public KrcmtRptDaiTaskItemPk pk;

    /**
     * 集計項目種類
     0:所属職場
     1:勤務職場
     2:社員
     3:作業1
     4:作業2
     5:作業3
     6:作業4
     7:作業5"
     */
    @Column(name = "SUMMARY_TYPE")
    public int summaryType;

    @ManyToOne
    @JoinColumns(
            {@JoinColumn(name = "CID", referencedColumnName = "CID", insertable = false, updatable = false),
                    @JoinColumn(name = "CODE", referencedColumnName = "CODE", insertable = false, updatable = false)})
    public KrcmtRptDaiTask rptDaiTask;

    public KrcmtRptDaiTaskItem(KrcmtRptDaiTaskItemPk pk, int summaryType) {
        super();
        this.pk = pk;
        this.summaryType = summaryType;
    }

    @Override
    protected Object getKey() {
        return pk;
    }

    public SummaryItem toDomain(KrcmtRptDaiTaskItem entity) {
        return new SummaryItem(
                entity.pk.displayOrder,
                SummaryItemType.of(entity.summaryType)
        );
    }

    public void fromEntity(KrcmtRptDaiTaskItem entity) {
        this.summaryType = entity.summaryType;
        this.pk.displayOrder = entity.pk.displayOrder;
    }
}
