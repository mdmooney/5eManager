import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

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
 * Created by Michael on 11/12/2014.
 */

public class LibraryManager {
    public enum LibType { MONSTER, SPELL };
    public LibType selectedLibType;
    private String xmlFile;
    private String xmlTempFile;
    private String libName;
    private String entryTag;
    private String refClass;
    private LibraryMember dummyMember;

    public LibraryManager(LibType selectedLibType) {
        this.selectedLibType = selectedLibType;
        if (selectedLibType == LibType.MONSTER) {
            xmlFile = ManagerConstants.MONSTER_XML;
            xmlTempFile = ManagerConstants.MONSTER_TEMP_XML;
            libName = "StatLibrary";
            entryTag = "statblock";
            refClass = Monster.class.getName();
            dummyMember = new Monster();
        }
        else if (selectedLibType == LibType.SPELL) {
            xmlFile = ManagerConstants.SPELL_XML;
            xmlTempFile = ManagerConstants.SPELL_TEMP_XML;
            libName = "SpellLibrary";
            entryTag = "spellblock";
            refClass = Spell.class.getName();
            dummyMember = new Spell();
        }
        checkXml();
    }


    public void addToLibrary(LibraryMember newObj) {
        writeToLibrary(newObj);
        updateDbFile();
    }

    public void removeFromLibrary(ArrayList<Integer> delIndices) {
        deleteFromLibrary(delIndices);
        updateDbFile();
    }

    private void checkXml() {
        File f = new File(xmlFile);
        if (!f.exists()) {
            try {
                XMLOutputFactory xof = XMLOutputFactory.newFactory();
                XMLStreamWriter xsw = xof.createXMLStreamWriter(new FileWriter(xmlFile));
                com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter sw = new IndentingXMLStreamWriter(xsw);
                sw.setIndentStep("    ");

                sw.writeStartDocument("UTF-8", "1.0");
                sw.writeStartElement(libName);
                sw.writeEndDocument();
                sw.flush();
                sw.close();
                xsw.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void writeToLibrary(LibraryMember newObj) {
        if (newObj.getClass().getName().equals(refClass)) { //check to make sure the object we're adding matches the library type that's been set up
            ArrayList<LibEntry> entryList = getEntryList();
            try {
                JAXBContext context = JAXBContext.newInstance(newObj.getClass());
                XMLInputFactory xmlif = XMLInputFactory.newFactory();
                Unmarshaller unmarsh = context.createUnmarshaller();

                Marshaller marsh = JAXBContext.newInstance(newObj.getClass()).createMarshaller();
                marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marsh.setProperty("jaxb.fragment", true);

                File tempFile = new File(xmlTempFile);
                File dbFile = new File(xmlFile);

                FileOutputStream fos = new FileOutputStream(tempFile);
                XMLOutputFactory xmlof = XMLOutputFactory.newFactory();
                XMLStreamWriter xmlsw = xmlof.createXMLStreamWriter(fos, "UTF-8");

                com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter sw = new IndentingXMLStreamWriter(xmlsw);
                sw.setIndentStep("    ");

                xmlsw.writeStartDocument("UTF-8", "1.0");
                xmlsw.writeStartElement(libName);
                fos.flush();

                FileInputStream fr = new FileInputStream(dbFile);
                XMLStreamReader xmlsr = xmlif.createXMLStreamReader(fr);
                xmlsr.nextTag();
                xmlsr.require(START_ELEMENT, null, libName);
                xmlsr.nextTag();

                LibEntry refEntry = new LibEntry(newObj.getName(), -1);
                entryList.add(refEntry);
                Collections.sort(entryList);
                refEntry.setIndex(entryList.indexOf(refEntry));

                int stopPoint = refEntry.getIndex();

                for (int i = 0; i < stopPoint && xmlsr.getEventType() == START_ELEMENT; i++) {
                    xmlsr.require(XMLStreamConstants.START_ELEMENT, null, entryTag);
                    LibraryMember newEntry = dummyMember.getClass().cast(unmarsh.unmarshal(xmlsr));
                    if (newEntry != null) {
                        marsh.marshal(newEntry, sw);
                    }
                    if (xmlsr.getEventType() == CHARACTERS) {
                        xmlsr.next();
                    }
                }

                marsh.marshal(newObj, sw);

                for (int i = 0; i < entryList.size() - 1 && xmlsr.getEventType() == START_ELEMENT; i++) {
                    xmlsr.require(XMLStreamConstants.START_ELEMENT, null, entryTag);
                    LibraryMember newEntry = dummyMember.getClass().cast(unmarsh.unmarshal(xmlsr));
                    if (newEntry != null) {
                        marsh.marshal(newEntry, sw);
                    }
                    if (xmlsr.getEventType() == CHARACTERS) {
                        xmlsr.next();
                    }
                }
                xmlsw.writeEndElement();

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

    private void deleteFromLibrary(ArrayList<Integer> delIndices) {
        ArrayList<LibEntry> entryList = getEntryList();
        try {
            JAXBContext context = JAXBContext.newInstance(dummyMember.getClass());
            XMLInputFactory xmlif = XMLInputFactory.newFactory();
            Unmarshaller unmarsh = context.createUnmarshaller();
            Marshaller marsh = JAXBContext.newInstance(dummyMember.getClass()).createMarshaller();
            marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marsh.setProperty("jaxb.fragment", true);

            File tempFile = new File(xmlTempFile);
            File dbFile = new File(xmlFile);

            FileOutputStream fos = new FileOutputStream(tempFile);
            XMLOutputFactory xmlof = XMLOutputFactory.newFactory();
            XMLStreamWriter xmlsw = xmlof.createXMLStreamWriter(fos, "UTF-8");

            com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter sw = new IndentingXMLStreamWriter(xmlsw);
            sw.setIndentStep("    ");

            xmlsw.writeStartDocument("UTF-8", "1.0");
            xmlsw.writeStartElement(libName);
            fos.flush();

            FileInputStream fr = new FileInputStream(dbFile);
            XMLStreamReader xmlsr = xmlif.createXMLStreamReader(fr);
            xmlsr.nextTag();
            xmlsr.require(START_ELEMENT, null, libName);
            xmlsr.nextTag();

            for (int i = 0; i < entryList.size() && xmlsr.getEventType() == START_ELEMENT; i++) {
                xmlsr.require(XMLStreamConstants.START_ELEMENT, null, entryTag);
                LibraryMember libMember = dummyMember.getClass().cast(unmarsh.unmarshal(xmlsr));
                if (!delIndices.contains(i)) {
                    marsh.marshal(dummyMember.getClass().cast(libMember), sw);
                }

                if (xmlsr.getEventType() == CHARACTERS) {
                    xmlsr.next();
                }
            }

            xmlsw.writeEndElement();

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


    public ArrayList<LibEntry> getEntryList() {
        ArrayList<LibEntry> entryArray = new ArrayList<LibEntry>();
        int counter = 0;
        try {
            JAXBContext context = JAXBContext.newInstance(dummyMember.getClass());
            XMLInputFactory xmlif = XMLInputFactory.newFactory();
            Unmarshaller unmarsh = context.createUnmarshaller();
            XMLStreamReader xmlsr = xmlif.createXMLStreamReader(new FileReader(xmlFile));
            xmlsr.nextTag();
            xmlsr.require(START_ELEMENT, null, libName);
            xmlsr.nextTag();
            while (xmlsr.getEventType() == START_ELEMENT) {
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

    public LibraryMember loadMember(int location) {
        try {
            JAXBContext context = JAXBContext.newInstance(dummyMember.getClass());
            XMLInputFactory xmlif = XMLInputFactory.newFactory();
            Unmarshaller unmarsh = context.createUnmarshaller();
            XMLStreamReader xmlsr = xmlif.createXMLStreamReader(new FileReader(xmlFile));
            xmlsr.nextTag();
            xmlsr.require(START_ELEMENT, null, libName);
            xmlsr.nextTag();
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

    private void updateDbFile() {
        File tempFile = new File(xmlTempFile);
        File dbFile = new File(xmlFile);
        System.gc();
        try {
            Files.deleteIfExists(dbFile.toPath());
        }
        catch (FileSystemException fsex) {
            updateDbFile();
        }
        catch (IOException ioex) {
            ioex.printStackTrace();
        }
        tempFile.renameTo(dbFile);
    }

}
