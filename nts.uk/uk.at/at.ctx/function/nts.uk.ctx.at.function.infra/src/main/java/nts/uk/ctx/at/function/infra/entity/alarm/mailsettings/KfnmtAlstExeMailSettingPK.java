package nts.uk.ctx.at.function.infra.entity.alarm.mailsettings;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class KfnmtAlstExeMailSettingPK implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 会社ID */
    @Column(name = "CID")
    public String companyID;

    /** 個人職場区分
     * 0: 個人別
     * 1: 職場別
     */
    @Column(name = "PERSON_WKP_ATR")
    public int personWkpAtr;

    /** 通常自動区分
     * 通常: 0
     * 自動: 1
     */
    @Column(name = "NORMAL_AUTO_ATR")
    public int normalAutoAtr;

    /** 本人管理区分
     * 本人宛メール設定: 0
     * 管理者宛メール設定: 1
     */
    @Column(name = "PER_MANAGE_ATR")
    public int personalManagerAtr;
}
