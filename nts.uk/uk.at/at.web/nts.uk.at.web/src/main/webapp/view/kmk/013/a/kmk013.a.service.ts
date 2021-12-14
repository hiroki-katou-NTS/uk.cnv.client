module nts.uk.at.view.kmk013.a {
    export module service {
        let paths: any = {
            getDomainSet: "shared/selection/func/loadAllSetting"
        }
        export function getDomainSet(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getDomainSet);
        }
        export function saveAsExcel(): JQueryPromise<any> {
            let program = nts.uk.ui._viewModel.kiban.programName().split(" ");
       //     let programName = program[1] != null ? program[1] : "";           
                let domainType = "KMK013";
                if (program.length > 1){
                   program.shift();
                   domainType = domainType + program.join(" ");
                }
            return nts.uk.request.exportFile('/masterlist/report/print', { domainId: "CalculationSetting", domainType: domainType, languageId: 'ja', reportType: 0 });
        }
    }
}