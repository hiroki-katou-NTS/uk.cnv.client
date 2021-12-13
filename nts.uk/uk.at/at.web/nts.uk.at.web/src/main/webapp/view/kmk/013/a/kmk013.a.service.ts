module nts.uk.at.view.kmk013.a {
    export module service {
        let paths: any = {
            getDomainSet: "shared/selection/func/loadAllSetting"
        }
        export function getDomainSet(): JQueryPromise<any> {
            return nts.uk.request.ajax(paths.getDomainSet);
        }
        export function saveAsExcel(): JQueryPromise<any> {
            let program = __viewContext.program.programName;
       //     let programName = program[1] != null ? program[1] : "";           
                let domainType = "KMK013";
                if (program.length > 1){
                   domainType = domainType + "_" + program;
                }
            return nts.uk.request.exportFile('/masterlist/report/print', { domainId: "CalculationSetting", domainType: domainType, languageId: 'ja', reportType: 0 });
        }
    }
}