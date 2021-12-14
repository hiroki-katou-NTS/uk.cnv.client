package nts.uk.ctx.bs.employee.app.find.wkpdep;

import com.google.common.base.Strings;

import eu.medsea.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Value;
import nts.uk.ctx.bs.employee.dom.department.master.DepartmentInformation;
import nts.uk.ctx.bs.employee.dom.department.master.service.DepartmentInforParam;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceInformation;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceInforParam;

/**
 * @author HungTT
 */
@Value
@AllArgsConstructor
public class InformationDto {

    private String id;
    private String code;
    private String name;
    private String dispName;
    private String genericName;
    private String hierarchyCode;
    private String externalCode;

    public InformationDto(WorkplaceInformation wkpInfor) {
        this.id = wkpInfor.getWorkplaceId();
        this.code = wkpInfor.getWorkplaceCode().v() == null ? "" : wkpInfor.getWorkplaceCode().v();
        this.name = wkpInfor.getWorkplaceName().v();
        this.dispName = wkpInfor.getWorkplaceDisplayName().v();
        this.genericName = wkpInfor.getWorkplaceGeneric().v();
        this.hierarchyCode = wkpInfor.getHierarchyCode().v();
        if (wkpInfor.getWorkplaceExternalCode().isPresent())
            this.externalCode = wkpInfor.getWorkplaceExternalCode().get().v();
        else
            this.externalCode = null;
    }

    public InformationDto(DepartmentInformation depInfor) {
        this.id = depInfor.getDepartmentId();
        this.code = depInfor.getDepartmentCode().v() == null ? "" : depInfor.getDepartmentCode().v();
        this.name = depInfor.getDepartmentName().v();
        this.dispName = depInfor.getDepartmentDisplayName().v();
        this.genericName = depInfor.getDepartmentGeneric().v();
        this.hierarchyCode = depInfor.getHierarchyCode().v();
        if (depInfor.getDepartmentExternalCode().isPresent())
            this.externalCode = depInfor.getDepartmentExternalCode().get().v();
        else
            this.externalCode = null;
    }

    public InformationDto(DepartmentInforParam depInfo) {
        this.id = depInfo.getDepartmentId();
        this.code = depInfo.getDepartmentCode();
        this.name = depInfo.getDepartmentName();
        this.dispName = depInfo.getDisplayName();
        this.genericName = depInfo.getGenericName();
        this.hierarchyCode = depInfo.getHierarchyCode();
        this.externalCode = depInfo.getExternalCode();
    }

    public InformationDto(WorkplaceInforParam wkpInfo) {
        this.id = wkpInfo.getWorkplaceId();
        this.code = wkpInfo.getWorkplaceCode();
        this.name = wkpInfo.getWorkplaceName();
        this.dispName = wkpInfo.getDisplayName();
        this.genericName = wkpInfo.getGenericName();
        this.hierarchyCode = wkpInfo.getHierarchyCode();
        this.externalCode = wkpInfo.getExternalCode();
    }
    
    public Integer getHierarchyCodeLength(){
    	if(Strings.isNullOrEmpty(this.hierarchyCode))
    		return null;
    	return this.hierarchyCode.length();
    }

}
