package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.premiumitem;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 *
 * @author Doan Duy Hung
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class KmldpPremiumAttendancePK implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name="CID")
    public String companyID;

    @Column(name="HIST_ID")
    public String historyID;

    @Column(name="PREMIUM_NO")
    public Integer displayNumber;

    @Column(name="ATTENDANCE_ID")
    public String attendanceID;
}