/*
 * Copyright 2011 Amazon Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amazonaws.eclipse.explorer;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public abstract class AbstractAwsResourceEditorInput implements IEditorInput {

    private final String regionEndpoint;
    private final String accountId;

    public AbstractAwsResourceEditorInput(String regionEndpoint, String accountId) {
        this.regionEndpoint = regionEndpoint;
        this.accountId = accountId;
    }
    
    public String getRegionEndpoint() {
        return regionEndpoint;
    }
    
    public String getAccountId() {
        return accountId;
    }
    
    public Object getAdapter(Class adapter) {
        return null;
    }

    public boolean exists() {
        return true;
    }

    public IPersistableElement getPersistable() {
        return null;
    }
}