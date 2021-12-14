package nts.uk.ctx.at.shared.infra.entity.agreement.management;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Ksrmt36AgrMgtClsPk implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会社ID
     * 分類３６協定時間
     */
    @Column(name = "CID")
    public String companyID;
    /** 分類コード
     *  分類３６協定時間
     */
    @Column(name = "CLS_CD")
    public String classificationCode;
    //３６協定労働制
    //0：一般労働制
    //1：変形労働時間制
    //分類３６協定時間
    @Column(name = "LABOR_SYSTEM_ATR")
    public int laborSystemAtr;
}
