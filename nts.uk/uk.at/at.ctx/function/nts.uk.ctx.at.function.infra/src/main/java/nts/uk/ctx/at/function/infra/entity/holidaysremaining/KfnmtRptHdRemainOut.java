package nts.uk.ctx.at.function.infra.entity.holidaysremaining;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 休暇残数管理表の出力項目設定
 */
@Entity
@NoArgsConstructor
@Table(name = "KFNMT_RPT_HD_REMAIN_OUT")
public class KfnmtRptHdRemainOut extends ContractUkJpaEntity implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 出力レイアウトID : 休暇残数管理表の出力項目設定.会社ID
     */
    @Id
    @Column(name = "LAYOUT_ID")
    public String layoutId;

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
     * 会社ID
     */
    @Basic(optional = false)
    @Column(name = "CID")
    public String cid;

    /**
     * コード
     */
    @Basic(optional = false)
    @Column(name = "CD")
    public String cd;

    /**
     * 名称
     */
    @Basic(optional = false)
    @Column(name = "NAME")
    public String name;

    /**
     * 年休の項目出力する
     */
    @Basic(optional = false)
    @Column(name = "YEARLY_HOLIDAY")
    public int yearlyHoliday;

    /**
     * ★内半日年休を出力する
     */
    @Basic(optional = false)
    @Column(name = "INSIDE_HALF_DAY")
    public int insideHalfDay;

    /**
     * 内時間年休残数を出力する
     */
    @Basic(optional = false)
    @Column(name = "INSIDE_HOURS")
    public int insideHours;

    /**
     * 積立年休の項目を出力する
     */
    @Basic(optional = false)
    @Column(name = "YEARLY_RESERVED")
    public int yearlyReserved;

    /**
     * 代休の項目を出力する
     */
    @Basic(optional = false)
    @Column(name = "OUT_ITEM_SUB")
    public int outItemSub;

    /**
     * 代休未消化出力する
     */
    @Basic(optional = false)
    @Column(name = "REPRESENT_SUB")
    public int representSub;

    /**
     * 代休残数を出力する
     */
    @Basic(optional = false)
    @Column(name = "REMAIN_CHARGE_SUB")
    public int remainChargeSub;

    /**
     * 振休の項目を出力する
     */
    @Basic(optional = false)
    @Column(name = "PAUSE_ITEM")
    public int pauseItem;

    /**
     * 振休未消化を出力する
     */
    @Basic(optional = false)
    @Column(name = "UNDIGESTED_PAUSE")
    public int undigestedPause;

    /**
     * 振休残数を出力する
     */
    @Basic(optional = false)
    @Column(name = "NUM_REMAIN_PAUSE")
    public int numRemainPause;

    /**
     * 時間外超過項目を出力する:出力する時間外超過項目.時間外超過項目を出力する
     */
    @Basic(optional = false)
    @Column(name = "HD60H_ITEM")
    public int hD60HItem;

    /**
     * 時間外超過項目を出力する:出力する時間外超過項目.時間外超過未消化を出力する
     */
    @Basic(optional = false)
    @Column(name = "HD60H_UNDIGESTED")
    public int hD60HUndigested;

    /**
     * 時間外超過項目を出力する:出力する時間外超過項目.時間外超過未消化を出力する
     */
    @Basic(optional = false)
    @Column(name = "HD60H_REMAIN")
    public int hD60HRemain;

    /**
     * 公休の項目を出力する
     */
    @Basic(optional = false)
    @Column(name = "OUTPUT_ITEMS_HOLIDAYS")
    public int outputItemsHolidays;

    /**
     * 公休繰越数を出力する
     */
    @Basic(optional = false)
    @Column(name = "OUTPUT_HOLIDAY_FORWARD")
    public int outputHolidayForward;

    /**
     * 公休月度残を出力する
     */
    @Basic(optional = false)
    @Column(name = "MONTHLY_PUBLIC")
    public int monthlyPublic;

    /**
     * 子の看護休暇の項目を出力する
     */
    @Basic(optional = false)
    @Column(name = "CHILD_CARE_LEAVE")
    public int childCareLeave;

    /**
     * 介護休暇の項目を出力する
     */
    @Basic(optional = false)
    @Column(name = "NURSING_CARE_LEAVE")
    public int nursingCareLeave;

    @OneToMany( mappedBy = "kfnmtHdRemainManage", orphanRemoval = true,cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @JoinTable(name = "KFNMT_RPT_HD_REMAIN_HDSP")
    public List<KfnmtRptHdRemainHdsp> kfnmtSpecialHolidays;

    @Override
    public Object getKey() {
        return layoutId;
    }

    public KfnmtRptHdRemainOut(String layoutId,
                               int itemSelType,
                               String sid,
                               String cid,
                               String cd,
                               String name,
                               int yearlyHoliday,
                               int insideHalfDay,
                               int insideHours,
                               int yearlyReserved,
                               int outItemSub,
                               int representSub,
                               int remainChargeSub,
                               int pauseItem,
                               int undigestedPause,
                               int numRemainPause,
                               int hD60HItem,
                               int hD60HRemain,
                               int hD60HUndigested,
                               int outputItemsHolidays,
                               int outputHolidayForward,
                               int monthlyPublic,
                               int childCareLeave,
                               int nursingCareLeave,
                               List<KfnmtRptHdRemainHdsp> kfnmtSpecialHolidays) {
        super();
        this.layoutId = layoutId;
        this.itemSelType = itemSelType;
        this.sid = sid;
        this.cid = cid;
        this.cd = cd;
        this.name = name;
        this.yearlyHoliday = yearlyHoliday;
        this.insideHalfDay = insideHalfDay;
        this.insideHours = insideHours;
        this.yearlyReserved = yearlyReserved;
        this.outItemSub = outItemSub;
        this.representSub = representSub;
        this.remainChargeSub = remainChargeSub;
        this.pauseItem = pauseItem;
        this.undigestedPause = undigestedPause;
        this.numRemainPause = numRemainPause;
        this.hD60HItem = hD60HItem;
        this.hD60HUndigested = hD60HUndigested;
        this.hD60HRemain = hD60HRemain;
        this.outputItemsHolidays = outputItemsHolidays;
        this.outputHolidayForward = outputHolidayForward;
        this.monthlyPublic = monthlyPublic;
        this.childCareLeave = childCareLeave;
        this.nursingCareLeave = nursingCareLeave;
        this.kfnmtSpecialHolidays = kfnmtSpecialHolidays;
    }
}
