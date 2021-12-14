package nts.uk.ctx.at.function.infra.entity.alarmworkplace.checkcondition;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class KfnmtCatMapEachTypePk implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "CID")
    public String companyID;

    @Column(name = "CATEGORY")
    public Integer category;

    @Column(name = "CATEGORY_ITEM_CD")
    public String categoryItemCD;

    @Column(name = "WP_ERROR_ALARM_CHKID")
    public String wkAlarmCheckId;

    @Column(name = "FIXED_OPTIONAL")
    public Boolean fixedOp;

}
