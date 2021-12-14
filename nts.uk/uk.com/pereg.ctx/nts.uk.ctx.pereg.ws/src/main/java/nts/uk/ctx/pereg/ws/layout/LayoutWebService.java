package nts.uk.ctx.pereg.ws.layout;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.arc.layer.ws.WebService;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.app.find.remainingnumber.rervleagrtremnum.ResvLeaRemainNumberFinder;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveGrantRemainService;
import nts.uk.ctx.pereg.app.command.addemployee.AddEmployeeCommand;
import nts.uk.ctx.pereg.app.find.common.GetSPHolidayNextGrantDate;
import nts.uk.ctx.pereg.app.find.common.SpecialleaveInformation;
import nts.uk.ctx.pereg.app.find.layout.RegisterLayoutFinder;
import nts.uk.ctx.pereg.app.find.layout.dto.EmpMaintLayoutDto;
import nts.uk.ctx.pereg.app.find.layoutdef.NewLayoutDto;
import nts.uk.ctx.pereg.app.find.person.category.PerInfoCtgFullDto;
import nts.uk.ctx.pereg.app.find.processor.GetNameWorkTime;
import nts.uk.ctx.pereg.app.find.processor.GetNameWorkTimeOutput;
import nts.uk.ctx.pereg.app.find.processor.PeregProcessor;
import nts.uk.shr.pereg.app.find.PeregQuery;

/**
 * @author sonnlb
 *
 */
@Path("ctx/pereg/layout")
@Produces(MediaType.APPLICATION_JSON)
public class LayoutWebService extends WebService {

	@Inject
	private RegisterLayoutFinder layoutFinder;
	
	@Inject
	private PeregProcessor layoutProcessor;
	
//	@Inject
//	private LayoutingProcessor layoutingProcessor;

	@Inject 
	private SpecialLeaveGrantRemainService specialLeaveGrantRemainService;
	
	@Inject
	private ResvLeaRemainNumberFinder resvLeaNumberFinder;
	
	@Inject 
	private GetSPHolidayNextGrantDate getSPHolidayNextGrantDate;
	
	@Inject
	private GetNameWorkTime getNameWorkTime;
	
	@Path("getByCreateType")
	@POST
	public NewLayoutDto getByCreateType(AddEmployeeCommand command) {
		return this.layoutFinder.getLayoutByCreateType(command);
	}
	
	/**
	 * get category and it's children
	 * @author xuan vinh
	 * 
	 * @param ctgId
	 * @return
	 */
	
	@Path("find/getctgtab/{categoryid}")
	@POST
	public List<PerInfoCtgFullDto> getCtgTab(@PathParam("categoryid")String ctgId){
		return this.layoutProcessor.getCtgTab(ctgId);
	}
	
	/**
	 * @author xuan vinh
	 * @param query
	 * @return
	 */
	
	@Path("find/gettabdetail")
	@POST
	public EmpMaintLayoutDto getTabDetail(PeregQuery query){
		return this.layoutProcessor.getCategoryDetail(query);
	}
	
//	@Path("find/gettabdetail")
//	@POST
//	public EmpMaintLayoutDto getTabDetail(List<PeregQuery> query){
//		return this.layoutProcessor.getCategoryDetail(query);
//	}
	
	
	@Path("calDayTime/{sid}/{specialCD}")
	@POST
	public Object calDayTime(@PathParam("sid")String sid , @PathParam("specialCD")int specialCD){
		String dayTime = specialLeaveGrantRemainService.calDayTime(sid, specialCD);
		return new Object[] {dayTime};
	}
	
	@POST
	@Path("getResvLeaNumber/{sid}")
	public Object getResvLeaNumber(@PathParam("sid") String employeeId) {
		String dayNumber = resvLeaNumberFinder.getResvLeaRemainNumber(employeeId);
		return new Object[] {dayNumber};
	}
	/**
	 * Category Special Holiday CS00025 ~
	 * 次回特休情報を取得する
	 * @param 社員ID sid
	 * @param 特別休暇コード specialCD
	 * @param 特休付与基準日 referDate
	 * @param 適用設定 appSet
	 * @param 特休付与テーブルコード grantTableCD
	 * @param 付与日数 grantedDays
	 * @param 入社年月日 entryDate NULL
	 * @param 年休付与基準日 year NULL
	 * @return 次回付与日 grantDate
	 */
	@POST
	@Path("getSPHolidayGrantDate")
	public GeneralDate getSPHolidayGrantDate(SpecialleaveInformation specialLeaveInfo){
		return getSPHolidayNextGrantDate.getSPHolidayGrantDate(specialLeaveInfo);
	}
	
	/**
	 * Category Special Holiday CS00025 ~
	 * 次回特休情報を取得する
	 * @param 社員ID sid
	 * @param 特別休暇コード specialCD
	 * @param 特休付与基準日 referDate
	 * @param 適用設定 appSet
	 * @param 特休付与テーブルコード grantTableCD
	 * @param 付与日数 grantedDays
	 * @param 入社年月日 entryDate NULL
	 * @param 年休付与基準日 year NULL
	 * @return 次回付与日 grantDate
	 */
	@POST
	@Path("getSPHolidayGrantDateBySids")
	public Map<String, GeneralDate> getSPHolidayGrantDateBySids(List<SpecialleaveInformation> params){
		return getSPHolidayNextGrantDate.getAllSPHolidayGrantDateBySids(params);
	}
	
	@Path("find/nameWorkTime/{workTimeCode}")
	@POST
	public GetNameWorkTimeOutput getWorkTimeName(@PathParam("workTimeCode")String workTimeCode){
		return this.getNameWorkTime.getNameWorkTime(workTimeCode);
	}
}
