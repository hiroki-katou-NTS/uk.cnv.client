module qmm013.b.service {
    var paths: any = {
        getPaymentDateProcessingList: "pr/proto/paymentdatemaster/processing/findall"
    }

    export function getPaymentDateProcessingList(): JQueryPromise<Array<any>> {
        var dfd = $.Deferred<Array<any>>();
        dfd.resolve();
        return dfd.promise();
    }
}