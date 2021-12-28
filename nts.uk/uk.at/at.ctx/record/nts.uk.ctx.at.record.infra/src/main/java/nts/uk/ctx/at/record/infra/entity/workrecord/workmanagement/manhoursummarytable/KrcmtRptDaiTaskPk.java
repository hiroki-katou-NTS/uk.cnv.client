package nts.uk.ctx.at.record.infra.entity.workrecord.workmanagement.manhoursummarytable;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class KrcmtRptDaiTaskPk implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 会社ID
     */
    @Column(name = "CID")
    public String cid;

    /**
     * コード
     */
    @Column(name = "CODE")
    public String code;

}
