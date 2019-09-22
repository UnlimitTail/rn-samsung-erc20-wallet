export interface InitParam {
    contractAddress: string;
    chainnetUrl: string;
    tokenDecimal: number;
}

export interface TransferParam {
    toAddress: string;
    amount: number;
    gasLimit: number;
}

export function init(params: InitParam): Promise<void>;
export function needToUpdate(): Promise<boolean>;
export function getAddress(): Promise<string>;
export function getBalance(): Promise<number>;
export function transfer(params: TransferParam): Promise<string>;
