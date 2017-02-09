module nts.uk.pr.view.qmm011.e {
    export module service {
        var paths: any = {
            updateInsuranceBusinessType: "pr/insurance/labor/businesstype/update"

        };
        export function updateInsuranceBusinessType(insuranceBusinessType: model.InsuranceBusinessTypeDto) {
            var dfd = $.Deferred<any>();
            var data = { insuranceBusinessType: insuranceBusinessType, comanyCode: "CC0001" };
            nts.uk.request.ajax(paths.updateInsuranceBusinessType, data)
                .done(function(res: any) {
                    dfd.resolve(res);
                    //xyz
                })
                .fail(function(res) {
                    dfd.reject(res);
                })

        }
        export module model {
           
        }
    }
}