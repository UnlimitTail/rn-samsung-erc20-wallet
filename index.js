import { NativeModules, Platform } from 'react-native';
import { runInContext } from 'vm';

const { SamsungErc20Wallet } = NativeModules;

function exe(fn) {
    return new Promise((resolve, reject) => {
        if (Platform.OS !== 'android') {
            return reject('It supports only android')
        }

        fn((err, response) => err ? reject(err) : resolve(response))
    })
}

exports.init = async function(params) {
    return exe(callback => SamsungErc20Wallet.init(params, callback))
}

exports.needToUpdate = async function() {
    return exe(callback => SamsungErc20Wallet.needToUpdate(callback))
}

exports.getAddress = async function() {
    return exe(callback => SamsungErc20Wallet.getAddress(callback))
}

exports.getBalance = async function() {
    return exe(callback => SamsungErc20Wallet.getBalance(callback))
}

exports.transfer = async function(params) {
    return exe(callback => SamsungErc20Wallet.transfer(params, callback))
}

export default SamsungErc20Wallet;
