package de.unibi.agbi.biodwh2.disgenet.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.disgenet.DisGeNetDataSource;
import org.apache.commons.io.FileUtils;


import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DisGeNetUpdater extends Updater<DisGeNetDataSource>
{
    //public final String DownloadPageUrl = "https://www.disgenet.org/downloads";
    public final String CurrentVersion = "https://www.disgenet.org/static/disgenet_ap1/files/downloads/readme.txt";


    private static final String[] FileNames =
            {

            "curated_gene_disease_associations.tsv.gz", "befree_gene_disease_associations.tsv.gz",
            "all_gene_disease_pmid_associations.tsv.gz", "all_gene_disease_associations.tsv.gz",
            "curated_variant_disease_associations.tsv.gz", "befree_variant_disease_associations.tsv.gz",
            "all_variant_disease_associations.tsv.gz", "all_variant_disease_pmid_associations.tsv.gz",
            "disease_to_disease_CURATED.tsv.gz"

            };



    public DisGeNetUpdater(final DisGeNetDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException
    {
        Map<String, String> dateMapper = new HashMap<String, String>();
        dateMapper.put("01", "January");
        dateMapper.put("02", "February");
        dateMapper.put("03", "March");
        dateMapper.put("04", "April");
        dateMapper.put("05", "May");
        dateMapper.put("06", "June");
        dateMapper.put("07", "July");
        dateMapper.put("08", "August");
        dateMapper.put("09", "September");
        dateMapper.put("10", "October");
        dateMapper.put("11", "November");
        dateMapper.put("12", "December");

        /*
        final String[] months =
                {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
                };
        */


        try
        {
            String html = HTTPClient.getWebsiteSource(CurrentVersion);
            String[] splitVersion = html.split(" "); // Splits contents by word(s).
            String currentDate = splitVersion[splitVersion.length-2]+splitVersion[splitVersion.length-1];
            String[] splittedDate = currentDate.split(",");
            String finalVersion = " ";

            for (Map.Entry<String, String> month : dateMapper.entrySet())
            {
                if (splittedDate[0].equals(month.getValue()))
                {
                   finalVersion = splittedDate[1] + "." + month.getKey();
                }



            }

            // Retrieves the date since it is contained in the last two entries of this string array.

            return parseVersion( finalVersion );

        }


        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private Version parseVersion(String version) throws UpdaterMalformedVersionException
    {
        try
        {
            return Version.parse(version);

        } catch (NullPointerException | NumberFormatException e)
        {
            throw new UpdaterMalformedVersionException(version, e);

        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException
    {
        boolean success = true;
        for (String name : FileNames)
            success = success && downloadFile(name, workspace, dataSource);
        return success;
    }


    private boolean downloadFile(final String fileName, final Workspace workspace,
                                 final DataSource dataSource) throws UpdaterConnectionException
    {
        try
        {
            String sourceFilePath = dataSource.resolveSourceFilePath(workspace, fileName);
            HTTPClient.downloadFileAsBrowser("https://www.disgenet.org/static/disgenet_ap1/files/downloads/" + fileName, sourceFilePath);
        } catch (IOException e)
        {
            throw new UpdaterConnectionException("Failed to download '" + fileName + "'", e);
        }
        return true;
    }


}
