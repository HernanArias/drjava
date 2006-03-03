/*BEGIN_COPYRIGHT_BLOCK
 *
 * This file is part of DrJava.  Download the current version of this project:
 * http://sourceforge.net/projects/drjava/ or http://www.drjava.org/
 *
 * DrJava Open Source License
 * 
 * Copyright (C) 2001-2003 JavaPLT group at Rice University (javaplt@rice.edu)
 * All rights reserved.
 *
 * Developed by:   Java Programming Languages Team
 *                 Rice University
 *                 http://www.cs.rice.edu/~javaplt/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"),
 * to deal with the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 *     - Redistributions of source code must retain the above copyright 
 *       notice, this list of conditions and the following disclaimers.
 *     - Redistributions in binary form must reproduce the above copyright 
 *       notice, this list of conditions and the following disclaimers in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the names of DrJava, the JavaPLT, Rice University, nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this Software without specific prior written permission.
 *     - Products derived from this software may not be called "DrJava" nor
 *       use the term "DrJava" as part of their names without prior written
 *       permission from the JavaPLT group.  For permission, write to
 *       javaplt@rice.edu.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
 * THE CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR 
 * OTHER DEALINGS WITH THE SOFTWARE.
 * 
END_COPYRIGHT_BLOCK*/

package edu.rice.cs.drjava.model;

import junit.framework.*;

import java.io.File;

import edu.rice.cs.drjava.DrJavaTestCase;

/**
* DummyGetDocumentsTest for unit testing DummyGetDocuments.  Uses
* JUnit for testing.
*
* @author <a href="mailto:ericc@rice.edu">Eric Shao-yu Cheng</a>
* @version $Id$
*/
public class DummyGlobalModelTest extends DrJavaTestCase {
    

    /**
    * Creates a new instance of DummyGetDocuments, calls
    * getDocumentsForFile() and ensures the method throws an
    * UnsupportedOperationException. 
    *
    * @exception java.io.IOException if an error occurs
    */
    public void testGetDocumentForFile() throws java.io.IOException {
 DummyGlobalModel dummy = new DummyGlobalModel();
 try {
     dummy.getDocumentForFile(new File(""));
 }
 catch (UnsupportedOperationException e) {
     assertTrue("This message should never be seen", true);
     return;
 }
 fail("expected that UnsupportedOperationException is thrown");
    }

    /**
    * Creates a new instance of DummyGetDocuments, calls
    * getDocumentForFile() and ensures the method throws an
    * UnsupportedOperationException.
    *
    * @exception java.io.IOException if an error occurs
    */
    public void testIsAlreadyOpen() throws java.io.IOException {
 DummyGlobalModel dummy = new DummyGlobalModel();
 try {
     dummy.getDocumentForFile(new File(""));
 }
 catch (UnsupportedOperationException e) {
     assertTrue("This message should never be seen", true);
     return;
 }
 fail("expected that UnsupportedOperationException is thrown");
    }


    /**
    * Creates a new instance of DummyGetDocuments, calls
    * getOpenDefinitionsDocuments() and ensures the method throws an
    * UnsupportedOperationException.
    *
    * @exception java.io.IOException if an error occurs
    */
    public void testGetDefinitionsDocuments() {
 DummyGlobalModel dummy = new DummyGlobalModel();
 try {
     dummy.getOpenDefinitionsDocuments();
 }
 catch (UnsupportedOperationException e) {
     assertTrue("This message should never be seen", true);
     return;
 }
 fail("expected that UnsupportedOperationException is thrown");
    }


    /**
    * Creates a new instance of DummyGetDocuments, calls
    * hasModifiedDocuments() and ensures the method throws an
    * UnsupportedOperationException.
    *
    * @exception java.io.IOException if an error occurs
    */
    public void testHasModifiedDocuments() {
 DummyGlobalModel dummy = new DummyGlobalModel();
 try {
     dummy.hasModifiedDocuments();
 }
 catch (UnsupportedOperationException e) {
     assertTrue("This message should never be seen", true);
     return;
 }
 fail("expected that UnsupportedOperationException is thrown");
    }
 

}