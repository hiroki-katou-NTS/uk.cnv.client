module nts.uk.at.view.kml004.a.service {
     var paths: any = {
        findAllSpecialHoliday: "shared/specialholiday/findByCid/",
        deleteSpecialHoliday: "shared/specialholiday/delete",
        addSpecialHoliday: "shared/specialholiday/add",
        updateSpecialHoliday: "shared/specialholiday/update",
        findWorkType: "at/screen/worktype/findAll"
    }

    export function findAllSpecialHoliday(): JQueryPromise<Array<viewmodel.model.ISpecialHolidayDto>> {
        return nts.uk.request.ajax("at",paths.findAllSpecialHoliday);
    }

    export function deleteSpecialHoliday(specialholiday: any) { 
        return nts.uk.request.ajax("at",paths.deleteSpecialHoliday, specialholiday)
    }

    export function addSpecialHoliday(specialholiday: any) {
        return nts.uk.request.ajax("at", paths.addSpecialHoliday, specialholiday);
    }

    export function updateSpecialHoliday(specialholiday: any) {
        return nts.uk.request.ajax("at",paths.updateSpecialHoliday, specialholiday);
    }
    
     export function findWorkType(): JQueryPromise<any> {
        return nts.uk.request.ajax("at",paths.findWorkType);
    }
}