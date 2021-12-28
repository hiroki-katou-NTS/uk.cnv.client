package nts.uk.ctx.at.record.dom.jobmanagement.displayformat;

import java.util.Optional;

/**
 * 工数入力表示フォーマットRepository
 * 
 * @author tutt
 *
 */
public interface ManHrInputDisplayFormatRepository {

	/**
	 * [1] Insert(工数入力表示フォーマット)
	 * 
	 * @param format
	 */
	void insert(ManHrInputDisplayFormat format);

	/**
	 * [2] Update(工数入力表示フォーマット)
	 * 
	 * @param format
	 */
	void update(ManHrInputDisplayFormat format);

	/**
	 * [3] Delete(会社ID)
	 * 
	 * @param cId
	 */
	void delete(String cId);

	/**
	 * [4] Get
	 * 
	 * @param cId
	 * @return
	 */
	Optional<ManHrInputDisplayFormat> get(String cId);
}
