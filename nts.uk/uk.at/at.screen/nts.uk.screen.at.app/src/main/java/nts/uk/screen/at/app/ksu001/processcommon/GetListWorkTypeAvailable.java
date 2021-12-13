/**
 * 
 */
package nts.uk.screen.at.app.ksu001.processcommon;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.schedule.app.find.schedule.setting.functioncontrol.ScheFuncControlCorrectionFinder;
import nts.uk.ctx.at.schedule.dom.displaysetting.functioncontrol.ScheFunctionControl;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.worktype.AttendanceHolidayAttr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.worktype.algorithm.GetWorkTypeServiceShare;
import nts.uk.screen.at.app.ksu001.displayinworkinformation.WorkTypeDto;
import nts.uk.screen.at.app.ksu001.displayinworkinformation.WorkTypeInfomation;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * @author laitv 
 * ScreenQuery 利用可能な勤務種類リストを取得する
 * Path : UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).個人別と共通の処理.利用可能な勤務種類リストを取得する
 */
@Stateless
public class GetListWorkTypeAvailable {

	@Inject
	private BasicScheduleService basicScheduleService;
	@Inject
	private GetWorkTypeServiceShare getWorkTypeService;
	
	@Inject
	private WorkTypeRepository workTypeRepo;
	
	@Inject
	private ScheFuncControlCorrectionFinder scheFuncControlCorrectionFinder;
	
	
	
	public List<WorkTypeInfomation> getData() { // ・List<勤務種類, 必須任意不要区分, 出勤休日区分>

		String cid = AppContexts.user().companyId();
		List<WorkTypeDto> workTypeDtos;
		// 1 取得する()
		Optional<ScheFunctionControl> scheFunctionControl = scheFuncControlCorrectionFinder.getData(cid);
		
		// 2: スケジュール修正の機能制御.表示可能勤務種類制御
		if (scheFunctionControl.map(x -> x.getDisplayWorkTypeControl() == NotUseAtr.NOT_USE).orElse(false)) {
			
			// //2.1: <<Public>> 廃止されていない勤務種類をすべて取得する
			workTypeDtos = basicScheduleService.getAllWorkTypeNotAbolished(cid) 
								.stream() // loop
								.map(x -> new WorkTypeDto(x))
								.collect(Collectors.toList());
										
		} else {
			
			// //2.2: <<Public>> 指定した勤務種類をすべて取得する
			workTypeDtos = this.workTypeRepo.getPossibleWorkTypeAndOrder(
					cid, 
					scheFunctionControl.map(x -> x.getDisplayableWorkTypeCodeList())
									.orElse(Collections.emptyList())
									.stream()
									.map(x -> x.v())
									.collect(Collectors.toList())
					)
			.stream()
			.map(x -> new WorkTypeDto(x.getWorkTypeCode(), x.getName(), x.getAbbreviationName(), x.getMemo()))
			.collect(Collectors.toList());
			
		}
		// loop：勤務種類 in 勤務種類リスト
		
		return workTypeDtos
				.stream()
				.map(x -> this.convertOutput(x).orElse(null))
				.filter(x -> x != null)
				.collect(Collectors.toList());
		
	}
	
	private Optional<WorkTypeInfomation> convertOutput(WorkTypeDto x) {
		// 就業時間帯の必須チェック
		SetupType workTimeSetting = 
				basicScheduleService.checkNeededOfWorkTimeSetting(x.getWorkTypeCode());
		Optional<WorkType> worktype = getWorkTypeService.getWorkType(x.getWorkTypeCode());
		if(!worktype.isPresent())
			return Optional.empty();		
		// 1日半日出勤・1日休日系の判定 - (Thực hiện thuật toán [Kiểm tra hệ thống đi làm
		// nửa ngày・ nghỉ cả ngày ])
		AttendanceHolidayAttr attHdAtr = worktype.get().chechAttendanceDay().toAttendanceHolidayAttr();
		return Optional.of(new WorkTypeInfomation(x, workTimeSetting.value, attHdAtr.value));
	}
}
