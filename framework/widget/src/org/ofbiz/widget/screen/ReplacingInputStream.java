/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.widget.screen;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.io.FilterInputStream;
import java.util.LinkedList;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.webapp.view.AbstractViewHandler;
import org.ofbiz.webapp.view.ApacheFopWorker;
import org.ofbiz.webapp.view.ViewHandlerException;
import org.ofbiz.widget.form.FormStringRenderer;
import org.ofbiz.widget.form.MacroFormRenderer;
import org.ofbiz.widget.html.HtmlScreenRenderer;

/**
 * Uses XSL-FO formatted templates to generate PDF, PCL, POSTSCRIPT etc.  views
 * This handler will use JPublish to generate the XSL-FO
 */
class ReplacingInputStream extends FilterInputStream {

    LinkedList<Integer> inQueue = new LinkedList<Integer>();
    LinkedList<Integer> outQueue = new LinkedList<Integer>();
    final byte[] search, replacement;

    protected ReplacingInputStream(InputStream in, byte[] search,
                                                   byte[] replacement) {
        super(in);
        this.search = search;
        this.replacement = replacement;
    }

    private boolean isMatchFound() {
        Iterator<Integer> inIter = inQueue.iterator();
        for (int i = 0; i < search.length; i++){
        	if (!inIter.hasNext() || search[i] != inIter.next())
                return false;
        }
        return true;
    }

    private void readAhead() throws IOException {
        // Work up some look-ahead.
        while (inQueue.size() < search.length) {
            int next = super.read();
            inQueue.offer(next);
            if (next == -1)
                break;
        }
    }


    @Override
    public int read() throws IOException {

        // Next byte already determined.
        if (outQueue.isEmpty()) {

            readAhead();
            if (isMatchFound()) {
            	for (int i = 0; i < search.length; i++)
                	inQueue.remove();
                
            	int a = super.read();
            	int b = super.read();
            	int c = super.read();
            	String str = "";
            	char[] repChar = {(char)a, (char)b,(char)c};
            	for(char x: repChar){
    				str += x;
    			}
    			byte replaceBytes = Byte.valueOf(str);
                outQueue.offer((int) replaceBytes);
            } else{
           		outQueue.add(inQueue.remove());
            }
        }
        return outQueue.remove();
    }

    // TODO: Override the other read methods.
}