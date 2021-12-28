package nts.uk.ctx.at.record.dom.workmanagement.workinitselectset;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 作業項目
 * @author HieuLt 
 *
 */
@Getter
@AllArgsConstructor
public class TaskItem {
	/**作業1 **/
	private Optional<TaskCode> otpWorkCode1;
	
	/**作業2 **/
	private Optional<TaskCode> otpWorkCode2;
	
	/**作業3 **/
	private Optional<TaskCode> otpWorkCode3;

	/**作業4 **/
	private Optional<TaskCode> otpWorkCode4;
	
	/**作業5 **/
	private Optional<TaskCode> otpWorkCode5;
}
