package nts.uk.screen.at.app.kwr004.b;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MonthlyOutputItemsDto {

    // B4	順位
    private int rank;

    // B4_3_1 印刷対象フラグ
    private boolean printTargetFlag;

    // B4_3_2 名称
    private String name;

    // B4_3_3_2	属性
    private int attribute;

    private List<OutputItemDetailAttItemDto> selectedListItems ;

}
