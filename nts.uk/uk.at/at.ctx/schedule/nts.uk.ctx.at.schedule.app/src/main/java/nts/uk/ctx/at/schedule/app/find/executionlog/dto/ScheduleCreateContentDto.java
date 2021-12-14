package nts.uk.ctx.at.schedule.app.find.executionlog.dto;

import lombok.Setter;
import nts.arc.primitive.PrimitiveValueBase;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.schedule.dom.executionlog.*;

import java.util.Optional;

/**
 * The Class ExecutionContentDto.
 */
@Setter
public class ScheduleCreateContentDto implements ScheduleCreateContentSetMemento {

	/** The execution id. */
	public String executionId;

	/** 確定済みにする */
	public Boolean confirm;

	/** 作成種類 */
	public Integer implementAtr;

	/** 再作成条件.対象者を限定する */
    public Boolean reTargetAtr;

    /** 再作成条件.確定済みも対象とする */
    public Boolean reOverwriteConfirmed;

    /** 再作成条件.手修正・申請反映も対象とする */
    public Boolean reOverwriteRevised;

    /** 再作成条件.対象者の条件.異動者 */
    public Boolean transfer;

    /** 再作成条件.対象者の条件.休職休業者 */
    public Boolean leaveOfAbsence;

    /** 再作成条件.対象者の条件.短時間勤務者 */
    public Boolean shortWorkingHours;

    /** 再作成条件.対象者の条件.労働条件変更者 */
    public Boolean changedWorkingConditions;

    /** 作成方法の指定.作成方法 */
	public Integer createMethodAtr;

	/** 作成方法の指定.コピー開始日 */
	public GeneralDate copyStartDate;

	/** 作成方法の指定.マスタ参照先 */
	public Integer referenceMaster;

	/** 月間パターンコード */
	public String monthlyPatternCode;

	public String monthlyPatternName;

    /** The start date. */
	public GeneralDate startDate;
	
	/** The end date. */
	public GeneralDate endDate;
	
	/** The execution start. */
	public GeneralDateTime executionStart;
	
	/** The execution end. */
	public GeneralDateTime executionEnd;
	
	/** The count execution. */
	public Integer countExecution;
	
	/** The count error. */
	public Integer countError;

	@Override
	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	@Override
	public void setcreationType(ImplementAtr creationType) {
		this.implementAtr = creationType.value;
	}

	@Override
	public void setSpecifyCreation(SpecifyCreation specifyCreation) {
		this.copyStartDate = specifyCreation.getCopyStartDate().orElse(null);
		this.createMethodAtr = specifyCreation.getCreationMethod().value;
		this.referenceMaster = specifyCreation.getReferenceMaster().map(i -> i.value).orElse(null);
		this.monthlyPatternCode = specifyCreation.getMonthlyPatternCode().map(PrimitiveValueBase::v).orElse(null);
	}

	@Override
	public void setRecreateCondition(Optional<RecreateCondition> recreateCondition) {
		recreateCondition.ifPresent(cnd -> {
		    this.reTargetAtr = cnd.getReTargetAtr();
		    this.reOverwriteConfirmed = cnd.getReOverwriteConfirmed();
		    this.reOverwriteRevised = cnd.getReOverwriteRevised();
			cnd.getNarrowingEmployees().ifPresent(cnd2 -> {
                this.transfer = cnd2.isTransfer();
                this.leaveOfAbsence = cnd2.isLeaveOfAbsence();
                this.shortWorkingHours = cnd2.isShortWorkingHours();
                this.changedWorkingConditions = cnd2.isChangedWorkingConditions();
			});
		});
	}


}
