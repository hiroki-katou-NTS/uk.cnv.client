module jhn002.a.service {
    import ajax = nts.uk.request.ajax;
    import format = nts.uk.text.format;

    let paths = {
        getListReport:          'hr/notice/report/regis/person/getAll-Jhn002',
        getReportDetails:       'hr/notice/report/item/findOne',
        saveData:               'hr/notice/report/regis/report/agent/save',       
        getListDoc:             'hr/notice/report/regis/person/document/findAll',
        layout: {
            getAll:     'ctx/pereg/person/maintenance/findSimple/{0}',
            getDetails: 'ctx/pereg/person/maintenance/findLayoutData',
            register:   'facade/pereg/register'
        }
    };
    
    // chỉ bao gồm những report chưa xóa.
    export function getListReport() {
        return ajax(format(paths.getListReport));
    }
    
    export function getListReportSaveDraft() {
        return ajax('hr' , paths.getListReportSaveDraft);
    }

    export function getReportDetails(obj: any) {
        return ajax(paths.getReportDetails, obj);
    }
    
    export function removeData(objRemove) {
        return ajax('hr' , paths.remove, objRemove);
    }
   
    export function getListDoc(param: any) {
        return ajax('hr' , paths.getListDoc, param);
    }
    
     export function saveData(command: any) {
        return ajax('hr' , paths.saveData, command);
    }
    
    export function saveDraftData(command: any) {
        return ajax('hr' , paths.saveDraftData, command);
    }
}