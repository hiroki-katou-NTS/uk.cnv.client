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
public class Ksrmt36AgrMgtWkpPk implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 職場ID
     * 職場３６協定時間
     */
    @Column(name = "WKP_ID")
    public String workplaceId;
    /**
     * ３６協定労働制
     * 0：一般労働制
     * 1：変形労働時間制
     * 職場３６協定時間
     */
    @Column(name = "LABOR_SYSTEM_ATR")
    public int laborSystemAtr;
}
