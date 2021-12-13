module nts.uk.at.view.kdl058.a.service {
    var paths: any = {
        getAllHoliday: "at/schedule/holiday/getAllHoliday",
        createPublicHoliday : "at/schedule/holiday/create",
        updatePublicHoliday : "at/schedule/holiday/update",
        deletePublicHoliday : "at/schedule/holiday/delete",
    }
    export function getHolidayByDate(): JQueryPromise<Array<viewmodel.PublicHoliday>> {
        return nts.uk.request.ajax("at", paths.getAllHoliday, {});
    }

    /** Create PublicHoliday */
    export function createPublicHoliday(publicHoliday: viewmodel.PublicHoliday): JQueryPromise<void> {
        return nts.uk.request.ajax("at", paths.createPublicHoliday, publicHoliday);
    }
    /** Update PublicHoliday */
    export function updatePublicHoliday(publicHoliday: viewmodel.PublicHoliday): JQueryPromise<void> {
        return nts.uk.request.ajax("at", paths.updatePublicHoliday, publicHoliday);
    }

    /** Delete PublicHoliday */
    export function deletePublicHoliday(command: any): JQueryPromise<void> {
        return nts.uk.request.ajax("at", paths.deletePublicHoliday, command);
    }
}