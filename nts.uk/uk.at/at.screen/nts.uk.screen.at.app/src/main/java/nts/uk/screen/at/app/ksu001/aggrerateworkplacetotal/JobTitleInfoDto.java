package nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal;

import lombok.Builder;
import lombok.Data;
import nts.uk.ctx.bs.employee.dom.jobtitle.info.JobTitleInfo;

@Data
@Builder
public class JobTitleInfoDto {
	
	// 会社ID
	public String companyId;

	/** The job title history id. */
	// 職位履歴ID
	public String jobTitleHistoryId;

	/** The is manager. */
	// 管理職とする
	public boolean isManager;
	
	/** The job title id. */
	// 職位ID
	public String jobTitleId;

	/** The job title code. */
	// 職位コード
	public String jobTitleCode;

	/** The job title name. */
	// 職位名称
	public String jobTitleName;
	
	

	/** The sequence code. */
	// 序列コード
	public String sequenceCode;
	
	
	public static JobTitleInfoDto fromDomain(JobTitleInfo domain) {
		
		return JobTitleInfoDto.builder()
					.companyId(domain.getCompanyId().v())
					.jobTitleHistoryId(domain.getJobTitleHistoryId())
					.isManager(domain.isManager())
					.jobTitleId(domain.getJobTitleId())
					.jobTitleCode(domain.getJobTitleCode().v())
					.jobTitleName(domain.getJobTitleName().v())
					.sequenceCode(null)
					.build();
	}
}
