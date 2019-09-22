import { NativeModules, Platform } from 'react-native';

const { SamsungErc20Wallet } = NativeModules;

async function exe(fn) {
    if (Platform.OS !== 'android') throw new Error('It supports only android')

    return fn()
}

const init = async function(params) {
    return exe(async () => SamsungErc20Wallet.init(params))
}

const needToUpdate = async function() {
    return exe(async () => SamsungErc20Wallet.needToUpdate())
}

const getAddress = async function() {
    return exe(async () => SamsungErc20Wallet.getAddress())
}

const getBalance = async function() {
    return exe(async () => SamsungErc20Wallet.getBalance())
}

const transfer = async function(params) {
    return exe(async () => SamsungErc20Wallet.transfer(params, ))
}

export {
    init,
    needToUpdate,
    getAddress,
    getBalance,
    transfer,
}

export default SamsungErc20Wallet;
