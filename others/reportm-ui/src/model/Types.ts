export enum ArgumentType {
    TEXT = "TEXT",
    DATE = "DATE",
}

export interface Argument {
    name: string
    type: ArgumentType
    value?: any
}

export interface Extract {
    name: string
    cellReference: string
}

export interface ReportAttributes {
    id: number
    name: string
    description?: string
    emails?: string
    reportTmpl: string
    args: Argument[]
    extracts: Extract[]
    reportQuery?: ReportQuery
}

export interface ReportQuery {
    total: number
    query: string
}

export interface CobApp {
    core: any,
    ui: any
}