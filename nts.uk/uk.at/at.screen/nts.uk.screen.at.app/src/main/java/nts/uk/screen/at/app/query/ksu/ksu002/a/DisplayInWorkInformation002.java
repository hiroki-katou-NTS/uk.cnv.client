package nts.uk.screen.at.app.query.ksu.ksu002.a;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.worktype.AttendanceHolidayAttr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.algorithm.GetWorkTypeServiceShare;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.DisplayInWorkInfoDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.WorkScheduleWorkInforDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.WorkTypeDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.WorkTypeInfomation;
import nts.uk.screen.at.app.query.ksu.ksu002.a.input.DisplayInWorkInfoInput;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author chungnt ScreenQuery 勤務情報で表示する (KSU002)
 */

@Stateless
public class DisplayInWorkInformation002 {
	@Inject
	private BasicScheduleService basicScheduleService;
	@Inject
	private GetWorkTypeServiceShare getWorkTypeService;
	@Inject
	private GetScheduleActualOfWorkInfo002 getScheduleActualOfWorkInfo002;

	public DisplayInWorkInfoDto getDataWorkInfo(DisplayInWorkInfoInput param) {

		DisplayInWorkInfoDto result = new DisplayInWorkInfoDto();

		String cid = AppContexts.user().companyId();
		List<WorkTypeInfomation> listWorkTypeInfo = new ArrayList<>();

		// <<Public>> 廃止されていない勤務種類をすべて取得する
		List<WorkType> listWorkType = basicScheduleService.getAllWorkTypeNotAbolished(cid);
		List<WorkTypeDto> listWorkTypeDto = listWorkType.stream().map(mapper -> {
			return new WorkTypeDto(mapper);
		}).collect(Collectors.toList());

		for (int i = 0; i < listWorkTypeDto.size(); i++) {

			// <<Public>> 廃止されていない勤務種類をすべて取得する
			WorkTypeDto workTypeDto = listWorkTypeDto.get(i);
			// 就業時間帯の必須チェック
			SetupType workTimeSetting = basicScheduleService
					.checkNeededOfWorkTimeSetting(listWorkTypeDto.get(i).getWorkTypeCode());

			// 1日半日出勤・1日休日系の判定 - (Thực hiện thuật toán [Kiểm tra hệ thống đi làm
			// nửa ngày・ nghỉ cả ngày ])
			Optional<WorkType>worktype = getWorkTypeService.getWorkType(listWorkTypeDto.get(i).getWorkTypeCode());
			if(!worktype.isPresent())
				continue;
			AttendanceHolidayAttr attHdAtr = worktype.get().chechAttendanceDay().toAttendanceHolidayAttr();

			WorkTypeInfomation workTypeInfomation = new WorkTypeInfomation(workTypeDto, workTimeSetting.value,
					attHdAtr.value);
			listWorkTypeInfo.add(workTypeInfomation);
		}
		result.setListWorkTypeInfo(listWorkTypeInfo);

		// Lấy data Schedule
		// Get schedule/actual results with work information
		List<WorkScheduleWorkInforDto> listWorkScheduleWorkInfor = getScheduleActualOfWorkInfo002
				.getDataScheduleAndAactualOfWorkInfo(param);

		result.setListWorkScheduleWorkInfor(listWorkScheduleWorkInfor);
		return result;
	}
}
