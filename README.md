
# react-native-dft-onyx-sdk-wrapper

## Getting started

`$ npm install react-native-dft-onyx-sdk-wrapper --save`

### Mostly automatic installation

`$ react-native link react-native-dft-onyx-sdk-wrapper`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-dft-onyx-sdk-wrapper` and add `RNDftOnyxSdkWrapper.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNDftOnyxSdkWrapper.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNDftOnyxSdkWrapperPackage;` to the imports at the top of the file
  - Add `new RNDftOnyxSdkWrapperPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-dft-onyx-sdk-wrapper'
  	project(':react-native-dft-onyx-sdk-wrapper').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-dft-onyx-sdk-wrapper/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-dft-onyx-sdk-wrapper')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNDftOnyxSdkWrapper.sln` in `node_modules/react-native-dft-onyx-sdk-wrapper/windows/RNDftOnyxSdkWrapper.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Dft.Onyx.Sdk.Wrapper.RNDftOnyxSdkWrapper;` to the usings at the top of the file
  - Add `new RNDftOnyxSdkWrapperPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNDftOnyxSdkWrapper from 'react-native-dft-onyx-sdk-wrapper';

// TODO: What to do with the module?
RNDftOnyxSdkWrapper;
```
  