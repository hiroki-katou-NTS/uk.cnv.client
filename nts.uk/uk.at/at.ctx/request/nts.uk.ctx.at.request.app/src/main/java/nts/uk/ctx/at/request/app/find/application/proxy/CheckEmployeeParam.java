package nts.uk.ctx.at.request.app.find.application.proxy;

import lombok.Data;
import nts.arc.time.GeneralDate;

import java.util.List;

@Data
public class CheckEmployeeParam {

    private int appType;

    private String baseDate;

    private List<String> lstEmployeeId;

    private Integer overtimeAppAtr;

}
