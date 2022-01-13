package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.ChoiceCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoCommentItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoNumItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoSelectionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoTimeItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppNumValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkSuppComment;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkSuppInfo;

/**
 * 
 * @author tutt
 *
 */
public class WorkSuppInfoTest {
	
	@Test
	public void getter() {
		
		List<SuppInfoTimeItem> suppInfoTimeItems = new ArrayList<>();
		suppInfoTimeItems.add(new SuppInfoTimeItem(new SuppInfoNo(1), new AttendanceTime(200)));
		
		List<SuppInfoNumItem> suppInfoNumItems = new ArrayList<>();
		suppInfoNumItems.add(new SuppInfoNumItem(new SuppInfoNo(1), new SuppNumValue(1)));
		
		List<SuppInfoCommentItem> suppInfoCommentItems = new ArrayList<SuppInfoCommentItem>();
		suppInfoCommentItems.add(new SuppInfoCommentItem(new SuppInfoNo(1), new WorkSuppComment("comment")));
		
		List<SuppInfoSelectionItem> suppInfoSelectionItems = new ArrayList<SuppInfoSelectionItem>();
		suppInfoSelectionItems.add(new SuppInfoSelectionItem(new SuppInfoNo(1), new ChoiceCode("code")));
		
		WorkSuppInfo info = new WorkSuppInfo(suppInfoTimeItems, suppInfoNumItems, suppInfoCommentItems, suppInfoSelectionItems);
		NtsAssert.invokeGetters(info);
	}

}
