using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Dft.Onyx.Sdk.Wrapper.RNDftOnyxSdkWrapper
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNDftOnyxSdkWrapperModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNDftOnyxSdkWrapperModule"/>.
        /// </summary>
        internal RNDftOnyxSdkWrapperModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNDftOnyxSdkWrapper";
            }
        }
    }
}
