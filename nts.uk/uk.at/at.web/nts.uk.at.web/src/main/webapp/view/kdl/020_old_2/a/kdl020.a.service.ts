module nts.uk.at.view.kdl020.a.service {

    import ajax = nts.uk.request.ajax;
    import format = nts.uk.text.format;
    var paths: any = {
        startPage: "at/request/dialog/annualholiday/startPage",
        changeID: "at/request/dialog/annualholiday/changeID"

    }
 
    export function startPage(param){ 
        return ajax("at",paths.startPage, param);
    }
    
    export function changeID(param){ 
        return ajax("at",paths.changeID, param);
    }
}