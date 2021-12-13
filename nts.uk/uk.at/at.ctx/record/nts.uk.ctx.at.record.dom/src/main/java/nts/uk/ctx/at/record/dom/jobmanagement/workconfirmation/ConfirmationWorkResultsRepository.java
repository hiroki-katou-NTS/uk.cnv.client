package nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;

/**
 * @author thanhpv
 * @name 作業実績の確認Repository
 */
public interface ConfirmationWorkResultsRepository {

	/**
	 * @name [1] Insert(作業実績の確認)
	 */
	void insert(ConfirmationWorkResults confirmationWorkResults);
	
	/**
	 * @name [2] Update(作業実績の確認)
	 */
	void update(ConfirmationWorkResults confirmationWorkResults);
	
	/**
	 * @name [3] Delete(作業実績の確認)
	 */
	void delete(ConfirmationWorkResults confirmationWorkResults);
	
	/**
	 * @name [4] Get
	 * @Description 社員IDと対象日を指定して作業実績の確認を取得する
	 * @input targetSID 対象者
	 * @input targetYMD 対象日
	 * @output Optional<作業実績の確認>
	 */
	Optional<ConfirmationWorkResults> get(String targetSID, GeneralDate targetYMD);
	
	/**
	 * @name [5] Get
	 * @Description 社員IDと確認者と対象日を指定して作業実績の確認を取得する
	 * @input targetSID 対象者
	 * @input startDate 開始日
	 * @input endDate 終了日
	 * @output List<作業実績の確認>
	 */
	List<ConfirmationWorkResults> get(String targetSid, GeneralDate startDate, GeneralDate endDate);
	
	/**
	 * @name [6] Get
	 * @Description 社員IDと確認者と対象日を指定して作業実績の確認を取得する
	 * @input targetSID 対象者
	 * @input confirmerSid 確認者
	 * @input targetYMD 対象日
	 * @output Optional<作業実績の確認>
	 */
	Optional<ConfirmationWorkResults> get(String targetSid, String confirmerSid, GeneralDate targetYMD);
	
	/**
	 * @name [7] Get
	 * @Description 社員IDと確認者と対象日を指定して作業実績の確認を取得する
	 * @input targetSID 対象者
	 * @input confirmerSid 確認者
	 * @input startDate 開始日
	 * @input endDate 終了日
	 * @output List<作業実績の確認>
	 */
	List<ConfirmationWorkResults> get(String targetSid, String confirmerSid, GeneralDate startDate, GeneralDate endDate);
}
