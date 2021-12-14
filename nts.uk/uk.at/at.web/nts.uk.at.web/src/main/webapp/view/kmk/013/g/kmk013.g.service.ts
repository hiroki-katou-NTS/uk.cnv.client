module nts.uk.at.view.kmk013.g {
    export module service {
        let paths: any = {
            loadAllSetting : "shared/selection/func/loadAllSetting",
            loadTmpWorkMng: "at/record/workrecord/temporarywork/find",
            loadMidnightTime: "at/record/daily/night/find",
            loadWeekManage: "ctx/at/shared/workrule/weekmanage/find",
            loadGoOutManage: "at/shared/workrecord/goout/find",
            loadEntranceExit: "at/shared/workrecord/entranceexit/find",
            regAgg : "shared/selection/func/regAgg",
            regWorkMulti : "shared/selection/func/regWorkMulti",
            regTempWork : "shared/selection/func/regTempWork",
            regFlexWorkSet : "shared/selection/func/regFlexWorkSet",
            regTmpWorkMng: "at/record/workrecord/temporarywork/save",
            addMidnightTime: "at/record/daily/night/add",
            updateMidnightTime: "at/record/daily/night/update",
            regWeekManage: "ctx/at/shared/workrule/weekmanage/register",
            regGoOutManage: "at/shared/workrecord/goout/save",
            regEntranceExit: "at/shared/workrecord/entranceexit/save",
        };

        export function loadAllSetting(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.loadAllSetting);
        }

        export function loadTmpWorkSetting(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.loadTmpWorkMng);
        }

        export function loadMidnightTime(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.loadMidnightTime);
        }

        export function loadWeekManage(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.loadWeekManage);
        }

        export function loadGoOutManage(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.loadGoOutManage);
        }

        export function loadEntranceExit(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.loadEntranceExit);
        }

        export function regWorkMulti(obj: any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.regWorkMulti, obj);
        }

        export function regTempWork(obj: any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.regTempWork, obj);
        }

        export function regAgg(obj: any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.regAgg, obj);
        }

        export function regFlexWorkSet(obj: any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.regFlexWorkSet, obj);
        }

        export function regTmpWorkMng(use: boolean, obj: any): JQueryPromise<any> {
            if (use)
                return nts.uk.request.ajax(paths.regTmpWorkMng, obj);
            else {
                const dfd = $.Deferred<any>();
                dfd.resolve();
                return dfd.promise();
            }
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

        export function regGoOutManage(use: boolean, obj: any): JQueryPromise<any> {
            if (use)
                return nts.uk.request.ajax(paths.regGoOutManage, obj);
            else {
                const dfd = $.Deferred<any>();
                dfd.resolve();
                return dfd.promise();
            }
        }

        export function regEntranceExit(obj: any): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.regEntranceExit, obj);
        }
    }
}