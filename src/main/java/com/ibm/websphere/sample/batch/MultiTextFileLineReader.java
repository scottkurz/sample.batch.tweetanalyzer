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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import com.ibm.websphere.sample.jpa.TweetDataObject;


/**
 * An implementation of ItemReader that reads serialized serialized objects from a directory 
 * of files (ending in a specific file extension like ".json") containing such things.
 * 
 * @author Cassandra Newcomer
 * @author David Follis
 */
@Dependent
public class MultiTextFileLineReader implements ItemReader {

	private static final Logger log = Logger.getLogger( MultiTextFileLineReader.class.getName() );

	@Inject
	@BatchProperty(name = "inputDir")
	String inputDir;

	@Inject
	@BatchProperty(name = "inputExt")
	String inputExt;

	private ReaderState rs;


	private FileReader fr;
	private BufferedReader br;

	/**
	 * An inner class used to contain information needed for a restart.
	 * We are assuming the contents of the directory don't change across a restart/rollback. 
	 */
	private class ReaderState implements Serializable {

		private static final long serialVersionUID = -5400406421952921947L;

		// Since we use 0-indexed counting, we want to increment to 0 along with opening the first new file, so start at -1 
		private static final int NOT_READ_ANYTHING_YET = -1;

		private LinkedList<String> listOfFiles;
		private int currentFileIndex = NOT_READ_ANYTHING_YET;
		private int currentRecord;
		private boolean noMoreInputFiles = false;

		public void listOfFiles(LinkedList<String> l) {
			listOfFiles = l;
		}
		public String[] listOfFiles() {
			return (String[])listOfFiles.toArray(new String[0]);
		}

		public void incrementCurrentFileIndex() {
			++currentFileIndex;
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
		log.log(Level.FINE, "Checkpointing at file index # "+ rs.currentFileIndex +
				" after record # " + rs.currentRecord);                

		return rs;
	}

	@Override
	public void close() throws Exception {
		if (br != null) {
			br.close();
		}
		if (fr != null) {
			fr.close();
		}

	}

	@Override
	public void open(Serializable arg0) throws Exception {

		if (arg0!=null) {
			// Restarting from a checkpoint, so get our saved status
			rs = (ReaderState)arg0;
			// Open up the then-current file to read
			fr = new FileReader(rs.listOfFiles()[rs.currentFileIndex]);
			br = new BufferedReader(fr);
			// Skim through the file to get to last record read at the last checkpoint
			int recNum = 0;
			while (recNum < rs.currentRecord) {
				// Read and discard record we've already processed
				String line = br.readLine();
				++recNum;
			}

			log.log(Level.INFO, "reading file # " + rs.currentFileIndex + ", starting after record # "+rs.currentRecord + " file = " + rs.listOfFiles()[rs.currentFileIndex]);                

		} else {
			// Starting from scratch
			rs = new ReaderState();

			// Get the list of files from the input directory
			File sourceFolder = new File(inputDir);
			File[] listOfFiles = sourceFolder.listFiles();
			if (listOfFiles != null) {
				LinkedList<String> ll = new LinkedList<String>();
				for (int i=0;i<listOfFiles.length;++i) {
					if ((listOfFiles[i].isFile()) && (listOfFiles[i].getName().endsWith(inputExt))) {
						ll.add(listOfFiles[i].getCanonicalPath());
					}
				}
				rs.listOfFiles(ll);
			} else {
				String excMessage = "Bad input directory. Directory: " + inputDir + " unable to be opened as source of stored tweet files";
				log.log(Level.SEVERE, excMessage);
				throw new IllegalArgumentException(excMessage);
			}

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

		String line = null;

		while (!rs.noMoreInputFiles && line == null) {
			try {
				line = br.readLine();      	
				if (line == null) {
					setupNextFile();
				} 
			} catch (IOException iox) {
				br.close();
				fr.close();
				throw new RuntimeException("Caught exception in readItem", iox);
			}
		}
		
		if (line != null) {
			rs.incrementCurrentRecord();
			return deserialize(line);
		} else {
			return null;
		}
	}

	private TweetDataObject deserialize(String line) {
		Jsonb jsonb = JsonbBuilder.create();
		return jsonb.fromJson(line, TweetDataObject.class);
	}

	/**
	 * Called when we're looking for the next file to process.  This looks at the list in our 
	 * read status and tries to set up an ObjectInputStream for it.
	 * @throws IOException For errors opening a file in the list
	 */
	private void setupNextFile() throws IOException {
		
		// Close previous readers
		if (br != null) {
			br.close();
		} if (fr != null) {
			fr.close();
		}
		
		if (++rs.currentFileIndex < rs.listOfFiles().length) {
			String s = rs.listOfFiles()[rs.currentFileIndex];
			fr = new FileReader(s);
			br = new BufferedReader(fr);        
			log.log(Level.INFO, "reading "+s);                
			rs.currentRecord = 0;  // new file, reset the record number to zero
		} else {
			log.log(Level.INFO, "No more files");
			rs.noMoreInputFiles = true;
		}
	}    
}
