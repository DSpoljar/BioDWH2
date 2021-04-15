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

public class HPOUpdater extends OBOOntologyUpdater<HPODataSource>
{

    public final String currentVersionLink = "https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.obo";
    //public final String phenotypeLink = "http://purl.obolibrary.org/obo/hp/hpoa/genes_to_phenotype.txt";



    public HPOUpdater(final HPODataSource dataSource) {
        super(dataSource);
    }





    @Override
    public Version getNewestVersion()
    {

        try
        {
            return getVersionFromDownloadFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
          return null;
    }

    @Override
    protected String getDownloadUrl()
    {
        return currentVersionLink;
    }

    @Override
    protected Version getVersionFromDataVersionLine(String dataVersion)
    {

        final String[] versionParts = dataVersion.split("releases/")[1].split("-");
        return new Version(Integer.parseInt(versionParts[0]), Integer.parseInt(versionParts[1]),
                           Integer.parseInt(versionParts[2]));
    }


    private Version getVersionFromDownloadFile() throws IOException
    {

        InputStreamReader inputReader = new InputStreamReader(HTTPClient.getUrlInputStream(currentVersionLink),
                                                              StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputReader);
        String line = bufferedReader.readLine();
        while (line != null && !line.trim().startsWith("data-version:"))
            line = bufferedReader.readLine();

        try
        {
            return parseVersion(line);

        } catch (UpdaterMalformedVersionException e)
        {
            e.printStackTrace();
        }
       return null;
    }


    private Version parseVersion(String version) throws UpdaterMalformedVersionException
    {


        version = version.replaceAll("data-version: hp/releases/", "");

        for(int i = 0; i < version.length(); i++)
        {
           if (version.charAt(i) == '-')
           {
             version =  version.replace('-', '.');
           }

        }


        try
        {
            return Version.parse(version);

        } catch (NullPointerException | NumberFormatException e) {
            throw new UpdaterMalformedVersionException(version, e);
        }
    }



    @Override
    protected String getTargetFileName()
    {

        return "hp.obo";
    }


}
