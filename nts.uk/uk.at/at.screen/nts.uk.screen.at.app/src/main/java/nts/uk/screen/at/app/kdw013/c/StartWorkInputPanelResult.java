package nts.uk.screen.at.app.kdw013.c;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;

/**
 * 利用可能作業リスト
 * @author tutt
 *
 */
@AllArgsConstructor
@Getter
public class StartWorkInputPanelResult {
	
	/** 利用可能作業1リスト */
	private List<Task> taskList1;
	
	/** 利用可能作業2リスト */
	private List<Task> taskList2;
	
	/** 利用可能作業3リスト */
	private List<Task> taskList3;
	
	/** 利用可能作業4リスト */
	private List<Task> taskList4;
	
	/** 利用可能作業5リスト */
	private List<Task> taskList5;
	
}
