import { NativeModules, Platform } from 'react-native';

const { SamsungErc20Wallet } = NativeModules;

function exe(fn) {
    return new Promise((resolve, reject) => {
        if (Platform.OS !== 'android') {
            return reject('It supports only android')
        }

        fn((err, response) => err ? reject(err) : resolve(response))
    })
}

const init = async function(params) {
    return exe(callback => SamsungErc20Wallet.init(params, callback))
}

const needToUpdate = async function() {
    return exe(callback => SamsungErc20Wallet.needToUpdate(callback))
}

const getAddress = async function() {
    return exe(callback => SamsungErc20Wallet.getAddress(callback))
}

const getBalance = async function() {
    return exe(callback => SamsungErc20Wallet.getBalance(callback))
}

const transfer = async function(params) {
    return exe(callback => SamsungErc20Wallet.transfer(params, callback))
}

export {
    init,
    needToUpdate,
    getAddress,
    getBalance,
    transfer,
}

export default SamsungErc20Wallet;
