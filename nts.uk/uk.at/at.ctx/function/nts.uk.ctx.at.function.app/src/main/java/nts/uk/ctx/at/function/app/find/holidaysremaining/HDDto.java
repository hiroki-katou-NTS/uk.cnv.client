package nts.uk.ctx.at.function.app.find.holidaysremaining;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class HDDto {
    List<HdRemainManageDto> listFreeSetting;
    List<HdRemainManageDto> listStandard;

}