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
package com.amazonaws.eclipse.datatools.enablement.simpledb.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.amazonaws.eclipse.core.AwsToolkitCore;

/**
 * Simple table dialog to allow use user to enter multiple values for an
 * attribute.
 */
class MultiValueEditorDialog extends MessageDialog {

    private static final String NEW_VALUE = "<new value>";
    private final SimpleDBItem item;
    private final String attributeName;
    private final List<String> attributeValues;

    List<String> getAttributeValues() {
        return this.attributeValues;
    }

    private TableViewer tableViewer;

    public MultiValueEditorDialog(final Shell parentShell, final SimpleDBItem item, final String attributeName) {
        super(parentShell, "Edit attribute values", AwsToolkitCore.getDefault().getImageRegistry()
                .get(AwsToolkitCore.IMAGE_AWS_ICON), "", MessageDialog.NONE, new String[] { "OK", "Cancel" }, 0);
        this.item = item;
        this.attributeName = attributeName;
        this.attributeValues = new ArrayList<String>();
        this.attributeValues.addAll(this.item.attributes.get(this.attributeName));
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected Control createDialogArea(final Composite parent) {

        Composite composite = new Composite(parent, SWT.None);
        GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(composite);

        TableColumnLayout layout = new TableColumnLayout();
        composite.setLayout(layout);

        this.tableViewer = new TableViewer(composite);
        this.tableViewer.getTable().setHeaderVisible(true);
        TableColumn tableColumn = new TableColumn(this.tableViewer.getTable(), SWT.NONE);
        tableColumn.setText("Attributes");
        layout.setColumnData(tableColumn, new ColumnWeightData(100));

        this.tableViewer.setContentProvider(new AbstractTableContentProvider() {

            @Override
            public Object[] getElements(final Object inputElement) {
                Object[] rowsPlusNew = new Object[MultiValueEditorDialog.this.attributeValues.size() + 1];
                MultiValueEditorDialog.this.attributeValues.toArray(rowsPlusNew);
                rowsPlusNew[rowsPlusNew.length - 1] = NEW_VALUE;
                return rowsPlusNew;
            }
        });

        this.tableViewer.setLabelProvider(new AbstractTableLabelProvider() {

            @Override
            public String getColumnText(final Object element, final int columnIndex) {
                return (String) element;
            }
        });

        final Table table = this.tableViewer.getTable();
        final TableEditor editor = new TableEditor(table);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        table.addListener(SWT.MouseUp, new Listener() {

            public void handleEvent(final Event event) {
                Rectangle clientArea = table.getClientArea();
                Point pt = new Point(event.x, event.y);
                int index = table.getTopIndex();
                while ( index < table.getItemCount() ) {
                    boolean visible = false;
                    final TableItem item = table.getItem(index);

                    // Only one column, but loop is here for completeness
                    for ( int i = 0; i < table.getColumnCount(); i++ ) {
                        Rectangle rect = item.getBounds(i);
                        if ( rect.contains(pt) ) {
                            final int column = i;
                            final Text text = new Text(table, SWT.NONE);
                            final int idx = index;

                            Listener textListener = new Listener() {

                                public void handleEvent(final Event e) {
                                    switch (e.type) {
                                    case SWT.FocusOut:
                                        modifyValue(item, column, idx, text);
                                        text.dispose();
                                        break;
                                    case SWT.Traverse:
                                        switch (e.detail) {
                                        case SWT.TRAVERSE_RETURN:
                                            modifyValue(item, column, idx, text);
                                            // FALL THROUGH
                                        case SWT.TRAVERSE_ESCAPE:
                                            text.dispose();
                                            e.doit = false;
                                        }
                                        break;
                                    }
                                }
                            };

                            text.addListener(SWT.FocusOut, textListener);
                            text.addListener(SWT.Traverse, textListener);
                            editor.setEditor(text, item, i);
                            text.setText(item.getText(i));

                            text.selectAll();
                            text.setFocus();
                            return;
                        }
                        if ( !visible && rect.intersects(clientArea) ) {
                            visible = true;
                        }
                    }
                    if ( !visible ) {
                        return;
                    }
                    index++;
                }
            }
        });

        this.tableViewer.setInput(this.item);
        this.tableViewer.getTable().getItem(this.attributeValues.size())
        .setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));

        return composite;
    }

    private void modifyValue(final TableItem item, final int column, final int index, final Text text) {
        String newValue = text.getText();
        if ( newValue.length() == 0 ) {
            this.attributeValues.remove(index);
            this.tableViewer.refresh();
        } else {
            item.setText(column, newValue);
            if ( index == item.getParent().getItemCount() - 1 ) {
                this.attributeValues.add(newValue);
                this.tableViewer.refresh();
                item.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));

                this.tableViewer.getTable().getItem(this.attributeValues.size())
                .setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
            } else {
                this.attributeValues.set(index, newValue);
            }
        }
    }
}