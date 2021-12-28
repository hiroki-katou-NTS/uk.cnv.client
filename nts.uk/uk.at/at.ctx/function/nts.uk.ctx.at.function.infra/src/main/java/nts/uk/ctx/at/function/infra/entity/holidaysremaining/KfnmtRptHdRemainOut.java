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
    public boolean yearlyHoliday;

    /**
     * ★内半日年休を出力する
     */
    @Basic(optional = false)
    @Column(name = "INSIDE_HALF_DAY")
    public boolean insideHalfDay;

    /**
     * 内時間年休残数を出力する
     */
    @Basic(optional = false)
    @Column(name = "INSIDE_HOURS")
    public boolean insideHours;

    /**
     * 積立年休の項目を出力する
     */
    @Basic(optional = false)
    @Column(name = "YEARLY_RESERVED")
    public boolean yearlyReserved;

    /**
     * 代休の項目を出力する
     */
    @Basic(optional = false)
    @Column(name = "OUT_ITEM_SUB")
    public boolean outItemSub;

    /**
     * 代休未消化出力する
     */
    @Basic(optional = false)
    @Column(name = "REPRESENT_SUB")
    public boolean representSub;

    /**
     * 代休残数を出力する
     */
    @Basic(optional = false)
    @Column(name = "REMAIN_CHARGE_SUB")
    public boolean remainChargeSub;

    /**
     * 振休の項目を出力する
     */
    @Basic(optional = false)
    @Column(name = "PAUSE_ITEM")
    public boolean pauseItem;

    /**
     * 振休未消化を出力する
     */
    @Basic(optional = false)
    @Column(name = "UNDIGESTED_PAUSE")
    public boolean undigestedPause;

    /**
     * 振休残数を出力する
     */
    @Basic(optional = false)
    @Column(name = "NUM_REMAIN_PAUSE")
    public boolean numRemainPause;

    /**
     * 時間外超過項目を出力する:出力する時間外超過項目.時間外超過項目を出力する
     */
    @Basic(optional = false)
    @Column(name = "HD60H_ITEM")
    public boolean hD60HItem;

    /**
     * 時間外超過項目を出力する:出力する時間外超過項目.時間外超過未消化を出力する
     */
    @Basic(optional = false)
    @Column(name = "HD60H_UNDIGESTED")
    public boolean hD60HUndigested;

    /**
     * 時間外超過項目を出力する:出力する時間外超過項目.時間外超過未消化を出力する
     */
    @Basic(optional = false)
    @Column(name = "HD60H_REMAIN")
    public boolean hD60HRemain;

    /**
     * 公休の項目を出力する
     */
    @Basic(optional = false)
    @Column(name = "OUTPUT_ITEMS_HOLIDAYS")
    public boolean outputItemsHolidays;

    /**
     * 公休繰越数を出力する
     */
    @Basic(optional = false)
    @Column(name = "OUTPUT_HOLIDAY_FORWARD")
    public boolean outputHolidayForward;

    /**
     * 公休月度残を出力する
     */
    @Basic(optional = false)
    @Column(name = "MONTHLY_PUBLIC")
    public boolean monthlyPublic;

    /**
     * 子の看護休暇の項目を出力する
     */
    @Basic(optional = false)
    @Column(name = "CHILD_CARE_LEAVE")
    public boolean childCareLeave;

    /**
     * 介護休暇の項目を出力する
     */
    @Basic(optional = false)
    @Column(name = "NURSING_CARE_LEAVE")
    public boolean nursingCareLeave;

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
        this.yearlyHoliday = yearlyHoliday ==1;
        this.insideHalfDay = insideHalfDay == 1;
        this.insideHours = insideHours == 1;
        this.yearlyReserved = yearlyReserved == 1;
        this.outItemSub = outItemSub == 1;
        this.representSub = representSub == 1;
        this.remainChargeSub = remainChargeSub == 1;
        this.pauseItem = pauseItem == 1;
        this.undigestedPause = undigestedPause == 1;
        this.numRemainPause = numRemainPause == 1;
        this.hD60HItem = (hD60HItem==1);
        this.hD60HUndigested = hD60HUndigested == 1;
        this.hD60HRemain = hD60HRemain == 1;
        this.outputItemsHolidays = outputItemsHolidays == 1;
        this.outputHolidayForward = outputHolidayForward == 1;
        this.monthlyPublic = monthlyPublic == 1;
        this.childCareLeave = (childCareLeave == 1);
        this.nursingCareLeave = nursingCareLeave == 1;
        this.kfnmtSpecialHolidays = kfnmtSpecialHolidays;
    }
}
