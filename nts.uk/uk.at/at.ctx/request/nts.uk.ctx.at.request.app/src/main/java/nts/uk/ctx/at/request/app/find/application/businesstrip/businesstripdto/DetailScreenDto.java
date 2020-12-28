package nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.dom.application.businesstrip.service.DetailScreenB;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailScreenDto {

    private BusinessTripInfoOutputDto businessTripInfoOutputDto;

    private BusinessTripDto businessTripDto;

    private List<ScreenWorkNameDetailDto> screenDetails;

    public static DetailScreenDto fromDomain(DetailScreenB domain) {
        return new DetailScreenDto(
                BusinessTripInfoOutputDto.convertToDto(domain.getBusinessTripInfoOutput()),
                BusinessTripDto.fromDomain(domain.getBusinessTrip()),
                Collections.emptyList()
        );
    }
}
