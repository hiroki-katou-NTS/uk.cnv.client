package nts.uk.ctx.at.shared.dom.workrule.shiftmaster;

import java.util.Optional;

import lombok.Getter;
import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

/**
 * シフトマスタ
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.シフトマスタ.シフトマスタ
 * @author tutk
 */
@Getter
public class ShiftMaster extends WorkInformation implements DomainAggregate {

	private static final long serialVersionUID = 1L;

	/** 会社ID **/
	private final String companyId;
	/** コード **/
	private final ShiftMasterCode shiftMasterCode;
	/** 表示情報 **/
	private ShiftMasterDisInfor displayInfor;
	/** 取り込みコード **/
	private Optional<ShiftMasterImportCode> importCode;



	/**
	 * 作る
	 * @param companyId 会社ID
	 * @param shiftMaterCode コード
	 * @param displayInfor 表示情報
	 * @param workTypeCode 勤務種類コード
	 * @param workTimeCode 就業時間帯コード
	 * @param importCode 取り込みコード
	 */
	public ShiftMaster(
			String companyId, ShiftMasterCode shiftMaterCode, ShiftMasterDisInfor displayInfor
		,	String workTypeCode, String workTimeCode, Optional<ShiftMasterImportCode> importCode
	) {

		super( workTypeCode, workTimeCode );

		this.companyId = companyId;
		this.shiftMasterCode = shiftMaterCode;
		this.displayInfor = displayInfor;
		this.importCode = importCode;

	}

	/**
	 * 作る
	 * @param companyId 会社ID
	 * @param shiftMaterCode コード
	 * @param displayInfor 表示情報
	 * @param workTypeCode 勤務種類コード
	 * @param workTimeCode 就業時間帯コード
	 * @param importCode 取り込みコード
	 */
	public static ShiftMaster create(
			String companyId, ShiftMasterCode shiftMaterCode, ShiftMasterDisInfor displayInfor
		,	WorkTypeCode workTypeCode, Optional<WorkTimeCode> workTimeCode
		,	Optional<ShiftMasterImportCode> importCode
	) {
		return new ShiftMaster(
				companyId, shiftMaterCode, displayInfor
			,	workTypeCode.v(), workTimeCode.map(WorkTimeCode::v).orElse(null)
			,	importCode
		);
	}



	/**
	 * エラーチェックする
	 * @param require
	 */
	public void checkError(Require require) {

		val status = this.checkErrorCondition( require );
		switch( status ) {
			case WORKTYPE_WAS_DELETE:
				throw new BusinessException("Msg_1608");
			case WORKTIME_WAS_DELETE:
				throw new BusinessException("Msg_1609");
			case WORKTIME_ARE_REQUIRE_NOT_SET:
				throw new BusinessException("Msg_435");
			case WORKTIME_ARE_SET_WHEN_UNNECESSARY:
				throw new BusinessException("Msg_434");
			default:
		}

	}

	/**
	 * 変更する
	 * @param displayInfor 表示情報
	 * @param importCode 取り込みコード
	 * @param workInformation 勤務情報
	 */
	public void change(ShiftMasterDisInfor displayInfor, Optional<ShiftMasterImportCode> importCode, WorkInformation workInformation) {

		this.displayInfor = displayInfor;
		this.importCode = importCode;

		this.setWorkTimeCode( workInformation.getWorkTimeCode() );
		this.setWorkTypeCode( workInformation.getWorkTypeCode() );

	}





	public static interface Require extends WorkInformation.Require {
	}

}
