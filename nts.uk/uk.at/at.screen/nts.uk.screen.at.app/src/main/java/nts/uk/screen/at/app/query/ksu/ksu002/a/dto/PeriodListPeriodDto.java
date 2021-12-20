package nts.uk.screen.at.app.query.ksu.ksu002.a.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.calendar.period.DatePeriod;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PeriodListPeriodDto {
	//・$週合計の期間リスト
	public List<DatePeriod> datePeriodList;
	//・$取得期間
	public DatePeriod datePeriod;
}
