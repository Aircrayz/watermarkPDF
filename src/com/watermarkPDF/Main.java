package com.watermarkPDF;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

class WatermarkPdf {

    public static void main(String[] args) throws IOException, DocumentException {

        String input = "";
        String output = "";
        String background = "";
        String type = "";

        for (String arg : args) {
            if(arg.contains("-help")){
                System.out.println("Following params can be provided:\n" +
                        "\n"+
                        "Param:\tDescription:\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tExample:\n" +
                        "-i:\tPath of the original pdf (has to be provided)\t\t\t\t\t\t\t\t\t\t\t\t\t\t-i:C:/Documents/Test.pdf\n" +
                        "-w:\tWatermark object, can be either a path to an image or pdf or a string (has to be provided)(if anything other than pdf, the type must be provided)\t-w:C:/Documents/Watermark.pdf\n" +
                        "-o:\tPath of the output file. If not provided the input file will be overwritten\t\t\t\t\t\t\t\t\t\t-o:C:/Documents/Watermarked.pdf\n" +
                        "-t:\tType of the Watermarked object. Has to be one of 'pdf', 'img' or 'text' (if not provided, it assumes to be pdf)\t\t\t\t\t\t-t:img\n");
                return;
            }
            else if (arg.contains("-i:")) {
                input = arg.replace("-i:", "");
            }
            else if (arg.contains("-o:")) {
                output = arg.replace("-o:", "");
            }
            else if (arg.contains("-w:")) {
                background = arg.replace("-w:", "");
            }
            else if (arg.contains("-t:")) {
                type = arg.replace("-t:", "");
                if (type.equals("pdf") || type.equals("img") || type.equals("text")) {

                } else {
                    System.out.println("Wrong type provided. Type has to be 'pdf', 'img' or 'text'.");
                    return;
                }
            }
            else{
                System.out.println("Unknown param: "+ arg +". Type -help for more information.");
                return;
            }
            //System.out.println(arg);
        }

        if(input.equals("")){
            System.out.println("No input file defined. Type '-help' for more information.");
            return;
        }
        else{
            if(!exists(input) ) {
                System.out.println("Provided input file don't exist. Please check the path.");
                return;
            }

        }
        if(background == ""){
            System.out.println("No background file defined. Type '-help' for more information.");
            return;
        }
        else{
            if(!background.contains(".pdf") && type.equals("")){
                System.out.println("No PDF file provided. If you want to use an image or plain text, you have to provive the type. Type '-help' for more imformation.");
                return;
            }
            if(!exists(background) && !type.equals("text")){
                System.out.println("Provided background file don't exist. Please check the path.");
                return;
            }
        }
        if(output == ""){
            output = input;
        }
        else {
            String separator = "\\";
            String[] tmpOutputArr = output.split(Pattern.quote(separator));
            String tmpOutput = output.replace(tmpOutputArr[tmpOutputArr.length - 1], "");
            if (!exists(tmpOutput)) {
                System.out.println("Provided output file don't exist. Please check the path.");
                return;
            }
        }
        if(!fileAccessible(output) && exists(output)){
            System.out.println("Output file is currently not accessible. Please close the file and try again.");
            return;
        }

        // read existing pdf
        PdfReader reader = new PdfReader(new FileInputStream(input));
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(output));

        // properties
        PdfContentByte over;
        Rectangle pagesize;
        float x, y;
        int n = reader.getNumberOfPages();

        switch (type){
            case "text":
                Font FONT = new Font(Font.FontFamily.HELVETICA, 34, Font.BOLD, new GrayColor(0.5f));
                Phrase p = new Phrase(background, FONT);
                for (int i = 1; i <= n; i++) {
                    // get page size and position
                    pagesize = reader.getPageSizeWithRotation(i);
                    x = (pagesize.getLeft() + pagesize.getRight()) / 2;
                    y = (pagesize.getTop() + pagesize.getBottom()) / 2;
                    over = stamper.getOverContent(i);
                    over.saveState();
                    // add watermark text
                    ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, 0);
                    over.restoreState();
                }
                break;
            case "img":
                // image watermark
                Image img = Image.getInstance(background);
                float w = img.getScaledWidth();
                float h = img.getScaledHeight();
                for (int i = 1; i <= n; i++) {
                    // get page size and position
                    pagesize = reader.getPageSizeWithRotation(i);
                    x = (pagesize.getLeft() + pagesize.getRight()) / 2;
                    y = (pagesize.getTop() + pagesize.getBottom()) / 2;
                    over = stamper.getOverContent(i);
                    over.saveState();
                    // add watermark image
                    over.addImage(img, w, 0, 0, h, x - (w / 2), y - (h / 2));
                    over.restoreState();
                }
                break;
            case "png":
            default:
                PdfReader watermark = new PdfReader(new FileInputStream(background));
                PdfImportedPage watermarkPage = stamper.getImportedPage(watermark, 1);
                for (int i = 1; i <= n; i++) {
                    over = stamper.getOverContent(i);
                    over.saveState();
                    over.addTemplate(watermarkPage,0,0);
                    over.restoreState();
                }
        }
        stamper.close();
        reader.close();
    }

    private static boolean exists(String path){
        File tmpFile = new File(path);
        boolean exists = tmpFile.exists();
        return exists;
    }
    private static boolean fileAccessible(String path){
        File tmpFile = new File(path);
        File sameTmpFile = new File(path);
        return tmpFile.renameTo(sameTmpFile);
    }
}

// set transparency
//PdfGState state = new PdfGState();
//state.setFillOpacity(0.2f);
//over.setGState(state);