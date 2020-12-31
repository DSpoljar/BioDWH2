package de.unibi.agbi.biodwh2.disgenet.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.disgenet.DisGeNetDataSource;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DisGeNetUpdater extends Updater<DisGeNetDataSource>
{
    public final String DownloadPageUrl = "https://www.disgenet.org/downloads";
    public final String CurrentVersion = "https://www.disgenet.org/static/disgenet_ap1/files/downloads/readme.txt";



    public DisGeNetUpdater(final DisGeNetDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException
    {

        try
        {
            String html = HTTPClient.getWebsiteSource(CurrentVersion);
            String[] splitVersion = CurrentVersion.split(" | "); // Splits contents by word(s).

            // Retrieves the date since it is contained in the last two entries of this string array.

            return parseVersion(
                    splitVersion[splitVersion.length-2]+splitVersion[splitVersion.length-1]);
        }

            //System.out.println(splitVersion[splitVersion.length-2]+splitVersion[splitVersion.length-1]); test purposes.


        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
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
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException
    {

        final String dumpFilePath = dataSource.resolveSourceFilePath(workspace, "rawDrugCentral.sql.gz");

        downloadDisgenetDatabases(dumpFilePath);

        return true;
    }

    private void downloadDisgenetDatabases(final String dumpFilePath) throws UpdaterException {
        File newFile = new File(dumpFilePath);
        URL downloadFileUrl = getDisgenetFileUrl();
        try {
            FileUtils.copyURLToFile(downloadFileUrl, newFile);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }


    private URL getDisgenetFileUrl() throws UpdaterException
    {
        try {

           String db1 = HTTPClient.getWebsiteSource("https://www.disgenet.org/static/disgenet_ap1/files/downloads/curated_gene_disease_associations.tsv.gz");
           String db2 = HTTPClient.getWebsiteSource("https://www.disgenet.org/static/disgenet_ap1/files/downloads/befree_gene_disease_associations.tsv.gz");
           String db3 = HTTPClient.getWebsiteSource("https://www.disgenet.org/static/disgenet_ap1/files/downloads/all_gene_disease_pmid_associations.tsv.gz");
           String db4 = HTTPClient.getWebsiteSource("https://www.disgenet.org/static/disgenet_ap1/files/downloads/all_gene_disease_associations.tsv.gz");

            String[] disgenetDatabases  = {db1, db2, db3, db4};

            for (String word : disgenetDatabases)
                if (word.contains("TODO DISGENET dump")) // TODO
                    return new URL(word.split("\"")[3]);

        } catch (IOException e) {
            e.printStackTrace();
        }



        throw new UpdaterConnectionException("Failed to get database download URL from download page");
    }
}
