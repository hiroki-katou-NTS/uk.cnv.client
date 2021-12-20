module nts.uk.ui.at.kdw013.share {
    const { time } = nts.uk as any;
    const { byId } = time.format as { byId: (format: string, time: number) => string };

    // get time as minute in date
    export const getTimeOfDate = (date: Date) => {
        if (!_.isDate(date)) {
            return null;
        }

        const hour = date.getHours();
        const minute = date.getMinutes();

        return hour * 60 + minute;
    };

    // update time of date
    export const setTimeOfDate = (date: Date, time: number) => {
        if (!validateNumb(time)) {
            return moment(date).clone().toDate();
        }

        const hour = Math.floor(time / 60);
        const minute = Math.floor(time % 60);

        return moment(date).clone().set('hour', hour).set('minute', minute).toDate();
    };

    // validate (model) value range
    export const validateNumb = (value: number | null) => {
        return _.isNumber(value) && !_.isNaN(value) && 0 <= value && value <= 1440;
    };

    // convert value from model to view
    export const number2String = (value: number) => {
        const hour = Math.floor(value / 60);
        const minute = Math.floor(value % 60);

        return `${hour}:${_.padStart(`${minute}`, 2, '0')}`;
    };

    // convert value from view to model
    // null: null value
    // -3: invalid charactor
    // -2: minute invalid
    // -1: negative value
    // 1441: over 24 hours value
    export const string2Number = (value: string): number | null => {
        if (value === '') {
            return null;
        }

        const numb = Number(value.replace(/:/, ''));

        // input other charactor
        if (isNaN(numb)) {
            return -3;
        }

        // input lower 0
        if (numb < 0) {
            return -1;
        }

        const hour = Math.floor(numb / 100);
        const minute = Math.floor(numb % 100);

        // hour is not valid
        if (hour > 24) {
            return 1440;
        }

        // case is not equal 24:00
        if (hour === 24 && minute !== 0) {
            return 1440;
        }

        // minute is not valid
        if (minute > 59) {
            return -2;
        }

        return hour * 60 + minute;
    };

    export const formatTime = (value: number, format: 'Clock_Short_HM' | 'Time_Short_HM' | 'Time_Short_HM') => byId(format, value);

    export const getTasks = (wg: a.WorkGroupDto, tasks: c.TaskDto[]) => {
        const { workCD1, workCD2, workCD3, workCD4, workCD5 } = wg;
        const task1 = _.find(tasks,{ 'taskFrameNo': 1, 'code': workCD1 });
        const task2 = _.find(tasks,{ 'taskFrameNo': 2, 'code': workCD2 });
        const task3 = _.find(tasks,{ 'taskFrameNo': 3, 'code': workCD3 });
        const task4 = _.find(tasks,{ 'taskFrameNo': 4, 'code': workCD4 });
        const task5 = _.find(tasks,{ 'taskFrameNo': 5, 'code': workCD5 });

        return [task1, task2, task3, task4, task5];
    };

    export const getTask = (wg: a.WorkGroupDto, tasks: c.TaskDto[]) => {
        const [task1, task2, task3, task4, task5] = getTasks(wg, tasks);

        return task5 || task4 || task3 || task2 || task1;
    };

    export const getTitles = (wg: a.WorkGroupDto, tasks: c.TaskDto[], character?) => {

        let taskNames = _.chain(getTasks(wg, tasks))
            .filter((item) => { return item })
            .map((item) => {
                return item.displayInfo.taskName;
            }).value();

        return taskNames.join(character ? character : "\n");

    };

    export const getBackground = (wg: a.WorkGroupDto, tasks: c.TaskDto[]) => {
        const task =  _.find(tasks, { 'taskFrameNo': 1, 'code': wg.workCD1 });

        if (task) {
            const { displayInfo } = task;

            if (displayInfo) {
                return displayInfo.color;
            }
        }

        return '';
    };
}