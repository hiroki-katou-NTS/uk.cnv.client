/// <reference path="../../reference.ts"/>

module nts.uk.ui.koExtentions {

    /**
     * CheckBox binding handler
     */
    class NtsFileUploadBindingHandler implements KnockoutBindingHandler {
        /**
         * Constructor.
         */
        constructor() {
        }

        /**
         * Init.
         */
        init(element: any, valueAccessor: () => any, allBindingsAccessor: () => any, viewModel: any, bindingContext: KnockoutBindingContext): void {
            // Get data
            let data = valueAccessor();
            let fileName = data.filename;
            let suportedExtension = ko.unwrap(data.accept);
            let textId: string = ko.unwrap(data.text);
            let control = $(element);
            let onchange = data.onchange;

            let fileuploadContainer = $("<div class='nts-fileupload-container'></div>");
            let fileBrowserButton = $("<INPUT class='browser-button' type='button' />");
            let browserButtonText: string;
            if (textId) {
                browserButtonText = nts.uk.resource.getText(textId);
            } else {
                browserButtonText = "browser";
            }
            fileBrowserButton.val(browserButtonText);
            let fileNameLable = $("<span class='filename'></span> ");
            let fileInput = $("<input style ='display:none' type='file' class='fileinput'/>");
            if (suportedExtension) {
                fileInput.attr("accept", suportedExtension.toString());
            }
            fileuploadContainer.append(fileBrowserButton);
            fileuploadContainer.append(fileNameLable);
            fileuploadContainer.append(fileInput);
            fileuploadContainer.appendTo(control);
            fileInput.change(function() {
                if (fileName) {
                    data.filename($(this).val());
                }
                if (typeof onchange == 'function') {
                    onchange($(this).val());
                }
            });
            fileBrowserButton.click(function() {
                fileInput.click();
            });

        }

        /**
         * Update
         */
        update(element: any, valueAccessor: () => any, allBindingsAccessor: () => any, viewModel: any, bindingContext: KnockoutBindingContext): void {
            let data = valueAccessor();
            let fileName = ko.unwrap(data.filename);
            let control = $(element);
            let fileNameLable = control.parent().find(".filename");
            fileNameLable.text(fileName);
        }
    }


    ko.bindingHandlers['ntsFileUpload'] = new NtsFileUploadBindingHandler();
}