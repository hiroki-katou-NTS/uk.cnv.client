module nts.uk.cloud.view.cld001.a {

    export module service {
        var servicePath: any = {
            regist: "ctx/cld/operate/tenant/regist",
            generatePassword: "ctx/cld/operate/tenant/generatePassword"
        }
        export function registTenant(command: any): JQueryPromise<void> {
            return nts.uk.request.ajax(servicePath.regist, command);
        }

        export function generatePassword(): JQueryPromise<Array<model.generatePasswordDto>> {
            return nts.uk.request.ajax(servicePath.generatePassword);
        }

        export interface generatePasswordDto {
            password: string;
        }

        export interface registTenantDto {
        	tenanteCode: string;
        	tenantPassword: string;
        	tenantStartDate: string;
        	administratorLoginId: string;
        	administratorPassword: string;
        	optionCode: string;
			companyName: string;
        }
    }
}