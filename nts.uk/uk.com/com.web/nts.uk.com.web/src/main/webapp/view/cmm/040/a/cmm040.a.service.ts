module nts.uk.com.view.cmm040.a.service {

    let paths: any = {
        getDataStart: "at/screen/cmm040/start",
        update: "at/screen/cmm040/update",
        deleteWorkLocation: "at/screen/cmm040/deleteScreenA",
        getWorkPlace: "at/screen/cmm040/getWorkPlace",
        radiusEnum: 'at/screen/cmm040/enum',
        insert: "at/screen/cmm040/insert",
        checkWorkplace: "at/screen/cmm040/checkWorkplace",
        checkDelete: "at/screen/cmm040/checkDelete"
    }

    export function getDataStart(): any {
        return nts.uk.request.ajax("at", paths.getDataStart);

    }
    export function insert(param): any {
        return nts.uk.request.ajax("at", paths.insert, param);

    }
    export function update(param): any {
        return nts.uk.request.ajax("at", paths.update, param);

    }

    export function deleteWorkLocation(param): any {
        return nts.uk.request.ajax("at", paths.deleteWorkLocation, param);

    }
    export function getWorkPlace(param): any {
        return nts.uk.request.ajax("at", paths.getWorkPlace, param);

    }
    export function radiusEnum(): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.radiusEnum);
    }

    export function checkWorkplace(param: any): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.checkWorkplace, param);
    }

    export function checkDelete(): JQueryPromise<any> {
        return nts.uk.request.ajax("at", paths.checkDelete);
    }
}