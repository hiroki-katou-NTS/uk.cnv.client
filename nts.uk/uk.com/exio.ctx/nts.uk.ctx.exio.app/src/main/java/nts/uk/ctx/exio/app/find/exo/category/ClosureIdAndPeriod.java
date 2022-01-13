package nts.uk.ctx.exio.app.find.exo.category;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;

@AllArgsConstructor
@Getter
@Setter
public class ClosureIdAndPeriod {
    private Integer closureId;
    private GeneralDate startDate;
    private GeneralDate endDate;
}
