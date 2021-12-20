package nts.uk.screen.com.app.find.cas012;


import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.pub.jobtitle.AffJobTitleBasicExport;
import nts.uk.ctx.bs.employee.pub.jobtitle.SyJobTitlePub;
import nts.uk.ctx.bs.employee.pub.workplace.SWkpHistExport;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;
import nts.uk.ctx.sys.auth.app.find.grant.roleindividual.dto.JobTitle;
import nts.uk.ctx.sys.auth.app.find.grant.roleindividual.dto.WorkPlaceInfo;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;

/**
 * ScreenQuery: ロール個人別付与を選択する
 * UKDesign.UniversalK.共通.CAS_権限設定.CAS012_管理者ロールの付与.A:管理者ロールの付与.Ａ：メニュー別OCD.ロール個人別付与を選択する
 * @author : chinh.hm
 */
@Stateless
public class SelectIndividualRoleGrantScreenQuery {
    @Inject
    private WorkplacePub workplacePub;
    @Inject
    private SyJobTitlePub syJobTitlePub;

    public WplAndJobInfoDto getPersonalPromotion(String cid, String sid){
        WplAndJobInfoDto rs = new WplAndJobInfoDto();
        GeneralDate baseDate = GeneralDate.today();
        rs.setCid(cid);
        rs.setEmployeeId(sid);
        Optional<SWkpHistExport> sWkpHistExport = workplacePub.findBySidNew(cid, sid, baseDate);
        if (sWkpHistExport.isPresent()) {
            SWkpHistExport export = sWkpHistExport.get();
            rs.setWorkplaceId(export.getWorkplaceId());
            rs.setWorkplaceCode(export.getWorkplaceCode());
            rs.setWkpDisplayName(export.getWkpDisplayName());
            rs.setWorkplaceName(export.getWorkplaceName());
        }
        Optional<AffJobTitleBasicExport> affJobTitleBasicExport = syJobTitlePub.getBySidAndBaseDate(sid, baseDate);
        if (affJobTitleBasicExport.isPresent()) {
            AffJobTitleBasicExport jobTitleBasicExport = affJobTitleBasicExport.get();
            rs.setJobTitleId(jobTitleBasicExport.getJobTitleId());
            rs.setJobTitleCode(jobTitleBasicExport.getJobTitleCode());
            rs.setJobTitleName(jobTitleBasicExport.getJobTitleName());
        }
        return rs;
    }
}
