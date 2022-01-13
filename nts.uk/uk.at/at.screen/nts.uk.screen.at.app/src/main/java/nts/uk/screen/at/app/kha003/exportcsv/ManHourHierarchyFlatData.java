package nts.uk.screen.at.app.kha003.exportcsv;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.screen.at.app.kha003.DisplayInfoDto;

import java.util.Map;

@AllArgsConstructor
@Getter
public class ManHourHierarchyFlatData {
    private String codeLv1;
    private String codeLv2;
    private String codeLv3;
    private String codeLv4;
    private DisplayInfoDto lstDispInfo;
    private Map<String, Integer> lstVerticalValue;
    private int total;
}
