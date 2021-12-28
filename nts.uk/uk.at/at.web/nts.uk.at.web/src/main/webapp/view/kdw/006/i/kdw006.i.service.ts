module nts.uk.at.view.kdw006.i.service {
    let servicePath = {
        start: 'at/record/workrecord/manhourrecordusesetting/start',

        register: 'at/record/workrecord/manhourrecordusesetting/register',
    };

    export function start(): JQueryPromise<any> {
        return nts.uk.request.ajax(servicePath.start);
    }

    export function register(command: any): JQueryPromise<any> {
        return nts.uk.request.ajax(servicePath.register, command);
    }
}
