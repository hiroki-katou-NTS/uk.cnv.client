package nts.uk.screen.at.app.query.kdw.kdw003.g;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
/**
 * 作業初期選択設定を取得する
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW003_日別実績の修正.G：初期作業選択設定.メニュー別OCD.作業初期選択設定を取得する
 * @author quytb
 *
 */
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSel;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSelHist;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSelHistRepo;
@Stateless
public class GetTaskInitialSelSettingScreenQuery {
	@Inject
	TaskInitialSelHistRepo taskInitialSelHistRepo;
	
	public TaskInitialSelSettingDto getTaskInintialSelSetting(String employeeId) {		
		
		Optional<TaskInitialSelHist> opt = taskInitialSelHistRepo.getById(employeeId);
		TaskInitialSelSettingDto taskInitialSelSettingDto = new TaskInitialSelSettingDto();
		if(opt.isPresent()) {
			String empId = opt.get().getEmpId();
			List<TaskInitialSel> listHist = opt.get().getLstHistory();
			List<String> lstStartDate = new ArrayList<String>();
			List<String> lstEndDate = new ArrayList<String>();
			List<String> lstTaskItem = new ArrayList<String>();
			List<Integer> lstId = new ArrayList<Integer>();
			taskInitialSelSettingDto.setEmployeeId(empId);
			
			List<TaskInitialSel> listHistSort = listHist.stream().sorted(Comparator.comparing(TaskInitialSel::start).reversed()).collect(Collectors.toList());
			listHistSort.stream().forEach(hist -> {	
				lstStartDate.add(hist.getDatePeriod().start().toString());
				lstEndDate.add(hist.getDatePeriod().end().toString());
				lstTaskItem.add(hist.getTaskItem().getOtpWorkCode1().get().v());
				lstTaskItem.add(hist.getTaskItem().getOtpWorkCode2().get().v());
				lstTaskItem.add(hist.getTaskItem().getOtpWorkCode3().get().v());
				lstTaskItem.add(hist.getTaskItem().getOtpWorkCode4().get().v());
				lstTaskItem.add(hist.getTaskItem().getOtpWorkCode5().get().v());
			});		
			for(int i = 0; i < listHist.size(); i ++) {
				lstId.add(i);
			}
			taskInitialSelSettingDto.setIds(lstId);
			taskInitialSelSettingDto.setLstStartDate(lstStartDate);
			taskInitialSelSettingDto.setLstEndDate(lstEndDate);
			taskInitialSelSettingDto.setLstTaskItem(lstTaskItem);
		}
		return taskInitialSelSettingDto;
	}
}
