module cmm015.a.service {
    var paths = {
        getAllPayClassification: "basic/payclassification/findAllPayClassification",
        addPayClassification: "basic/payclassification/add",
        updatePayClassification: "basic/payclassification/update",
        removePayClassification: "basic/payclassification/remove"
    }

    /**
     * Get list 
     */

    export function getAllPayClassification(): JQueryPromise<Array<viewmodel.model.PayClassificationDto>> {
        var dfd = $.Deferred<Array<any>>();
        nts.uk.request.ajax("com", paths.getAllPayClassification)
            .done(function(res: Array<any>) {
                dfd.resolve(res);
            })
            .fail(function(res) {
                dfd.reject(res);
            })
        return dfd.promise();
    }

    /**
    * update 
    */

    export function addPayClassification(payClassification: viewmodel.model.PayClassificationDto) {
        var dfd = $.Deferred<Array<any>>();
        nts.uk.request.ajax("com", paths.addPayClassification, payClassification).done(
            function(res: any) {
                dfd.resolve(res);
            }
            )
            .fail(function(res) {
                dfd.reject(res);
            })
        return dfd.promise();
    }


    /**
     * update Classification
     */

    export function updatePayClassification(payClassification: viewmodel.model.PayClassificationDto) {
        var dfd = $.Deferred<Array<any>>();
        nts.uk.request.ajax("com", paths.updatePayClassification, payClassification).done(
            function(res: any) {
                dfd.resolve(res);
            }
        )
            .fail(function(res) {
                dfd.reject(res);
            })

        return dfd.promise();
        
    }

    /**
    * remove Classification
    */

    export function removePayClassification(payClassification: viewmodel.model.RemovePayClassificationCommand) {
        var dfd = $.Deferred<Array<any>>();
        nts.uk.request.ajax("com", paths.removePayClassification, payClassification).done(
            function(res: any) {
                dfd.resolve(res);
            }
        )
            .fail(function(res) {
                dfd.reject(res);
            })
        return dfd.promise();
    }


}
