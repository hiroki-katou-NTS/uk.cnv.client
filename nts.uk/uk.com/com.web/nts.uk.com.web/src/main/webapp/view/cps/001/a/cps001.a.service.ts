module cps001.a.service {
    import ajax = nts.uk.request.ajax;
    import format = nts.uk.text.format;

    let paths: any = {
        layout: {
            getAll: "ctx/bs/person/maintenance/findAll",
            getDetails: "ctx/bs/person/maintenance/findOne/{0}"
        },
        category: {
            'getData': 'bs/employee/category/getAll/{0}'
        },
        person: {
            'getPerson': 'bs/employee/person/findByEmployeeId/{0}'
        },
        saveData: ''
    };

    export function getPerson(id: string) {
        return ajax(format(paths.person.getPerson, id));
    }

    export function getCats(id: string) {
        return ajax(format(paths.category.getData, id));
    };

    export function getAllLayout() {
        return ajax(paths.layout.getAll);
    };

    export function getCurrentLayout(id: string) {
        return ajax(format(paths.layout.getDetails, id));
    }

    export function getData() {
        return ajax(paths.getData);
    }

    export function saveData(command) {
        return ajax(paths.saveData, command);
    }
}