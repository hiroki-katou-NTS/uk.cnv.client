package nts.uk.ctx.sys.portal.dom.webmenu.jobtitletying;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
/**
 * @author yennth
 * The class JobTitleTying
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class JobTitleTying extends AggregateRoot{
	
	/** The company id*/
	private String companyId;
	
	/**	The job id */
	private String jobId;
	
	/**	The web menu code */
	private String webMenuCode;

	public JobTitleTying(String companyId, String jobId, String webMenuCode) {
		this.companyId = companyId;
		this.jobId = jobId;
		this.webMenuCode = webMenuCode;
	}
	
	/**
	 * @author yennth
	 * update web menu code 
	 * @param companyId
	 * @param jobId
	 * @param webMenuCode
	 * @return
	 */
	public static JobTitleTying updateWebMenuCode(String companyId, String jobId, String webMenuCode){
		return new JobTitleTying (companyId, jobId, webMenuCode);
	}
	/**
	 * @author yennth
	 * insert web menu code
	 * @param companyId
	 * @param jobId
	 * @param webMenuCode
	 * @return
	 */
	public static JobTitleTying insertWebMenuCode(String companyId, String jobId, String webMenuCode){
		return new JobTitleTying (companyId, jobId, webMenuCode);
	}
	
	public static JobTitleTying createFromJavaType(String companyId, String jobId, String webMenuCode) {
		return new JobTitleTying(companyId, jobId, webMenuCode);
	}	
}
