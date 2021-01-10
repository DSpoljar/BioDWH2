package de.unibi.agbi.biodwh2.hpo.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.OBOOntologyUpdater;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.hpo.HPODataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HPOUpdater extends Updater<HPODataSource>
{

    public final String currentVersionLink = "https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.obo";
    public final String downloadLink = "http://purl.obolibrary.org/obo/hp.obo";



    public HPOUpdater(final HPODataSource dataSource) {
        super(dataSource);
    }




    @Override
    public Version getNewestVersion() throws UpdaterException
    {

        try
        {
           return parseVersion(getVersionFromOBOFile());

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }


    private String getVersionFromOBOFile() throws IOException
    {
        InputStreamReader inputReader = new InputStreamReader(HTTPClient.getUrlInputStream(currentVersionLink),
                                                              StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputReader);
        String line = bufferedReader.readLine();
        while (line != null && !line.trim().startsWith("data-version:"))
            line = bufferedReader.readLine();

        return line;

    }


    private Version parseVersion(String version) throws UpdaterMalformedVersionException
    {
        try {
            return Version.parse(version);
        } catch (NullPointerException | NumberFormatException e) {
            throw new UpdaterMalformedVersionException(version, e);
        }
    }




    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterConnectionException
    {

        try
        {
            final String targetFilePath = dataSource.resolveSourceFilePath(workspace, "testFile");
            HTTPClient.downloadFileAsBrowser(downloadLink, targetFilePath);
            
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download '" + "testFile" + "'", e);
        }
        return true;
    }


}
