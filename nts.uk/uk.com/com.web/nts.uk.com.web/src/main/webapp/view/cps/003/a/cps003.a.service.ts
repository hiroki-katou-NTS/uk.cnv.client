module cps003.a.service {
    import ajax = nts.uk.request.ajax;

    export const push = {
        data: (command: any) => ajax(`ctx/pereg/grid-layout/save-data`, command),
        setting: (command: any) => ajax(`ctx/pereg/grid-layout/save-setting`, command)
    }

    export const fetch = {
        data: (command: any) => ajax(`ctx/pereg/grid-layout/get-data`, command),
        setting: (cid: string) => ajax(`ctx/pereg/grid-layout/get-setting/${cid}`), // cid: categoryId
        category: (uid: string) => ajax(`ctx/pereg/employee/category/getall/${uid}`)
    }
}