# rn-samsung-erc20-wallet

It supports only Android

## Getting started

`$ npm install rn-samsung-erc20-wallet --save`

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

// TODO: What to do with the module?
SamsungErc20Wallet;
```
