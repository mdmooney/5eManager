/**
 * Created by Michael on 09/11/2014.
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(name = "StatLibrary")
@XmlAccessorType(XmlAccessType.FIELD)

public class StatLibrary {
    @XmlElement (name = "statblock")
    private ArrayList<Monster> blockList;

    public void setBlockList(ArrayList<Monster> blockList) {
        this.blockList = blockList;
    }

    public ArrayList<Monster> getBlocksList() {
        return blockList;
    }
}

