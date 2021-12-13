module nts.uk.at.view.kmk013.g {
    export module service {
        let paths = {
            loadMidnightTime: "at/record/daily/night/find",
            loadWeekManage: "ctx/at/shared/workrule/weekmanage/find",
            addMidnightTime: "at/record/daily/night/add",
            updateMidnightTime: "at/record/daily/night/update",
            regWeekManage: "ctx/at/shared/workrule/weekmanage/register",
        };

        export function loadMidnightTime(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.loadMidnightTime);
        }

        export function loadWeekManage(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.loadWeekManage);
        }

        export function regMidnightTimeMng(obj: any): JQueryPromise<any> {
            const dfd = $.Deferred<any>();
            loadMidnightTime().done(data => {
                if (data)
                    nts.uk.request.ajax(paths.updateMidnightTime, obj).done(() => {
                        dfd.resolve();
                    }).fail((error: any) => {
                        dfd.reject(error);
                    });
                else
                    nts.uk.request.ajax(paths.addMidnightTime, obj).done(() => {
                        dfd.resolve();
                    }).fail((error: any) => {
                        dfd.reject(error);
                    });
            }).fail(error => {
                dfd.reject(error);
            });
            return dfd.promise();
        }

        export function regWeekManage(obj: any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.regWeekManage, obj);
        }
    }
}