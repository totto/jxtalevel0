package no.auke.level0;

/**
 * Created by IntelliJ IDEA.
 * User: totto
 * Date: 10/4/11
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import net.jxta.document.Attributable;
import net.jxta.document.Attribute;
import net.jxta.document.Element;
import net.jxta.document.StructuredDocument;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.Message.ElementIterator;
import net.jxta.endpoint.MessageElement;
import net.jxta.peer.PeerID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.rendezvous.RendezVousService;

public class Tools {

    public Tools() {

    }

    public static void popConnectedRendezvous(RendezVousService TheRendezVous, String Name) {

        List<PeerID> TheList = TheRendezVous.getLocalRendezVousView();
        Iterator<PeerID> Iter = TheList.iterator();
        int Count = 0;

        while (Iter.hasNext()) {

            Count = Count + 1;

            PopInformationMessage(Name, "Connected to rendezvous:\n\n"
                    + Iter.next().toString());

        }

        if (Count==0) {

            PopInformationMessage(Name, "No rendezvous connected to this rendezvous!");

        }

    }

    public static void popConnectedPeers(RendezVousService TheRendezVous, String Name) {

        List<PeerID> TheList = TheRendezVous.getLocalRendezVousView();
        Iterator<PeerID> Iter = TheList.iterator();
        int Count = 0;

        while (Iter.hasNext()) {

            Count = Count + 1;

            PopInformationMessage(Name, "Peer connected to this rendezvous:\n\n"
                    + Iter.next().toString());

        }

        if (Count==0) {

            PopInformationMessage(Name, "No peers connected to this rendezvous!");

        }

    }

    public static void CheckForMulticastUsage(String Name, NetworkConfigurator TheNC) throws IOException {

        if (JOptionPane.YES_OPTION==PopYesNoQuestion(Name, "Do you want to enable multicasting?")) {

            TheNC.setUseMulticast(true);

        } else {

            TheNC.setUseMulticast(false);

        }

    }

    public static void CheckForRendezVousSeedAddition(String Name, String TheSeed, NetworkConfigurator TheNC) {

        if (JOptionPane.YES_OPTION==PopYesNoQuestion(Name, "Do you want to add seed: " + TheSeed + "?")) {

            URI LocalSeedingRendezVousURI = URI.create(TheSeed);
            TheNC.addSeedRendezvous(LocalSeedingRendezVousURI);

        }

    };

    public static void PopInformationMessage(String Name, String Message) {

        JOptionPane.showMessageDialog(null, Message, Name, JOptionPane.INFORMATION_MESSAGE);

    }

    public static void PopErrorMessage(String Name,String Message) {

        JOptionPane.showMessageDialog(null, Message, Name, JOptionPane.ERROR_MESSAGE);

    }

    public static void PopWarningMessage(String Name, String Message) {

        JOptionPane.showMessageDialog(null, Message, Name, JOptionPane.WARNING_MESSAGE);

    }

    public static int PopYesNoQuestion(String Name, String Question) {

        return JOptionPane.showConfirmDialog(null, Question, Name, JOptionPane.YES_NO_OPTION);

    }

    public static void CheckForExistingConfigurationDeletion(String Name, File ConfigurationFile) throws IOException {

        if (JOptionPane.YES_OPTION==PopYesNoQuestion(Name, "Do you want to delete the existing configuration in:\n\n"
                + ConfigurationFile.getCanonicalPath())) {

            NetworkManager.RecursiveDelete(ConfigurationFile);

        }

    }

    public static void DisplayMessageContent(String Name, Message TheMessage) {

        try {

            String ToDisplay = "--- Message Start ---\n";

            ElementIterator MyElementIterator = TheMessage.getMessageElements();

            while (MyElementIterator.hasNext()) {

                MessageElement MyMessageElement = MyElementIterator.next();

                ToDisplay = ToDisplay + "Element : " +
                        MyElementIterator.getNamespace() + " :: "
                        + MyMessageElement.getElementName()
                        + "  [" + MyMessageElement + "]\n";

            }

            ToDisplay = ToDisplay + "--- Message End ---";

            PopInformationMessage(Name,ToDisplay);

        } catch (Exception Ex) {

            PopErrorMessage(Name, Ex.toString());

        }

    }

    public static final void GoToSleep(long Duration) {

        long Delay = System.currentTimeMillis() + Duration;

        while (System.currentTimeMillis()<Delay) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException Ex) {
                // We don't care
            }
        }

    }

    /**
     *  Recursively copy elements beginnging with <code>from</code> into the
     *  document identified by <code>intoDoc</code>.
     *
     *  @param intoDoc  the document into which the elements which will be
     *  copied.
     *  @param intoElement  the element which will serve as the parent for
     *  the elements being copied.
     *  @param from the root element of the hierarchy which will be copied.
     *  @param copyAttributes whether the elements' attributes should be copied
     *         or not
     **/
    public static void copyElements(StructuredDocument intoDoc, Element intoElement, Element from, boolean recursive, boolean copyAttributes) {

        // Copying current level element
        Element newElement = intoDoc.createElement(from.getKey(), from.getValue());
        intoElement.appendChild(newElement);

        // Copy attributes (eventually)
        if ( copyAttributes )  {

            if ((from instanceof Attributable) && (newElement instanceof Attributable)) {

                Enumeration eachAttrib = ((Attributable) from).getAttributes();

                while (eachAttrib.hasMoreElements()) {
                    Attribute anAttrib = (Attribute) eachAttrib.nextElement();
                    ((Attributable) newElement).addAttribute(anAttrib.getName(), anAttrib.getValue());
                }

            }

        }

        // Looping through the child elements (eventually)
        if ( recursive ) {

            for (Enumeration eachChild = from.getChildren(); eachChild.hasMoreElements();) {

                // recurse to add the children.
                copyElements(intoDoc, newElement, (Element) eachChild.nextElement(), recursive, copyAttributes);

            }

        }

    }

}
