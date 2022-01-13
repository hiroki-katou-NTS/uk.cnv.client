package nts.uk.screen.at.app.kdw013.e;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.screen.at.app.kdw013.a.TaskDto;

/**
 * 
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class GetWorkDataMasterInforDto {
	
	/** 作業1一覧*/
	public List<TaskDto> tasks1;
	
	/** 作業2一覧*/
	public List<TaskDto> tasks2;
	
	/** 作業3一覧*/
	public List<TaskDto> tasks3;
	
	/** 作業4一覧*/
	public List<TaskDto> tasks4;
	
	/** 作業5一覧*/
	public List<TaskDto> tasks5;
}
