module nts.uk.at.view.kdl001.a {
    export module service {
        var paths: any = {
            findByCodeList: "at/shared/worktimesetting/findByCodes",
            findByTime: "at/shared/worktimesetting/findByTime",
            findByCodeNew: 'at/shared/worktimesetting/findByCodeNew',
        }
        
        export function findByCodeList(command): JQueryPromise<any> {
            //return nts.uk.request.ajax(paths.findByCodeList, command);
            return nts.uk.request.ajax(paths.findByCodeNew, command);
        }
        
        export function findByTime(command): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.findByTime, command);
        }
    }
}