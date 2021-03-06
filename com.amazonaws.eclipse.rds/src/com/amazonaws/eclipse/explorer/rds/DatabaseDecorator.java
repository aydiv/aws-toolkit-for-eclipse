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
package com.amazonaws.eclipse.explorer.rds;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import com.amazonaws.eclipse.explorer.rds.RDSExplorerNodes.DatabaseNode;

public class DatabaseDecorator implements ILightweightLabelDecorator {

    public void addListener(ILabelProviderListener listener) {}
    public void removeListener(ILabelProviderListener listener) {}

    public void dispose() {}

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void decorate(Object element, IDecoration decoration) {
        if (element instanceof DatabaseNode) {
            DatabaseNode databaseNode = (DatabaseNode)element;
            String engine = databaseNode.getDBInstance().getEngine();
            String version = databaseNode.getDBInstance().getEngineVersion();
            decoration.addSuffix(" [" + engine + " " + version + "]");
        } else {
            System.out.println("Decorating: " + element);
        }
    }

}
