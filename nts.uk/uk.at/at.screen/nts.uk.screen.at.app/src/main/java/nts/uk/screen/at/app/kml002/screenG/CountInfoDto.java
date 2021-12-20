package nts.uk.screen.at.app.kml002.screenG;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CountInfoDto {
	// 回数集計種類
    private List<CountNumberOfTimeDto> countNumberOfTimeDtos;
    // 選択した項目リスト
    private List<NumberOfTimeTotalDto> numberOfTimeTotalDtos;

}

