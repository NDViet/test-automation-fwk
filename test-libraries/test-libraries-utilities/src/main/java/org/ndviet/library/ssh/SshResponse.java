/**
 * Copyright 2019 Drew Thorstensen
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ndviet.library.ssh;

public final class SshResponse {

    private String stdOutput;
    private String errOutput;
    private String joinedOutput;
    private int returnCode;

    SshResponse(String stdOutput, String errOutput, int returnCode) {
        super();
        this.stdOutput = stdOutput;
        this.errOutput = errOutput;
        this.joinedOutput = null;
        this.returnCode = returnCode;
    }

    SshResponse(String stdOutput, String errOutput, String joinedOutput, int returnCode) {
        super();
        this.stdOutput = stdOutput;
        this.errOutput = errOutput;
        this.joinedOutput = joinedOutput;
        this.returnCode = returnCode;
    }

    public String getStdOutput() {
        return stdOutput;
    }

    public String getErrOutput() {
        return errOutput;
    }

    public String getJoinedOutput() {
        return joinedOutput;
    }

    public int getReturnCode() {
        return returnCode;
    }

}
