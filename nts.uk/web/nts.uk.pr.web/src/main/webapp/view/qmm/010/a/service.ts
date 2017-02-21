module nts.uk.pr.view.qmm010.a {
    export module service {
        var paths: any = {
            findAllLaborInsuranceOffice: "ctx/pr/core/insurance/labor/findall",
            findLaborInsuranceOffice: "ctx/pr/core/insurance/labor/findLaborInsuranceOffice",
            addLaborInsuranceOffice: "ctx/pr/core/insurance/labor/add",
            updateLaborInsuranceOffice: "ctx/pr/core/insurance/labor/update",
            deleteLaborInsuranceOffice: "ctx/pr/core/insurance/labor/delete",
        };

        //Function connection service FindAll Labor Insurance Office
        export function findAllLaborInsuranceOffice(): JQueryPromise<Array<model.LaborInsuranceOfficeFindOutDto>> {
            var dfd = $.Deferred<Array<model.LaborInsuranceOfficeInDto>>();
            nts.uk.request.ajax(paths.findAllLaborInsuranceOffice)
                .done(function(res: Array<model.LaborInsuranceOfficeFindOutDto>) {
                    dfd.resolve(res);
                    //xyz
                })
                .fail(function(res) {
                    dfd.reject(res);
                })
            return dfd.promise();
        }
        //Function connection service findLaborInsuranceOffice By Code
        export function findLaborInsuranceOffice(officeCode: string): JQueryPromise<model.LaborInsuranceOfficeDto> {
            var dfd = $.Deferred<model.LaborInsuranceOfficeDto>();
            nts.uk.request.ajax(paths.findLaborInsuranceOffice + "/" + officeCode)
                .done(function(res: model.LaborInsuranceOfficeDto) {
                    dfd.resolve(res);
                    //xyz
                })
                .fail(function(res) {
                    dfd.reject(res);
                })
            return dfd.promise();
        }
        //Function connection service add LaborInsuranceOffice
        export function addLaborInsuranceOffice(laborInsuranceOfficeDto: model.LaborInsuranceOfficeDto): JQueryPromise<any> {
            var dfd = $.Deferred<any>();
            var data = { laborInsuranceOfficeDto: laborInsuranceOfficeDto };
            nts.uk.request.ajax(paths.addLaborInsuranceOffice, data)
                .done(function(res: any) {
                    dfd.resolve(res);
                    //xyz
                })
                .fail(function(res) {
                    dfd.reject(res);
                })
            return dfd.promise();
        }
        //Function connection service update LaborInsuranceOffice
        export function updateLaborInsuranceOffice(laborInsuranceOfficeDto: model.LaborInsuranceOfficeDto): JQueryPromise<any> {
            var dfd = $.Deferred<any>();
            var data = { laborInsuranceOfficeDto: laborInsuranceOfficeDto };
            nts.uk.request.ajax(paths.updateLaborInsuranceOffice, data)
                .done(function(res: any) {
                    dfd.resolve(res);
                    //xyz
                })
                .fail(function(res) {
                    dfd.reject(res);
                })
            return dfd.promise();
        }
        //Function connection service delete LaborInsuranceOffice  
        export function deleteLaborInsuranceOffice(laborInsuranceOfficeDeleteDto: model.LaborInsuranceOfficeDeleteDto): JQueryPromise<any> {
            var dfd = $.Deferred<any>();
            var data = { laborInsuranceOfficeDeleteDto: laborInsuranceOfficeDeleteDto };
            nts.uk.request.ajax(paths.deleteLaborInsuranceOffice, data)
                .done(function(res: any) {
                    dfd.resolve(res);
                    //xyz
                })
                .fail(function(res) {
                    dfd.reject(res);
                })
            return dfd.promise();
        }

        /**
       * Model namespace. LaborInsuranceOfficeDto
       */
        export module model {
            export class LaborInsuranceOfficeDto {
                /** The code. officeCode*/
                code: string;
                /** The name. officeName*/
                name: string;
                /** The short name. */
                shortName: string;
                /** The pic name. */
                picName: string;
                /** The pic position. */
                picPosition: string;
                /** The potal code. */
                potalCode: string;
                /** The prefecture. */
                prefecture: string;
                /** The address 1 st. */
                address1st: string;
                /** The address 2 nd. */
                address2nd: string;
                /** The kana address 1 st. */
                kanaAddress1st: string;
                /** The kana address 2 nd. */
                kanaAddress2nd: string;
                /** The phone number. */
                // TODO: TelephoneNo
                phoneNumber: string;
                /** The city sign. */
                citySign: string;
                /** The office mark. */
                officeMark: string;
                /** The office no A. */
                officeNoA: string;
                /** The office no B. */
                officeNoB: string;
                /** The office no C. */
                officeNoC: string;
                /** The memo. */
                memo: string;

            }
            export class LaborInsuranceOfficeFindOutDto {
                /** The code. officeCode*/
                code: string;
                /** The name. officeName*/
                name: string;
            }
            export enum TypeActionLaborInsuranceOffice {
                add = 1,
                update = 2
            }
            export class LaborInsuranceOfficeDeleteDto {
                code: string;
                version: number;
            }

        }
    }
}