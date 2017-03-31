module qpp008.b.service {
    var paths: any = {
        getPaymentDateProcessingList: "pr/proto/paymentdatemaster/processing/findall"
    }
    
    export function getPaymentDateProcessingList(): JQueryPromise<Array<any>> {
        var dfd = $.Deferred<Array<any>>();
        nts.uk.request.ajax(paths.getPaymentDateProcessingList)
            .done(function(res: Array<any>) {
                dfd.resolve(res);
            }).fail(function(res: any) {
                dfd.reject(res);
            })
        return dfd.promise();
    }
}