/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.deviceinfo.firmwareversion;

import android.support.annotation.VisibleForTesting;

import com.android.settings.R;
import android.os.SystemProperties;

public class FpgaVersionDialogController {

    @VisibleForTesting
    static int FPGA_VERSION_VALUE_ID = R.id.fpga_version_value;

    private final FirmwareVersionDialogFragment mDialog;

    public FpgaVersionDialogController(FirmwareVersionDialogFragment dialog) {
        mDialog = dialog;
    }

    /**
     * Updates FPGA version to the dialog.
     */
    public void initialize() {
        mDialog.setText(FPGA_VERSION_VALUE_ID, getFpgaVersion());
    }
    
    public String getFpgaVersion(){
        String ver = null;
    	ver = SystemProperties.get("hw.build.version.fpga", "unknown");
        if(ver.startsWith("unknown"))
        	return "Unavailable";
        return ver;
    }
}
