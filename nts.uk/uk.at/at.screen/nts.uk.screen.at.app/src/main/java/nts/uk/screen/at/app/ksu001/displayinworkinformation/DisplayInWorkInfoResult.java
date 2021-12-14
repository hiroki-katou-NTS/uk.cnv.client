/**
 * 
 */
package nts.uk.screen.at.app.ksu001.displayinworkinformation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto;

/**
 * @author laitv
 * 【Output】
     ・List<勤務種類, 必須任意不要区分, 出勤休日区分>
    ・ List<勤務予定（勤務情報）dto>
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DisplayInWorkInfoResult {
	
	public List<WorkTypeInfomation> listWorkTypeInfo;         // List<勤務種類, 必須任意不要区分, 出勤休日区分>
	public List<WorkScheduleWorkInforDto> listWorkScheduleWorkInfor;  // List<勤務予定（勤務情報）dto>
	
}
