package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskName;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.TaskContent;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author tutt
 *
 */
public class OneDayFavoriteSetTest {
	
	@Test
	public void getter() {
		List<TaskContentForEachSupportFrame> taskContents = new ArrayList<>();
		List<TaskContent> taskContentList = new ArrayList<>();
		taskContentList.add(new TaskContent(1, new WorkCode("123")));
		
		taskContents.add(new TaskContentForEachSupportFrame(new SupportFrameNo(1), taskContentList, Optional.of(new AttendanceTime(1))));
		
		List<TaskBlockDetailContent> taskBlockDetailContents = new ArrayList<>();
		TaskBlockDetailContent content = new TaskBlockDetailContent(new TimeWithDayAttr(1), new TimeWithDayAttr(2), taskContents);
		
		taskBlockDetailContents.add(content);
		OneDayFavoriteSet set = new OneDayFavoriteSet("sId", "favId", new FavoriteTaskName("name"), taskBlockDetailContents);
		
		NtsAssert.invokeGetters(set);
	}
	
	@Test
	public void testAddOneDayFavSet() {
		List<TaskContentForEachSupportFrame> taskContents = new ArrayList<>();
		List<TaskContent> taskContentList = new ArrayList<>();
		taskContentList.add(new TaskContent(1, new WorkCode("123")));
		
		taskContents.add(new TaskContentForEachSupportFrame(new SupportFrameNo(1), taskContentList, Optional.of(new AttendanceTime(1))));
		
		List<TaskBlockDetailContent> taskBlockDetailContents = new ArrayList<>();
		TaskBlockDetailContent content = new TaskBlockDetailContent(new TimeWithDayAttr(1), new TimeWithDayAttr(2), taskContents);
		
		taskBlockDetailContents.add(content);
		OneDayFavoriteSet set = OneDayFavoriteSet.addOneDayFavSet("sId", new FavoriteTaskName("name"), taskBlockDetailContents);
		set.setTaskName(new FavoriteTaskName("name1"));
		NtsAssert.invokeGetters(set);
	}

}
