package nts.uk.screen.at.app.kml002.screenG;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CountInfoDto {

    private List<CountNumberOfTimeDto> countNumberOfTimeDtos;

    private List<NumberOfTimeTotalDto> numberOfTimeTotalDtos;

}

