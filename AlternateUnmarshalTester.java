/**
 * Created by Michael on 28/11/2014.
 *//*


import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;
import javax.xml.bind.*;
import static javax.xml.stream.XMLStreamConstants.*;
import javax.xml.stream.*;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

public class AlternateUnmarshalTester {

    JAXBContext context;
    Unmarshaller unmarsh;
    XMLInputFactory xmlif;
    XMLStreamReader xmlsr;

    ArrayList<String> names = new ArrayList<String>();

    public static void main(String [] args) {
        new AlternateUnmarshalTester().go();
    }

    public void go(){
        try {
            context = JAXBContext.newInstance(Monster.class);
            unmarsh = context.createUnmarshaller();

            //attempt 3: StAX-JAXB combo
            xmlif = XMLInputFactory.newFactory();

            names = getNamesList();
            Monster newMon = loadMonster(10);
            newMon.setType("Impostor badger");
            updateLibrary(newMon);
        }
        catch (NullPointerException nex) {
            nex.printStackTrace();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ArrayList<String> getNamesList() {
        ArrayList<String> retArray = new ArrayList<String>();
        try {
            xmlsr = xmlif.createXMLStreamReader(new FileReader(ManagerConstants.MONSTER_ALT_XML));
            xmlsr.nextTag();
            xmlsr.require(START_ELEMENT, null, "StatLibrary");
            xmlsr.nextTag();
            while (xmlsr.getEventType() == START_ELEMENT) {
                xmlsr.require(XMLStreamConstants.START_ELEMENT, null, "statblock");
                Monster mon = (Monster) unmarsh.unmarshal(xmlsr);
                retArray.add(mon.getName());
                if (xmlsr.getEventType() == CHARACTERS) {
                    xmlsr.next();
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return retArray;
    }


    private void updateLibrary(Monster newMon) {
        try {
            Marshaller marsh = JAXBContext.newInstance(Monster.class).createMarshaller();
            marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marsh.setProperty("jaxb.fragment", true);

            FileOutputStream fos = new FileOutputStream(ManagerConstants.MONSTER_CLIP_XML);
            XMLOutputFactory xmlof = XMLOutputFactory.newFactory();
            XMLStreamWriter xmlsw = xmlof.createXMLStreamWriter(fos, "UTF-8");

            com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter sw = new IndentingXMLStreamWriter(xmlsw);
            sw.setIndentStep("    ");

            xmlsw.writeStartDocument("UTF-8", "1.0");
            xmlsw.writeStartElement("StatLibrary");
            fos.flush();

            xmlsr = xmlif.createXMLStreamReader(new FileReader(ManagerConstants.MONSTER_ALT_XML));
            xmlsr.nextTag();
            xmlsr.require(START_ELEMENT, null, "StatLibrary");
            xmlsr.nextTag();

            String refName = newMon.getName();
            names.add(refName);
            Collections.sort(names);
            int stopPoint = names.indexOf(refName);
            System.out.println("Stop point: " + stopPoint);

            for (int i = 0; i < stopPoint && xmlsr.getEventType() == START_ELEMENT; i ++) {
                xmlsr.require(XMLStreamConstants.START_ELEMENT, null, "statblock");
                Monster mon = (Monster) unmarsh.unmarshal(xmlsr);
                System.out.println(mon.getName());
                marsh.marshal(mon, sw);
                if (xmlsr.getEventType() == CHARACTERS) {
                    xmlsr.next();
                }
            }

            marsh.marshal(newMon, sw);
            System.out.println(newMon.getName());

            for (int i = 0; i < names.size() -1 && xmlsr.getEventType() == START_ELEMENT; i ++) {
                xmlsr.require(XMLStreamConstants.START_ELEMENT, null, "statblock");
                Monster mon = (Monster) unmarsh.unmarshal(xmlsr);
                System.out.println(mon.getName());
                marsh.marshal(mon, sw);
                if (xmlsr.getEventType() == CHARACTERS) {
                    xmlsr.next();
                }
            }

            xmlsw.writeEndElement();
            fos.flush();
            fos.close();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    */
/**
     * Method to deserialize a monster from the database at a specific index.
     * @param location The index from which to retrieve the monster.
     * @return The deserialized monster at the given index.
     *//*


    private Monster loadMonster(int location) {
        try {
            xmlsr = xmlif.createXMLStreamReader(new FileReader(ManagerConstants.MONSTER_ALT_XML));
            xmlsr.nextTag();
            xmlsr.require(START_ELEMENT, null, "StatLibrary");
            xmlsr.nextTag();
            for (int i = 0; i < location && xmlsr.hasNext(); i++) {
                xmlsr.next();
                while (xmlsr.getEventType() != XMLStreamReader.START_ELEMENT) {
                    xmlsr.next();
                    if (xmlsr.getEventType() == START_ELEMENT && !xmlsr.getLocalName().equals("statblock")) {
                        xmlsr.next();
                    }
                }
            }
            return (Monster) unmarsh.unmarshal(xmlsr);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
*/
