module nts.custombinding {
    export class SortListControl implements KnockoutBindingHandler {
        style: string = `<style type="text/css" id="sortable-style">
            .sortable-container {
                width: 100%;
                display: block;
                border: 1px solid #ccc;
            }

            .sortable-container .fixed-header {
                line-height: 24px;
                background-color: #CFF1A5;
                border-bottom: 1px solid #ccc;
            }

            .sortable-container .sortable-area {
                cursor: pointer;
                overflow-x: hidden;
                overflow-y: scroll;
                position: relative;
            }

            .sortable-container .sort-row-item {
                line-height: 24px;
                border: 1px solid #ccc;
                border-top: 0;
                border-left: 0;
                border-right: 0;                
                padding-left: 10px;
                overflow: hidden;
                text-overflow: ellipsis;
                font-size: 14px;
                font-family: "DroidSansMono", "Meiryo";
            }

            .sortable-container .sort-row-item.ui-sortable-helper {
                background-color: #fff;
                border-top: 1px solid #ccc;
            }

            .sortable-container .sort-row-item.ui-sortable-placeholder {
                background-color: #fff;
                visibility: visible !important;
            }
          </style>`;

        init = (element: HTMLElement, valueAccessor: any, allBindingsAccessor: any, viewModel: any, bindingContext: KnockoutBindingContext) => {
            let self = this,
                data = valueAccessor(),
                key = data.optionValue || 'id',
                text = data.optionText || 'name',
                height = data.height || ((data.rows || 15) * 24),
                $element = $(element),
                $head = $('<div>', { 'class': 'fixed-header', text: '名称' }),
                $body = $('<ul>', { 'class': 'sortable-area', 'height': String(height).replace(/[pxt]/g, '') + 'px' });

            // add style to <head> on first run
            if (!$('#sortable-style').length) {
                $('head').append(self.style);
            }

            $element
                .append($head)
                .append($body)
                .addClass('sortable-container');

            $body
                .sortable({
                    axis: "y",
                    items: ".sort-row-item",
                    update: function(event, ui) {
                        let _options = [],
                            rows = $(event.target).find('li.sort-row-item');

                        _.each(rows, element => {
                            _options.push($(element).data("item"));
                        });

                        data.options(_.filter(_options, x => !!x));
                    }
                }).disableSelection();
        }
        update = (element: HTMLElement, valueAccessor: any, allBindingsAccessor: any, viewModel: any, bindingContext: KnockoutBindingContext) => {
            let $element = $(element),
                data = valueAccessor(),
                key = data.optionsValue || 'id',
                text = data.optionText || 'name',
                $body = $element.find('ul.sortable-area'),
                options: Array<any> = ko.unwrap(data.options);

            $body.empty();
            _.each(options, row => {
                let $div = $('<li>', {
                    text: row[text],
                    'class': 'sort-row-item'
                })
                    .data('item', row)
                    .disableSelection();

                $body.append($div);
            });
        }
    }
}

ko.bindingHandlers["ntsSortList"] = new nts.custombinding.SortListControl();