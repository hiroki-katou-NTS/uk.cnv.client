package nts.uk.ctx.at.shared.infra.entity.agreement.management;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Ksrmt36AgrMgtCmpPk implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 会社ID
     */
    @Column(name = "CID")
    public String companyID;
    /**
     * ３６協定労働制
     * 0：一般労働制
     * 1：変形労働時間制
     * 会社３６協定時間
     */
    @Column(name = "LABOR_SYSTEM_ATR")
    public int laborSystemAtr;
}