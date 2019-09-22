# rn-samsung-erc20-wallet

<b>## It supports only Android ##</b>

## Getting started

`$ npm install rn-samsung-erc20-wallet --save`

then, you need to install samsung keystore sdk

```
cp -r node_modules/rn-samsung-erc20-wallet/android/libs-samsung-sdk android/libs-samsung-sdk
cp -r node_modules/rn-samsung-erc20-wallet/android/libs-samsung-sdk android/app/libs-samsung-sdk
```
I don't the exact reason why but you need to copy it at both.

### Mostly automatic installation

`$ react-native link rn-samsung-erc20-wallet`

### Manual installation

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.reactlibrary.SamsungErc20WalletPackage;` to the imports at the top of the file
  - Add `new SamsungErc20WalletPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':rn-samsung-erc20-wallet'
  	project(':rn-samsung-erc20-wallet').projectDir = new File(rootProject.projectDir, 	'../node_modules/rn-samsung-erc20-wallet/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':rn-samsung-erc20-wallet')
  	```
#### iOS

n/a



## Usage
```javascript
import SamsungErc20Wallet from 'rn-samsung-erc20-wallet';

// initializing
try {
	await SW.init({
		contractAddress: '0xA158241357872A52B3130E02C24B1359FE2F....',
		chainnetUrl: 'https://mainnet.infura.io/v3/....',
	})

	console.log('success to initialize')
}
catch (e) {
	console.error(e)
}

// checking update
try {
	const needUpdate = await SW.needToUpdate()
	console.log('needUpdate', needUpdate)
}
catch (e) {
	console.error(e)
}

// getting my erc20 wallet address
try {
	const addr = await SW.getAddress()
	console.log('addr', addr)
}
catch (e) {
	console.error(e)
}

// getting balance
try {
	const balance = await SW.getBalance()
	console.log('balance', balance)
}
catch (e) {
	console.error(e)
}

// trasfer
try {
	const param = {
		toAddress: '0x007a4fad964225B94eA41f64Ca08a97248d64920',
		amount: 10
	}

	const transaction = await SW.transfer(param)
	console.log('transaction', transaction)
}
catch (e) {
	console.error(e)
}
```
