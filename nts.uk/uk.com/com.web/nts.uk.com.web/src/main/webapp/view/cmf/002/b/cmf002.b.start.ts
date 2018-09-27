module nts.uk.com.view.cmf002.b {
    __viewContext.ready(function() {
        __viewContext.transferred.ifPresent(data => {
            if (!data.roleAuthority) {
                 nts.uk.request.jump("/view/cmf/002/a/index.xhtml");
            } else {
                nts.uk.ui.windows.setShared("CMF002B_PARAMS", data.roleAuthority);
            }
        });
        __viewContext['screenModel'] = new viewmodel.ScreenModel();
            __viewContext.bind(__viewContext['screenModel']);

    });
}
