package nts.uk.ctx.at.shared.dom.schedule.basicschedule;

/**
 * 
 * @author sonnh1
 *
 */
public interface BasicScheduleService {
	
	/**
	 * Return state of error checking process: nothing or throw error
	 */
	void ErrorCheckingStatus();

	/**
	 * Check needed of Work Time setting
	 * 
	 * @param workTypeCd
	 * @return SetupType
	 */
	SetupType checkNeededOfWorkTimeSetting(String workTypeCd);

	/**
	 * Check required of input type
	 * 
	 * @param dayType
	 * @return SetupType
	 */
	SetupType checkRequiredOfInputType(DayType dayType);

	/**
	 * Check work day
	 * 
	 * @param workTypeCd
	 * @return WorkStyle
	 */
	WorkStyle checkWorkDay(String workTypeCd);

	/**
	 * Check required
	 * 
	 * @param morningWorkStyle
	 * @param afternoonWorkStyle
	 * @return SetupType
	 */
	SetupType checkRequired(SetupType morningWorkStyle, SetupType afternoonWorkStyle);

}
