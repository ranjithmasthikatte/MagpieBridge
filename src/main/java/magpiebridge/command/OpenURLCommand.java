package magpiebridge.command;

import com.google.gson.JsonPrimitive;
import com.ibm.wala.util.io.FileUtil;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import magpiebridge.core.MagpieClient;
import magpiebridge.core.MagpieServer;
import magpiebridge.core.WorkspaceCommand;
import magpiebridge.util.URIUtils;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.services.LanguageClient;

/**
 * Implementation of opening an URL in the client or the default browser.
 *
 * @author Julian Dolby
 * @author Linghui Luo
 */
public class OpenURLCommand implements WorkspaceCommand {

  @Override
  public void execute(ExecuteCommandParams params, MagpieServer server, LanguageClient client) {
    if (Desktop.isDesktopSupported()) {
      try {
        String uri;
        Object uriJson = params.getArguments().get(0);
        if (uriJson instanceof JsonPrimitive) {
          uri = ((JsonPrimitive) uriJson).getAsString();
        } else {
          uri = (String) uriJson;
        }
        showHTMLinClientOrBroswer(server, client, uri);
      } catch (IOException | URISyntaxException e) {
        MagpieServer.ExceptionLogger.log(e);
        e.printStackTrace();
      }
    }
  }

  /**
   * Show A HTML page with the given URI in the client, or in a browser if the client doesn't
   * support this.
   *
   * @param server The MagpieServer
   * @param client The IDE/Editor
   * @param uri The URI which should be opened
   * @throws IOException IO exception
   * @throws URISyntaxException URI exception
   */
  public static void showHTMLinClientOrBroswer(
      MagpieServer server, LanguageClient client, String uri)
      throws IOException, URISyntaxException {
    if (server.clientSupportShowHTML()) {
      if (client instanceof MagpieClient) {
        MessageParams mp = new MessageParams();
        mp.setType(MessageType.Info);
        mp.setMessage(new String(FileUtil.readBytes(new URL(URIUtils.checkURI(uri)).openStream())));
        ((MagpieClient) client).showHTML(mp);
      }
    } else {
      if (Desktop.isDesktopSupported())
        Desktop.getDesktop().browse(new URI(URIUtils.checkURI(uri)));
    }
  }
}
