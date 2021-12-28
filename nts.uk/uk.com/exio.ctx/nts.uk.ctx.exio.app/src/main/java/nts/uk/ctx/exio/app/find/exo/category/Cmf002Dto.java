package nts.uk.ctx.exio.app.find.exo.category;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.exio.app.find.exo.condset.ClosureExport;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Cmf002Dto {
    ExOutCtgDto exOutCtgDto;
    List<ClosureExport> closureExports;
}
