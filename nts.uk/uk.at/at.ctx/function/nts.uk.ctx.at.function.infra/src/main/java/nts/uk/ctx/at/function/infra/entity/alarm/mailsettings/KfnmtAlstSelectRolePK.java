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
public class KfnmtAlstSelectRolePK implements Serializable {
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

    /** 選択したロール */
    @Column(name = "ROLE_ID")
    public String roleId;
}
