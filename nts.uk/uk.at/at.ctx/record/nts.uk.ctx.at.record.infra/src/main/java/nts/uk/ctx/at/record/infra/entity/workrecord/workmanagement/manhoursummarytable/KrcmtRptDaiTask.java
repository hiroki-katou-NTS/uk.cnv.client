package nts.uk.ctx.at.record.infra.entity.workrecord.workmanagement.manhoursummarytable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.*;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entity: 工数集計表フォーマット
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCMT_RPT_DAI_TASK")
public class KrcmtRptDaiTask extends ContractUkJpaEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    public KrcmtRptDaiTaskPk pk;

    /**
     * 名称
     */
    @Column(name = "NAME")
    public String name;

    /**
     * 合計単位
     * 0:年月日
     * 1:年月
     */
    @Column(name = "TOTAL_UNIT")
    public int totalUnit;

    /**
     * 表示形式
     * 0:10進数
     * 1:60進数
     * 2:分単位
     */
    @Column(name = "DISPLAY_FORMAT")
    public int displayFormat;

    /**
     * 縦計・横計を表示する
     * 0:しない
     * 1:する
     */
    @Column(name = "SUMVERTCL_HORI_DIS")
    public boolean sumVerticalHorizontalDisplay;

    @OneToMany(mappedBy = "rptDaiTask", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(name = "KRCMT_RPT_DAI_TASK_ITEM")
    public List<KrcmtRptDaiTaskItem> rptDaiTaskItems;

    @Override
    protected Object getKey() {
        return pk;
    }

    public ManHourSummaryTableFormat toDomain() {
        return new ManHourSummaryTableFormat(
                new ManHourSummaryTableCode(this.pk.code),
                new ManHourSummaryTableName(this.name),
                DetailFormatSetting.create(
                        DisplayFormat.of(this.displayFormat),
                        TotalUnit.of(this.totalUnit),
                        NotUseAtr.valueOf(this.sumVerticalHorizontalDisplay),
                        this.rptDaiTaskItems.stream().map(x -> x.toDomain(x)).collect(Collectors.toList())
                ));
    }

    public static KrcmtRptDaiTask toEntity(ManHourSummaryTableFormat domain) {
        String cid = AppContexts.user().companyId();
        val detailList = domain.getDetailFormatSetting().getSummaryItemList().stream().map(x -> new KrcmtRptDaiTaskItem(
                new KrcmtRptDaiTaskItemPk(
                        cid,
                        domain.getCode().v(),
                        x.getHierarchicalOrder()),
                x.getSummaryItemType().value)).collect(Collectors.toList());

        return new KrcmtRptDaiTask(
                new KrcmtRptDaiTaskPk(
                        cid,
                        domain.getCode().v()
                ),
                domain.getName().v(),
                domain.getDetailFormatSetting().getTotalUnit().value,
                domain.getDetailFormatSetting().getDisplayFormat().value,
                domain.getDetailFormatSetting().getDisplayVerticalHorizontalTotal().value == 1,
                detailList
        );
    }

    public void fromEntity(KrcmtRptDaiTask entity) {
        this.name = entity.name;
        this.totalUnit = entity.totalUnit;
        this.displayFormat = entity.displayFormat;
        this.sumVerticalHorizontalDisplay = entity.sumVerticalHorizontalDisplay;

        this.rptDaiTaskItems.removeIf(r -> !entity.rptDaiTaskItems.contains(r));
        entity.rptDaiTaskItems.forEach(e -> {
            val checkExist = this.rptDaiTaskItems.stream().filter(x -> x.pk.equals(e.pk)).findFirst();
            if (checkExist.isPresent()) {
                checkExist.get().fromEntity(e);
            } else {
                this.rptDaiTaskItems.add(e);
            }
        });
    }
}
