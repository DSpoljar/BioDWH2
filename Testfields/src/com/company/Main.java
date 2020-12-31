package com.company;



public class Main {

    public static void main(String[] args)
    {
        /*
        String strMain = "Alpha, Beta, Delta, Gamma, Sigma";
        String[] arrSplit = strMain.split("{1}");
        for (int i=0; i < arrSplit.length; i++)
        {
            System.out.println(arrSplit[i]);
        } */


        final String CurrentVersion = "Any content, materials, information or software downloaded or otherwise obtained through " +
                                             "the use of the site is done at your own discretion and risk. The IBI group shall have no responsibility " +
                                             "for any damage to your computer system or loss of data that results from the download of any content, " +
                                             "materials, information or software. The IBI group reserves the right to make changes or " +
                                             "updates to the site at any time without notice. \n" +
                                             "\n" +
                                             "If you have any further questions, please email us at support@disgenet.org\n" +
                                             "\n" + "last modified: May, 2020";


        String[] arrSplit = CurrentVersion.split(" | ");

        System.out.println(arrSplit[arrSplit.length-2]+arrSplit[arrSplit.length-1]);

        /*
        int indexCounter = arrSplit.length;
        for (String word : arrSplit)
        {

            if (word.contains("modified:"))
            {

                 System.out.println("match: "+arrSplit[indexCounter+1]);
            }
            else
            {
                System.out.println(word);
            }



        } */



    }
}
