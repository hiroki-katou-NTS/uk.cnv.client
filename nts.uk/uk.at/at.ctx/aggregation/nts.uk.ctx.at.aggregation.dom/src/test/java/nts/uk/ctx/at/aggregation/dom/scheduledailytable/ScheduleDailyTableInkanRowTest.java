package nts.uk.ctx.at.aggregation.dom.scheduledailytable;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.shr.com.enumcommon.NotUseAtr;

public class ScheduleDailyTableInkanRowTest {

	@Test
	public void testCreate_Msg_2085() {
		
		List<ScheduleDailyTableInkanTitle> titleList = Arrays.asList(
				new ScheduleDailyTableInkanTitle("a1"),
				new ScheduleDailyTableInkanTitle("a2"),
				new ScheduleDailyTableInkanTitle("a3"),
				new ScheduleDailyTableInkanTitle("a4"),
				new ScheduleDailyTableInkanTitle("a5"),
				new ScheduleDailyTableInkanTitle("a6"),
				new ScheduleDailyTableInkanTitle("a7")
				);
		
		NtsAssert.businessException("Msg_2085", 
			() -> ScheduleDailyTableInkanRow.create(NotUseAtr.USE, titleList)
		);
	}
	
	
	@Test
	public void testCreate_ok() {
		
		List<ScheduleDailyTableInkanTitle> titleList = Arrays.asList(
				new ScheduleDailyTableInkanTitle("a1"),
				new ScheduleDailyTableInkanTitle("a2"),
				new ScheduleDailyTableInkanTitle("a3"),
				new ScheduleDailyTableInkanTitle("a4"),
				new ScheduleDailyTableInkanTitle("a5"),
				new ScheduleDailyTableInkanTitle("a6")
				);
		
		ScheduleDailyTableInkanRow result = ScheduleDailyTableInkanRow.create(NotUseAtr.USE, titleList);
		
		assertThat( result.getNotUseAtr() ).isEqualTo( NotUseAtr.USE );
		assertThat( result.getTitleList() )
			.extracting( d -> d.v() )
			.containsExactly( "a1", "a2", "a3", "a4", "a5", "a6");
	}
	
}
