package nts.uk.ctx.at.request.infra.entity.application.overtime;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class KrqdtAppOvertimeMultiTimesPK {
    @Column(name = "CID")
    public String companyId;

    @Column(name = "APP_ID")
    public String appId;

    @Column(name = "OVERTIME_NUMBER")
    public int overtimeNumber;
}
