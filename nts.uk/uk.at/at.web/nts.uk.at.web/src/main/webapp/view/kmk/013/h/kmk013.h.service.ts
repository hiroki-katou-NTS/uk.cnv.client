module nts.uk.at.view.kmk013.h {
    export module service {
        let paths: any = {
            findByCId : "at/record/calculation/findByCode",
            add:"at/record/calculation/add",
            update:"at/record/calculation/update",
        };
        
        export function findByCompanyId(): JQueryPromise<any> {
            return nts.uk.request.ajax("at", paths.findByCId);
        }
        
        export function save(obj: any): JQueryPromise<any> {
            const dfd = $.Deferred();
            findByCompanyId().done(data => {
                if (data)
                    nts.uk.request.ajax("at", paths.update, obj).done(() => {
                        dfd.resolve();
                    }).fail((error: any) => {
                        dfd.reject(error);
                    });
                else
                    return nts.uk.request.ajax("at", paths.add,obj).done(() => {
                        dfd.resolve();
                    }).fail((error: any) => {
                        dfd.reject(error);
                    });
            }).fail((error: any) => {
                dfd.reject(error);
            });
            return dfd.promise();
        }

    }
}