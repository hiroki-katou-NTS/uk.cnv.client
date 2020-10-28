package nts.uk.ctx.sys.auth.infra.entity.anniversary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class BpsdtPsAnniversaryInfoPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "PID")
    private String personalId;

    @NotNull
    @Column(name = "ANNIVERSARY_DATE")
    private String anniversary;
}
