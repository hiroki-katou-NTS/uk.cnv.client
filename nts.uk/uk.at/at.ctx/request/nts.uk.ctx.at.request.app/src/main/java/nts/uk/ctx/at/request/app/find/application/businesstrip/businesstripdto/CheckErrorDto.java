package nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckErrorDto {

    private boolean result;

    private String msg;

}
