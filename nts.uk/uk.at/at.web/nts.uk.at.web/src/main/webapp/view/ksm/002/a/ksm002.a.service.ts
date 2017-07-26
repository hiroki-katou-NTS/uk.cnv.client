module ksm002.a.service {
    import ajax = nts.uk.request.ajax;
    import format = nts.uk.text.format;
    var paths = {
        getAllSpecDate: "at/schedule/shift/businesscalendar/specificdate/getallspecificdate",
        getSpecDateByIsUse: "at/schedule/shift/businesscalendar/specificdate/getspecificdatebyuse/{0}",
        getCompanyStartDay: "at/schedule/shift/businesscalendar/businesscalendar/getcompanystartday",
        getComSpecDateByCompanyDate: "at/schedule/shift/specificdayset/company/getcompanyspecificdaysetbydate/{0}",
        getComSpecDateByCompanyDateWithName: "at/schedule/shift/specificdayset/company/getcompanyspecificdaysetbydatewithname/{0}",
        insertComSpecDate: "at/schedule/shift/specificdayset/company/insertcompanyspecificdate",
        updateComSpecDate: "at/schedule/shift/specificdayset/company/updatecompanyspecificdate",
        deleteComSpecDate: "at/schedule/shift/specificdayset/company/deletecompanyspecificdate"
    }
    
    
     /**
     * get all data
     */
    export function getAllSpecificDate(): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.getAllSpecDate );
    }
    /**
     * get specific date item is use
     */
    export function getSpecificDateByIsUse(useAtr: number): JQueryPromise<any> {
        return ajax("at", format(paths.getSpecDateByIsUse,useAtr));
    }
    /**
     *get companySpecificDate 
     */
    export function getCompanySpecificDateByCompanyDate(processDate: number): JQueryPromise<any> {
        return ajax("at", format(paths.getComSpecDateByCompanyDate,processDate));
    }

    /**
     *get companySpecificDate WITH NAME 
     */
    export function getCompanySpecificDateByCompanyDateWithName(processDate: string): JQueryPromise<any> {
        return ajax("at", format(paths.getComSpecDateByCompanyDateWithName,processDate));
    }

    /**
     * get start day of company
     */
    export function getCompanyStartDay(): JQueryPromise<any> {
        return ajax("at", paths.getCompanyStartDay);
    }
    /**
     * Insert companySpecDate
     */
    export function insertComSpecificDate(lstComSpecificDateItem: Array<viewmodel.CompanySpecificDateCommand>): JQueryPromise<Array<any>> {
        return ajax("at", paths.insertComSpecDate, lstComSpecificDateItem);
    }
    /**
     *Update companySpecDate 
     */
    export function updateComSpecificDate(command): JQueryPromise<any> {
        return ajax(paths.updateComSpecDate, command);
    }

    /**
     * Insert companySpecDate
     */
    export function deleteComSpecificDate(command): JQueryPromise<any> {
        return ajax(paths.deleteComSpecDate, command);
    }

}