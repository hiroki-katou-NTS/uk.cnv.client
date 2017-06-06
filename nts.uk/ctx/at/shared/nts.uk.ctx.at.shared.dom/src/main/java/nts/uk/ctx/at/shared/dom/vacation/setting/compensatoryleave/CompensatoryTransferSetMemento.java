/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave;

/**
 * The Interface CompensatoryTransferSetMemento.
 */
public interface CompensatoryTransferSetMemento {
	
	/**
	 * Sets the certain time.
	 *
	 * @param certainTime the new certain time
	 */
	void setCertainTime(OneDayTime certainTime);
	
	/**
	 * Sets the use division.
	 *
	 * @param useDivision the new use division
	 */
	void setUseDivision(boolean useDivision);
	
	/**
	 * Sets the one day time.
	 *
	 * @param oneDayTime the new one day time
	 */
	void setOneDayTime(OneDayTime oneDayTime);
	
	/**
	 * Sets the half day time.
	 *
	 * @param halfDayTime the new half day time
	 */
	void setHalfDayTime(OneDayTime halfDayTime);
	
	/**
	 * Sets the transfer division.
	 *
	 * @param transferDivision the new transfer division
	 */
	void setTransferDivision(TransferSettingDivision transferDivision); 
	
	/**
	 * Sets the compensatory occurrence division.
	 *
	 * @param compensatoryOccurrenceDivision the new compensatory occurrence division
	 */
	void setCompensatoryOccurrenceDivision(CompensatoryOccurrenceDivision compensatoryOccurrenceDivision);
}
