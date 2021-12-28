module nts.uk.ui.at.kdw013.resizer {
     @handler({
            bindingName: 'sb-resizer',
            validatable: true,
            virtual: false
        })
        export class SidebarResizerHandler implements KnockoutBindingHandler {
            init = (element: HTMLElement, valueAccessor: () => FullCalendar.Calendar, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } => {
                const calendar = valueAccessor();
                const cache = { md: -1, cw: 0 };
                const ctn = $('.fc-container.cf').get(0);

                $(element)
                    .on('mousemove', (e: JQueryEvent) => {
                        const oe = e.originalEvent as MouseEvent;
                        const bound = element.getBoundingClientRect();

                        if (bound.right - 7 <= oe.clientX && bound.right >= oe.clientX) {
                            ctn.classList.add('resizer');
                        } else {
                            ctn.classList.remove('resizer');
                        }
                    })
                    .on('mouseout', () => {
                        if (cache.md === -1) {
                            ctn.classList.remove('resizer');
                        }
                    })
                    .on('mousedown', (evt: JQueryEvent) => {
                        const oe = evt.originalEvent as MouseEvent;

                        if (ctn.classList.contains('resizer')) {
                            cache.md = oe.clientX;
                            cache.cw = element.offsetWidth;
                        }
                    })
                    .on('mouseup', () => {
                        cache.md = -1;
                        cache.cw = 0;
                    });

                $(window)
                    .on('mousemove', (evt: JQueryEvent) => {
                        const { cw, md } = cache;
                        const oe = evt.originalEvent as MouseEvent;

                        if (md !== -1) {
                            element.style.width = `${cw + oe.clientX - md}px`;

                            calendar.updateSize();
                        }
                    })
                    .on('mouseup', () => {
                        cache.md = -1;
                        cache.cw = 0;

                        ctn.classList.remove('resizer');
                    });

                return { controlsDescendantBindings: true };
            }

        }
}