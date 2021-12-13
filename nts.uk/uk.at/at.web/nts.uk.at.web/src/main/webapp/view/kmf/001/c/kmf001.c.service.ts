module nts.uk.pr.view.kmf001.c {
    export module service {
        /**
         *  Service paths
         */
        var servicePath: any = {
            findManageDistinct: 'ctx/at/share/vacation/setting/annualpaidleave/find/managedistinct',
            findPreemptionPermit: 'ctx/at/share/vacation/setting/annualpaidleave/find/preemptionpermit',
            findDisplayDivision: 'ctx/at/share/vacation/setting/annualpaidleave/find/displaydivision',
            findTimeUnit: 'ctx/at/share/vacation/setting/annualpaidleave/find/timeunit',
            findMaxDayReference: 'ctx/at/share/vacation/setting/annualpaidleave/find/maxdayreference',
            roundProcessClassification: 'ctx/at/share/vacation/setting/annualpaidleave/find/roundProcessCla',
            roundProcessClassific: 'ctx/at/share/vacation/setting/annualpaidleave/find/roundProcessClassific',
            
            save: 'ctx/at/share/vacation/setting/annualpaidleave/save',
            findSetting: 'ctx/at/share/vacation/setting/annualpaidleave/find/setting',
            findLeaveCount: "at/shared/scherec/leaveCount/get",
            registerLeaveCount: "at/shared/scherec/leaveCount/register"
        };
        
        export function findManageDistinct(): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.findManageDistinct);
        }
        
        export function findPreemptionPermit(): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.findPreemptionPermit);
        }
        
        export function findDisplayDivision(): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.findDisplayDivision);
        }
        
        export function findTimeUnit(): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.findTimeUnit);
        }
        
        export function findMaxDayReference(): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.findMaxDayReference);
        }
        
        export function roundProcessClassification(): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.roundProcessClassification);
        }
        
        export function roundProcessClassific(): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.roundProcessClassific);
        }

        export function save(command: any): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.save, command);
        }
        
        export function findSetting(): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.findSetting);
        }

        export function findLeaveCount(): JQueryPromise<any> {
          return nts.uk.request.ajax(servicePath.findLeaveCount);
        }

        export function registerLeaveCount(param: any): JQueryPromise<any> {
          return nts.uk.request.ajax(servicePath.registerLeaveCount, param);
        }

        /**
        * Model namespace.
        */
        export module model {

            export class EnumerationModel {

                value: number;
                fieldName: string;
                localizedName: string;

                constructor(value: number, fieldName: string, localizedName: string) {
                    let self = this;
                    self.value = value;
                    self.fieldName = fieldName;
                    self.localizedName = localizedName;
                }
            }
        }
    }
}
