module nts.uk.at.view.kdl001.a {
    export module service {
        const paths = {
            findByCodeList: "at/shared/worktimesetting/findByCodes",
            findByTime: "at/shared/worktimesetting/findByTime",
            findByCodeNew: 'at/shared/worktimesetting/findByCodeNew',
            registerKeyword: "at/shared/worktime/filtercondition/registerKeyword",
            searchByKeyword: "at/shared/worktime/filtercondition/searchByKeyword",
            searchByDetails: "at/shared/worktime/filtercondition/searchByDetails",
        }
        
        export function findByCodeList(command): JQueryPromise<any> {
            //return nts.uk.request.ajax(paths.findByCodeList, command);
            return nts.uk.request.ajax(paths.findByCodeNew, command);
        }
        
        export function findByTime(command): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.findByTime, command);
        }

        export function registerKeyword(param: any): JQueryPromise<any> {
          return nts.uk.request.ajax(paths.registerKeyword, param);
        }

        export function searchByKeyword(param: any): JQueryPromise<any> {
          return nts.uk.request.ajax(paths.searchByKeyword, param);
        }

        export function searchByDetails(param: any): JQueryPromise<any> {
          return nts.uk.request.ajax(paths.searchByDetails, param);
        }
    }
}