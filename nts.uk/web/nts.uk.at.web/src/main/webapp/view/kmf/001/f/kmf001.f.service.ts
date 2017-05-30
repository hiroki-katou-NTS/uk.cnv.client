module nts.uk.pr.view.kmf001.f {
    export module service {
        /**
         *  Service paths
         */
        var servicePath: any = {
            update: 'ctx/at/shared/vacation/setting/compensatoryleave/save',
            find: 'ctx/at/shared/vacation/setting/compensatoryleave/find',
            enumManageDistinct: 'ctx/at/shared/vacation/setting/compensatoryleave/enum/managedistinct',
            enumApplyPermission: 'ctx/at/shared/vacation/setting/compensatoryleave/enum/applypermission',
            enumExpirationTime: 'ctx/at/shared/vacation/setting/compensatoryleave/enum/expirationtime',
            enumTimeVacationDigestiveUnit: 'ctx/at/shared/vacation/setting/compensatoryleave/enum/timevacationdigestiveunit',
            enumCompensatoryOccurrenceDivision: 'ctx/at/shared/vacation/setting/compensatoryleave/enum/compensatoryoccurrencedivision',
            enumTransferSettingDivision: 'ctx/at/shared/vacation/setting/compensatoryleave/enum/transfersettingdivision'
        };

        export function update(command: any): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.update, command);
        }

        export function find(): JQueryPromise<any> {
            return nts.uk.request.ajax(servicePath.find);
        }

        /**
             * Get VacationExpiration Enum.
             */
        export function getEnumManageDistinct(): JQueryPromise<Array<model.Enum>> {
            return nts.uk.request.ajax(servicePath.enumManageDistinct);
        }
        
        export function getEnumApplyPermission(): JQueryPromise<Array<model.Enum>> {
            return nts.uk.request.ajax(servicePath.enumApplyPermission);
        }
        /**
             * Get VacationExpiration Enum.
             */
        export function getEnumExpirationTime(): JQueryPromise<Array<model.Enum>> {
            return nts.uk.request.ajax(servicePath.enumExpirationTime);
        }
        /**
             * Get VacationExpiration Enum.
             */
        export function getEnumTimeVacationDigestiveUnit(): JQueryPromise<Array<model.Enum>> {
            return nts.uk.request.ajax(servicePath.enumTimeVacationDigestiveUnit);
        }
        /**
             * Get VacationExpiration Enum.
             */
        export function getEnumCompensatoryOccurrenceDivision(): JQueryPromise<Array<model.Enum>> {
            return nts.uk.request.ajax(servicePath.enumCompensatoryOccurrenceDivision);
        }
        /**
             * Get VacationExpiration Enum.
             */
        export function getEnumTransferSettingDivision(): JQueryPromise<Array<model.Enum>> {
            return nts.uk.request.ajax(servicePath.enumTransferSettingDivision);
        }
        /**
        * Model namespace.
        */
        export module model {

            export class EnumerationModel {

                code: string;
                name: string;

                constructor(code: string, name: string) {
                    let self = this;
                    self.name = name;
                    self.code = code;
                }
            }

            export class Enum {
                value: number;
                fieldName: string;
                localizedName: string;

                constructor(value: number, fieldName: string, localizedName: string) {
                    this.value = value;
                    this.fieldName = fieldName;
                    this.localizedName = localizedName;
                }
            }
        }
    }
}
