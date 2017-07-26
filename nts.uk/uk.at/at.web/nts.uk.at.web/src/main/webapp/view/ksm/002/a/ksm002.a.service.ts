module ksm002.a.service {
    var paths = {
        //Workplace
        getWorkplaceSpecDateSet: "at/schedule/shift/specificdayset/workplace/getworkplacespecificdate",
        //Company
        getAllSpecDate: "at/schedule/shift/businesscalendar/specificdate/getallspecificdate",
        getSpecDateByIsUse: "at/schedule/shift/businesscalendar/specificdate/getspecificdatebyuse",
        getCompanyStartDay: "at/schedule/shift/businesscalendar/businesscalendar/getcompanystartday",
        getComSpecDateByCompanyDate: "at/schedule/shift/specificdayset/company/getcompanyspecificdaysetbydate",
        insertComSpecDate: "at/schedule/shift/specificdayset/company/insertcompanyspecificdate",
        updateComSpecDate: "at/schedule/shift/specificdayset/company/updatecompanyspecificdate",
        deleteComSpecDate: "at/schedule/shift/specificdayset/company/deletecompanyspecificdate",
        getComSpecDateByCompanyDateWithName: "at/schedule/shift/specificdayset/company/getcompanyspecificdaysetbydatewithname"
    }
    
    
     /**
     * 
     */
    export function getAllSpecificDate(): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getAllSpecDate );
    }
    /**
     * 
     */
    export function getSpecificDateByIsUse(useAtr: number): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getSpecDateByIsUse + "/" + useAtr);
    }
    /**
     *get companySpecificDate 
     */
    export function getCompanySpecificDateByCompanyDate(processDate: number): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getComSpecDateByCompanyDate + "/" + processDate);
    }

    /**
     *get companySpecificDate WITH NAME 
     */
    export function getCompanySpecificDateByCompanyDateWithName(processDate: string): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getComSpecDateByCompanyDateWithName + "/" + processDate);
    }

    /**
     * get start day of company
     */
    export function getCompanyStartDay(): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getCompanyStartDay);
    }
    /**
     * Insert companySpecDate
     */
    export function insertComSpecificDate(lstComSpecificDateItem: Array<viewmodel.CompanySpecificDateCommand>): JQueryPromise<Array<any>> {
        return nts.uk.request.ajax("at", paths.insertComSpecDate, lstComSpecificDateItem);
    }
    /**
     *Update companySpecDate 
     */
    export function updateComSpecificDate(lstUpdComSpecificDateItem: Array<viewmodel.CompanySpecificDateCommand>): JQueryPromise<Array<any>> {
        return nts.uk.request.ajax("at", paths.updateComSpecDate, lstUpdComSpecificDateItem);
    }

    /**
     * Insert companySpecDate
     */
    export function deleteComSpecificDate(command): JQueryPromise<any> {
        return nts.uk.request.ajax(paths.deleteComSpecDate, command);
    }

}