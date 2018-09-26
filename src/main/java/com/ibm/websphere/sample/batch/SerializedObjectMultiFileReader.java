/*
 * Copyright 2018 International Business Machines Corp.
 * 
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. Licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.websphere.sample.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;


/**
 * An implementation of ItemReader that reads serialized serialized objects from a directory 
 * of files (ending in ".txt") containing such things.
 * 
 * @author Cassandra Newcomer
 * @author David Follis
 */
@Dependent
public class SerializedObjectMultiFileReader implements ItemReader {
    
    private static final Logger log = Logger.getLogger( SerializedObjectMultiFileReader.class.getName() );

    @Inject
    @BatchProperty(name = "inputDir")
    String inputDir;
    
    private ReaderState rs;
    private FileInputStream fis;
    private ObjectInputStream ois;
    
    /**
     * An inner class used to contain information needed for a restart.
     * We are assuming the contents of the directory don't change across a restart/rollback. 
     */
    private class ReaderState implements Serializable {

        private static final long serialVersionUID = -5400406421952921947L;
        private LinkedList<String> listOfFiles;
        private int currentFileIndex;
        private int currentRecord;
        
        public void listOfFiles(LinkedList<String> l) {
            listOfFiles = l;
        }
        public String[] listOfFiles() {
            return (String[])listOfFiles.toArray(new String[0]);
        }
        
        public void currentFileIndex(int cfi) {
            currentFileIndex = cfi;
        }
        public int currentFileIndex() {
            return currentFileIndex;
        }
        public void incrementCurrentFileIndex() {
            ++currentFileIndex;
        }
        public int currentRecord() {
            return currentRecord;
        }
        public void currentRecord(int i) {
            currentRecord = i;
        }
        public void incrementCurrentRecord() {
            ++currentRecord;
        }
        
        private void writeObject(java.io.ObjectOutputStream stream) throws IOException {
            stream.writeObject(listOfFiles);
            stream.writeInt(currentFileIndex);
            stream.writeInt(currentRecord);
        }
        
        private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
            listOfFiles = (LinkedList<String>)stream.readObject();
            currentFileIndex = stream.readInt();
            currentRecord = stream.readInt();
        }
        
        
    }
    
    @Override
    public Serializable checkpointInfo() throws Exception {
        return rs;
    }

    @Override
    public void close() throws Exception {
        ois.close();        
    }

    @Override
    public void open(Serializable arg0) throws Exception {
        
        if (arg0!=null) {
            // Restarting from a checkpoint, so get our saved status
            rs = (ReaderState)arg0;
            // Open up the then-current file to read
            fis = new FileInputStream(rs.listOfFiles()[rs.currentFileIndex()]);
            ois = new ObjectInputStream(fis);     
            // Skim through the file to get to last record read at the last checkpoint
            int recNum = 0;
            while (recNum<rs.currentRecord()) {
                // Read and discard record we've already processed
                Object o = ois.readObject();
                ++recNum;
            }
            
            log.log(Level.INFO, "reading "+rs.listOfFiles()[rs.currentFileIndex()]+
                      " starting at record "+rs.currentFileIndex());                
    
        } else {
            // Starting from scratch
            rs = new ReaderState();
            
            // Get the list of files from the input directory
            File sourceFolder = new File(inputDir);
            File[] listOfFiles = sourceFolder.listFiles();
            if (listOfFiles != null) {
                LinkedList<String> ll = new LinkedList<String>();
                for (int i=0;i<listOfFiles.length;++i) {
                    if ((listOfFiles[i].isFile()) && (listOfFiles[i].getName().endsWith(".txt"))) {
                        ll.add(listOfFiles[i].getCanonicalPath());
                    }
                }
                rs.listOfFiles(ll);
            } else {
                String excMessage = "Bad input directory. Directory: " + inputDir + " unable to be opened as source of stored tweet files";
                log.log(Level.SEVERE, excMessage);
                throw new IllegalArgumentException(excMessage);
            }
            
            // Start at the beginning of the list
            rs.currentFileIndex(0);
            rs.currentRecord(0);
            setupNextFile();
        }
    
    }


    
    /**
     * Reads the next object.
     * Assumes any IOException is caused by reaching the end of the current file.
     * If that happens it tries to move to the next file by calling setupNextFile( ).
     * If it can't read an object from that file, we give up
     * Returns null when we run out of files
     * @return a Status object or null 
     */
    @Override
    public Object readItem() throws Exception {
        
        Object readObject = null;
        try {
        readObject = ois.readObject();
        rs.incrementCurrentRecord();
        } catch (IOException iox) {
            // probably end-of-file...
            // Close this file
            ois.close();
            // increment index and try to set up another file
            rs.incrementCurrentFileIndex();
            rs.currentRecord(0);  // new file, reset the record number to zero
            boolean gotAnotherFile = setupNextFile();
            if (gotAnotherFile) {
                // got another file, read from it
                try {
                    readObject = ois.readObject();
                    rs.incrementCurrentRecord();
                } catch (IOException iox2) {
                    // well.. An empty file?  Give up...
                    readObject=null;
                }
            } else {
                // no more files
                readObject=null;
                log.log(Level.INFO, "No more files");
            }
        }
        return readObject;
        
    }
    
    /**
     * Called when we're looking for the next file to process.  This looks at the list in our 
     * read status and tries to set up an ObjectInputStream for it.
     * @return true if it worked, false if we ran out of files
     * @throws IOException For errors opening a file in the list
     */
    private boolean setupNextFile() throws IOException {
        boolean retVal;
        if (rs.currentFileIndex()<rs.listOfFiles().length) {
            String s = rs.listOfFiles()[rs.currentFileIndex()];
                fis = new FileInputStream(s);
                ois = new ObjectInputStream(fis);        
                log.log(Level.INFO, "reading "+s);                
                retVal=true;
        } else {
            // no more files
            retVal=false;
        }
        return retVal;
    }    

}
