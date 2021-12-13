package nts.uk.ctx.at.function.infra.entity.holidaysremaining;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 出力する特別休暇
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KFNMT_RPT_HD_REMAIN_HDSP")
public class KfnmtRptHdRemainHdsp extends ContractUkJpaEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @EmbeddedId
    public KfnmtRptHdRemainHdspPk kfnmtRptHdRemainHdspPk;

    /**
     * 会社ID
     */
    @Basic(optional = false)
    @Column(name = "CID")
    public String cid;

    /**
     * 項目選択区分: 休暇残数管理表の出力項目設定.項目選択区分
     * 0:定型選択
     * 1:自由設定
     */
    @Basic(optional = false)
    @Column(name = "ITEM_SEL_TYPE")
    public int itemSelType;

    /**
     * 社員ID:休暇残数管理表の出力項目設定:社員ID
     */
    @Basic(optional = false)
    @Column(name = "SID")
    public String sid;
    /**
     * コード
     */
    @Basic(optional = false)
    @Column(name = "CD")
    public String cd;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumns({
            @JoinColumn(name = "CD", referencedColumnName = "CD", insertable = false, updatable = false),
            @JoinColumn(name = "LAYOUT_ID", referencedColumnName = "LAYOUT_ID", insertable = false, updatable = false)
    })
    public KfnmtRptHdRemainOut kfnmtHdRemainManage;

    @Override
    protected Object getKey() {
        return kfnmtRptHdRemainHdspPk;
    }
}
