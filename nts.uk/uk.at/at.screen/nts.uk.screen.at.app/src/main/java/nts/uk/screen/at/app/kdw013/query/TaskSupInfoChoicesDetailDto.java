package nts.uk.screen.at.app.kdw013.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesDetail;

/**
 * @author thanhpv
 *
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskSupInfoChoicesDetailDto {

	/** 履歴ID */
	public String historyId;

	/** 項目ID */
	public Integer itemId;

	/** コード */
	public String code;

	/** 名称 */
	public String name;

	/** 外部コード */
	public String externalCode;

	public TaskSupInfoChoicesDetailDto(TaskSupInfoChoicesDetail domain) {
		this.historyId = domain.getHistoryId();
		this.itemId = domain.getItemId();
		this.code = domain.getCode().v();
		this.name = domain.getName().v();
		this.externalCode = domain.getExternalCode().map(c->c.v()).orElse(null);
	}
}

