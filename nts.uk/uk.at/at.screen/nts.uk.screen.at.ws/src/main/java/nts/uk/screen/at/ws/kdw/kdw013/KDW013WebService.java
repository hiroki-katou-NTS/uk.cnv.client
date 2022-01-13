package nts.uk.screen.at.ws.kdw.kdw013;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.command.workrecord.workmanagement.AddWorkRecodConfirmationCommand;
import nts.uk.ctx.at.record.app.command.workrecord.workmanagement.DeleteWorkResultConfirmCommand;
import nts.uk.screen.at.app.kdw013.a.AddWorkRecordConfirmationCommandHandler;
import nts.uk.screen.at.app.kdw013.a.ChangeFavOneDayDisplayOrder;
import nts.uk.screen.at.app.kdw013.a.ChangeFavTaskDisplayOrder;
import nts.uk.screen.at.app.kdw013.a.ConfirmerDto;
import nts.uk.screen.at.app.kdw013.a.DeleteOneDayTaskSet;
import nts.uk.screen.at.app.kdw013.a.DeleteTaskSet;
import nts.uk.screen.at.app.kdw013.a.DeleteWorkRecordConfirmationCommandHandler;
import nts.uk.screen.at.app.kdw013.a.EmployeeDisplayInfo;
import nts.uk.screen.at.app.kdw013.a.RegisterWorkContentCommand;
import nts.uk.screen.at.app.kdw013.a.RegisterWorkContentDto;
import nts.uk.screen.at.app.kdw013.a.RegisterWorkContentHandler;
import nts.uk.screen.at.app.kdw013.a.StartProcess;
import nts.uk.screen.at.app.kdw013.a.StartProcessDto;
import nts.uk.screen.at.app.kdw013.a.TaskDto;
import nts.uk.screen.at.app.kdw013.a.favorite.oneday.FavOneDayDto;
import nts.uk.screen.at.app.kdw013.a.favorite.oneday.GetFavOneDay;
import nts.uk.screen.at.app.kdw013.a.favorite.task.FavTaskDto;
import nts.uk.screen.at.app.kdw013.a.favorite.task.GetFavTask;
import nts.uk.screen.at.app.kdw013.command.ChangeDisplayOrderCommand;
import nts.uk.screen.at.app.kdw013.command.DeleteFavoriteCommand;
import nts.uk.screen.at.app.kdw013.command.DeleteFavoriteForOneDayCommand;
import nts.uk.screen.at.app.kdw013.command.RegisterFavoriteCommand;
import nts.uk.screen.at.app.kdw013.command.RegisterFavoriteForOneDayCommand;
import nts.uk.screen.at.app.kdw013.command.UpdateFavNameCommand;
import nts.uk.screen.at.app.kdw013.command.UpdateOneDayFavNameCommand;
import nts.uk.screen.at.app.kdw013.e.GetAvailableWorkingCommand;
import nts.uk.screen.at.app.kdw013.e.GetWorkDataMasterInforCommand;
import nts.uk.screen.at.app.kdw013.e.GetWorkDataMasterInforDto;
import nts.uk.screen.at.app.kdw013.e.SelectTaskItem;
import nts.uk.screen.at.app.kdw013.e.StartWorkNoTime;
import nts.uk.screen.at.app.kdw013.e.UpdateAttendanceTimeZoneBySupportWork;
import nts.uk.screen.at.app.kdw013.e.UpdateAttendanceTimeZoneBySupportWorkCommand;
import nts.uk.screen.at.app.kdw013.f.AddNewFavoriteTask;
import nts.uk.screen.at.app.kdw013.f.StartTaskFavoriteRegister;
import nts.uk.screen.at.app.kdw013.f.UpdateFavName;
import nts.uk.screen.at.app.kdw013.g.AddOneDayNewFavoriteTaskSet;
import nts.uk.screen.at.app.kdw013.g.StartOneDayTaskSetRegister;
import nts.uk.screen.at.app.kdw013.g.UpdateOneDayTaskSetName;
import nts.uk.screen.at.app.kdw013.query.FavoriteTaskItemDto;
import nts.uk.screen.at.app.kdw013.query.OneDayFavoriteSetDto;

/**
 * 
 * @author tutt
 *
 */
@Path("screen/at/kdw013")
@Produces("application/json")
public class KDW013WebService {

	@Inject
	private DeleteWorkRecordConfirmationCommandHandler deleteWorkRecordConfirmationHandler;

	@Inject
	private AddWorkRecordConfirmationCommandHandler addWorkRecordConfirmationHandler;

	@Inject
	private ChangeDate changeDate;

	@Inject
	private StartProcess startProcess;
	
	@Inject
	private RegisterWorkContentHandler registerHandler;
	
	@Inject
	private DeleteOneDayTaskSet deleteOneDayTaskSet;
	
	@Inject
	private UpdateFavName updateFavName;
	
	@Inject
	private DeleteTaskSet deleteTaskSet;
	
	@Inject
	private StartTaskFavoriteRegister startTaskFavoriteRegister;
	
	@Inject
	private AddNewFavoriteTask addNewFavoriteTask;
	
	@Inject
	private AddOneDayNewFavoriteTaskSet addOneDayNewFavoriteTaskSet;
	
	@Inject
	private StartOneDayTaskSetRegister startOneDayTaskSetRegister;
	
	@Inject
	private UpdateOneDayTaskSetName updateOneDayTaskSetName;
	
	@Inject
	private UpdateAttendanceTimeZoneBySupportWork updateAttendanceTimeZoneBySupportWork;
	
	@Inject
	private GetFavTask getFavTask;
	
	@Inject
	private GetFavOneDay getFavOneDay;
	
	@Inject
	private ChangeFavTaskDisplayOrder changeFavTaskOrder;
	
	@Inject
	private ChangeFavOneDayDisplayOrder changeFavOneDayOrder;
	
	@Inject
	private StartWorkNoTime startWorkNoTime;
	
	@Inject
	private SelectTaskItem selectTaskItem;
	
	@POST
	@Path("a/get-fav-task")
	public FavTaskDto getFavTask() {
		return getFavTask.get();
	}
	
	@POST
	@Path("a/get-fav-one-day")
	public FavOneDayDto getFavOneDay() {
		return getFavOneDay.get();
	}
	
	// 初期起動処理
	@POST
	@Path("a/start")
	public StartProcessDto startProcess(StartParam param) {
		return startProcess.startProcess(param.getInputDate());
	}

	// 日付を変更する
	@POST
	@Path("a/changeDate")
	public EmployeeDisplayInfo changeDate(ChangeDateParam param) {
		return changeDate.changeDate(param.getEmployeeId(), param.getRefDate(), param.getDisplayPeriod().toDomain(),
				param.getItemIds());
	}

	// 作業実績を確認する
	@POST
	@Path("a/add_confirm")
	public List<ConfirmerDto> registerConfirmation(AddWorkRecodConfirmationCommand param) {
		return addWorkRecordConfirmationHandler.add(param);
	}

	// 作業実績の確認を解除する
	@POST
	@Path("a/delete_confirm")
	public List<ConfirmerDto> deleteConfirmation(DeleteWorkResultConfirmCommand param) {
		return deleteWorkRecordConfirmationHandler.delete(param);
	}

	// A:工数入力.メニュー別OCD
	// 作業内容を登録する
	@POST
	@Path("a/register_work_content")
	public RegisterWorkContentDto registerWorkContent(RegisterWorkContentCommand command) {
		return registerHandler.handle(command);
	}

	// A:1日作業セットを削除する
	@POST
	@Path("a/delete_oneday_task_set")
	public void deleteOneDayTaskSet(DeleteFavoriteForOneDayCommand command) {
		deleteOneDayTaskSet.deleteOneDayTaskSet(command);
	}
	
	// A:お気に入り作業の順番を変更する
	@POST
	@Path("a/update_task_dis_order")
	public void updateTaskDisplayOrder(ChangeDisplayOrderCommand command) {
		this.changeFavTaskOrder.changeDisplayOrder(command);
	}
	
	// A:1日作業セットの順番を変更する
	@POST
	@Path("a/update_one_day_dis_order")
	public void updateOneDayDisplayOrder(ChangeDisplayOrderCommand command) {
		changeFavOneDayOrder.changeDisplayOrder(command);
	}
	
	// A: お気に入り作業を削除する
	@POST
	@Path("a/delete_task_set")
	public void deleteTaskSet(DeleteFavoriteCommand command) {
		deleteTaskSet.deleteTaskSet(command);
	}
	
	// F: 作業お気に入り登録を起動する
	@POST
	@Path("f/start_task_fav_register")
	public FavoriteTaskItemDto startTaskFavRegister(StartTaskFavoriteRegisterParam param) {
		return startTaskFavoriteRegister.startTaskFavRegister(param.getFavId());
	}
	
	// F: 作業お気に入り名称を変更する
	@POST
	@Path("f/update_task_name")
	public void updateTaskName(UpdateFavNameCommand command) {
		updateFavName.updateFavName(command);
	}
	
	// F: 作業お気に入りを新規追加する
	@POST
	@Path("f/create_task_fav")
	public void createTaskFav(RegisterFavoriteCommand command) {
		addNewFavoriteTask.addNewFavoriteTask(command);
	}
	
	// G: 1日作業セットを新規追加する
	@POST
	@Path("g/create_task_fav")
	public void createTaskFav(RegisterFavoriteForOneDayCommand command) {
		addOneDayNewFavoriteTaskSet.addOneDayNewFavoriteTaskSet(command);
	}
	
	// G: 1日作業お気に入り名称を変更する
	@POST
	@Path("g/update_task_name")
	public void updateTaskName(UpdateOneDayFavNameCommand command) {
		updateOneDayTaskSetName.updateOneDayTaskSetName(command);
	}
		
	// G: 1日作業お気に入り登録を起動する
	@POST
	@Path("g/start_task_fav_register")
	public OneDayFavoriteSetDto startOneDayTaskFavRegister(StartTaskFavoriteRegisterParam param) {
		return startOneDayTaskSetRegister.startOneDayTaskSetRegister(param.getFavId());
	}
	
	// E: 応援時間帯を更新する
	@POST
	@Path("e/update_timezone")
	public void updateSupportTimezone(UpdateAttendanceTimeZoneBySupportWorkCommand command) {
		updateAttendanceTimeZoneBySupportWork.update(command);
	}
	
	// E: 時刻なし作業内容を起動する
	@POST
	@Path("e/start_task_content_without_time")
	public GetWorkDataMasterInforDto startTaskContent(GetWorkDataMasterInforCommand command) {
		return startWorkNoTime.startWorkNoTime(command);
	}
	
	// E: 作業項目を選択する
	@POST
	@Path("e/select_task_item")
	public SelectTaskItemDto selectTaskItem(GetAvailableWorkingCommand command) {
		return new SelectTaskItemDto(selectTaskItem.selectTaskItem(command));
	}

}
@Getter
@NoArgsConstructor
@AllArgsConstructor
class StartParam {
	// 基準日
	private GeneralDate inputDate;

}

@Getter
@NoArgsConstructor
@AllArgsConstructor
class SelectTaskItemDto {
	private List<TaskDto> taskDtos;
}
