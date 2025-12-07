package kz.kaznu.course.iws2;

public class Main
{
    //https://people.sc.fsu.edu/~jburkardt/data/csv/hw_200.csv
    public static void main(String[] args)
    {
        String url = null;
        String outputFolder = "Download";
        String fileName = "File";

        if(args.length == 1 && args[0].equals("--help"))
        {
            System.out.println("java fileDownloader <URL> <OutputFolder> <fileName>");
            return;
        }

        if(args.length < 3 )
        {
            System.out.println("java fileDownloader <URL> <OutputFolder> <fileName>");
            return;
        }

        url = args[0];
        outputFolder = args[1];
        fileName = args[2];

        FileDownloader fileDownloader = new FileDownloader(url, outputFolder, fileName);
        fileDownloader.downloadFile();
    }
}
