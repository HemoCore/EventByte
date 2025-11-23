export class Duration {
    private static readonly MS_PER_SECOND = 1000
    private static readonly MS_PER_MINUTE = 60 * Duration.MS_PER_SECOND
    private static readonly MS_PER_HOUR = 60 * Duration.MS_PER_MINUTE
    private static readonly MS_PER_DAY = 24 * Duration.MS_PER_HOUR

    private constructor(private _value: number) { }

    /**
     * The duration in milliseconds
     **/
    get value(): number {
        return this._value
    }

    static fromMs(ms: number) {
        return new Duration(ms)
    }

    static fromSeconds(seconds: number) {
        return new Duration(seconds * Duration.MS_PER_SECOND)
    }

    static fromMinutes(minutes: number) {
        return new Duration(minutes * Duration.MS_PER_MINUTE)
    }

    static fromHours(hours: number) {
        return new Duration(hours * Duration.MS_PER_HOUR)
    }

    static fromDays(days: number) {
        return new Duration(days * Duration.MS_PER_DAY)
    }

    static fromDate(date: Date) {
        return new Duration(date.getTime())
    }

    get seconds() {
        return this.value / Duration.MS_PER_SECOND
    }

    get minutes() {
        return this.value / Duration.MS_PER_MINUTE
    }

    get hours() {
        return this.value / Duration.MS_PER_HOUR
    }

    get days() {
        return this.value / Duration.MS_PER_DAY
    }

    add(duration: Duration) {
        return new Duration(this.value + duration.value)
    }

    sub(duration: Duration) {
        return new Duration(this.value - duration.value)
    }

    toDate() {
        return new Date(this.value)
    }
}
