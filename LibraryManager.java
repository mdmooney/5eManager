import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

import javax.swing.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.*;
import java.io.*;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

/**
 * Class for general library management, originally for Monsters and Spells but can very easily be expanded to include others in the future (items, etc.).
 * Instantiate this class to handle reading from/writing to the 5eManager's XML libraries.
 * Any class to be marshalled/unmarshalled with this class should implement the LibraryMember interface.
 * The Library Manager is used specifically where dynamic writing/deleting is needed at runtime. It is restricted to situations where an XML file contains only members of a single
 * class to be unmarshalled.
 */

public class LibraryManager {
    public enum LibType { MONSTER, SPELL, PLAYER } //enumerations, i.e. what kind of libraries are allowed

    public LibType selectedLibType;
    private String xmlFile;
    private String xmlTempFile;
    private String libName;
    private String entryTag;
    private String refClass;
    private LibraryMember dummyMember;

    /**
     * Constructs a LibraryManager for a given type of library.
     * @param selectedLibType Library type being accessed, including only enumerated values.
     */
    public LibraryManager(LibType selectedLibType) {
        this.selectedLibType = selectedLibType;
        //all library types covered in the enumerations at the top of the class' attributes must follow the following format:
        if (selectedLibType == LibType.MONSTER) { //declare which enumeration
            xmlFile = ManagerConstants.MONSTER_XML; //name of the XML file
            xmlTempFile = ManagerConstants.MONSTER_TEMP_XML; //name of the Temp file (used for writing)
            libName = "StatLibrary"; //tag that encloses the entire XML library
            entryTag = "statblock"; //tag that encloses each entry of the library's type
            refClass = Monster.class.getName(); //the class that the library's members unmarshal to
            dummyMember = new Monster(); //a dummy member of the above class
        }
        else if (selectedLibType == LibType.SPELL) {
            xmlFile = ManagerConstants.SPELL_XML;
            xmlTempFile = ManagerConstants.SPELL_TEMP_XML;
            libName = "SpellLibrary";
            entryTag = "spellblock";
            refClass = Spell.class.getName();
            dummyMember = new Spell();
        }
        else if (selectedLibType == LibType.PLAYER) {
            xmlFile = ManagerConstants.PLAYER_XML;
            xmlTempFile = ManagerConstants.PLAYER_TEMP_XML;
            libName = "PlayerLibrary";
            entryTag = "pcblock";
            refClass = Player.class.getName();
            dummyMember = new Player();
        }
        checkXml();
    }


    /**
     * Public method for adding to the library. Essentially calls the write method and then updates the database files.
     * @param newObj The new object (instance of whatever reference class is used by the library) to be written to the library.
     */
    public void addToLibrary(LibraryMember newObj) {
        writeToLibrary(newObj);
        updateDbFile();
    }

    /**
     * Public method for removing something from the library. Essentially calls the deletion method and then updates the database files.
     * @param delIndices The indices (positions) in the libraries of entries to be deleted (i.e. not included in the new file)
     */
    public void removeFromLibrary(ArrayList<Integer> delIndices) {
        deleteFromLibrary(delIndices);
        updateDbFile();
    }

    /**
     * Verifies that the library to be accessed actually exists as a file.
     * If it does not, an XML file of the appropriate format is created and written to disk.
     */
    private void checkXml() {
        File f = new File(xmlFile);
        if (!f.exists()) {
            try {
                XMLOutputFactory xof = XMLOutputFactory.newFactory(); //instantiate a new output factory
                XMLStreamWriter xsw = xof.createXMLStreamWriter(new FileOutputStream(xmlFile), "UTF-8"); //use output factory to create an XML stream writer from a FileOutputStream
                com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter sw = new IndentingXMLStreamWriter(xsw); //use external library to make an indenting XML Stream Writer (saves a lot of time for formatting)
                sw.setIndentStep("    "); //needed for the indenting stream writer to indicate what sort of indentation should be used per level
                sw.writeStartDocument("UTF-8", "1.0"); //necessary for formatting purposes
                sw.writeStartElement(libName); //create the element that encloses the entire library
                sw.writeEndDocument(); //end the document for now (all we're doing right now is creating it)
                sw.flush(); //flush everything
                sw.close();
                xsw.close(); //close all streams so that the file will be accessible
            }
            catch (Exception ex) { //error handler prints StackTrace to console, and also displays an error message at this step with the trace info.
                ex.printStackTrace();
                StringBuilder sb = new StringBuilder(ex.toString());
                for (StackTraceElement ste : ex.getStackTrace()) {
                    sb.append("\n\tat ");
                    sb.append(ste);
                }
                JOptionPane.showMessageDialog(new JFrame(), "An error occurred with XML checking/creation! Stack trace:\n" + sb.toString());
            }
        }
    }

    /**
     * Method for writing to the library. First, it makes a new file with the defined temporary name for the library.
     * Then, StAX scans down the existing library and inserts every entry into the temporary library in order until it reaches
     * the appropriate location for the new object (alphabetic by name, generally), JAXB marshals the new object, and StAX continues scanning
     * down the existing library to fill in the rest of the data.
     * Afterwards, the old library is deleted, and the temporary file becomes the new library.
     * @param newObj The new object (instance of whatever reference class is used by the library) to be written to the library.
     */
    private void writeToLibrary(LibraryMember newObj) {
        if (newObj.getClass().getName().equals(refClass)) { //check to make sure the object we're adding matches the library type that's been set up
            ArrayList<LibEntry> entryList = getEntryList(); //generate the list of library entries
            try {
                //set up JAXB unmarshaller
                JAXBContext context = JAXBContext.newInstance(newObj.getClass());
                XMLInputFactory xmlif = XMLInputFactory.newFactory();
                Unmarshaller unmarsh = context.createUnmarshaller();

                //set up JAXB marshalling
                Marshaller marsh = JAXBContext.newInstance(newObj.getClass()).createMarshaller();
                marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marsh.setProperty("jaxb.fragment", true);

                //set up relevant files (temporary and permanent library XML files)
                File tempFile = new File(xmlTempFile);
                File dbFile = new File(xmlFile);

                //set up StAX for writing output to temp file
                FileOutputStream fos = new FileOutputStream(tempFile);
                XMLOutputFactory xmlof = XMLOutputFactory.newFactory();
                XMLStreamWriter xmlsw = xmlof.createXMLStreamWriter(fos, "UTF-8");

                //use indenting stream writer from outside library (very useful) for temp file FOS
                com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter sw = new IndentingXMLStreamWriter(xmlsw);
                sw.setIndentStep("    ");

                //create the temporary file with appropriate formatting
                xmlsw.writeStartDocument("UTF-8", "1.0");
                xmlsw.writeStartElement(libName);
                fos.flush();

                //set up FileInputStream for existing database file reading (StAX)
                FileInputStream fr = new FileInputStream(dbFile);
                XMLStreamReader xmlsr = xmlif.createXMLStreamReader(fr);
                xmlsr.nextTag(); //skip over starting stuff
                xmlsr.require(START_ELEMENT, null, libName); //look for the start of the actual library, denoted by the all-enclosing element
                xmlsr.nextTag(); //jump forward

                LibEntry refEntry = new LibEntry(newObj.getName(), -1); //create a new library entry object outside of the range of indices for actual library entries
                entryList.add(refEntry); //add it to the list of library entries acquired earlier
                Collections.sort(entryList); //sort the whole list so we know where to put the new entry in order
                refEntry.setIndex(entryList.indexOf(refEntry)); //reset the index (previously -1) to its new location in the proper ordering of elements

                int stopPoint = refEntry.getIndex(); //the stop point is where StAX stops reading and inserts the new object before continuing

                //iterate until we reach the index to insert the new object, and make sure we're actually unmarshalling objects
                for (int i = 0; i < stopPoint && xmlsr.getEventType() == START_ELEMENT; i++) {
                    xmlsr.require(XMLStreamConstants.START_ELEMENT, null, entryTag);
                    LibraryMember newEntry = dummyMember.getClass().cast(unmarsh.unmarshal(xmlsr)); //unmarshal objects and cast according to the class specified by the library.
                    if (newEntry != null) {
                        marsh.marshal(newEntry, sw); //put every object into the temporary file
                    }
                    if (xmlsr.getEventType() == CHARACTERS) {
                        xmlsr.next(); //if we haven't unmarshalled an actual object, don't add anything and keep going
                    }
                }

                marsh.marshal(newObj, sw); //put the new object in its correct location in the temporary file

                for (int i = 0; i < entryList.size() - 1 && xmlsr.getEventType() == START_ELEMENT; i++) { //finish iterating over the rest of the original database file
                    xmlsr.require(XMLStreamConstants.START_ELEMENT, null, entryTag);
                    LibraryMember newEntry = dummyMember.getClass().cast(unmarsh.unmarshal(xmlsr)); //unmarshal objects and cast according to the class specified by the library.
                    if (newEntry != null) {
                        marsh.marshal(newEntry, sw); //put every object into the temporary file
                    }
                    if (xmlsr.getEventType() == CHARACTERS) {
                        xmlsr.next(); //if we haven't unmarshalled an actual object, don't add anything and keep going
                    }
                }
                xmlsw.writeEndElement(); //finish off the temporary XML document by putting in the end element

                //close all streams or else "still in use" errors tend to crop up because GC isn't always that prompt
                fos.flush();
                fos.close();
                xmlsw.close();
                sw.close();
                fr.close();
                xmlsr.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * "Deletes" an entry from the XML library.
     * Works essentially by creating a temporary XML file and reading through the original XML file with StAX, marshalling
     * every entry to the new temporary file except for that which is to be deleted.
     * After this is done, the original file is deleted and the temporary XML file becomes the new database file.
     * @param delIndices The indices (positions) in the libraries of entries to be deleted (i.e. not included in the new file)
     */

    private void deleteFromLibrary(ArrayList<Integer> delIndices) {
        ArrayList<LibEntry> entryList = getEntryList(); //generate the list of library entries
        try {
            //set up JAXB unmarshaller
            JAXBContext context = JAXBContext.newInstance(dummyMember.getClass());
            XMLInputFactory xmlif = XMLInputFactory.newFactory();
            Unmarshaller unmarsh = context.createUnmarshaller();

            //set up JAXB marshaller
            Marshaller marsh = JAXBContext.newInstance(dummyMember.getClass()).createMarshaller();
            marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marsh.setProperty("jaxb.fragment", true);

            //set up relevant files (temporary and permanent library XML files)
            File tempFile = new File(xmlTempFile);
            File dbFile = new File(xmlFile);

            //set up StAX for writing output to temp file
            FileOutputStream fos = new FileOutputStream(tempFile);
            XMLOutputFactory xmlof = XMLOutputFactory.newFactory();
            XMLStreamWriter xmlsw = xmlof.createXMLStreamWriter(fos, "UTF-8");

            //create indenting stream writer so file is formatted right
            com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter sw = new IndentingXMLStreamWriter(xmlsw);
            sw.setIndentStep("    ");

            //create temporary file
            xmlsw.writeStartDocument("UTF-8", "1.0");
            xmlsw.writeStartElement(libName);
            fos.flush();

            //set up StAX reader
            FileInputStream fr = new FileInputStream(dbFile);
            XMLStreamReader xmlsr = xmlif.createXMLStreamReader(fr);
            xmlsr.nextTag();
            xmlsr.require(START_ELEMENT, null, libName);
            xmlsr.nextTag();

            //iterate over entirety of entry list and re-marshal everything to the temp file except for whatever is being deleted
            for (int i = 0; i < entryList.size() && xmlsr.getEventType() == START_ELEMENT; i++) {
                xmlsr.require(XMLStreamConstants.START_ELEMENT, null, entryTag);
                LibraryMember libMember = dummyMember.getClass().cast(unmarsh.unmarshal(xmlsr)); //unmarshal to class appropriate to library
                if (!delIndices.contains(i)) {
                    marsh.marshal(dummyMember.getClass().cast(libMember), sw); //if the object(s) to be deleted doesn't include the unmarshalled member, marshal it to temp file
                }

                if (xmlsr.getEventType() == CHARACTERS) {
                    xmlsr.next();
                }
            }

            xmlsw.writeEndElement(); //write the end element

            //flush the stream and close ALL streams so that the database is free in memory
            fos.flush();
            fos.close();
            xmlsw.close();
            sw.close();
            fr.close();
            xmlsr.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the list of all entries in the library in the form of LibEntry objects (basically just includes entry names and indices).
     * @return ArrayList of all of the library's members in the form of LibEntry objects (include names and indices in the library)
     */
    public ArrayList<LibEntry> getEntryList() {
        ArrayList<LibEntry> entryArray = new ArrayList<LibEntry>();
        int counter = 0;
        try {
            //set up JAXB unmarshalling
            JAXBContext context = JAXBContext.newInstance(dummyMember.getClass());
            XMLInputFactory xmlif = XMLInputFactory.newFactory();
            Unmarshaller unmarsh = context.createUnmarshaller();

            //set up StAX reading
            XMLStreamReader xmlsr = xmlif.createXMLStreamReader(new FileReader(xmlFile));
            xmlsr.nextTag();
            xmlsr.require(START_ELEMENT, null, libName);
            xmlsr.nextTag();

            while (xmlsr.getEventType() == START_ELEMENT) {
                //unmarshal each individual library member, create a LibEntry for it, and discard the starting object
                xmlsr.require(XMLStreamConstants.START_ELEMENT, null, entryTag);
                LibraryMember libMember = (LibraryMember) unmarsh.unmarshal(xmlsr);
                entryArray.add(new LibEntry(libMember.getName(), counter));
                counter++;
                if (xmlsr.getEventType() == CHARACTERS) {
                    xmlsr.next();
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return entryArray;
    }

    /**
     * Finds the library member at a given index, and unmarshals it alone as a LibraryMember (interface for anything usable by the LibraryManager class)
     * so that it can be used for any purpose.
     * @param location The index to unmarshal the library entry from.
     * @return The library member at the given index.
     */

    public LibraryMember loadMember(int location) {
        try {
            //set up JAXB unmarshalling
            JAXBContext context = JAXBContext.newInstance(dummyMember.getClass());
            XMLInputFactory xmlif = XMLInputFactory.newFactory();
            Unmarshaller unmarsh = context.createUnmarshaller();

            //set up StAX reading
            XMLStreamReader xmlsr = xmlif.createXMLStreamReader(new FileReader(xmlFile));
            xmlsr.nextTag();
            xmlsr.require(START_ELEMENT, null, libName);
            xmlsr.nextTag();

            //iterate through library file until the index (desired library entry location) is reached, then unmarshal
            for (int i = 0; i < location && xmlsr.hasNext(); i++) {
                xmlsr.next();
                while (xmlsr.getEventType() != XMLStreamReader.START_ELEMENT) {
                    xmlsr.next();
                    if (xmlsr.getEventType() == START_ELEMENT && !xmlsr.getLocalName().equals(entryTag)) {
                        xmlsr.next();
                    }
                }
            }
            return dummyMember.getClass().cast(unmarsh.unmarshal(xmlsr));
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Removes the original database file on disk and replaces it with the temporary file created by the writing and deletion methods.
     * Requests garbage collection first as a safety, but should be safe as all streams are meticulously closed after manipulation.
     */
    private void updateDbFile() {
        //set up file names
        File tempFile = new File(xmlTempFile);
        File dbFile = new File(xmlFile);

        //requests garbage collection
        System.gc();

        try {
            Files.deleteIfExists(dbFile.toPath());
        }
        catch (FileSystemException fsex) {
            updateDbFile(); //recursively try this method again if access is restricted (usually happens when a stream doesn't close for some reason)
        }
        catch (IOException ioex) {
            ioex.printStackTrace();
        }
        tempFile.renameTo(dbFile); //rename the temporary file to become the new database file
    }

    /**
     * Getter for the "dummy member," generally an object of the class for which the LibraryManager is made, created with the default constructor.
     * @return The dummy member for the LibraryManager.
     */
    public LibraryMember getDummyMember() {
        return dummyMember;
    }

}
