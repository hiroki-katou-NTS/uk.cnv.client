package nts.uk.ctx.exio.app.find.exo.condset;

import lombok.Builder;
import lombok.Data;
import nts.arc.time.GeneralDate;

@Builder
@Data
public class ClosureExport {

    /**
     * The closure id.
     */
    private Integer closureId;

    /**
     * The closure name.
     */
    private String closureName;
    private GeneralDate startDate;
    private GeneralDate endDate;
}
